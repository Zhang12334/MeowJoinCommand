public class ConfigHandler {

    private final MeowJoinCommandPlugin plugin;  // 使用 MeowJoinCommandPlugin 类型
    private Economy economy;

    // 使用 MeowJoinCommandPlugin 类型的构造函数
    public ConfigHandler(MeowJoinCommandPlugin plugin) {
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
        plugin.getLogger().warning(plugin.getLanguage("cannotfindvaultMessage"));
        return false;
    }

    // 重载所有配置
    public void reloadConfig() {
        plugin.reloadConfig(); // 重新加载配置文件
        // 重新初始化经济服务
        if (!setupEconomy()) {
            plugin.getLogger().warning(plugin.getLanguage("cannotreloadecoserviceMessage"));
        }
        plugin.getLogger().info(plugin.getLanguage("reloadedMessage"));
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
                        plugin.getLogger().warning(plugin.getLanguage("unknowntypeMessage") + " " + type);
                        return false;
                }
            } else {
                plugin.getLogger().warning(plugin.getLanguage("executenotcorrectMessage") + " " + condition);
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
            final String type = (String) command.get("type");
            final String cmd = (String) command.get("command");
            int tickDelay = 0;

            if (command.containsKey("tick_delay")) {
                Object tickObj = command.get("tick_delay");
                if (tickObj instanceof Integer) {
                    tickDelay = (int) tickObj;
                } else {
                    plugin.getLogger().warning(plugin.getLanguage("tickdelaynotintMessage") + " " + tickObj);
                }
            }

            if (cmd != null) {
                final String finalCmd = cmd.replace("%player%", player.getName());

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
                                    plugin.getLogger().warning(plugin.getLanguage("unknowncommandMessage") + type);
                                    break;
                            }
                        }
                    }.runTaskLater(plugin, tickDelay); // 延迟tick执行
                } else {
                    plugin.getLogger().warning(plugin.getLanguage("commandnotcorrectMessage") + command);
                }
            }
        }
    }
}
