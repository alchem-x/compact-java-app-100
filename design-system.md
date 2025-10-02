# 🎨 Java应用设计系统

## 🎯 设计原则

基于Apple Human Interface Guidelines 2024，遵循以下核心原则：

- **清晰性** - 界面元素清晰可见，功能一目了然
- **一致性** - 统一的视觉语言和交互模式
- **深度感** - 通过层次和阴影营造空间感
- **可访问性** - 支持所有用户的使用需求
- **美学完整性** - 外观与功能完美结合

> *"简洁是复杂的终极形式。" - 达芬奇*

## 🎨 颜色系统

### 系统颜色 (iOS 18/macOS Sequoia)
```java
// 主要颜色
public static final Color BLUE = new Color(0, 122, 255);      // 主要操作
public static final Color GREEN = new Color(52, 199, 89);     // 成功状态
public static final Color RED = new Color(255, 59, 48);       // 危险操作
public static final Color ORANGE = new Color(255, 149, 0);    // 警告状态
public static final Color PURPLE = new Color(175, 82, 222);   // 创意功能
public static final Color TEAL = new Color(48, 176, 199);     // 辅助功能

// 中性色
public static final Color BLACK = new Color(0, 0, 0);
public static final Color WHITE = new Color(255, 255, 255);
public static final Color GRAY = new Color(142, 142, 147);
public static final Color GRAY2 = new Color(174, 174, 178);
public static final Color GRAY3 = new Color(199, 199, 204);
public static final Color GRAY4 = new Color(209, 209, 214);
public static final Color GRAY5 = new Color(229, 229, 234);
public static final Color GRAY6 = new Color(242, 242, 247);
```

### 语义颜色
```java
// 文本
public static final Color LABEL = new Color(0, 0, 0);           // 主要文本
public static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153); // 次要文本
public static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);   // 三级文本

// 背景
public static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
public static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(242, 242, 247);
public static final Color TERTIARY_SYSTEM_BACKGROUND = new Color(255, 255, 255);

// 分组背景
public static final Color SYSTEM_GROUPED_BACKGROUND = new Color(242, 242, 247);
public static final Color SECONDARY_SYSTEM_GROUPED_BACKGROUND = new Color(255, 255, 255);
```

## 📝 字体系统

### 字体规格 (San Francisco)
```java
// 大标题
public static final Font LARGE_TITLE = new Font("SF Pro Display", Font.BOLD, 34);

// 标题1-3
public static final Font TITLE1 = new Font("SF Pro Display", Font.BOLD, 28);
public static final Font TITLE2 = new Font("SF Pro Display", Font.BOLD, 22);
public static final Font TITLE3 = new Font("SF Pro Display", Font.BOLD, 20);

// 标题
public static final Font HEADLINE = new Font("SF Pro Display", Font.BOLD, 17);
public static final Font SUBHEADLINE = new Font("SF Pro Display", Font.PLAIN, 15);

// 正文
public static final Font BODY = new Font("SF Pro Text", Font.PLAIN, 17);
public static final Font CALLOUT = new Font("SF Pro Text", Font.PLAIN, 16);
public static final Font FOOTNOTE = new Font("SF Pro Text", Font.PLAIN, 13);

// 标注
public static final Font CAPTION1 = new Font("SF Pro Text", Font.PLAIN, 12);
public static final Font CAPTION2 = new Font("SF Pro Text", Font.PLAIN, 11);
```

## 📐 布局系统

### 间距 (8pt网格系统)
```java
// 基础间距单位
public static final int SPACING_2 = 2;   // 紧密间距
public static final int SPACING_4 = 4;   // 最小间距
public static final int SPACING_8 = 8;   // 小间距
public static final int SPACING_12 = 12; // 中等间距
public static final int SPACING_16 = 16; // 标准间距
public static final int SPACING_20 = 20; // 大间距
public static final int SPACING_24 = 24; // 超大间距
public static final int SPACING_32 = 32; // 巨大间距
```

### 圆角半径
```java
// 圆角规格
public static final int RADIUS_4 = 4;    // 小圆角
public static final int RADIUS_8 = 8;    // 标准圆角
public static final int RADIUS_12 = 12;  // 中圆角
public static final int RADIUS_16 = 16;  // 大圆角
public static final int RADIUS_20 = 20;  // 超大圆角
```

## 🧩 组件规范

### 按钮 (Button)

#### 按钮样式
```java
// 主要按钮 - 蓝色背景
var primaryButton = new JButton("确定");
primaryButton.setBackground(BLUE);
primaryButton.setForeground(WHITE);
primaryButton.setFont(HEADLINE);
primaryButton.setBorder(new RoundedBorder(RADIUS_8));
primaryButton.setPreferredSize(new Dimension(120, 44));

// 次要按钮 - 灰色背景
var secondaryButton = new JButton("取消");
secondaryButton.setBackground(GRAY6);
secondaryButton.setForeground(LABEL);
secondaryButton.setFont(HEADLINE);
```

#### 按钮尺寸
```java
// 标准尺寸
public static final Dimension BUTTON_SMALL = new Dimension(80, 32);
public static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
public static final Dimension BUTTON_LARGE = new Dimension(160, 50);

// 最小触摸区域
public static final int MIN_TOUCH_SIZE = 44;
```

#### 按钮状态
- **Default** - 标准外观
- **Highlighted** - 按下时颜色加深
- **Disabled** - 50%透明度

### 文本框 (Text Field)
```java
// 标准文本框
var textField = new JTextField();
textField.setFont(BODY);
textField.setBorder(BorderFactory.createCompoundBorder(
    new RoundedBorder(RADIUS_8),
    BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
));
textField.setBackground(GRAY6);
```

### 面板 (Panel)
```java
// 主面板
var mainPanel = new JPanel(new BorderLayout());
mainPanel.setBackground(SYSTEM_BACKGROUND);
mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_20, SPACING_20, SPACING_20, SPACING_20));

// 卡片面板
var cardPanel = new JPanel();
cardPanel.setBackground(SECONDARY_SYSTEM_GROUPED_BACKGROUND);
cardPanel.setBorder(new RoundedBorder(RADIUS_12));
```

## 🎯 交互原则

### 动画时长
```java
// 标准动画时长
public static final int DURATION_FAST = 150;      // 按钮反馈
public static final int DURATION_STANDARD = 250;  // 界面切换
public static final int DURATION_SLOW = 400;      // 页面转场
```

### 反馈状态
- **Default** - 默认状态
- **Hover** - 悬停反馈
- **Active** - 激活状态
- **Disabled** - 禁用状态

## 🔍 可访问性

### 对比度要求
- 正文文字: ≥ 4.5:1
- 大字体: ≥ 3:1
- 图标: ≥ 3:1

### 最小尺寸
- 触摸目标: 44pt
- 文字大小: 11pt

## 🚀 快速参考

### 常用颜色
```java
// 系统颜色
public static final Color BLUE = new Color(0, 122, 255);
public static final Color GREEN = new Color(52, 199, 89);
public static final Color RED = new Color(255, 59, 48);
public static final Color ORANGE = new Color(255, 149, 0);

// 文本颜色
public static final Color LABEL = new Color(0, 0, 0);
public static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);
public static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);

// 背景颜色
public static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
public static final Color GRAY6 = new Color(242, 242, 247);
```

### 快速创建按钮
```java
// 主要按钮
var primaryBtn = new JButton("确定");
primaryBtn.setBackground(BLUE);
primaryBtn.setForeground(WHITE);
primaryBtn.setFont(HEADLINE);
primaryBtn.setBorder(new RoundedBorder(RADIUS_8));
primaryBtn.setPreferredSize(new Dimension(120, 44));

// 次要按钮
var secondaryBtn = new JButton("取消");
secondaryBtn.setBackground(GRAY6);
secondaryBtn.setForeground(LABEL);
secondaryBtn.setFont(HEADLINE);
```

### 字体快速使用
```java
// 标题
LARGE_TITLE  // 34pt Bold - 主标题
TITLE1       // 28pt Bold - 页面标题
TITLE2       // 22pt Bold - 区域标题
HEADLINE     // 17pt Bold - 卡片标题

// 正文
BODY         // 17pt Regular - 主要内容
CALLOUT      // 16pt Regular - 次要内容
FOOTNOTE     // 13pt Regular - 辅助信息
CAPTION1     // 12pt Regular - 标注
```

### 间距和圆角
```java
// 间距常量
SPACING_4   // 4pt - 紧密间距
SPACING_8   // 8pt - 小间距
SPACING_12  // 12pt - 中等间距
SPACING_16  // 16pt - 标准间距
SPACING_20  // 20pt - 大间距
SPACING_24  // 24pt - 超大间距

// 圆角常量
RADIUS_4    // 4pt - 小圆角
RADIUS_8    // 8pt - 标准圆角
RADIUS_12   // 12pt - 中圆角
RADIUS_16   // 16pt - 大圆角
```

### 常用布局
```java
// 主面板
var mainPanel = new JPanel(new BorderLayout());
mainPanel.setBackground(SYSTEM_BACKGROUND);
mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_20, SPACING_20, SPACING_20, SPACING_20));

// 按钮面板
var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
buttonPanel.setBackground(SYSTEM_BACKGROUND);

// 文本框
var textField = new JTextField();
textField.setFont(BODY);
textField.setBackground(GRAY6);
textField.setBorder(new RoundedBorder(RADIUS_8));
```


---

*基于Apple Human Interface Guidelines 2024的精简设计系统。*

**核心理念**: 清晰、一致、美观 - 让设计服务于用户体验。
