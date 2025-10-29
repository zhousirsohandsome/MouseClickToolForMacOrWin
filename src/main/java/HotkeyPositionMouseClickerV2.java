package src.main.java;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

/**
 * 跨平台鼠标连点器 V2 - 支持多位置点击和自定义顺序
 * 
 * <p>V2版本新增功能：
 * <ul>
 *   <li>支持添加多个点击位置</li>
 *   <li>支持指定点击顺序</li>
 *   <li>按照指定顺序循环重复点击</li>
 *   <li>支持位置的添加、删除、编辑和排序</li>
 * </ul>
 * 
 * @author zhouzh
 * @date 2025-10-29
 * @version 2.0
 */
public class HotkeyPositionMouseClickerV2 {

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
     * 位置列表表格列名
     */
    private static final String[] POSITION_TABLE_COLUMNS = {"序号", "X坐标", "Y坐标", "备注"};

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
     * 点击位置列表
     */
    private List<ClickPosition> clickPositions = new ArrayList<>();

    /**
     * GUI组件引用
     */
    private JTable positionTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JTextField intervalField;
    private JTextField countField;
    private JComboBox<String> buttonCombo;
    private JCheckBox randomCheck;
    private JCheckBox fastModeCheck;
    private JTextField minField;
    private JTextField maxField;
    private JButton startBtn;
    private JButton stopBtn;

    /**
     * 点击位置数据类
     */
    private static class ClickPosition {
        int x;
        int y;
        String note;

        ClickPosition(int x, int y, String note) {
            this.x = x;
            this.y = y;
            this.note = note;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")" + (note != null && !note.isEmpty() ? " - " + note : "");
        }
    }

    /**
     * 构造函数
     */
    public HotkeyPositionMouseClickerV2() {
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
        prefs = Preferences.userNodeForPackage(HotkeyPositionMouseClickerV2.class);
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
            System.setProperty("apple.awt.application.name", "鼠标连点器 V2 - 多位置版");
            System.setProperty("apple.awt.fullscreenable", "true");
            try {
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "鼠标连点器 V2");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            } catch (Exception e) {
                // macOS特性设置失败，不影响主要功能
            }
        }
    }

    /**
     * 创建GUI界面
     */
    private void createGUI() {
        String title = isMac ? "鼠标连点器 V2 - 多位置版" : "鼠标连点器 V2 - Windows版";
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));

        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = createLogScrollPane();
        JPanel hotkeyPanel = createHotkeyPanel();

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(hotkeyPanel, BorderLayout.SOUTH);

        setupHotkeys(frame, startBtn, stopBtn);
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
        JPanel positionPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        positionPanel.setBorder(BorderFactory.createTitledBorder("点击位置列表（按顺序循环点击）"));

        // 创建表格
        tableModel = new DefaultTableModel(POSITION_TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 序号列不可编辑，其他列可编辑
                return column != 0;
            }
        };
        positionTable = new JTable(tableModel);
        positionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        positionTable.getTableHeader().setReorderingAllowed(false);

        // 设置列宽
        positionTable.getColumn("序号").setPreferredWidth(50);
        positionTable.getColumn("X坐标").setPreferredWidth(80);
        positionTable.getColumn("Y坐标").setPreferredWidth(80);
        positionTable.getColumn("备注").setPreferredWidth(150);

        JScrollPane tableScrollPane = new JScrollPane(positionTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 150));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addPosBtn = new JButton("添加位置 (" + modifierKey + "P)");
        JButton deletePosBtn = new JButton("删除选中");
        JButton moveUpBtn = new JButton("上移");
        JButton moveDownBtn = new JButton("下移");
        JButton clearAllBtn = new JButton("清空所有");
        JButton testPosBtn = new JButton("测试选中位置");
        JButton testAllBtn = new JButton("测试所有位置");

        buttonPanel.add(addPosBtn);
        buttonPanel.add(deletePosBtn);
        buttonPanel.add(moveUpBtn);
        buttonPanel.add(moveDownBtn);
        buttonPanel.add(clearAllBtn);
        buttonPanel.add(testPosBtn);
        buttonPanel.add(testAllBtn);

        // 事件处理
        addPosBtn.addActionListener(e -> addCurrentPosition());
        deletePosBtn.addActionListener(e -> deleteSelectedPosition());
        moveUpBtn.addActionListener(e -> movePositionUp());
        moveDownBtn.addActionListener(e -> movePositionDown());
        clearAllBtn.addActionListener(e -> clearAllPositions());
        testPosBtn.addActionListener(e -> testSelectedPosition());
        testAllBtn.addActionListener(e -> testAllPositions());

        // 表格编辑事件
        positionTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getFirstRow() >= 0) {
                SwingUtilities.invokeLater(() -> updatePositionFromTable(e.getFirstRow()));
            }
        });

        // 键盘快捷键：Delete键删除选中行
        positionTable.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteRow");
        positionTable.getActionMap().put("deleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPosition();
            }
        });

        positionPanel.add(tableScrollPane, BorderLayout.CENTER);
        positionPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshPositionTable();

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
        clickPanel.add(new JLabel("循环次数(0=无限):"));
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

        startBtn = new JButton("开始循环点击 (" + modifierKey + "1)");
        stopBtn = new JButton("停止 (" + modifierKey + "2)");
        JButton saveBtn = new JButton("保存设置");

        startBtn.addActionListener(e -> {
            if (updateSettings(intervalField, countField, minField, maxField) && validatePositions()) {
                buttonType = buttonCombo.getSelectedIndex();
                randomInterval = randomCheck.isSelected();
                fastMode = fastModeCheck.isSelected();
                startClicking();
            }
        });

        stopBtn.addActionListener(e -> stopClicking());

        saveBtn.addActionListener(e -> {
            if (updateSettings(intervalField, countField, minField, maxField)) {
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
                .append(modifierKey).append("P:添加当前位置");

        JLabel hotkeyLabel = new JLabel(hotkeyText.toString());
        hotkeyPanel.add(hotkeyLabel);
        return hotkeyPanel;
    }

    /**
     * 添加当前鼠标位置
     */
    private void addCurrentPosition() {
        try {
            Thread.sleep(GET_POSITION_DELAY);
            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            ClickPosition pos = new ClickPosition(mousePos.x, mousePos.y, "");

            // 检查是否重复
            if (isDuplicatePosition(pos)) {
                int result = JOptionPane.showConfirmDialog(null,
                        "位置 (" + pos.x + ", " + pos.y + ") 已存在，是否仍要添加？",
                        "重复位置确认", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    appendLog("⏭️ 已跳过重复位置");
                    return;
                }
            }

            clickPositions.add(pos);
            refreshPositionTable();
            appendLog("📌 已添加位置: " + pos);
            // 自动保存
            savePreferences();
            // 自动选中新添加的行
            positionTable.setRowSelectionInterval(clickPositions.size() - 1,
                    clickPositions.size() - 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            appendLog("❌ 添加位置失败: " + e.getMessage());
        } catch (Exception ex) {
            appendLog("❌ 添加位置失败: " + ex.getMessage());
        }
    }

    /**
     * 检查位置是否重复
     */
    private boolean isDuplicatePosition(ClickPosition pos) {
        for (ClickPosition existing : clickPositions) {
            if (existing.x == pos.x && existing.y == pos.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除选中位置
     */
    private void deleteSelectedPosition() {
        int selectedRow = positionTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < clickPositions.size()) {
            ClickPosition pos = clickPositions.remove(selectedRow);
            refreshPositionTable();
            appendLog("🗑️ 已删除位置: " + pos);
            // 自动保存
            savePreferences();
            // 如果有其他位置，保持选中状态
            if (!clickPositions.isEmpty()) {
                int newSelection = Math.min(selectedRow, clickPositions.size() - 1);
                positionTable.setRowSelectionInterval(newSelection, newSelection);
            }
        } else {
            showError("请先选择一个位置");
        }
    }

    /**
     * 上移选中位置
     */
    private void movePositionUp() {
        int selectedRow = positionTable.getSelectedRow();
        if (selectedRow > 0 && selectedRow < clickPositions.size()) {
            ClickPosition pos = clickPositions.remove(selectedRow);
            clickPositions.add(selectedRow - 1, pos);
            refreshPositionTable();
            positionTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
            appendLog("⬆️ 位置已上移");
        } else if (selectedRow < 0) {
            showError("请先选择一个位置");
        }
    }

    /**
     * 下移选中位置
     */
    private void movePositionDown() {
        int selectedRow = positionTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < clickPositions.size() - 1) {
            ClickPosition pos = clickPositions.remove(selectedRow);
            clickPositions.add(selectedRow + 1, pos);
            refreshPositionTable();
            positionTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
            appendLog("⬇️ 位置已下移");
        } else if (selectedRow < 0) {
            showError("请先选择一个位置");
        }
    }

    /**
     * 清空所有位置
     */
    private void clearAllPositions() {
        if (clickPositions.isEmpty()) {
            showError("位置列表已经为空");
            return;
        }
        int result = JOptionPane.showConfirmDialog(null,
                "确定要清空所有 " + clickPositions.size() + " 个位置吗？此操作不可恢复！", "确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            int count = clickPositions.size();
            clickPositions.clear();
            refreshPositionTable();
            appendLog("🗑️ 已清空所有 " + count + " 个位置");
            // 自动保存
            savePreferences();
        }
    }

    /**
     * 测试选中位置
     */
    private void testSelectedPosition() {
        int selectedRow = positionTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < clickPositions.size()) {
            ClickPosition pos = clickPositions.get(selectedRow);
            testSinglePosition(pos);
        } else {
            showError("请先选择一个位置");
        }
    }

    /**
     * 测试单个位置
     */
    private void testSinglePosition(ClickPosition pos) {
        Thread testThread = new Thread(() -> {
            try {
                appendLog("🎯 测试位置: " + pos);
                Point originalPos = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(pos.x, pos.y);
                Thread.sleep(500);
                performClick();
                robot.mouseMove(originalPos.x, originalPos.y);
                appendLog("✅ 位置测试完成");
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
     * 测试所有位置（按顺序）
     */
    private void testAllPositions() {
        if (clickPositions.isEmpty()) {
            showError("位置列表为空，无法测试");
            return;
        }

        Thread testThread = new Thread(() -> {
            try {
                appendLog("🎯 开始测试所有 " + clickPositions.size() + " 个位置");
                Point originalPos = MouseInfo.getPointerInfo().getLocation();

                for (int i = 0; i < clickPositions.size(); i++) {
                    ClickPosition pos = clickPositions.get(i);
                    appendLog("📍 测试位置 " + (i + 1) + "/" + clickPositions.size() + ": " + pos);
                    robot.mouseMove(pos.x, pos.y);
                    Thread.sleep(300);
                    performClick();
                    Thread.sleep(200);
                }

                robot.mouseMove(originalPos.x, originalPos.y);
                appendLog("✅ 所有位置测试完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                appendLog("❌ 位置测试被中断");
            } catch (Exception e) {
                appendLog("❌ 位置测试失败: " + e.getMessage());
            }
        }, "TestAllPositionsThread");
        testThread.start();
    }

    /**
     * 更新表格中的位置数据
     */
    private void updatePositionFromTable(int row) {
        if (row < 0 || row >= clickPositions.size()) {
            return;
        }

        try {
            Object xObj = tableModel.getValueAt(row, 1);
            Object yObj = tableModel.getValueAt(row, 2);
            Object noteObj = tableModel.getValueAt(row, 3);

            if (xObj == null || yObj == null) {
                refreshPositionTable();
                return;
            }

            String xText = xObj.toString().trim();
            String yText = yObj.toString().trim();
            String note = noteObj != null ? noteObj.toString().trim() : "";

            if (xText.isEmpty() || yText.isEmpty()) {
                showError("X坐标和Y坐标不能为空");
                refreshPositionTable();
                return;
            }

            int x = Integer.parseInt(xText);
            int y = Integer.parseInt(yText);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (x < 0 || x > screenSize.width || y < 0 || y > screenSize.height) {
                showError("坐标 (" + x + ", " + y + ") 超出屏幕范围！\n屏幕尺寸: "
                        + screenSize.width + " x " + screenSize.height);
                refreshPositionTable();
                return;
            }

            clickPositions.get(row).x = x;
            clickPositions.get(row).y = y;
            clickPositions.get(row).note = note;
            appendLog("✏️ 已更新位置 " + (row + 1) + ": " + clickPositions.get(row));
            // 自动保存位置列表
            savePreferences();
        } catch (NumberFormatException e) {
            showError("请输入有效的坐标数字（必须是整数）");
            refreshPositionTable();
        }
    }

    /**
     * 刷新位置表格
     */
    private void refreshPositionTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < clickPositions.size(); i++) {
            ClickPosition pos = clickPositions.get(i);
            tableModel.addRow(new Object[]{i + 1, pos.x, pos.y, pos.note});
        }
    }

    /**
     * 验证位置列表
     */
    private boolean validatePositions() {
        if (clickPositions.isEmpty()) {
            showError("请至少添加一个点击位置！\n提示：将鼠标移动到目标位置，然后按 " + modifierKey + "P 或点击\"添加位置\"按钮");
            return false;
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        for (int i = 0; i < clickPositions.size(); i++) {
            ClickPosition pos = clickPositions.get(i);
            if (pos.x < 0 || pos.x > screenSize.width || pos.y < 0 || pos.y > screenSize.height) {
                showError("位置 " + (i + 1) + " (" + pos.x + ", " + pos.y + ") 超出屏幕范围！\n"
                        + "屏幕尺寸: " + screenSize.width + " x " + screenSize.height + "\n"
                        + "请在表格中编辑该位置的坐标");
                // 自动选中问题位置
                positionTable.setRowSelectionInterval(i, i);
                return false;
            }
        }

        // 检查重复位置并提示
        checkDuplicatePositions();
        return true;
    }

    /**
     * 检查并提示重复位置
     */
    private void checkDuplicatePositions() {
        for (int i = 0; i < clickPositions.size(); i++) {
            for (int j = i + 1; j < clickPositions.size(); j++) {
                ClickPosition pos1 = clickPositions.get(i);
                ClickPosition pos2 = clickPositions.get(j);
                if (pos1.x == pos2.x && pos1.y == pos2.y) {
                    appendLog("⚠️ 检测到重复位置: 位置" + (i + 1) + " 和 位置" + (j + 1)
                            + " 坐标相同 (" + pos1.x + ", " + pos1.y + ")");
                }
            }
        }
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
            appendLog("📍 将按顺序循环点击 " + clickPositions.size() + " 个位置");

            Point originalPos = MouseInfo.getPointerInfo().getLocation();

            // 倒计时
            if (!performCountdown()) {
                clicking.set(false);
                return;
            }

            appendLog("🎯 开始循环点击（总位置数: " + clickPositions.size() + "）");
            int executedCycles = performCycleClicking(originalPos);

            restoreMousePosition(originalPos);
            clicking.set(false);
            appendLogFinal(executedCycles);
        }, "ClickThread");

        clickThread.setDaemon(true);
        clickThread.start();
    }

    /**
     * 执行倒计时
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
     * 执行循环点击
     */
    private int performCycleClicking(Point originalPos) {
        int executedCycles = 0;
        int totalClicks = 0;

        while (clicking.get() && (clickCount == 0 || executedCycles < clickCount)) {
            // 按顺序点击所有位置
            for (int i = 0; i < clickPositions.size() && clicking.get(); i++) {
                try {
                    ClickPosition pos = clickPositions.get(i);
                    robot.mouseMove(pos.x, pos.y);
                    int moveDelay = fastMode ? FAST_MODE_MOVE_DELAY : NORMAL_MODE_MOVE_DELAY;
                    Thread.sleep(moveDelay);

                    performClick();
                    totalClicks++;

                    // 更新日志
                    final int currentCycle = executedCycles + 1;
                    final int currentPos = i + 1;
                    final int currentTotal = totalClicks;
                    if (!fastMode || totalClicks % FAST_MODE_LOG_UPDATE_FREQUENCY == 0 || totalClicks == 1) {
                        SwingUtilities.invokeLater(() -> {
                            String message = "🖱️ 第" + currentCycle + "轮 位置" + currentPos + "/"
                                    + clickPositions.size() + " (" + pos + ") - 总点击: " + currentTotal;
                            if (clickCount > 0) {
                                message += " (轮次 " + currentCycle + "/" + clickCount + ")";
                            }
                            appendLog(message);
                        });
                    }

                    // 位置之间的间隔（最后一个位置后使用循环间隔）
                    if (clicking.get() && i < clickPositions.size() - 1) {
                        int waitTime = calculateWaitTime();
                        Thread.sleep(waitTime);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    SwingUtilities.invokeLater(() -> appendLog("🛑 连点被中断"));
                    return executedCycles;
                }
            }

            // 循环间隔
            if (clicking.get() && (clickCount == 0 || executedCycles + 1 < clickCount)) {
                try {
                    int waitTime = calculateWaitTime();
                    Thread.sleep(waitTime);
                    executedCycles++;
                    final int currentCycle = executedCycles;
                    SwingUtilities.invokeLater(() -> appendLog("🔄 完成第 " + currentCycle + " 轮循环"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return executedCycles;
                }
            } else if (clicking.get()) {
                executedCycles++;
            }
        }

        return executedCycles;
    }

    /**
     * 计算等待时间
     */
    private int calculateWaitTime() {
        if (randomInterval) {
            return minInterval + (int) (Math.random() * (maxInterval - minInterval));
        }
        return clickInterval;
    }

    /**
     * 恢复鼠标位置
     */
    private void restoreMousePosition(Point originalPos) {
        if (originalPos != null) {
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
     */
    private void appendLogFinal(int executedCycles) {
        SwingUtilities.invokeLater(() -> {
            appendLog("✅ 连点器停止，完成 " + executedCycles + " 轮循环");
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
                }
            }
        }
    }

    /**
     * 获取按钮掩码
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
     */
    private void setupHotkeys(JFrame frame, JButton startBtn, JButton stopBtn) {
        JRootPane rootPane = frame.getRootPane();
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        registerHotkey(rootPane, KeyEvent.VK_1, menuShortcutKeyMask, "start", startBtn);
        registerHotkey(rootPane, KeyEvent.VK_2, menuShortcutKeyMask, "stop", stopBtn);

        // Ctrl/⌘P - 添加当前位置
        KeyStroke getPosKey = KeyStroke.getKeyStroke(KeyEvent.VK_P, menuShortcutKeyMask);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getPosKey, "addPosition");
        rootPane.getActionMap().put("addPosition", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCurrentPosition();
            }
        });
    }

    /**
     * 注册快捷键
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

        // 加载位置列表
        int positionCount = prefs.getInt("positionCount", 0);
        clickPositions.clear();
        for (int i = 0; i < positionCount; i++) {
            int x = prefs.getInt("posX" + i, 0);
            int y = prefs.getInt("posY" + i, 0);
            String note = prefs.get("posNote" + i, "");
            clickPositions.add(new ClickPosition(x, y, note));
        }
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

        // 保存位置列表
        prefs.putInt("positionCount", clickPositions.size());
        for (int i = 0; i < clickPositions.size(); i++) {
            ClickPosition pos = clickPositions.get(i);
            prefs.putInt("posX" + i, pos.x);
            prefs.putInt("posY" + i, pos.y);
            prefs.put("posNote" + i, pos.note != null ? pos.note : "");
        }
    }

    /**
     * 显示错误消息
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 追加日志
     */
    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains(OS_MAC)) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "鼠标连点器 V2");
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // 忽略异常，使用默认外观
            }
        }

        SwingUtilities.invokeLater(HotkeyPositionMouseClickerV2::new);
    }
}

