import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

/**
 * @author zhouzh
 * @date 2025年10月27日 下午2:15
 * @since
 */
public class HotkeyPositionMouseClicker {

    private Robot robot;
    private AtomicBoolean clicking = new AtomicBoolean(false);
    private Thread clickThread;
    private Preferences prefs;

    // 操作系统检测
    private boolean isMac;
    private boolean isWindows;
    private String modifierKey;  // "⌘" for Mac, "Ctrl" for Windows

    // 配置参数
    private int clickInterval = 100;
    private int clickCount = 0;
    private int buttonType = 0;
    private boolean randomInterval = false;
    private int minInterval = 50;
    private int maxInterval = 200;

    // 点击位置参数
    private int clickX = -1;  // -1 表示当前位置
    private int clickY = -1;
    private boolean useCurrentPosition = true;

    // GUI 组件引用
    private JTextField xField;
    private JTextField yField;
    private JRadioButton customPosRadio;
    private JTextArea logArea;

    public HotkeyPositionMouseClicker() {
        // 检测操作系统
        String osName = System.getProperty("os.name").toLowerCase();
        isMac = osName.contains("mac");
        isWindows = osName.contains("windows");
        modifierKey = isMac ? "⌘" : "Ctrl";

        prefs = Preferences.userNodeForPackage(HotkeyPositionMouseClicker.class);
        loadPreferences();

        try {
            robot = new Robot();
            robot.setAutoDelay(10);
        } catch (AWTException e) {
            showError("无法初始化机器人实例: " + e.getMessage());
            System.exit(1);
        }

        setupOSFeatures();
        createGUI();
    }

    private void setupOSFeatures() {
        if (isMac) {
            System.setProperty("apple.awt.application.name", "鼠标连点器 - 快捷键版");
            System.setProperty("apple.awt.fullscreenable", "true");
            try {
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "鼠标连点器");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            } catch (Exception e) {
                System.out.println("macOS 特性设置失败: " + e.getMessage());
            }
        }
    }

    private void createGUI() {
        String title = isMac ? "鼠标连点器 - 快捷键版" : "鼠标连点器 - Windows版";
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 点击位置面板
        JPanel positionPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        positionPanel.setBorder(BorderFactory.createTitledBorder("点击位置设置"));

        JRadioButton currentPosRadio = new JRadioButton("当前鼠标位置", useCurrentPosition);
        customPosRadio = new JRadioButton("自定义位置", !useCurrentPosition);

        ButtonGroup positionGroup = new ButtonGroup();
        positionGroup.add(currentPosRadio);
        positionGroup.add(customPosRadio);

        xField = new JTextField(clickX == -1 ? "" : String.valueOf(clickX));
        yField = new JTextField(clickY == -1 ? "" : String.valueOf(clickY));
        xField.setEnabled(!useCurrentPosition);
        yField.setEnabled(!useCurrentPosition);

        JButton getPosButton = new JButton("获取当前位置 (" + modifierKey + "P)");
        JButton testPosButton = new JButton("测试位置 (" + modifierKey + "T)");

        positionPanel.add(currentPosRadio);
        positionPanel.add(new JLabel());
        positionPanel.add(customPosRadio);
        positionPanel.add(new JLabel());
        positionPanel.add(new JLabel("X坐标:"));
        positionPanel.add(xField);
        positionPanel.add(new JLabel("Y坐标:"));
        positionPanel.add(yField);
        positionPanel.add(getPosButton);
        positionPanel.add(testPosButton);

        // 点击设置面板
        JPanel clickPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        clickPanel.setBorder(BorderFactory.createTitledBorder("点击设置"));

        JTextField intervalField = new JTextField(String.valueOf(clickInterval));
        JTextField countField = new JTextField(String.valueOf(clickCount));
        JComboBox<String> buttonCombo = new JComboBox<>(new String[]{"左键", "右键", "中键"});
        buttonCombo.setSelectedIndex(buttonType);

        JCheckBox randomCheck = new JCheckBox("随机间隔", randomInterval);
        JTextField minField = new JTextField(String.valueOf(minInterval));
        JTextField maxField = new JTextField(String.valueOf(maxInterval));

        clickPanel.add(new JLabel("点击间隔(ms):"));
        clickPanel.add(intervalField);
        clickPanel.add(new JLabel("点击次数(0=无限):"));
        clickPanel.add(countField);
        clickPanel.add(new JLabel("鼠标按钮:"));
        clickPanel.add(buttonCombo);
        clickPanel.add(randomCheck);
        clickPanel.add(new JLabel());
        clickPanel.add(new JLabel("最小间隔:"));
        clickPanel.add(minField);
        clickPanel.add(new JLabel("最大间隔:"));
        clickPanel.add(maxField);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton startBtn = new JButton("开始 (" + modifierKey + "1)");
        JButton stopBtn = new JButton("停止 (" + modifierKey + "2)");
        JButton saveBtn = new JButton("保存设置");

        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(saveBtn);

        // 状态显示
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        // 根据操作系统选择字体
        String fontName = isMac ? "Monaco" : (isWindows ? "Consolas" : "Monospaced");
        logArea.setFont(new Font(fontName, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("运行日志"));

        // 快捷键说明面板
        JPanel hotkeyPanel = new JPanel();
        hotkeyPanel.setLayout(new FlowLayout());
        hotkeyPanel.setBorder(BorderFactory.createTitledBorder("快捷键说明"));

        String hotkeyText = modifierKey + "1:开始  " + modifierKey + "2:停止  " + 
                           modifierKey + "P:获取位置  " + modifierKey + "T:测试位置  " + 
                           modifierKey + "S:快速保存";
        JLabel hotkeyLabel = new JLabel(hotkeyText);
        hotkeyPanel.add(hotkeyLabel);

        // 事件处理 - 位置设置
        currentPosRadio.addActionListener(e -> {
            useCurrentPosition = true;
            xField.setEnabled(false);
            yField.setEnabled(false);
            logArea.append("📍 使用当前鼠标位置\n");
        });

        customPosRadio.addActionListener(e -> {
            useCurrentPosition = false;
            xField.setEnabled(true);
            yField.setEnabled(true);
            logArea.append("🎯 使用自定义位置\n");
        });

        getPosButton.addActionListener(e -> {
            getCurrentMousePosition();
        });

        testPosButton.addActionListener(e -> {
            if (updatePositionSettings()) {
                testClickPosition();
            }
        });

        // 事件处理 - 点击控制
        startBtn.addActionListener(e -> {
            if (updateSettings(intervalField, countField, minField, maxField) &&
                    updatePositionSettings()) {
                buttonType = buttonCombo.getSelectedIndex();
                randomInterval = randomCheck.isSelected();
                startClicking();
            }
        });

        stopBtn.addActionListener(e -> stopClicking());

        saveBtn.addActionListener(e -> {
            if (updateSettings(intervalField, countField, minField, maxField) &&
                    updatePositionSettings()) {
                savePreferences();
                logArea.append("✅ 设置已保存\n");
            }
        });

        mainPanel.add(positionPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(clickPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(hotkeyPanel, BorderLayout.SOUTH);

        setupHotkeys(frame, startBtn, stopBtn, getPosButton, testPosButton, saveBtn);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 获取当前鼠标位置（核心功能）
     */
    private void getCurrentMousePosition() {
        try {
            // 短暂延迟，确保用户有时间移动鼠标
            Thread.sleep(300);

            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            xField.setText(String.valueOf(mousePos.x));
            yField.setText(String.valueOf(mousePos.y));

            // 自动切换到自定义位置模式
            if (!customPosRadio.isSelected()) {
                customPosRadio.setSelected(true);
                useCurrentPosition = false;
                xField.setEnabled(true);
                yField.setEnabled(true);
            }

            logArea.append("📌 快捷键获取位置: " + mousePos.x + ", " + mousePos.y + "\n");
            logArea.append("🎯 已自动切换到自定义位置模式\n");

        } catch (Exception ex) {
            logArea.append("❌ 获取位置失败: " + ex.getMessage() + "\n");
        }
    }

    /**
     * 更新位置设置
     */
    private boolean updatePositionSettings() {
        if (useCurrentPosition) {
            clickX = -1;
            clickY = -1;
            return true;
        }

        try {
            String xText = xField.getText().trim();
            String yText = yField.getText().trim();

            if (xText.isEmpty() || yText.isEmpty()) {
                showError("请输入有效的坐标数字");
                return false;
            }

            clickX = Integer.parseInt(xText);
            clickY = Integer.parseInt(yText);

            // 获取屏幕尺寸进行验证
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (clickX < 0 || clickX > screenSize.width || clickY < 0 || clickY > screenSize.height) {
                showError("坐标超出屏幕范围！屏幕尺寸: " + screenSize.width + "x" + screenSize.height);
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showError("请输入有效的坐标数字");
            return false;
        }
    }

    /**
     * 测试点击位置
     */
    private void testClickPosition() {
        new Thread(() -> {
            try {
                if (useCurrentPosition) {
                    logArea.append("📍 测试：将在当前鼠标位置点击\n");
                    logArea.append("💡 提示：请将鼠标移动到目标位置\n");
                } else {
                    logArea.append("🎯 测试：将在位置 (" + clickX + ", " + clickY + ") 点击\n");

                    // 保存原始位置
                    Point originalPos = MouseInfo.getPointerInfo().getLocation();

                    // 移动到目标位置
                    robot.mouseMove(clickX, clickY);
                    Thread.sleep(500);

                    // 执行测试点击
                    performClick();

                    // 移回原始位置
                    robot.mouseMove(originalPos.x, originalPos.y);

                    logArea.append("✅ 位置测试完成\n");
                }
            } catch (Exception e) {
                logArea.append("❌ 位置测试失败: " + e.getMessage() + "\n");
            }
        }).start();
    }

    private void startClicking() {
        if (clicking.compareAndSet(false, true)) {
            clickThread = new Thread(() -> {
                logArea.append("🚀 连点器启动中...3秒后开始\n");

                // 保存原始鼠标位置（用于恢复）
                Point originalPos = null;
                if (!useCurrentPosition) {
                    originalPos = MouseInfo.getPointerInfo().getLocation();
                    logArea.append("📍 原始位置已保存: " + originalPos.x + ", " + originalPos.y + "\n");
                }

                try {
                    for (int i = 3; i > 0; i--) {
                        if (!clicking.get()) {
                            logArea.append("🛑 用户取消\n");
                            return;
                        }
                        final int count = i;
                        SwingUtilities.invokeLater(() ->
                                logArea.append("⏰ " + count + "秒后开始...\n"));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    logArea.append("🛑 启动被中断\n");
                    clicking.set(false);
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    if (useCurrentPosition) {
                        logArea.append("🎯 开始连点（当前鼠标位置）\n");
                    } else {
                        logArea.append("🎯 开始连点（固定位置: " + clickX + ", " + clickY + "）\n");
                    }
                });

                int executedClicks = 0;

                while (clicking.get() && (clickCount == 0 || executedClicks < clickCount)) {
                    try {
                        // 移动到指定位置（如果不是使用当前位置）
                        if (!useCurrentPosition) {
                            robot.mouseMove(clickX, clickY);
                            Thread.sleep(50); // 等待移动完成
                        }

                        performClick();

                        executedClicks++;
                        final int currentCount = executedClicks;
                        SwingUtilities.invokeLater(() -> {
                            logArea.append("🖱️ 点击次数: " + currentCount +
                                    (clickCount > 0 ? "/" + clickCount : "") + "\n");
                            logArea.setCaretPosition(logArea.getDocument().getLength());
                        });

                        if (clicking.get()) {
                            int waitTime = randomInterval ?
                                    minInterval + (int) (Math.random() * (maxInterval - minInterval)) :
                                    clickInterval;

                            Thread.sleep(waitTime);
                        }

                    } catch (InterruptedException e) {
                        SwingUtilities.invokeLater(() -> {
                            logArea.append("🛑 连点被中断\n");
                        });
                        break;
                    }
                }

                // 恢复原始位置（如果使用了固定位置）
                if (!useCurrentPosition && originalPos != null) {
                    try {
                        robot.mouseMove(originalPos.x, originalPos.y);
                        logArea.append("📍 鼠标位置已恢复\n");
                    } catch (Exception e) {
                        logArea.append("⚠️ 鼠标位置恢复失败: " + e.getMessage() + "\n");
                    }
                }

                clicking.set(false);
                int finalExecutedClicks = executedClicks;
                SwingUtilities.invokeLater(() -> {
                    logArea.append("✅ 连点器停止，总共点击: " + finalExecutedClicks + " 次\n");
                });
            });

            clickThread.setDaemon(true);
            clickThread.start();
        }
    }

    /**
     * 执行点击操作
     */
    private void performClick() {
        try {
            robot.mousePress(getButtonMask());
            Thread.sleep(20 + (int) (Math.random() * 30));
            robot.mouseRelease(getButtonMask());
        } catch (InterruptedException e) {
            try {
                robot.mouseRelease(getButtonMask());
            } catch (Exception ex) {
                // 忽略释放时的异常
            }
            throw new RuntimeException("点击被中断", e);
        }
    }

    private void stopClicking() {
        if (clicking.compareAndSet(true, false)) {
            if (clickThread != null && clickThread.isAlive()) {
                clickThread.interrupt();
                try {
                    clickThread.join(1000);
                } catch (InterruptedException e) {
                    System.out.println("停止等待被中断");
                }
            }
        }
    }

    private int getButtonMask() {
        switch (buttonType) {
            case 0:
                return InputEvent.BUTTON1_DOWN_MASK;
            case 1:
                return InputEvent.BUTTON3_DOWN_MASK;
            case 2:
                return InputEvent.BUTTON2_DOWN_MASK;
            default:
                return InputEvent.BUTTON1_DOWN_MASK;
        }
    }

    /**
     * 设置跨平台快捷键（自动适配Mac和Windows）
     */
    private void setupHotkeys(JFrame frame, JButton startBtn, JButton stopBtn,
                              JButton getPosButton, JButton testPosButton, JButton saveBtn) {
        JRootPane rootPane = frame.getRootPane();
        // 自动获取当前系统的快捷键修饰键（Mac使用Meta，Windows使用Ctrl）
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        // Ctrl/⌘1 - 开始连点
        KeyStroke startKey = KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(startKey, "start");
        rootPane.getActionMap().put("start", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                startBtn.doClick();
            }
        });

        // Ctrl/⌘2 - 停止连点
        KeyStroke stopKey = KeyStroke.getKeyStroke(KeyEvent.VK_2, menuShortcutKeyMask);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stopKey, "stop");
        rootPane.getActionMap().put("stop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                stopBtn.doClick();
            }
        });

        // Ctrl/⌘P - 获取当前位置
        KeyStroke getPosKey = KeyStroke.getKeyStroke(KeyEvent.VK_P, menuShortcutKeyMask);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getPosKey, "getPosition");
        rootPane.getActionMap().put("getPosition", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                getPosButton.doClick();
            }
        });

        // Ctrl/⌘T - 测试位置
        KeyStroke testPosKey = KeyStroke.getKeyStroke(KeyEvent.VK_T, menuShortcutKeyMask);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(testPosKey, "testPosition");
        rootPane.getActionMap().put("testPosition", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                testPosButton.doClick();
            }
        });

        // Ctrl/⌘S - 快速保存设置
        KeyStroke saveKey = KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcutKeyMask);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveKey, "saveSettings");
        rootPane.getActionMap().put("saveSettings", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                saveBtn.doClick();
            }
        });

        // F12 - 快速获取位置（备用快捷键，跨平台通用）
        KeyStroke f12Key = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f12Key, "quickGetPosition");
        rootPane.getActionMap().put("quickGetPosition", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                getCurrentMousePosition();
            }
        });
    }

    private boolean updateSettings(JTextField intervalField, JTextField countField,
                                   JTextField minField, JTextField maxField) {
        try {
            clickInterval = Integer.parseInt(intervalField.getText());
            clickCount = Integer.parseInt(countField.getText());
            minInterval = Integer.parseInt(minField.getText());
            maxInterval = Integer.parseInt(maxField.getText());

            if (clickInterval < 1 || minInterval < 1 || maxInterval < 1) {
                showError("间隔时间必须大于0");
                return false;
            }
            if (minInterval >= maxInterval) {
                showError("最小间隔必须小于最大间隔");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showError("请输入有效的数字");
            return false;
        }
    }

    private void loadPreferences() {
        clickInterval = prefs.getInt("interval", 100);
        clickCount = prefs.getInt("count", 0);
        buttonType = prefs.getInt("button", 0);
        randomInterval = prefs.getBoolean("random", false);
        minInterval = prefs.getInt("minInterval", 50);
        maxInterval = prefs.getInt("maxInterval", 200);
        clickX = prefs.getInt("clickX", -1);
        clickY = prefs.getInt("clickY", -1);
        useCurrentPosition = prefs.getBoolean("useCurrentPosition", true);
    }

    private void savePreferences() {
        prefs.putInt("interval", clickInterval);
        prefs.putInt("count", clickCount);
        prefs.putInt("button", buttonType);
        prefs.putBoolean("random", randomInterval);
        prefs.putInt("minInterval", minInterval);
        prefs.putInt("maxInterval", maxInterval);
        prefs.putInt("clickX", clickX);
        prefs.putInt("clickY", clickY);
        prefs.putBoolean("useCurrentPosition", useCurrentPosition);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            // 只在Mac上设置macOS特定的系统属性
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac")) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "鼠标连点器");
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(HotkeyPositionMouseClicker::new);
    }
}
