package src.main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

/**
 * 跨平台鼠标连点器 - 支持macOS和Windows
 * 
 * <p>功能特性：
 * <ul>
 *   <li>支持当前鼠标位置和自定义坐标点击</li>
 *   <li>支持快捷键操作（自动适配Mac和Windows）</li>
 *   <li>支持极速模式和随机间隔模式</li>
 *   <li>配置自动保存和恢复</li>
 * </ul>
 * 
 * @author zhouzh
 * @date 2025-10-27
 * @version 1.0
 */
public class HotkeyPositionMouseClicker {

    /**
     * 表示使用当前鼠标位置的坐标值
     */
    private static final int CURRENT_POSITION = -1;

    /**
     * 默认点击间隔（毫秒）
     */
    private static final int DEFAULT_CLICK_INTERVAL = 100;

    /**
     * 默认最小间隔（毫秒）
     */
    private static final int DEFAULT_MIN_INTERVAL = 50;

    /**
     * 默认最大间隔（毫秒）
     */
    private static final int DEFAULT_MAX_INTERVAL = 200;

    /**
     * 极速模式点击延迟（毫秒）
     */
    private static final int FAST_MODE_CLICK_DELAY = 1;

    /**
     * 普通模式最小点击延迟（毫秒）
     */
    private static final int NORMAL_MODE_MIN_DELAY = 20;

    /**
     * 普通模式最大点击延迟（毫秒）
     */
    private static final int NORMAL_MODE_MAX_DELAY = 50;

    /**
     * 极速模式鼠标移动延迟（毫秒）
     */
    private static final int FAST_MODE_MOVE_DELAY = 10;

    /**
     * 普通模式鼠标移动延迟（毫秒）
     */
    private static final int NORMAL_MODE_MOVE_DELAY = 50;

    /**
     * 获取鼠标位置延迟（毫秒）
     */
    private static final int GET_POSITION_DELAY = 300;

    /**
     * 测试点击位置延迟（毫秒）
     */
    private static final int TEST_POSITION_DELAY = 500;

    /**
     * 倒计时秒数
     */
    private static final int COUNTDOWN_SECONDS = 3;

    /**
     * 倒计时间隔（毫秒）
     */
    private static final int COUNTDOWN_INTERVAL_MS = 1000;

    /**
     * 极速模式日志更新频率
     */
    private static final int FAST_MODE_LOG_UPDATE_FREQUENCY = 10;

    /**
     * 线程等待超时时间（毫秒）
     */
    private static final int THREAD_JOIN_TIMEOUT_MS = 1000;

    /**
     * 日志区域行数
     */
    private static final int LOG_AREA_ROWS = 10;

    /**
     * 日志区域列数
     */
    private static final int LOG_AREA_COLS = 40;

    /**
     * 日志字体大小
     */
    private static final int LOG_FONT_SIZE = 12;

    /**
     * 边框间距
     */
    private static final int BORDER_PADDING = 20;

    /**
     * 组件间距
     */
    private static final int COMPONENT_SPACING = 10;

    /**
     * 最小有效间隔时间（毫秒）
     */
    private static final int MIN_INTERVAL_THRESHOLD = 1;

    /**
     * 左键类型
     */
    private static final int BUTTON_TYPE_LEFT = 0;

    /**
     * 右键类型
     */
    private static final int BUTTON_TYPE_RIGHT = 1;

    /**
     * 中键类型
     */
    private static final int BUTTON_TYPE_MIDDLE = 2;

    /**
     * macOS修饰键符号
     */
    private static final String MAC_MODIFIER_KEY = "⌘";

    /**
     * Windows修饰键符号
     */
    private static final String WINDOWS_MODIFIER_KEY = "Ctrl";

    /**
     * macOS系统名称标识
     */
    private static final String OS_MAC = "mac";

    /**
     * Windows系统名称标识
     */
    private static final String OS_WINDOWS = "windows";

    /**
     * macOS字体名称
     */
    private static final String FONT_MONACO = "Monaco";

    /**
     * Windows字体名称
     */
    private static final String FONT_CONSOLAS = "Consolas";

    /**
     * 默认字体名称
     */
    private static final String FONT_MONOSPACED = "Monospaced";

    /**
     * Robot对象
     */
    private Robot robot;

    /**
     * 点击状态标志
     */
    private AtomicBoolean clicking = new AtomicBoolean(false);

    /**
     * 点击线程
     */
    private Thread clickThread;

    /**
     * 配置管理器
     */
    private Preferences prefs;

    /**
     * 是否为Mac系统
     */
    private boolean isMac;

    /**
     * 是否为Windows系统
     */
    private boolean isWindows;

    /**
     * 修饰键符号
     */
    private String modifierKey;

    /**
     * 点击间隔（毫秒）
     */
    private int clickInterval = DEFAULT_CLICK_INTERVAL;

    /**
     * 点击次数（0表示无限）
     */
    private int clickCount = 0;

    /**
     * 按钮类型
     */
    private int buttonType = BUTTON_TYPE_LEFT;

    /**
     * 是否随机间隔
     */
    private boolean randomInterval = false;

    /**
     * 最小间隔（毫秒）
     */
    private int minInterval = DEFAULT_MIN_INTERVAL;

    /**
     * 最大间隔（毫秒）
     */
    private int maxInterval = DEFAULT_MAX_INTERVAL;

    /**
     * 是否启用极速模式
     */
    private boolean fastMode = false;

    /**
     * 点击X坐标（-1表示当前位置）
     */
    private int clickX = CURRENT_POSITION;

    /**
     * 点击Y坐标（-1表示当前位置）
     */
    private int clickY = CURRENT_POSITION;

    /**
     * 是否使用当前鼠标位置
     */
    private boolean useCurrentPosition = true;

    /**
     * X坐标输入框
     */
    private JTextField xField;

    /**
     * Y坐标输入框
     */
    private JTextField yField;

    /**
     * 自定义位置单选按钮
     */
    private JRadioButton customPosRadio;

    /**
     * 日志显示区域
     */
    private JTextArea logArea;

    /**
     * 极速模式复选框
     */
    private JCheckBox fastModeCheck;

    /**
     * 构造函数
     */
    public HotkeyPositionMouseClicker() {
        detectOperatingSystem();
        initializePreferences();
        initializeRobot();
        setupOSFeatures();
        createGUI();
    }

    /**
     * 检测操作系统
     */
    private void detectOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        isMac = osName.contains(OS_MAC);
        isWindows = osName.contains(OS_WINDOWS);
        modifierKey = isMac ? MAC_MODIFIER_KEY : WINDOWS_MODIFIER_KEY;
    }

    /**
     * 初始化配置管理器
     */
    private void initializePreferences() {
        prefs = Preferences.userNodeForPackage(HotkeyPositionMouseClicker.class);
        loadPreferences();
    }

    /**
     * 初始化Robot对象
     */
    private void initializeRobot() {
        try {
            robot = new Robot();
            robot.setAutoDelay(0);
        } catch (AWTException e) {
            showError("无法初始化机器人实例: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 设置操作系统特定特性
     */
    private void setupOSFeatures() {
        if (isMac) {
            System.setProperty("apple.awt.application.name", "鼠标连点器 - 快捷键版");
            System.setProperty("apple.awt.fullscreenable", "true");
            try {
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "鼠标连点器");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            } catch (Exception e) {
                // macOS特性设置失败，不影响主要功能
            }
        }
    }

    /**
     * GUI组件引用（用于事件处理）
     */
    private JTextField intervalField;
    private JTextField countField;
    private JComboBox<String> buttonCombo;
    private JCheckBox randomCheck;
    private JTextField minField;
    private JTextField maxField;
    private JButton startBtn;
    private JButton stopBtn;
    private JButton saveBtn;
    private JButton getPosButton;
    private JButton testPosButton;

    /**
     * 创建GUI界面
     */
    private void createGUI() {
        String title = isMac ? "鼠标连点器 - 快捷键版" : "鼠标连点器 - Windows版";
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));

        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = createLogScrollPane();
        JPanel hotkeyPanel = createHotkeyPanel();

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(hotkeyPanel, BorderLayout.SOUTH);

        setupHotkeys(frame, startBtn, stopBtn, getPosButton, testPosButton, saveBtn);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 创建主面板
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_PADDING,
                BORDER_PADDING, BORDER_PADDING));

        JPanel positionPanel = createPositionPanel();
        JPanel clickPanel = createClickPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(positionPanel);
        mainPanel.add(Box.createVerticalStrut(COMPONENT_SPACING));
        mainPanel.add(clickPanel);
        mainPanel.add(Box.createVerticalStrut(COMPONENT_SPACING));
        mainPanel.add(buttonPanel);

        return mainPanel;
    }

    /**
     * 创建位置设置面板
     */
    private JPanel createPositionPanel() {
        JPanel positionPanel = new JPanel(new GridLayout(0, 2, COMPONENT_SPACING, COMPONENT_SPACING));
        positionPanel.setBorder(BorderFactory.createTitledBorder("点击位置设置"));

        JRadioButton currentPosRadio = new JRadioButton("当前鼠标位置", useCurrentPosition);
        customPosRadio = new JRadioButton("自定义位置", !useCurrentPosition);

        ButtonGroup positionGroup = new ButtonGroup();
        positionGroup.add(currentPosRadio);
        positionGroup.add(customPosRadio);

        String xText = clickX == CURRENT_POSITION ? "" : String.valueOf(clickX);
        String yText = clickY == CURRENT_POSITION ? "" : String.valueOf(clickY);
        xField = new JTextField(xText);
        yField = new JTextField(yText);
        xField.setEnabled(!useCurrentPosition);
        yField.setEnabled(!useCurrentPosition);

        getPosButton = new JButton("获取当前位置 (" + modifierKey + "P)");
        testPosButton = new JButton("测试位置 (" + modifierKey + "T)");

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

        // 事件处理
        currentPosRadio.addActionListener(e -> {
            useCurrentPosition = true;
            xField.setEnabled(false);
            yField.setEnabled(false);
            appendLog("📍 使用当前鼠标位置");
        });

        customPosRadio.addActionListener(e -> {
            useCurrentPosition = false;
            xField.setEnabled(true);
            yField.setEnabled(true);
            appendLog("🎯 使用自定义位置");
        });

        getPosButton.addActionListener(e -> getCurrentMousePosition());

        testPosButton.addActionListener(e -> {
            if (updatePositionSettings()) {
                testClickPosition();
            }
        });

        return positionPanel;
    }

    /**
     * 创建点击设置面板
     */
    private JPanel createClickPanel() {
        JPanel clickPanel = new JPanel(new GridLayout(0, 2, COMPONENT_SPACING, COMPONENT_SPACING));
        clickPanel.setBorder(BorderFactory.createTitledBorder("点击设置"));

        intervalField = new JTextField(String.valueOf(clickInterval));
        countField = new JTextField(String.valueOf(clickCount));
        String[] buttonNames = {"左键", "右键", "中键"};
        buttonCombo = new JComboBox<>(buttonNames);
        buttonCombo.setSelectedIndex(buttonType);

        randomCheck = new JCheckBox("随机间隔", randomInterval);
        fastModeCheck = new JCheckBox("极速模式（最大化点击速率）", fastMode);
        minField = new JTextField(String.valueOf(minInterval));
        maxField = new JTextField(String.valueOf(maxInterval));

        clickPanel.add(new JLabel("点击间隔(ms):"));
        clickPanel.add(intervalField);
        clickPanel.add(new JLabel("点击次数(0=无限):"));
        clickPanel.add(countField);
        clickPanel.add(new JLabel("鼠标按钮:"));
        clickPanel.add(buttonCombo);
        clickPanel.add(randomCheck);
        clickPanel.add(fastModeCheck);
        clickPanel.add(new JLabel("最小间隔:"));
        clickPanel.add(minField);
        clickPanel.add(new JLabel("最大间隔:"));
        clickPanel.add(maxField);

        return clickPanel;
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        startBtn = new JButton("开始 (" + modifierKey + "1)");
        stopBtn = new JButton("停止 (" + modifierKey + "2)");
        saveBtn = new JButton("保存设置");

        // 事件处理
        startBtn.addActionListener(e -> {
            if (updateSettings(intervalField, countField, minField, maxField)
                    && updatePositionSettings()) {
                buttonType = buttonCombo.getSelectedIndex();
                randomInterval = randomCheck.isSelected();
                fastMode = fastModeCheck.isSelected();
                startClicking();
            }
        });

        stopBtn.addActionListener(e -> stopClicking());

        saveBtn.addActionListener(e -> {
            if (updateSettings(intervalField, countField, minField, maxField)
                    && updatePositionSettings()) {
                buttonType = buttonCombo.getSelectedIndex();
                randomInterval = randomCheck.isSelected();
                fastMode = fastModeCheck.isSelected();
                savePreferences();
                appendLog("✅ 设置已保存");
            }
        });

        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(saveBtn);

        return buttonPanel;
    }

    /**
     * 创建日志滚动面板
     */
    private JScrollPane createLogScrollPane() {
        logArea = new JTextArea(LOG_AREA_ROWS, LOG_AREA_COLS);
        logArea.setEditable(false);
        String fontName = getFontName();
        logArea.setFont(new Font(fontName, Font.PLAIN, LOG_FONT_SIZE));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("运行日志"));
        return scrollPane;
    }

    /**
     * 获取字体名称
     */
    private String getFontName() {
        if (isMac) {
            return FONT_MONACO;
        } else if (isWindows) {
            return FONT_CONSOLAS;
        }
        return FONT_MONOSPACED;
    }

    /**
     * 创建快捷键说明面板
     */
    private JPanel createHotkeyPanel() {
        JPanel hotkeyPanel = new JPanel();
        hotkeyPanel.setLayout(new FlowLayout());
        hotkeyPanel.setBorder(BorderFactory.createTitledBorder("快捷键说明"));

        StringBuilder hotkeyText = new StringBuilder();
        hotkeyText.append(modifierKey).append("1:开始  ")
                .append(modifierKey).append("2:停止  ")
                .append(modifierKey).append("P:获取位置  ")
                .append(modifierKey).append("T:测试位置  ")
                .append(modifierKey).append("S:快速保存");

        JLabel hotkeyLabel = new JLabel(hotkeyText.toString());
        hotkeyPanel.add(hotkeyLabel);
        return hotkeyPanel;
    }

    /**
     * 获取当前鼠标位置
     */
    private void getCurrentMousePosition() {
        try {
            Thread.sleep(GET_POSITION_DELAY);
            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            xField.setText(String.valueOf(mousePos.x));
            yField.setText(String.valueOf(mousePos.y));

            if (!customPosRadio.isSelected()) {
                customPosRadio.setSelected(true);
                useCurrentPosition = false;
                xField.setEnabled(true);
                yField.setEnabled(true);
            }

            appendLog("📌 快捷键获取位置: " + mousePos.x + ", " + mousePos.y);
            appendLog("🎯 已自动切换到自定义位置模式");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            appendLog("❌ 获取位置失败: " + e.getMessage());
        } catch (Exception ex) {
            appendLog("❌ 获取位置失败: " + ex.getMessage());
        }
    }

    /**
     * 更新位置设置
     * 
     * @return 更新是否成功
     */
    private boolean updatePositionSettings() {
        if (useCurrentPosition) {
            clickX = CURRENT_POSITION;
            clickY = CURRENT_POSITION;
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

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (clickX < 0 || clickX > screenSize.width
                    || clickY < 0 || clickY > screenSize.height) {
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
        Thread testThread = new Thread(() -> {
            try {
                if (useCurrentPosition) {
                    appendLog("📍 测试：将在当前鼠标位置点击");
                    appendLog("💡 提示：请将鼠标移动到目标位置");
                } else {
                    appendLog("🎯 测试：将在位置 (" + clickX + ", " + clickY + ") 点击");

                    Point originalPos = MouseInfo.getPointerInfo().getLocation();
                    robot.mouseMove(clickX, clickY);
                    Thread.sleep(TEST_POSITION_DELAY);
                    performClick();
                    robot.mouseMove(originalPos.x, originalPos.y);
                    appendLog("✅ 位置测试完成");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                appendLog("❌ 位置测试失败: " + e.getMessage());
            } catch (Exception e) {
                appendLog("❌ 位置测试失败: " + e.getMessage());
            }
        }, "TestClickThread");
        testThread.start();
    }

    /**
     * 开始点击
     */
    private void startClicking() {
        if (!clicking.compareAndSet(false, true)) {
            return;
        }

        clickThread = new Thread(() -> {
            appendLog("🚀 连点器启动中...3秒后开始");

            Point originalPos = null;
            if (!useCurrentPosition) {
                originalPos = MouseInfo.getPointerInfo().getLocation();
                appendLog("📍 原始位置已保存: " + originalPos.x + ", " + originalPos.y);
            }

            // 倒计时
            if (!performCountdown()) {
                clicking.set(false);
                return;
            }

            appendLogStartClicking();
            int executedClicks = performClicking(originalPos);

            restoreMousePosition(originalPos);
            clicking.set(false);
            appendLogFinal(executedClicks);
        }, "ClickThread");

        clickThread.setDaemon(true);
        clickThread.start();
    }

    /**
     * 执行倒计时
     * 
     * @return 是否成功完成倒计时
     */
    private boolean performCountdown() {
        try {
            for (int i = COUNTDOWN_SECONDS; i > 0; i--) {
                if (!clicking.get()) {
                    appendLog("🛑 用户取消");
                    return false;
                }
                final int count = i;
                SwingUtilities.invokeLater(() -> appendLog("⏰ " + count + "秒后开始..."));
                Thread.sleep(COUNTDOWN_INTERVAL_MS);
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            appendLog("🛑 启动被中断");
            return false;
        }
    }

    /**
     * 记录开始点击日志
     */
    private void appendLogStartClicking() {
        SwingUtilities.invokeLater(() -> {
            if (useCurrentPosition) {
                appendLog("🎯 开始连点（当前鼠标位置）");
            } else {
                appendLog("🎯 开始连点（固定位置: " + clickX + ", " + clickY + "）");
            }
        });
    }

    /**
     * 执行点击循环
     * 
     * @param originalPos 原始鼠标位置
     * @return 执行的点击次数
     */
    private int performClicking(Point originalPos) {
        int executedClicks = 0;

        while (clicking.get() && (clickCount == 0 || executedClicks < clickCount)) {
            try {
                if (!useCurrentPosition) {
                    robot.mouseMove(clickX, clickY);
                    int moveDelay = fastMode ? FAST_MODE_MOVE_DELAY : NORMAL_MODE_MOVE_DELAY;
                    Thread.sleep(moveDelay);
                }

                performClick();
                executedClicks++;

                updateClickCountLog(executedClicks);

                if (clicking.get()) {
                    int waitTime = calculateWaitTime();
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                SwingUtilities.invokeLater(() -> appendLog("🛑 连点被中断"));
                break;
            }
        }

        return executedClicks;
    }

    /**
     * 计算等待时间
     * 
     * @return 等待时间（毫秒）
     */
    private int calculateWaitTime() {
        if (randomInterval) {
            return minInterval + (int) (Math.random() * (maxInterval - minInterval));
        }
        return clickInterval;
    }

    /**
     * 更新点击次数日志
     * 
     * @param executedClicks 已执行点击次数
     */
    private void updateClickCountLog(int executedClicks) {
        final int currentCount = executedClicks;
        if (!fastMode || executedClicks % FAST_MODE_LOG_UPDATE_FREQUENCY == 0 || executedClicks == 1) {
            SwingUtilities.invokeLater(() -> {
                String logMessage = "🖱️ 点击次数: " + currentCount
                        + (clickCount > 0 ? "/" + clickCount : "");
                appendLog(logMessage);
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    /**
     * 恢复鼠标位置
     * 
     * @param originalPos 原始位置
     */
    private void restoreMousePosition(Point originalPos) {
        if (!useCurrentPosition && originalPos != null) {
            try {
                robot.mouseMove(originalPos.x, originalPos.y);
                appendLog("📍 鼠标位置已恢复");
            } catch (Exception e) {
                appendLog("⚠️ 鼠标位置恢复失败: " + e.getMessage());
            }
        }
    }

    /**
     * 记录最终日志
     * 
     * @param executedClicks 总点击次数
     */
    private void appendLogFinal(int executedClicks) {
        SwingUtilities.invokeLater(() -> {
            appendLog("✅ 连点器停止，总共点击: " + executedClicks + " 次");
        });
    }

    /**
     * 执行点击操作
     */
    private void performClick() {
        try {
            robot.mousePress(getButtonMask());
            int clickDelay = fastMode ? FAST_MODE_CLICK_DELAY
                    : (NORMAL_MODE_MIN_DELAY + (int) (Math.random()
                            * (NORMAL_MODE_MAX_DELAY - NORMAL_MODE_MIN_DELAY)));
            Thread.sleep(clickDelay);
            robot.mouseRelease(getButtonMask());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            try {
                robot.mouseRelease(getButtonMask());
            } catch (Exception ex) {
                // 忽略释放时的异常
            }
            // 不抛出运行时异常，只记录日志
            appendLog("⚠️ 点击操作被中断");
        }
    }

    /**
     * 停止点击
     */
    private void stopClicking() {
        if (clicking.compareAndSet(true, false)) {
            if (clickThread != null && clickThread.isAlive()) {
                clickThread.interrupt();
                try {
                    clickThread.join(THREAD_JOIN_TIMEOUT_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    // 记录日志而不是使用System.out
                }
            }
        }
    }

    /**
     * 获取按钮掩码
     * 
     * @return 按钮掩码值
     */
    private int getButtonMask() {
        switch (buttonType) {
            case BUTTON_TYPE_LEFT:
                return InputEvent.BUTTON1_DOWN_MASK;
            case BUTTON_TYPE_RIGHT:
                return InputEvent.BUTTON3_DOWN_MASK;
            case BUTTON_TYPE_MIDDLE:
                return InputEvent.BUTTON2_DOWN_MASK;
            default:
                return InputEvent.BUTTON1_DOWN_MASK;
        }
    }

    /**
     * 设置快捷键
     * 
     * @param frame 主窗口
     * @param startBtn 开始按钮
     * @param stopBtn 停止按钮
     * @param getPosButton 获取位置按钮
     * @param testPosButton 测试位置按钮
     * @param saveBtn 保存按钮
     */
    private void setupHotkeys(JFrame frame, JButton startBtn, JButton stopBtn,
            JButton getPosButton, JButton testPosButton, JButton saveBtn) {
        JRootPane rootPane = frame.getRootPane();
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        registerHotkey(rootPane, KeyEvent.VK_1, menuShortcutKeyMask, "start", startBtn);
        registerHotkey(rootPane, KeyEvent.VK_2, menuShortcutKeyMask, "stop", stopBtn);
        registerHotkey(rootPane, KeyEvent.VK_P, menuShortcutKeyMask, "getPosition", getPosButton);
        registerHotkey(rootPane, KeyEvent.VK_T, menuShortcutKeyMask, "testPosition", testPosButton);
        registerHotkey(rootPane, KeyEvent.VK_S, menuShortcutKeyMask, "saveSettings", saveBtn);

        // F12备用快捷键
        KeyStroke f12Key = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f12Key, "quickGetPosition");
        rootPane.getActionMap().put("quickGetPosition", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCurrentMousePosition();
            }
        });
    }

    /**
     * 注册快捷键
     * 
     * @param rootPane 根面板
     * @param keyCode 按键代码
     * @param modifiers 修饰键
     * @param actionKey 动作键
     * @param button 按钮
     */
    private void registerHotkey(JRootPane rootPane, int keyCode, int modifiers,
            String actionKey, JButton button) {
        if (button == null) {
            return;
        }
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        rootPane.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick();
            }
        });
    }

    /**
     * 更新设置
     * 
     * @param intervalField 间隔输入框
     * @param countField 次数输入框
     * @param minField 最小间隔输入框
     * @param maxField 最大间隔输入框
     * @return 更新是否成功
     */
    private boolean updateSettings(JTextField intervalField, JTextField countField,
            JTextField minField, JTextField maxField) {
        try {
            clickInterval = Integer.parseInt(intervalField.getText());
            clickCount = Integer.parseInt(countField.getText());
            minInterval = Integer.parseInt(minField.getText());
            maxInterval = Integer.parseInt(maxField.getText());

            if (clickInterval < MIN_INTERVAL_THRESHOLD || minInterval < MIN_INTERVAL_THRESHOLD
                    || maxInterval < MIN_INTERVAL_THRESHOLD) {
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

    /**
     * 加载配置
     */
    private void loadPreferences() {
        clickInterval = prefs.getInt("interval", DEFAULT_CLICK_INTERVAL);
        clickCount = prefs.getInt("count", 0);
        buttonType = prefs.getInt("button", BUTTON_TYPE_LEFT);
        randomInterval = prefs.getBoolean("random", false);
        fastMode = prefs.getBoolean("fastMode", false);
        minInterval = prefs.getInt("minInterval", DEFAULT_MIN_INTERVAL);
        maxInterval = prefs.getInt("maxInterval", DEFAULT_MAX_INTERVAL);
        clickX = prefs.getInt("clickX", CURRENT_POSITION);
        clickY = prefs.getInt("clickY", CURRENT_POSITION);
        useCurrentPosition = prefs.getBoolean("useCurrentPosition", true);
    }

    /**
     * 保存配置
     */
    private void savePreferences() {
        prefs.putInt("interval", clickInterval);
        prefs.putInt("count", clickCount);
        prefs.putInt("button", buttonType);
        prefs.putBoolean("random", randomInterval);
        prefs.putBoolean("fastMode", fastMode);
        prefs.putInt("minInterval", minInterval);
        prefs.putInt("maxInterval", maxInterval);
        prefs.putInt("clickX", clickX);
        prefs.putInt("clickY", clickY);
        prefs.putBoolean("useCurrentPosition", useCurrentPosition);
    }

    /**
     * 显示错误消息
     * 
     * @param message 错误消息
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 追加日志
     * 
     * @param message 日志消息
     */
    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains(OS_MAC)) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "鼠标连点器");
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // 忽略异常，使用默认外观
            }
        }

        SwingUtilities.invokeLater(HotkeyPositionMouseClicker::new);
    }
}
