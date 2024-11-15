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
        plugin.getLogger().warning("没有找到 Vault 插件或经济服务无法正常工作。");
        return false;
    }

    // 重载所有配置
    public void reloadConfig() {
        plugin.reloadConfig(); // 重新加载配置文件

        // 重新初始化经济服务
        if (!setupEconomy()) {
            plugin.getLogger().warning("经济服务无法重载，请确保 Vault 插件存在。");
        }

        plugin.getLogger().info("MeowJoinCommand 配置已重新加载！");
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
                            plugin.getLogger().warning("玩家 " + player.getName() + " 没有权限: " + value);
                            return false;
                        }
                        break;
                    case "money":
                        if (!checkMoneyCondition(player, value)) {
                            plugin.getLogger().warning("玩家 " + player.getName() + " 金钱条件不满足: " + value);
                            return false;
                        }
                        break;
                    default:
                        plugin.getLogger().warning("未知的条件类型: " + type);
                        return false;
                }
            } else {
                plugin.getLogger().warning("条件配置格式不正确: " + condition);
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
            // 使用 final 关键字来解决编译错误
            final String type = (String) command.get("type");
            String cmd = (String) command.get("command"); // 取消 final，允许后续赋值
            int tickDelay = 0;  // 默认延迟为0

            if (command.containsKey("tick_delay")) {
                Object tickObj = command.get("tick_delay");
                if (tickObj instanceof Integer) {
                    tickDelay = (int) tickObj; // 直接转换为 int
                } else {
                    plugin.getLogger().warning("tick_delay 必须是整数，当前值: " + tickObj);
                }
            }

            if (cmd != null) {
                cmd = cmd.replace("%player%", player.getName()); // 替换 %player% 为玩家名字
            }

            // 执行命令的逻辑，延迟执行
            if (type != null && cmd != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        switch (type) {
                            case "player":
                                // 玩家执行指令
                                player.performCommand(cmd);
                                break;
                            case "console":
                                // 服务器执行指令
                                Bukkit.dispatchCommand(console, cmd);
                                break;
                            default:
                                plugin.getLogger().warning("未知的命令类型: " + type);
                                break;
                        }
                    }
                }.runTaskLater(plugin, tickDelay); // 延迟tick执行
            } else {
                plugin.getLogger().warning("命令配置格式不正确: " + command);
            }
        }
    }
}
