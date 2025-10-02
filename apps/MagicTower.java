import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new MagicTower().setVisible(true);
    });
}

static class MagicTower extends JFrame implements ActionListener {
    private static final int CELL_SIZE = 40;
    private static final int GRID_SIZE = 15;
    private static final int PANEL_SIZE = CELL_SIZE * GRID_SIZE;

    private GamePanel gamePanel;
    private GameState gameState;
    private Timer gameTimer;


    public MagicTower() {
        gameState = new GameState();
        initializeGUI();
        startGame();
    }

    private void initializeGUI() {
        setTitle("魔塔游戏 🏰");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        StatusPanel statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setFocusable(true);
        addKeyListener(new GameKeyListener());
    }

    private void startGame() {
        gameTimer = new Timer(100, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gamePanel.repaint();
    }

    class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
            setBackground(Color.BLACK);
            setFocusable(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            gameState.draw(g, this);
        }
    }

    class StatusPanel extends JPanel {
        private JLabel hpLabel, atkLabel, defLabel, goldLabel, floorLabel, levelLabel, expLabel;

        public StatusPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(200, PANEL_SIZE));
            setBackground(Color.DARK_GRAY);

            levelLabel = createStatusLabel("⭐ 等级: " + gameState.player.level);
            hpLabel = createStatusLabel("❤️ 生命值: " + gameState.player.hp);
            atkLabel = createStatusLabel("⚔️ 攻击力: " + gameState.player.atk);
            defLabel = createStatusLabel("🛡️ 防御力: " + gameState.player.def);
            expLabel = createStatusLabel("📊 经验: " + gameState.player.exp + "/" + (gameState.player.level * 100));
            goldLabel = createStatusLabel("💰 金币: " + gameState.player.gold);
            floorLabel = createStatusLabel("🏰 楼层: " + gameState.currentFloor);

            add(Box.createVerticalStrut(20));
            add(createTitleLabel("角色状态"));
            add(Box.createVerticalStrut(10));
            add(levelLabel);
            add(hpLabel);
            add(atkLabel);
            add(defLabel);
            add(expLabel);
            add(goldLabel);
            add(floorLabel);

            Timer updateTimer = new Timer(100, e -> updateStatus());
            updateTimer.start();
        }

        private JLabel createTitleLabel(String text) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("微软雅黑", Font.BOLD, 18));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            return label;
        }

        private JLabel createStatusLabel(String text) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return label;
        }

        private void updateStatus() {
            levelLabel.setText("⭐ 等级: " + gameState.player.level);
            hpLabel.setText("❤️ 生命值: " + gameState.player.hp);
            atkLabel.setText("⚔️ 攻击力: " + gameState.player.atk);
            defLabel.setText("🛡️ 防御力: " + gameState.player.def);
            expLabel.setText("📊 经验: " + gameState.player.exp + "/" + (gameState.player.level * 100));
            goldLabel.setText("💰 金币: " + gameState.player.gold);
            floorLabel.setText("🏰 楼层: " + gameState.currentFloor);
        }
    }

    class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    gameState.movePlayer(0, -1);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    gameState.movePlayer(0, 1);
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    gameState.movePlayer(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    gameState.movePlayer(1, 0);
                    break;
            }
        }
    }
}

// Boss角色类 - 包含诗号和对话
static class Boss {
    String name;
    String title;
    int hp, atk, def, exp, gold;
    String poetry;
    String dialog;
    String playerDialog;
    String deathWords;
    BossItem item;

    public Boss(String name, String title, int hp, int atk, int def, int exp, int gold,
                String poetry, String dialog, String playerDialog, String deathWords, BossItem item) {
        this.name = name;
        this.title = title;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.exp = exp;
        this.gold = gold;
        this.poetry = poetry;
        this.dialog = dialog;
        this.playerDialog = playerDialog;
        this.deathWords = deathWords;
        this.item = item;
    }
}

// Boss掉落物品
static class BossItem {
    String name;
    String type;
    String effect;
    String description;

    public BossItem(String name, String type, String effect, String description) {
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.description = description;
    }
}

// 五关六将配置
static class BossConfig {
    static final Boss[] BOSSES = {
            // 第一关 - 烈焰守卫
            new Boss("烈焰守卫·赤焰", "熔岩守护者", 80, 15, 3, 50, 30,
                    "赤焰滔天焚万物，岩浆滚滚炼真金。\n守关一战试勇者，烈焰无情断凡心。",
                    "愚蠢的凡人，竟敢闯入魔塔！我的烈焰将把你烧成灰烬，成为这熔岩中的又一个亡魂！",
                    "我是为了拯救世界而来的勇者！你的烈焰阻挡不了我前进的脚步！",
                    "不可能...我的烈焰...竟然被...熄灭了...",
                    new BossItem("烈焰之心", "accessory", "attack+5", "蕴含着火焰力量的宝石，能够增强攻击力")),

            // 第二关 - 寒冰法师
            new Boss("寒冰法师·霜语", "极寒统治者", 120, 20, 5, 80, 50,
                    "冰霜万里凝天地，寒风呼啸冻人心。\n魔塔二层我为王，霜雪纷飞葬孤魂。",
                    "感受极寒的恐怖吧！在我的冰霜领域中，你的血液都将冻结，心脏都将停止跳动！",
                    "我的意志比冰霜更加坚定！为了世界的和平，我必须击败你！",
                    "冰雪...消融了...我的...永恒寒冬...",
                    new BossItem("寒冰法袍", "armor", "defense+8", "由千年寒冰编织而成的法袍，具有强大的防御力")),

            // 第三关 - 暗影刺客
            new Boss("暗影刺客·夜刃", "无声死神", 100, 35, 2, 100, 70,
                    "暗影随行无踪影，夜刃出鞘必饮血。\n三层魔塔潜杀机，一步走错永长眠。",
                    "在黑暗中，我就是死神！你甚至看不到我的身影，就已经倒在了血泊之中！",
                    "正义的光芒会照亮一切黑暗！你的暗杀技巧在我面前毫无用处！",
                    "暗影...被...光明...驱散...了...",
                    new BossItem("影之靴", "boots", "speed+10", "能够让穿戴者行动如风，提升闪避能力")),

            // 第四关 - 雷霆战士
            new Boss("雷霆战士·雷震", "雷电掌控者", 180, 40, 8, 150, 100,
                    "雷霆万钧震九霄，电闪雷鸣破长空。\n四层高塔雷电域，天威浩荡灭顽凶。",
                    "感受天雷的愤怒吧！在我的雷电之下，一切都将化为灰烬，包括你那可笑的正义！",
                    "即使面对天雷，我也不会退缩！我相信正义的力量能够战胜一切邪恶！",
                    "雷电...也...无法...阻止...正义...",
                    new BossItem("雷霆之锤", "weapon", "attack+15", "蕴含着雷电神力的战锤，每一击都带着闪电的力量")),

            // 第五关 - 石化魔女
            new Boss("石化魔女·美杜莎", "凝视终结者", 220, 45, 12, 200, 150,
                    "美目盼兮摄人魂，凝视如霜石化身。\n五层魔塔绝美景，却是人间葬魂林。",
                    "看着我的眼睛，可爱的小勇者！在我的凝视下，你将成为永恒的雕像，永远守护在这座魔塔中！",
                    "我不会被你的美貌所迷惑！我的内心坚定如钢，不会被任何事物动摇！",
                    "我的...美丽...我的...力量...都...消失了...",
                    new BossItem("女神之泪", "accessory", "hp+50", "传说中女神的眼泪，能够大幅增强生命力")),

            // 第六关 - 终极魔王
            new Boss("终极魔王·暗黑破坏神", "真物持有者", 500, 60, 20, 1000, 1000,
                    "黑暗笼罩天地间，魔王降世灭人寰。\n真物在手我为尊，六层高塔我为天。\n千年等待终有时，今日重临覆乾坤！",
                    "终于有人来到了我的面前！我是这个世界真正的统治者！真物的力量让我无所不能，你这点微末的实力在我面前如同蝼蚁！整个世界都将在我的黑暗下颤抖！",
                    "我就是为了击败你而来的！无论你有多强大，我都不会放弃！为了这个世界，为了所有人，我一定要打败你，夺回真物！",
                    "不可能...真物的力量...竟然...被...正义...战胜了...我...不甘心...啊！！！",
                    new BossItem("真物", "artifact", "all+100", "传说中能够改变世界的神秘宝物，蕴含着无穷的力量"))
    };
}

// 游戏状态管理
static class GameState {
    public Player player;
    public int currentFloor;
    private Floor[] floors;
    private boolean[] bossDefeated;
    private List<BossItem> inventory;
    private GameMode mode;

    public enum GameMode {
        EXPLORING, DIALOG, BATTLE, VICTORY, GAME_OVER
    }

    public GameState() {
        player = new Player(2, 2);  // 从更合理的位置开始
        currentFloor = 1;
        bossDefeated = new boolean[6];
        inventory = new ArrayList<>();
        mode = GameMode.EXPLORING;

        // 显示开场故事
        showOpeningStory();

        initializeFloors();
    }

    // 显示开场故事
    private void showOpeningStory() {
        String story = """
                
                🏰 魔塔传说 🏰
                
                在遥远的魔幻大陆，传说中的真物蕴含着改变世界的力量。
                魔王夺取了真物，将其封印在魔塔之巅。
                作为被选中的勇者，你必须闯过五关，斩灭六将，
                最终击败魔王，夺回真物，拯救这个世界！
                
                💎 五关六将，每一关都有强大的Boss守护
                📜 每个Boss都有独特的诗号和背景故事
                ⚔️ 战斗胜利后获得装备和属性提升
                🏆 最终目标是击败魔王，获得真物！
                
                操作说明：
                ↑↓←→ 或 WASD 键移动角色
                走到楼梯处可进入下一层
                必须先击败每层的Boss才能继续前进
                """;
        System.out.println(story);
    }

    private void initializeFloors() {
        floors = new Floor[6];
        for (int i = 0; i < floors.length; i++) {
            floors[i] = new Floor(i + 1, this);
        }
    }

    public void movePlayer(int dx, int dy) {
        if (mode != GameMode.EXPLORING) return;

        int newX = player.x + dx;
        int newY = player.y + dy;

        if (newX >= 0 && newX < 15 && newY >= 0 && newY < 15) {
            Cell targetCell = getCurrentFloor().getCell(newX, newY);

            if (targetCell.canPass()) {
                player.x = newX;
                player.y = newY;

                if (targetCell.hasItem()) {
                    collectItem(targetCell.item);
                    targetCell.clearItem();
                } else if (targetCell.hasMonster()) {
                    battleMonster(targetCell.monster);
                    if (targetCell.monster.isDead()) {
                        targetCell.clearMonster();
                    }
                } else if (targetCell.type == CellType.STAIRS_UP && currentFloor < floors.length) {
                    // 检查是否击败了当前层的Boss
                    if (currentFloor < 6 && !bossDefeated[currentFloor - 1]) {
                        showMessage("必须先击败本层的Boss才能继续前进！");
                        player.x -= dx;
                        player.y -= dy;
                        return;
                    }
                    currentFloor++;
                    player.x = 1;
                    player.y = 13;
                    // 检查是否需要触发Boss战
                    checkBossEncounter();
                } else if (targetCell.type == CellType.STAIRS_DOWN && currentFloor > 1) {
                    currentFloor--;
                    player.x = 13;
                    player.y = 1;
                }
            }
        }
    }

    private void collectItem(Item item) {
        switch (item.type) {
            case HP_POTION:
                player.hp += 50;
                break;
            case ATK_BOOST:
                player.atk += 5;
                break;
            case DEF_BOOST:
                player.def += 5;
                break;
            case GOLD:
                player.gold += 10;
                break;
        }
    }

    // 检查Boss遭遇
    private void checkBossEncounter() {
        if (currentFloor <= 6 && !bossDefeated[currentFloor - 1]) {
            mode = GameMode.DIALOG;
            Boss boss = BossConfig.BOSSES[currentFloor - 1];
            showBossDialog(boss);
        }
    }

    // 显示Boss对话
    private void showBossDialog(Boss boss) {
        String message = String.format("\n⚔️ 遭遇了 %s！\n\n%s的诗号：\n\"%s\"\n\n%s：%s\n\n勇者：%s",
                boss.name, boss.name, boss.poetry, boss.name, boss.dialog, boss.playerDialog);

        showMessage(message);

        // 延迟后开始战斗
        new Timer(3000, e -> {
            ((Timer) e.getSource()).stop();
            startBossBattle(boss);
        }).start();
    }

    // 开始Boss战斗
    private void startBossBattle(Boss boss) {
        mode = GameMode.BATTLE;
        String battleStart = String.format("\n🔥 战斗开始！\n\n%s - %s\nHP: %d | 攻击: %d | 防御: %d",
                boss.name, boss.title, boss.hp, boss.atk, boss.def);
        showMessage(battleStart);

        // 创建战斗线程
        new Thread(() -> {
            int playerHP = player.hp;
            int bossHP = boss.hp;
            int round = 1;

            while (playerHP > 0 && bossHP > 0) {
                StringBuilder roundMessage = new StringBuilder();
                roundMessage.append(String.format("\n--- 第%d回合 ---\n", round));

                // 玩家攻击
                int playerDamage = Math.max(1, player.atk - boss.def);
                boolean critical = Math.random() < 0.2;
                int finalDamage = critical ? playerDamage * 2 : playerDamage;

                bossHP -= finalDamage;

                if (critical) {
                    roundMessage.append(String.format("💥 暴击！勇者造成 %d 点伤害！(Boss剩余HP: %d)\n",
                            finalDamage, Math.max(0, bossHP)));
                } else {
                    roundMessage.append(String.format("⚔️ 勇者攻击，造成 %d 点伤害！(Boss剩余HP: %d)\n",
                            finalDamage, Math.max(0, bossHP)));
                }

                if (bossHP <= 0) break;

                // Boss特殊技能
                String skillResult = executeBossSkill(boss, playerHP);
                if (skillResult != null) {
                    roundMessage.append(skillResult);
                    // 从技能结果中提取新的HP值
                    if (skillResult.contains("勇者剩余HP:")) {
                        int hpStart = skillResult.indexOf("勇者剩余HP: ") + 8;
                        int hpEnd = skillResult.indexOf(")", hpStart);
                        try {
                            playerHP = Integer.parseInt(skillResult.substring(hpStart, hpEnd).trim());
                        } catch (NumberFormatException ex) {
                            // 保持原HP值
                        }
                    }
                }

                if (playerHP <= 0) break;

                // Boss普通攻击
                int bossDamage = Math.max(1, boss.atk - player.def);
                playerHP -= bossDamage;
                roundMessage.append(String.format("🗡️ %s攻击，造成 %d 点伤害！(勇者剩余HP: %d)\n",
                        boss.name, bossDamage, Math.max(0, playerHP)));

                showMessage(roundMessage.toString());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                round++;
            }

            // 战斗结果
            if (playerHP > 0) {
                victory(boss);
            } else {
                gameOver();
            }
        }).start();
    }

    // 执行Boss特殊技能
    private String executeBossSkill(Boss boss, int playerHP) {
        String bossName = boss.name;
        StringBuilder result = new StringBuilder();

        if (bossName.contains("烈焰")) {
            if (Math.random() < 0.3) {
                int burnDamage = 5;
                result.append(String.format("🔥 %s使用烈焰灼烧！造成 %d 点持续伤害！(勇者剩余HP: %d)\n",
                        bossName, burnDamage, Math.max(0, playerHP - burnDamage)));
            }
        } else if (bossName.contains("寒冰")) {
            if (Math.random() < 0.3) {
                result.append(String.format("❄️ %s使用寒冰冻结！勇者被冻结一回合！\n", bossName));
                return result.toString();
            }
        } else if (bossName.contains("暗影")) {
            if (Math.random() < 0.3) {
                if (Math.random() < 0.5) {
                    result.append(String.format("👤 %s使用暗影潜行！攻击被闪避！\n", bossName));
                    return result.toString();
                }
            }
        } else if (bossName.contains("雷霆")) {
            if (Math.random() < 0.3) {
                int thunderDamage = 15;
                result.append(String.format("⚡ %s召唤天雷！造成 %d 点雷电伤害！(勇者剩余HP: %d)\n",
                        bossName, thunderDamage, Math.max(0, playerHP - thunderDamage)));
            }
        } else if (bossName.contains("魔女")) {
            if (Math.random() < 0.3) {
                if (Math.random() < 0.5) {
                    result.append(String.format("👁️ %s使用石化凝视！勇者被石化，防御力下降！\n", bossName));
                    player.def = Math.max(0, player.def - 2);
                }
            }
        }

        return result.length() > 0 ? result.toString() : null;
    }

    // 战斗胜利
    private void victory(Boss boss) {
        // 显示死亡台词
        if (boss.deathWords != null) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            showMessage(String.format("\n%s：%s", boss.name, boss.deathWords));
        }

        // 获得奖励
        player.exp += boss.exp;
        player.gold += boss.gold;
        showMessage(String.format("\n💰 获得 %d 经验值，%d 金币！", boss.exp, boss.gold));

        // 获得物品
        if (boss.item != null) {
            inventory.add(boss.item);
            showMessage(String.format("\n✨ 获得了 %s！%s", boss.item.name, boss.item.description));
            applyItemEffect(boss.item);
        }

        // 标记Boss被击败
        bossDefeated[currentFloor - 1] = true;

        // 检查升级
        checkLevelUp();

        // 检查是否通关
        if (currentFloor == 6) {
            gameComplete();
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            showMessage("\n🚪 通往下一层的楼梯已经开启...");
            mode = GameMode.EXPLORING;
        }
    }

    // 应用物品效果
    private void applyItemEffect(BossItem item) {
        if (item.effect.contains("attack+")) {
            int bonus = Integer.parseInt(item.effect.split("\\+")[1]);
            player.atk += bonus;
            showMessage(String.format("⚔️ 攻击力提升 %d 点！当前攻击力：%d", bonus, player.atk));
        } else if (item.effect.contains("defense+")) {
            int bonus = Integer.parseInt(item.effect.split("\\+")[1]);
            player.def += bonus;
            showMessage(String.format("🛡️ 防御力提升 %d 点！当前防御力：%d", bonus, player.def));
        } else if (item.effect.contains("hp+")) {
            int bonus = Integer.parseInt(item.effect.split("\\+")[1]);
            player.hp += bonus;
            showMessage(String.format("❤️ 生命值提升 %d 点！当前生命值：%d", bonus, player.hp));
        } else if (item.effect.contains("all+")) {
            int bonus = Integer.parseInt(item.effect.split("\\+")[1]);
            player.atk += bonus;
            player.def += bonus;
            player.hp += bonus;
            showMessage(String.format("⭐ 全属性提升 %d 点！", bonus));
            showMessage(String.format("⚔️ 攻击力：%d | 🛡️ 防御力：%d | ❤️ 生命值：%d", player.atk, player.def, player.hp));
        }
    }

    // 检查升级
    private void checkLevelUp() {
        int expNeeded = player.level * 100;
        if (player.exp >= expNeeded) {
            player.level++;
            player.exp -= expNeeded;
            int hpBonus = 20;
            int attackBonus = 5;
            int defenseBonus = 3;

            player.hp += hpBonus;
            player.atk += attackBonus;
            player.def += defenseBonus;

            showMessage(String.format("\n🆙 等级提升！当前等级：%d", player.level));
            showMessage(String.format("❤️ 生命值 +%d | ⚔️ 攻击力 +%d | 🛡️ 防御力 +%d",
                    hpBonus, attackBonus, defenseBonus));
        }
    }

    // 游戏通关
    private void gameComplete() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        showMessage("\n🏆 恭喜通关！");
        showMessage("\n✨ 真物的光芒照耀着整个世界...");
        showMessage("\n🌟 你成功拯救了这片大陆，成为了真正的英雄！");
        showMessage("\n📜 传说将被永远铭记，你的名字将与真物同在！");

        // 显示最终统计
        showMessage("\n=== 最终统计 ===");
        showMessage(String.format("勇者等级：%d", player.level));
        showMessage(String.format("最终属性：❤️ HP %d | ⚔️ 攻击 %d | 🛡️ 防御 %d",
                player.hp, player.atk, player.def));
        showMessage(String.format("获得金币：%d", player.gold));
        showMessage(String.format("拥有物品：%s",
                inventory.stream().map(item -> item.name).reduce((a, b) -> a + ", " + b).orElse("无")));

        mode = GameMode.VICTORY;
    }

    // 游戏结束
    private void gameOver() {
        showMessage("\n💀 游戏结束！勇者倒下了...");
        mode = GameMode.GAME_OVER;
    }

    // 显示消息
    private void showMessage(String message) {
        System.out.println(message);
        // 这里可以扩展为GUI对话框
    }

    private void battleMonster(Monster monster) {
        while (!monster.isDead() && player.hp > 0) {
            int damageToMonster = Math.max(1, player.atk - monster.def);
            monster.hp -= damageToMonster;

            if (!monster.isDead()) {
                int damageToPlayer = Math.max(1, monster.atk - player.def);
                player.hp -= damageToPlayer;
            }
        }

        if (player.hp > 0 && monster.isDead()) {
            player.gold += monster.reward;
        }
    }

    public Floor getCurrentFloor() {
        return floors[currentFloor - 1];
    }

    public boolean isBossDefeated(int floor) {
        return bossDefeated[floor - 1];
    }

    public void draw(Graphics g, JPanel panel) {
        getCurrentFloor().draw(g, panel);
        player.draw(g, panel);
    }
}

static class Player {
    public int x, y;
    public int hp, atk, def, gold, exp, level;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.level = 1;
        this.hp = 100;
        this.atk = 10;
        this.def = 5;
        this.gold = 0;
        this.exp = 0;
    }

    public void draw(Graphics g, JPanel panel) {
        g.setColor(Color.YELLOW);
        g.fillRect(x * 40 + 5, y * 40 + 5, 30, 30);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        g.drawString("🧙", x * 40 + 10, y * 40 + 28);
    }
}

static class Floor {
    private Cell[][] cells;
    private int floorNumber;
    private GameState gameState;

    public Floor(int floorNumber, GameState gameState) {
        this.floorNumber = floorNumber;
        this.gameState = gameState;
        cells = new Cell[15][15];
        initializeFloor();
    }

    private void initializeFloor() {
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                cells[x][y] = new Cell(x, y, CellType.FLOOR);
            }
        }

        // 边界墙壁
        for (int i = 0; i < 15; i++) {
            cells[i][0].type = CellType.WALL;
            cells[i][14].type = CellType.WALL;
            cells[0][i].type = CellType.WALL;
            cells[14][i].type = CellType.WALL;
        }

        // 根据楼层生成不同的布局
        generateFloorLayout();
    }

    private void generateFloorLayout() {
        switch (floorNumber) {
            case 1:
                // 第一层 - 烈焰守卫的房间
                generateBossRoomLayout();
                placeItemsLevel1();
                placeMonstersLevel1();
                cells[7][1].type = CellType.STAIRS_UP;
                // 在中间放置Boss - 只有当Boss未被击败时才显示
                if (!gameState.isBossDefeated(1)) {
                    cells[7][7].monster = new BossMonster(BossConfig.BOSSES[0]);
                }
                break;
            case 2:
                // 第二层 - 寒冰法师的房间
                generateBossRoomLayout();
                placeItemsLevel2();
                placeMonstersLevel2();
                cells[1][1].type = CellType.STAIRS_DOWN;
                cells[7][1].type = CellType.STAIRS_UP;
                if (!gameState.isBossDefeated(2)) {
                    cells[7][7].monster = new BossMonster(BossConfig.BOSSES[1]);
                }
                break;
            case 3:
                // 第三层 - 暗影刺客
                generateBossRoomLayout();
                cells[1][1].type = CellType.STAIRS_DOWN;
                cells[7][1].type = CellType.STAIRS_UP;
                if (!gameState.isBossDefeated(3)) {
                    cells[7][7].monster = new BossMonster(BossConfig.BOSSES[2]);
                }
                break;
            case 4:
                // 第四层 - 雷霆战士
                generateBossRoomLayout();
                cells[1][1].type = CellType.STAIRS_DOWN;
                cells[7][1].type = CellType.STAIRS_UP;
                if (!gameState.isBossDefeated(4)) {
                    cells[7][7].monster = new BossMonster(BossConfig.BOSSES[3]);
                }
                break;
            case 5:
                // 第五层 - 石化魔女
                generateBossRoomLayout();
                cells[1][1].type = CellType.STAIRS_DOWN;
                cells[7][1].type = CellType.STAIRS_UP;
                if (!gameState.isBossDefeated(5)) {
                    cells[7][7].monster = new BossMonster(BossConfig.BOSSES[4]);
                }
                break;
            case 6:
                // 第六层 - 终极魔王
                generateFinalBossRoomLayout();
                cells[1][1].type = CellType.STAIRS_DOWN;
                if (!gameState.isBossDefeated(6)) {
                    cells[7][7].monster = new BossMonster(BossConfig.BOSSES[5]);
                }
                break;
        }
    }

    // Boss房间布局
    private void generateBossRoomLayout() {
        // 创建Boss房间 - 中间是Boss，周围是通道
        for (int x = 5; x < 10; x++) {
            for (int y = 5; y < 10; y++) {
                if (x == 7 && y == 7) {
                    continue; // Boss位置
                }
                cells[x][y].type = CellType.WALL;
            }
        }

        // 创建通道
        for (int i = 3; i < 12; i++) {
            cells[i][3].type = CellType.FLOOR;
            cells[i][11].type = CellType.FLOOR;
            cells[3][i].type = CellType.FLOOR;
            cells[11][i].type = CellType.FLOOR;
        }

        // 连接入口和Boss房间
        cells[7][3].type = CellType.FLOOR;
        cells[7][11].type = CellType.FLOOR;
        cells[3][7].type = CellType.FLOOR;
        cells[11][7].type = CellType.FLOOR;
    }

    // 最终Boss房间布局
    private void generateFinalBossRoomLayout() {
        // 创建最终的魔王房间 - 更加宏伟的布局
        for (int x = 4; x < 11; x++) {
            for (int y = 4; y < 11; y++) {
                if (x == 7 && y == 7) {
                    continue; // Boss位置
                }
                if ((x == 4 || x == 10) && (y == 4 || y == 10)) {
                    continue; // 角落留空
                }
                cells[x][y].type = CellType.WALL;
            }
        }

        // 创建通道
        for (int i = 2; i < 13; i++) {
            cells[i][2].type = CellType.FLOOR;
            cells[i][12].type = CellType.FLOOR;
            cells[2][i].type = CellType.FLOOR;
            cells[12][i].type = CellType.FLOOR;
        }

        // 连接入口和魔王房间
        cells[7][2].type = CellType.FLOOR;
        cells[7][12].type = CellType.FLOOR;
        cells[2][7].type = CellType.FLOOR;
        cells[12][7].type = CellType.FLOOR;
    }

    private void generateWallsPattern1() {
        // 简单的房间布局
        for (int x = 3; x < 12; x++) {
            cells[x][3].type = CellType.WALL;
            cells[x][11].type = CellType.WALL;
        }
        for (int y = 3; y < 12; y++) {
            cells[3][y].type = CellType.WALL;
            cells[11][y].type = CellType.WALL;
        }

        // 门
        cells[7][3].type = CellType.FLOOR;
        cells[7][11].type = CellType.FLOOR;
        cells[3][7].type = CellType.FLOOR;
        cells[11][7].type = CellType.FLOOR;
    }

    private void generateWallsPattern2() {
        // 更复杂的迷宫布局
        for (int x = 2; x < 13; x++) {
            if (x != 7) {
                cells[x][5].type = CellType.WALL;
                cells[x][9].type = CellType.WALL;
            }
        }
        for (int y = 2; y < 13; y++) {
            if (y != 7) {
                cells[5][y].type = CellType.WALL;
                cells[9][y].type = CellType.WALL;
            }
        }
    }

    private void placeItemsLevel1() {
        cells[2][2].item = new Item(ItemType.HP_POTION);
        cells[4][4].item = new Item(ItemType.ATK_BOOST);
        cells[6][6].item = new Item(ItemType.GOLD);
        cells[8][8].item = new Item(ItemType.DEF_BOOST);
        cells[10][10].item = new Item(ItemType.HP_POTION);
    }

    private void placeItemsLevel2() {
        cells[2][2].item = new Item(ItemType.HP_POTION);
        cells[12][2].item = new Item(ItemType.ATK_BOOST);
        cells[2][12].item = new Item(ItemType.DEF_BOOST);
        cells[12][12].item = new Item(ItemType.HP_POTION);
    }

    private void placeMonstersLevel1() {
        cells[5][5].monster = new Monster("史莱姆", 20, 5, 2, 5);
        cells[9][9].monster = new Monster("蝙蝠", 15, 8, 1, 8);
        cells[7][7].monster = new Monster("骷髅", 30, 10, 3, 12);
    }

    private void placeMonstersLevel2() {
        cells[3][3].monster = new Monster("哥布林", 35, 12, 4, 15);
        cells[11][3].monster = new Monster("蜘蛛", 25, 15, 2, 18);
        cells[3][11].monster = new Monster("兽人", 45, 18, 6, 25);
        cells[11][11].monster = new Monster("黑暗史莱姆", 40, 20, 5, 30);
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public void draw(Graphics g, JPanel panel) {
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                cells[x][y].draw(g, panel);
            }
        }
    }
}

static class Cell {
    public int x, y;
    public CellType type;
    public Item item;
    public Monster monster;

    public Cell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public boolean canPass() {
        return type != CellType.WALL && (monster == null || monster.isDead());
    }

    public boolean hasItem() {
        return item != null;
    }

    public boolean hasMonster() {
        return monster != null && !monster.isDead();
    }

    public void clearItem() {
        item = null;
    }

    public void clearMonster() {
        monster = null;
    }

    public void draw(Graphics g, JPanel panel) {
        int px = x * 40;
        int py = y * 40;

        // 绘制地板
        if (type == CellType.FLOOR || type == CellType.STAIRS_UP || type == CellType.STAIRS_DOWN) {
            g.setColor(Color.GRAY);
            g.fillRect(px, py, 40, 40);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(px, py, 40, 40);
        }

        // 绘制墙壁
        if (type == CellType.WALL) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(px, py, 40, 40);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(px, py, 40, 40);
        }

        // 绘制楼梯
        if (type == CellType.STAIRS_UP) {
            g.setColor(Color.CYAN);
            g.fillRect(px + 5, py + 5, 30, 30);
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            g.drawString("⬆️", px + 10, py + 28);
        } else if (type == CellType.STAIRS_DOWN) {
            g.setColor(Color.CYAN);
            g.fillRect(px + 5, py + 5, 30, 30);
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            g.drawString("⬇️", px + 10, py + 28);
        }

        // 绘制物品
        if (hasItem()) {
            item.draw(g, panel, px, py);
        }

        // 绘制怪物
        if (hasMonster()) {
            monster.draw(g, panel, px, py);
        }
    }
}

// Boss怪物类 - 用于地图显示
static class BossMonster extends Monster {
    private Boss bossData;

    public BossMonster(Boss boss) {
        super(boss.name, boss.hp, boss.atk, boss.def, boss.gold);
        this.bossData = boss;
    }

    @Override
    public void draw(Graphics g, JPanel panel, int x, int y) {
        if (!isDead()) {
            // Boss特殊的外观 - 更大的尺寸
            g.setColor(Color.MAGENTA);
            g.fillRect(x + 2, y + 2, 36, 36);
            g.setColor(Color.RED);
            g.drawRect(x + 1, y + 1, 38, 38);

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

            String emoji = switch (bossData.name) {
                case "烈焰守卫·赤焰" -> "🔥";
                case "寒冰法师·霜语" -> "❄️";
                case "暗影刺客·夜刃" -> "👤";
                case "雷霆战士·雷震" -> "⚡";
                case "石化魔女·美杜莎" -> "👁️";
                case "终极魔王·暗黑破坏神" -> "👑";
                default -> "👹";
            };

            g.drawString(emoji, x + 8, y + 30);

            // 绘制Boss血条 - 更宽的血条
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x + 2, y + 36, 36, 4);
            g.setColor(Color.RED);
            int hpWidth = (int) ((double) hp / bossData.hp * 36);
            g.fillRect(x + 2, y + 36, hpWidth, 4);
        }
    }
}

static enum CellType {
    FLOOR, WALL, STAIRS_UP, STAIRS_DOWN
}

static class Item {
    public ItemType type;

    public Item(ItemType type) {
        this.type = type;
    }

    public void draw(Graphics g, JPanel panel, int x, int y) {
        g.setColor(Color.YELLOW);
        g.fillRect(x + 10, y + 10, 20, 20);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        String emoji = switch (type) {
            case HP_POTION -> "🧪";
            case ATK_BOOST -> "⚔️";
            case DEF_BOOST -> "🛡️";
            case GOLD -> "💰";
        };

        g.drawString(emoji, x + 12, y + 26);
    }
}

static enum ItemType {
    HP_POTION, ATK_BOOST, DEF_BOOST, GOLD
}

static class Monster {
    public String name;
    public int hp, atk, def, reward;
    private int maxHp;

    public Monster(String name, int hp, int atk, int def, int reward) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.atk = atk;
        this.def = def;
        this.reward = reward;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void draw(Graphics g, JPanel panel, int x, int y) {
        if (!isDead()) {
            g.setColor(Color.RED);
            g.fillRect(x + 5, y + 5, 30, 30);
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

            String emoji = switch (name) {
                case "史莱姆" -> "🟢";
                case "蝙蝠" -> "🦇";
                case "骷髅" -> "💀";
                case "哥布林" -> "👺";
                case "蜘蛛" -> "🕷️";
                case "兽人" -> "👹";
                case "黑暗史莱姆" -> "⚫";
                default -> "👾";
            };

            g.drawString(emoji, x + 10, y + 28);

            // 绘制血条
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x + 5, y + 35, 30, 3);
            g.setColor(Color.GREEN);
            int hpWidth = (int) ((double) hp / maxHp * 30);
            g.fillRect(x + 5, y + 35, hpWidth, 3);
        }
    }
}