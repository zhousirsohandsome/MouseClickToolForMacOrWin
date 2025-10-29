# 鼠标连点器 - 跨平台快捷键版

一个功能强大的跨平台鼠标连点工具，支持 macOS 和 Windows 系统，支持快捷键操作和位置记忆功能。

## 📦 版本说明

- **V1.0** - 基础版本：单位置点击，支持当前鼠标位置和自定义坐标
- **V2.0** - 多位置版本：支持多个点击位置，可指定点击顺序，按顺序循环点击

## ✨ 功能特性

- 🎯 **智能位置选择**
  - 支持当前鼠标位置点击
  - 支持自定义固定坐标点击
  - 一键获取鼠标当前位置
  - 位置测试功能

- 🖥️ **跨平台支持**
  - ✅ macOS 系统（自动适配 ⌘ 键）
  - ✅ Windows 系统（自动适配 Ctrl 键）
  - 自动检测操作系统并适配界面和快捷键
  - 原生系统外观和体验

- ⌨️ **快捷键支持（自动适配系统）**
  - macOS: `⌘1` / Windows: `Ctrl+1` - 开始连点
  - macOS: `⌘2` / Windows: `Ctrl+2` - 停止连点
  - macOS: `⌘P` / Windows: `Ctrl+P` - 获取当前位置
  - macOS: `⌘T` / Windows: `Ctrl+T` - 测试点击位置
  - macOS: `⌘S` / Windows: `Ctrl+S` - 快速保存设置
  - `F12` - 备用快速获取位置（跨平台通用）

- 🖱️ **灵活的点击配置**
  - 可设置点击间隔（毫秒）
  - 支持设置点击次数（0 = 无限）
  - 支持左键、右键、中键
  - 支持随机间隔模式（模拟真人操作）
  - **极速模式**：最大化点击速率，适合需要高速点击的场景

- ⚡ **性能优化**
  - Robot 自动延迟优化（设置为 0ms）
  - 极速模式：点击延迟从 20-50ms 降至 1ms
  - 鼠标移动延迟优化（极速模式下减少等待时间）
  - 智能日志更新（极速模式下降低 UI 更新频率）
  - 理论最高速率：每秒 80-100 次点击（取决于系统性能）

- 💾 **设置持久化**
  - 自动保存用户设置
  - 下次启动自动恢复配置

## 🚀 快速开始

### 环境要求

- Java 8 或更高版本
- macOS 或 Windows 系统
- Maven 3.x（可选，用于构建）

### 编译运行

#### V1.0 版本（单位置版本）

```bash
# 克隆项目
git clone <your-repo-url>
cd MouseClickToolForMacOrWin

# 编译 V1.0 版本
javac --release 8 src/main/java/HotkeyPositionMouseClicker.java -d out

# 运行 V1.0 版本
java -cp out HotkeyPositionMouseClicker
```

#### V2.0 版本（多位置版本）✨ 推荐

```bash
# 编译 V2.0 版本
javac --release 8 src/main/java/HotkeyPositionMouseClickerV2.java -d out

# 运行 V2.0 版本
java -cp out HotkeyPositionMouseClickerV2
```

**Windows 用户：**
```bash
# V1.0 版本
javac --release 8 src/main/java/HotkeyPositionMouseClicker.java -d out
java -cp out HotkeyPositionMouseClicker

# V2.0 版本
javac --release 8 src/main/java/HotkeyPositionMouseClickerV2.java -d out
java -cp out HotkeyPositionMouseClickerV2
```

**macOS/Linux 用户：**
```bash
# V1.0 版本
javac --release 8 src/main/java/HotkeyPositionMouseClicker.java -d out
java -cp out HotkeyPositionMouseClicker

# V2.0 版本
javac --release 8 src/main/java/HotkeyPositionMouseClickerV2.java -d out
java -cp out HotkeyPositionMouseClickerV2
```

**一键编译并运行：**
```bash
# Windows (PowerShell) - V1.0
javac --release 8 src/main/java/HotkeyPositionMouseClicker.java -d out; java -cp out HotkeyPositionMouseClicker

# Windows (PowerShell) - V2.0
javac --release 8 src/main/java/HotkeyPositionMouseClickerV2.java -d out; java -cp out HotkeyPositionMouseClickerV2

# macOS/Linux - V1.0
javac --release 8 src/main/java/HotkeyPositionMouseClicker.java -d out && java -cp out HotkeyPositionMouseClicker

# macOS/Linux - V2.0
javac --release 8 src/main/java/HotkeyPositionMouseClickerV2.java -d out && java -cp out HotkeyPositionMouseClickerV2
```

**注意：**
- 如果遇到版本不匹配错误（`UnsupportedClassVersionError`），使用 `--release 8` 参数编译以确保与 Java 8+ 兼容
- 程序启动后会显示图形界面窗口
- **V2.0 版本** 提供了更强大的多位置点击功能，推荐使用

## 📁 项目结构

```
MouseClickToolForMacOrWin/
├── src/
│   └── main/
│       └── java/
│           ├── HotkeyPositionMouseClicker.java      # V1.0 - 单位置版本源代码
│           └── HotkeyPositionMouseClickerV2.java    # V2.0 - 多位置版本源代码
├── out/                                             # 编译输出目录（自动生成，已忽略）
├── LICENSE                                          # MIT 许可证文件
├── README.md                                        # 项目说明文档
└── .gitignore                                      # Git 忽略文件配置
```

### 目录说明

- **`src/main/java/`** - Java 源代码目录（符合标准 Java 项目结构）
- **`out/`** - 编译输出目录，存放编译后的 `.class` 文件
- **`LICENSE`** - 项目许可证文件（MIT）
- **`README.md`** - 项目使用说明文档
- **`.gitignore`** - Git 版本控制忽略规则配置

## 📖 使用说明

### V1.0 版本（单位置版本）

#### 基本使用

1. **启动程序**：运行 `HotkeyPositionMouseClicker` 类
2. **选择点击位置**：
   - 选择"当前鼠标位置"：将在鼠标当前位置点击
   - 选择"自定义位置"：输入 X、Y 坐标或使用快捷键获取当前位置（macOS: `⌘P`, Windows: `Ctrl+P`）
3. **配置点击参数**：
   - 设置点击间隔（毫秒）
   - 设置点击次数（0 表示无限）
   - 选择鼠标按钮类型
   - 可选：开启随机间隔模式
   - 可选：开启极速模式（最大化点击速率）
4. **开始连点**：
   - 点击"开始"按钮或使用快捷键（macOS: `⌘1`, Windows: `Ctrl+1`）
   - 程序将在 3 秒倒计时后开始连点
5. **停止连点**：
   - 点击"停止"按钮或使用快捷键（macOS: `⌘2`, Windows: `Ctrl+2`）

#### V1.0 快捷键说明

| 功能 | macOS 快捷键 | Windows 快捷键 |
|------|-------------|---------------|
| 开始连点 | `⌘1` | `Ctrl+1` |
| 停止连点 | `⌘2` | `Ctrl+2` |
| 获取当前鼠标位置 | `⌘P` | `Ctrl+P` |
| 测试设置的位置 | `⌘T` | `Ctrl+T` |
| 快速保存设置 | `⌘S` | `Ctrl+S` |
| 快速获取位置（备用） | `F12` | `F12` |

### V2.0 版本（多位置版本）✨

#### 核心功能

V2.0 版本在 V1.0 的基础上，新增了以下强大功能：

- **🎯 多位置管理**：支持添加多个点击位置，可编辑坐标和备注
- **📋 位置列表表格**：清晰的表格界面显示所有位置（序号、X坐标、Y坐标、备注）
- **🔄 顺序控制**：支持上移/下移调整点击顺序，按指定顺序循环点击
- **💾 位置保存**：所有位置配置自动保存，下次启动自动恢复

#### V2.0 使用步骤

1. **添加多个点击位置**：
   - 将鼠标移动到第一个目标位置
   - 点击"添加位置"按钮或按快捷键 `⌘P`/`Ctrl+P`
   - 重复以上步骤，添加所有需要点击的位置

2. **调整点击顺序**：
   - 在表格中选中某个位置行
   - 点击"上移"或"下移"按钮调整顺序
   - 程序会按照表格中的顺序依次点击

3. **编辑位置信息**：
   - 直接在表格中双击单元格可编辑坐标或备注
   - 修改后会自动保存到位置列表

4. **管理位置**：
   - **删除位置**：选中后点击"删除选中"
   - **测试位置**：选中后点击"测试选中位置"可单独测试该位置
   - **清空所有**：点击"清空所有"可清空所有位置（需确认）

5. **配置点击参数**：
   - 设置点击间隔（毫秒）
   - 设置循环次数（0 = 无限循环）
   - 选择鼠标按钮类型
   - 可选：开启随机间隔模式或极速模式

6. **开始循环点击**：
   - 点击"开始循环点击"或按 `⌘1`/`Ctrl+1`
   - 程序会按照表格顺序依次点击所有位置
   - 完成一轮后自动开始下一轮循环
   - 日志会显示每轮循环和每个位置的点击状态

7. **保存配置**：
   - 点击"保存设置"按钮保存所有位置和配置

#### V2.0 快捷键说明

| 功能 | macOS 快捷键 | Windows 快捷键 |
|------|-------------|---------------|
| 开始循环点击 | `⌘1` | `Ctrl+1` |
| 停止 | `⌘2` | `Ctrl+2` |
| 添加当前位置 | `⌘P` | `Ctrl+P` |

#### V2.0 应用场景

- 🎮 **游戏操作**：按特定顺序点击多个按钮或区域
- 📝 **表单填写**：按顺序点击多个输入框
- 🔄 **自动化流程**：执行需要按顺序点击的多步骤操作
- ⚙️ **批量操作**：对多个位置进行有序的重复点击

#### V2.0 与 V1.0 对比

| 功能 | V1.0 | V2.0 |
|------|------|------|
| 点击位置数量 | 单个位置 | 多个位置（无限制） |
| 点击模式 | 单点重复 | 按顺序循环点击 |
| 位置编辑 | 单个坐标编辑 | 表格批量管理 |
| 位置备注 | ❌ | ✅ |
| 顺序控制 | ❌ | ✅（上移/下移） |
| 适用场景 | 单点重复点击 | 多位置有序点击 |

### 通用高级功能（V1.0 和 V2.0 都支持）

- **随机间隔模式**：开启后，点击间隔会在设定的最小值和最大值之间随机变化，模拟真人操作，避免被检测为机器人操作
- **极速模式**：
  - 最大化了点击速率，适合需要高速点击的场景
  - 启用后，点击延迟从 20-50ms 降至 1ms
  - 鼠标移动延迟从 50ms 降至 10ms
  - 日志更新频率自动降低（每 10 次更新一次），减少 UI 开销
  - 建议配合较小的点击间隔使用（如 10-20ms）
  - 注意：极速模式下动作较为机械化，容易被检测为自动化操作
- **位置测试**：
  - V1.0：测试单个位置是否正确
  - V2.0：可以单独测试表格中选中的位置
- **设置保存**：所有配置和位置会自动保存，下次启动时自动恢复

### 性能优化建议

为了获得最高的点击速率，建议：

1. **启用极速模式**：在"点击设置"面板中勾选"极速模式（最大化点击速率）"
2. **设置较小的点击间隔**：建议设置为 10-20ms（极速模式下）
3. **关闭随机间隔**：在追求最高速率时，关闭随机间隔模式
4. **使用固定位置**：如果点击位置固定，使用"自定义位置"而不是"当前鼠标位置"，可减少鼠标移动的时间
5. **减少日志输出**：极速模式下日志会自动降低更新频率，无需手动设置

**性能对比**：
- **普通模式**：点击延迟 20-50ms，适合大多数场景
- **极速模式**：点击延迟 1ms，配合 10ms 间隔，理论最高速率可达每秒 80-100 次点击

## 🛠️ 技术栈

- Java 8+
- Java Swing（GUI）
- Java AWT Robot（鼠标控制）
- Java Preferences API（设置持久化）

## 📝 注意事项

1. **系统权限**：
   - **macOS**：需要授予应用程序"辅助功能"权限（系统偏好设置 → 安全性与隐私 → 辅助功能）
   - **Windows**：可能需要以管理员权限运行（某些情况下控制鼠标需要管理员权限）
2. **坐标范围**：自定义坐标必须在屏幕范围内，程序会自动验证
3. **点击间隔**：
   - 普通模式：建议设置合理的点击间隔（50ms 以上），过快的点击可能影响系统稳定性
   - 极速模式：可以设置较小间隔（10-20ms），但需注意系统负荷
4. **固定位置模式**：使用固定位置点击时，程序会在结束后恢复鼠标到原始位置
5. **自动适配**：程序会自动检测操作系统，界面文字和快捷键会自动适配当前系统
6. **极速模式使用**：
   - 极速模式会最大化点击速率，但动作较为机械化
   - 建议在游戏或需要快速操作的场景中使用
   - 注意：某些应用可能会检测到极速模式下的自动化操作

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

## 👤 作者

**zhouzh**

- 创建日期：2025年10月27日

## 🙏 致谢

感谢所有使用和贡献本项目的开发者！

![Java](https://img.shields.io/badge/Java-8+-orange)
![License](https://img.shields.io/badge/license-MIT-blue)
![Platform](https://img.shields.io/badge/platform-macOS%20%7C%20Windows-lightgrey)
