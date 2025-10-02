import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * 天气预报应用
 * 模拟天气信息显示，包含当前天气、未来几天预报和天气图标
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new WeatherApp().setVisible(true);
    });
}

class WeatherApp extends JFrame {
    private WeatherData currentWeather;
    private List<WeatherData> forecast;
    private String currentCity = "北京";
    
    private JLabel cityLabel;
    private JLabel temperatureLabel;
    private JLabel weatherLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private JLabel updateTimeLabel;
    private JPanel forecastPanel;
    private JComboBox<String> cityComboBox;
    
    private javax.swing.Timer updateTimer;
    
    public WeatherApp() {
        initializeUI();
        initializeWeatherData();
        startUpdateTimer();
    }
    
    private void initializeUI() {
        setTitle("天气预报 - Weather App");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(135, 206, 235)); // 天蓝色背景
        add(mainPanel);
        
        // 顶部城市选择
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setOpaque(false);
        
        JLabel selectLabel = new JLabel("选择城市:");
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        
        String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "西安"};
        cityComboBox = new JComboBox<>(cities);
        cityComboBox.addActionListener(e -> {
            currentCity = (String) cityComboBox.getSelectedItem();
            updateWeatherData();
        });
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> updateWeatherData());
        
        topPanel.add(selectLabel);
        topPanel.add(cityComboBox);
        topPanel.add(refreshBtn);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // 中央当前天气面板
        JPanel currentPanel = createCurrentWeatherPanel();
        mainPanel.add(currentPanel, BorderLayout.CENTER);
        
        // 底部预报面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel forecastTitle = new JLabel("未来5天预报", SwingConstants.CENTER);
        forecastTitle.setForeground(Color.WHITE);
        forecastTitle.setFont(new Font("微软雅黑", Font.BOLD, 16));
        forecastTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        forecastPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        forecastPanel.setOpaque(false);
        forecastPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        bottomPanel.add(forecastTitle, BorderLayout.NORTH);
        bottomPanel.add(forecastPanel, BorderLayout.CENTER);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCurrentWeatherPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // 城市名称
        cityLabel = new JLabel(currentCity, SwingConstants.CENTER);
        cityLabel.setForeground(Color.WHITE);
        cityLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 温度
        temperatureLabel = new JLabel("25°C", SwingConstants.CENTER);
        temperatureLabel.setForeground(Color.WHITE);
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 48));
        temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 天气状况
        weatherLabel = new JLabel("晴天 ☀", SwingConstants.CENTER);
        weatherLabel.setForeground(Color.WHITE);
        weatherLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        weatherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 湿度和风速
        JPanel detailPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        detailPanel.setOpaque(false);
        
        humidityLabel = new JLabel("湿度: 65%", SwingConstants.CENTER);
        humidityLabel.setForeground(Color.WHITE);
        humidityLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        windLabel = new JLabel("风速: 3级 东南风", SwingConstants.CENTER);
        windLabel.setForeground(Color.WHITE);
        windLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        detailPanel.add(humidityLabel);
        detailPanel.add(windLabel);
        
        // 更新时间
        updateTimeLabel = new JLabel("", SwingConstants.CENTER);
        updateTimeLabel.setForeground(Color.LIGHT_GRAY);
        updateTimeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        updateTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(cityLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(temperatureLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(weatherLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(detailPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(updateTimeLabel);
        
        return panel;
    }
    
    private void initializeWeatherData() {
        updateWeatherData();
    }
    
    private void updateWeatherData() {
        // 模拟获取天气数据
        currentWeather = generateRandomWeather(currentCity, 0);
        
        // 生成未来5天预报
        forecast = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            forecast.add(generateRandomWeather(currentCity, i));
        }
        
        updateUI();
    }
    
    private WeatherData generateRandomWeather(String city, int daysFromNow) {
        Random random = new Random();
        
        // 基础温度（根据城市和季节调整）
        int baseTemp = getBaseTempForCity(city);
        int temperature = baseTemp + random.nextInt(10) - 5;
        
        // 天气类型
        WeatherType[] types = WeatherType.values();
        WeatherType type = types[random.nextInt(types.length)];
        
        // 湿度和风速
        int humidity = 40 + random.nextInt(40);
        int windSpeed = 1 + random.nextInt(5);
        String[] windDirections = {"东风", "南风", "西风", "北风", "东南风", "西南风", "东北风", "西北风"};
        String windDirection = windDirections[random.nextInt(windDirections.length)];
        
        LocalDate date = LocalDate.now().plusDays(daysFromNow);
        
        return new WeatherData(city, date, temperature, type, humidity, windSpeed, windDirection);
    }
    
    private int getBaseTempForCity(String city) {
        return switch (city) {
            case "北京" -> 15;
            case "上海" -> 18;
            case "广州", "深圳" -> 25;
            case "杭州", "南京" -> 17;
            case "成都" -> 16;
            case "西安" -> 14;
            default -> 18;
        };
    }
    
    private void updateUI() {
        // 更新当前天气
        cityLabel.setText(currentWeather.city);
        temperatureLabel.setText(currentWeather.temperature + "°C");
        weatherLabel.setText(currentWeather.type.description + " " + currentWeather.type.icon);
        humidityLabel.setText("湿度: " + currentWeather.humidity + "%");
        windLabel.setText("风速: " + currentWeather.windSpeed + "级 " + currentWeather.windDirection);
        
        String updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("更新时间: MM-dd HH:mm"));
        updateTimeLabel.setText(updateTime);
        
        // 更新背景颜色
        Color bgColor = getBackgroundColor(currentWeather.type);
        setBackgroundColor(bgColor);
        
        // 更新预报面板
        updateForecastPanel();
    }
    
    private void updateForecastPanel() {
        forecastPanel.removeAll();
        
        for (WeatherData weather : forecast) {
            JPanel dayPanel = createForecastDayPanel(weather);
            forecastPanel.add(dayPanel);
        }
        
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }
    
    private JPanel createForecastDayPanel(WeatherData weather) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        
        // 日期
        String dateStr = weather.date.format(DateTimeFormatter.ofPattern("MM/dd"));
        String dayOfWeek = getDayOfWeek(weather.date);
        
        JLabel dateLabel = new JLabel(dateStr, SwingConstants.CENTER);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel dayLabel = new JLabel(dayOfWeek, SwingConstants.CENTER);
        dayLabel.setForeground(Color.WHITE);
        dayLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 天气图标
        JLabel iconLabel = new JLabel(weather.type.icon, SwingConstants.CENTER);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 温度
        JLabel tempLabel = new JLabel(weather.temperature + "°", SwingConstants.CENTER);
        tempLabel.setForeground(Color.WHITE);
        tempLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 天气描述
        JLabel descLabel = new JLabel(weather.type.description, SwingConstants.CENTER);
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(dateLabel);
        panel.add(dayLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(tempLabel);
        panel.add(descLabel);
        
        return panel;
    }
    
    private String getDayOfWeek(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "周一";
            case TUESDAY -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY -> "周四";
            case FRIDAY -> "周五";
            case SATURDAY -> "周六";
            case SUNDAY -> "周日";
        };
    }
    
    private Color getBackgroundColor(WeatherType type) {
        return switch (type) {
            case SUNNY -> new Color(135, 206, 235); // 天蓝色
            case CLOUDY -> new Color(119, 136, 153); // 浅灰蓝色
            case RAINY -> new Color(70, 130, 180); // 钢蓝色
            case SNOWY -> new Color(176, 196, 222); // 浅钢蓝色
            case FOGGY -> new Color(128, 128, 128); // 灰色
        };
    }
    
    private void setBackgroundColor(Color color) {
        getContentPane().setBackground(color);
        repaint();
    }
    
    private void startUpdateTimer() {
        // 每30分钟自动更新一次天气
        updateTimer = new javax.swing.Timer(30 * 60 * 1000, e -> updateWeatherData());
        updateTimer.start();
    }
    
    // 天气类型枚举
    enum WeatherType {
        SUNNY("晴天", "☀"),
        CLOUDY("多云", "☁"),
        RAINY("雨天", "🌧"),
        SNOWY("雪天", "❄"),
        FOGGY("雾霾", "🌫");
        
        final String description;
        final String icon;
        
        WeatherType(String description, String icon) {
            this.description = description;
            this.icon = icon;
        }
    }
    
    // 天气数据类
    static class WeatherData {
        String city;
        LocalDate date;
        int temperature;
        WeatherType type;
        int humidity;
        int windSpeed;
        String windDirection;
        
        public WeatherData(String city, LocalDate date, int temperature, 
                          WeatherType type, int humidity, int windSpeed, String windDirection) {
            this.city = city;
            this.date = date;
            this.temperature = temperature;
            this.type = type;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.windDirection = windDirection;
        }
    }
}
