package com.meow.meowjoincommand.config;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private final JavaPlugin plugin;
    private Economy economy;

    public ConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeEconomy();  // 初始化经济服务
    }

    // 初始化 Vault 经济接口
    private void initializeEconomy() {
        // 尝试获取 Vault 经济服务
        if (!setupEconomy()) {
            plugin.getLogger().warning("没有找到 Vault 插件或经济服务无法正常工作。");
        }
    }

    // 设置 Vault 经济接口
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
        return economy != null;
    }

    // 检查并执行配置中的指令
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

    // 检查条件是否满足
    private boolean checkConditions(Player player, String path) {
        List<Map<?, ?>> conditions = plugin.getConfig().getMapList(path);

        for (Map<?, ?> condition : conditions) {
            String[] conditionArray = condition.values().toArray(new String[0]);
            String type = conditionArray[0];
            String value = conditionArray[1];

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
                    plugin.getLogger().warning("未知的条件类型: " + type);
                    return false;
            }
        }
        return true;
    }

    // 检查玩家金钱条件
    private boolean checkMoneyCondition(Player player, String condition) {
        double playerMoney = getPlayerMoney(player);
        String operator = condition.replaceAll("[^><=!]", "").trim(); // 获取操作符
        double value = Double.parseDouble(condition.replaceAll("[^0-9.-]", "").trim()); // 获取数值部分

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

    // 获取玩家金钱余额
    private double getPlayerMoney(Player player) {
        if (economy == null) {
            return 0.0;
        }
        return economy.getBalance(player);
    }

    // 执行配置中的命令
    private void executeCommands(Player player, String path) {
        List<Map<?, ?>> commands = plugin.getConfig().getMapList(path);
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        for (Map<?, ?> command : commands) {
            String[] commandArray = command.values().toArray(new String[0]);
            String type = commandArray[0];
            String cmd = commandArray[1].replace("%player%", player.getName());

            switch (type) {
                case "player":
                    player.performCommand(cmd);
                    break;
                case "console":
                    Bukkit.dispatchCommand(console, cmd);
                    break;
                default:
                    plugin.getLogger().warning("未知的命令类型: " + type);
                    break;
            }
        }
    }

    // 重新加载配置文件
    public void reload() {
        plugin.reloadConfig();  // 重新加载配置文件
        plugin.getLogger().info("配置文件已重新加载。");

        // 重新初始化经济服务
        initializeEconomy();
    }
}
