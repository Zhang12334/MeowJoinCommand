package com.meowjoincommand;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private final JavaPlugin plugin;
    private Economy economy;
    private LanguageManager languageManager;

    public ConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        languageManager = new LanguageManager(plugin.getConfig());
        setupEconomy(); // 初始化时尝试加载经济服务
        setupPAPI(); // 初始化 PAPI
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning(languageManager.getMessage("no-vault-plugin"));
            return false;
        }
    
        try {
            RegisteredServiceProvider<?> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = (Economy) rsp.getProvider();
                return true;
            } else {
                plugin.getLogger().warning(languageManager.getMessage("no-vault-plugin"));
                return false;
            }
        } catch (Exception e) {
            plugin.getLogger().warning(languageManager.getMessage("no-vault-plugin"));
            return false;
        }
    }

    // 初始化 PAPI
    private boolean setupPAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return true;
        } else {
            plugin.getLogger().warning(languageManager.getMessage("no-papi-plugin"));
            return false;
        }
    }    

    // 重载所有配置
    public void reloadConfig() {
        
        plugin.reloadConfig(); // 重新加载配置文件

        // 重新初始化经济服务
        setupEconomy();

        // 重新初始化PAPI
        setupPAPI();
    }

    // 检查并执行配置列表中的配置
    public void checkAndExecuteConfigs(Player player) {
        Map<String, Object> configList = plugin.getConfig().getConfigurationSection("configlist").getValues(false);

        for (String key : configList.keySet()) {
            if (plugin.getConfig().getBoolean("configlist." + key + ".enabled")) {
                int tickDelay = plugin.getConfig().getInt("configlist." + key + ".tick_delay", 0); // 获取 tick 延迟，默认是 0
                // 如果设置了 tick 延迟，则在延迟后执行
                if (tickDelay > 0) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (checkConditions(player, "configlist." + key + ".execute")) {
                            executeCommands(player, "configlist." + key + ".commands");
                        }
                    }, tickDelay);
                } else {
                    // 如果没有延迟，直接执行
                    if (checkConditions(player, "configlist." + key + ".execute")) {
                        executeCommands(player, "configlist." + key + ".commands");
                    }
                }
            }
        }
    }

    // 检查条件
    private boolean checkConditions(Player player, String path) {
        List<Map<?, ?>> conditions = plugin.getConfig().getMapList(path);

        for (Map<?, ?> condition : conditions) {
            String type = null;
            String value = null;

            for (Map.Entry<?, ?> entry : condition.entrySet()) {
                type = (String) entry.getKey();
                value = (String) entry.getValue();
            }

            if (type != null && value != null) {
                switch (type) {
                    case "permission":
                        if (!player.hasPermission(value)) {
                            return false;
                        }
                        break;
                    case "money":
                        if (!checkMoneyCondition(player, value)) {
                            return false;
                        }
                        break;
                    case "placeholderapi":
                        if (!checkPAPI(player, value)) {
                            return false;
                        }
                        break;                        
                    default:
                        plugin.getLogger().warning(String.format(languageManager.getMessage("unknownConditionType"), type));
                        return false;
                }
            } else {
                plugin.getLogger().warning(String.format(languageManager.getMessage("incorrectConditionFormat"), condition));
                return false;
            }
        }
        return true;
    }

    private boolean checkPAPI(Player player, String condition) {
        // 使用正则表达式拆分字符串
        String operator = "";
        String[] parts = null;
        // 优先拆分 != 防止错误分割字符串
        if (condition.contains("!=")) {
            operator = "!=";
            parts = condition.split("!=");
        } else if (condition.contains("=")) {
            operator = "=";
            parts = condition.split("=");
        }

        if (parts != null && parts.length == 2) {
            String placeholder = parts[0].trim();  // %some_placeholders%
            String expectedValue = parts[1].trim(); // some_value

            // 获取占位符的实际值
            String placeholderValue = PlaceholderAPI.setPlaceholders(player, placeholder);

            // 根据操作符判断
            if (operator.equals("=")) {
                // "=" 操作符：占位符值应该等于 expectedValue
                return placeholderValue.equals(expectedValue);
            } else if (operator.equals("!=")) {
                // "!=" 操作符：占位符值不等于 expectedValue
                return !placeholderValue.equals(expectedValue);
            }
        }

        // 如果没有找到操作符或格式不正确，返回 false
        plugin.getLogger().warning(String.format(languageManager.getMessage("incorrectConditionFormat"), condition));
        return false;
    }



    // 检查金钱条件
    private boolean checkMoneyCondition(Player player, String condition) {
        double playerMoney = getPlayerMoney(player);
        String operator = condition.replaceAll("[^><=!]", "").trim();
        double value = Double.parseDouble(condition.replaceAll("[^0-9.-]", "").trim());

        switch (operator) {
            case ">":
                return playerMoney > value;
            case "<":
                return playerMoney < value;
            case "=":
                return playerMoney == value;
            case "!":
                return playerMoney != value;
            case ">=":
                return playerMoney >= value;
            case "<=":
                return playerMoney <= value;
            default:
                return false;
        }
    }

    // 获取玩家金钱
    private double getPlayerMoney(Player player) {
        if (economy == null) {
            return 0.0;
        }
        return economy.getBalance(player);
    }

    // 执行命令
    private void executeCommands(Player player, String path) {
        List<Map<?, ?>> commands = plugin.getConfig().getMapList(path);
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        for (Map<?, ?> command : commands) {
            // 将 type 和 cmd 声明为 final
            final String type = (String) command.get("type");
            final String cmd = (String) command.get("command"); // 声明为 final，避免编译错误
            int tickDelay = 0;  // 默认延迟为0

            if (command.containsKey("tick_delay")) {
                Object tickObj = command.get("tick_delay");
                if (tickObj instanceof Integer) {
                    tickDelay = (int) tickObj; // 直接转换为 int
                } else {
                    plugin.getLogger().warning(String.format(languageManager.getMessage("tickDelayMustBeInt"), tickObj.toString()));                    
                }
            }

            if (cmd != null) {
                // 替换 %player% 为玩家名字
                final String finalCmd = cmd.replace("%player%", player.getName());

                // 执行命令的逻辑，延迟执行
                if (type != null && finalCmd != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            switch (type) {
                                case "player":
                                    // 玩家执行指令
                                    player.performCommand(finalCmd);
                                    break;
                                case "console":
                                    // 服务器执行指令
                                    Bukkit.dispatchCommand(console, finalCmd);
                                    break;
                                default:
                                    plugin.getLogger().warning(String.format(languageManager.getMessage("unknownCommandType"), type));
                                    break;
                            }
                        }
                    }.runTaskLater(plugin, tickDelay); // 延迟tick执行
                } else {
                    plugin.getLogger().warning(String.format(languageManager.getMessage("commandConfigFormatIncorrect"), command));
                }
            }
        }
    }
}