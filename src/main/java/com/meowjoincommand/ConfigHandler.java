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
                boolean conditionMet = false;
                switch (type) {
                    case "permission":
                        conditionMet = checkPermissionCondition(player, value);
                        break;
                    case "money":
                        conditionMet = checkMoneyCondition(player, value);
                        break;
                    case "placeholderapi":
                        conditionMet = checkPlaceholderCondition(player, value);
                        break;
                    default:
                        plugin.getLogger().warning(String.format(languageManager.getMessage("unknownConditionType"), type));
                        return false;
                }
                if (!conditionMet) {
                    return false;
                }
            } else {
                plugin.getLogger().warning(String.format(languageManager.getMessage("incorrectConditionFormat"), condition));
                return false;
            }
        }
        return true;
    }

    private boolean checkPermissionCondition(Player player, String value) {
        String[] permissions = value.split("\\s*\\|\\|\\s*");
        for (String perm : permissions) {
            if (player.hasPermission(perm.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMoneyCondition(Player player, String value) {
        String[] conditions = value.split("\\s*\\|\\|\\s*");
        for (String cond : conditions) {
            if (checkSingleMoneyCondition(player, cond.trim())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkSingleMoneyCondition(Player player, String condition) {
        String operator = condition.replaceAll("[^><=!]", "").trim();
        String numberPart = condition.replaceAll("[^0-9.-]", "").trim();
        
        if (numberPart.isEmpty()) {
            plugin.getLogger().warning(String.format(languageManager.getMessage("unknownConditionType"), condition));
            return false;
        }
        
        try {
            double value = Double.parseDouble(numberPart);
            double playerMoney = getPlayerMoney(player);
    
            switch (operator) {
                case ">": return playerMoney > value;
                case "<": return playerMoney < value;
                case "=": return playerMoney == value;
                case "!=": return playerMoney != value;
                case ">=": return playerMoney >= value;
                case "<=": return playerMoney <= value;
                default:
                    plugin.getLogger().warning(String.format(languageManager.getMessage("unknownOperator"), operator));
                    return false;
            }
        } catch (NumberFormatException e) {
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

    private boolean checkPlaceholderCondition(Player player, String value) {
        String[] conditions = value.split("\\s*\\|\\|\\s*");
        for (String cond : conditions) {
            if (checkSinglePlaceholderCondition(player, cond.trim())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkSinglePlaceholderCondition(Player player, String condition) {
        String operator = "";
        String[] parts = null;
        
        // 处理 != 优先
        if (condition.contains("!=")) {
            operator = "!=";
            parts = condition.split("!=", 2);
        } else if (condition.contains("=")) {
            operator = "=";
            parts = condition.split("=", 2);
        }
    
        if (parts == null || parts.length != 2) {
            plugin.getLogger().warning(String.format(languageManager.getMessage("incorrectConditionFormat"), condition));
            return false;
        }
    
        String placeholder = parts[0].trim();
        String expectedValue = parts[1].trim();
        String actualValue = PlaceholderAPI.setPlaceholders(player, placeholder);
    
        if (operator.equals("=")) {
            return actualValue.equals(expectedValue);
        } else if (operator.equals("!=")) {
            return !actualValue.equals(expectedValue);
        }
        
        plugin.getLogger().warning(String.format(languageManager.getMessage("unknownOperator"), operator));
        return false;
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