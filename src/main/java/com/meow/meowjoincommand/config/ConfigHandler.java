package com.meow.meowjoincommand.config;

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

    public ConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        setupEconomy(); // 初始化时尝试加载经济服务
    }

    // 初始化 Vault 经济接口
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            return true;
        }
        plugin.getLogger().warning(getLanguage("cannotfindvaultMessage"));
        return false;
    }

    // 重载所有配置
    public void reloadConfig() {
        plugin.reloadConfig(); // 重新加载配置文件
        // 重新初始化经济服务
        if (!setupEconomy()) {
            plugin.getLogger().warning(getLanguage("cannotreloadecoserviceMessage"));
        }
        plugin.getLogger().info(getLanguage("reloadedMessage"));
    }

    // 检查并执行配置列表中的配置
    public void checkAndExecuteConfigs(Player player) {
        Map<String, Object> configList = plugin.getConfig().getConfigurationSection("configlist").getValues(false);

        for (String key : configList.keySet()) {
            if (plugin.getConfig().getBoolean("configlist." + key + ".enabled")) {
                if (checkConditions(player, "configlist." + key + ".execute")) {
                    executeCommands(player, "configlist." + key + ".commands");
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
                    default:
                        plugin.getLogger().warning(getLanguage("unknowntypeMessage") + " " + type);
                        return false;
                }
            } else {
                plugin.getLogger().warning(getLanguage("executenotcorrectMessage") + " " + condition);
                return false;
            }
        }
        return true;
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
                    plugin.getLogger().warning(getLanguage("tickdelaynotintMessage") + " " + tickObj);
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
                                    plugin.getLogger().warning(getLanguage("unknowncommandMessage") + type);
                                    break;
                            }
                        }
                    }.runTaskLater(plugin, tickDelay); // 延迟tick执行
                } else {
                    plugin.getLogger().warning(getLanguage("commandnotcorrectMessage") + command);
                }
            }
        }
    }
}
