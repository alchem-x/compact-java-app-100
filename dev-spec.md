# 📋 Java开发规范

## 🎯 核心原则

- **简洁性** - 代码简洁易读，避免过度设计
- **一致性** - 统一的代码风格和命名规范
- **可维护性** - 易于理解和修改的代码结构
- **功能导向** - 技术服务于功能，而非炫技

## 🚀 Java 25 语法规范

### var 类型推断
```java
// ✅ 类型明显时使用
var button = new JButton("确定");
var fileChooser = new JFileChooser();

// ❌ 类型不明显时避免
var data = getData();
var result = process();
```

### record 记录类
```java
// ✅ 简单数据载体
record Point(int x, int y) {}
record AppInfo(String name, String fileName, String category, String emoji) {}

// ❌ 复杂状态管理
record CalculatorState(double value, String operator, boolean isNewInput) {
    // 避免在record中添加复杂逻辑
}
```

### switch 表达式和lambda语法
```java
// ✅ 简单值映射 - 优先使用
var color = switch(type) {
    case PRIMARY -> BLUE;
    case SUCCESS -> GREEN;
    case DANGER -> RED;
    default -> GRAY;
};

// ✅ 多值匹配
var category = switch(size) {
    case 1, 2, 3 -> "small";
    case 4, 5, 6 -> "medium";
    case 7, 8, 9 -> "large";
    default -> "unknown";
};

// ✅ 计算表达式
var result = switch(operator) {
    case '+' -> a + b;
    case '-' -> a - b;
    case '*' -> a * b;
    case '/' -> a / b;
    default -> 0;
};

// ✅ 条件判断使用传统方式
var statusColor = amount >= 300 ? GREEN : amount >= 200 ? BLUE : ORANGE;

// ❌ Java 25不支持switch case when语法
// ❌ 复杂业务逻辑应使用传统if-else
```

## 🏗️ 项目结构

### 标准结构
```java
void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MyApp().setVisible(true));
}

static class MyApp extends JFrame {
    public MyApp() {
        this.initializeGUI();
    }
    
    private void initializeGUI() {
        // UI初始化代码
    }
}
```

### 设计规范
- UI设计规范详见: [design-system.md](design-system.md)

## 📝 编码规范

### 命名约定
```java
// 类名 - PascalCase
class Calculator extends JFrame {}
class PasswordGenerator {}

// 方法名 - camelCase
private void initializeGUI() {}
private void createButton() {}

// 变量名 - camelCase
var mainPanel = new JPanel();
var isRunning = false;

// 常量名 - UPPER_SNAKE_CASE
public static final int MAX_SIZE = 100;
```

### 方法调用
```java
// ✅ 实例方法使用this.
public void initializeGUI() {
    this.createMainPanel();
    this.setupEventHandlers();
}

// ✅ 静态方法省略this.
SwingUtilities.invokeLater(() -> this.setVisible(true));
```

### Lambda表达式
```java
// ✅ 优先使用方法引用（代码清晰简洁时）
button.addActionListener(this::handleClick);
menuItem.addActionListener(this::showAboutDialog);

// ✅ 需要参数处理时使用lambda表达式
button.addActionListener((ev) -> this.handleComplexClick(ev.getSource()));
timer.addActionListener((ev) -> this.updateTime());

// ❌ 避免省略括号
button.addActionListener(ev -> handleClick());
```

## 🔧 构建规范

### 编译命令
```bash
# 编译 - 必须输出到out目录
javac -d out apps/FileName.java

# 运行 - Java 25现代方式
java apps/FileName.java

# 或运行编译版本
java -cp out FileName
```

### 代码组织
```java
// 导入语句
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 方法顺序
class MyApp extends JFrame {
    // 1. 构造方法
    // 2. 初始化方法
    // 3. 事件处理方法
    // 4. 业务逻辑方法
    // 5. 工具方法
}
```

## 🔍 代码质量

### 错误处理
```java
try {
    // 可能出错的代码
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "错误: " + e.getMessage());
}
```

### 资源管理
```java
// 使用try-with-resources
try (var reader = new FileReader(file)) {
    // 文件操作
}
```

## 📋 检查清单

### 代码规范
- [ ] 编译输出到out目录: `javac -d out apps/FileName.java`
- [ ] 实例方法使用`this.methodName()`
- [ ] Lambda表达式使用`(ev)`参数
- [ ] 使用合适的现代Java语法
- [ ] 命名规范一致

### 功能质量
- [ ] 核心功能正常
- [ ] 错误处理完善
- [ ] 用户体验良好

## 🚀 快速参考

### 完整示例
```java
void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
}

static class Calculator extends JFrame {
    private JButton addButton;
    private JTextField display;
    
    public Calculator() {
        this.initializeGUI();
        this.setupEventHandlers();
    }
    
    private void initializeGUI() {
        setTitle("计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 创建组件
        display = new JTextField();
        addButton = new JButton("+");
        
        // 布局
        var panel = new JPanel(new BorderLayout());
        panel.add(display, BorderLayout.NORTH);
        panel.add(addButton, BorderLayout.CENTER);
        add(panel);
        
        pack();
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(this::handleAdd);
    }
    
    private void handleAdd() {
        // 业务逻辑
        this.updateDisplay();
    }
    
    private void updateDisplay() {
        display.setText("结果");
    }
}
```

### 核心要点
- **简洁性** - 避免过度设计，保持代码简单
- **一致性** - 统一的命名和调用规范
- **现代化** - 合理使用Java 25新特性
- **可读性** - 代码清晰易懂，注重维护性

---

*代码是写给人看的，机器只是恰好能执行它。*
