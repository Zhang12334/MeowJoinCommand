package com.meowjoincommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.scheduler.BukkitRunnable;

public class MeowJoinCommand extends JavaPlugin implements Listener {

    private ConfigHandler configHandler;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        //bstats
        int pluginId = 23901;
        Metrics metrics = new Metrics(this, pluginId);

        // 初始化 LanguageManager
        languageManager = new LanguageManager(getConfig());

        // 检查前置库是否加载
        if (!Bukkit.getPluginManager().isPluginEnabled("MeowLibs")) {
            getLogger().warning(languageManager.getMessage("CanNotFoundMeowLibs"));
            // 禁用插件
            getServer().getPluginManager().disablePlugin(this); 
            return;           
        }

        // 加载配置文件
        saveDefaultConfig();
        configHandler = new ConfigHandler(this);

        if (getConfig().getBoolean("enable_plugin")) {
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info(languageManager.getMessage("startup"));
        } else {
            getLogger().info(languageManager.getMessage("pluginNotEnabled"));
        }

        // 更新检查

        String currentVersion = getDescription().getVersion();
        getLogger().info(languageManager.getMessage("nowusingversion") + " v" + currentVersion);
        getLogger().info(languageManager.getMessage("checkingupdate"));

        // 创建更新检查器
        CheckUpdate updateChecker = new CheckUpdate(
            getLogger(), // 日志记录器
            languageManager, // 语言管理器
            getDescription() // 插件版本信息
        );    

        // 异步执行更新检查
        new BukkitRunnable() {
            @Override
            public void run() {
                updateChecker.checkUpdate();
            }
        }.runTaskAsynchronously(this);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {         
            suggestions.add("reload");
        }
        return suggestions;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        configHandler.checkAndExecuteConfigs(player);  // 执行配置
    }

    @Override
    public void onDisable() {
        getLogger().info(languageManager.getMessage("shutdown"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("meowjoincommand")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                // 检查是否是具有权限的用户
                if (sender.hasPermission("meowjoincommand.reload")) {
                    // 调用配置加载方法
                    saveDefaultConfig();
                    languageManager = new LanguageManager(getConfig());
                    configHandler.reloadConfig();  // 热加载配置
                    sender.sendMessage(ChatColor.GREEN + languageManager.getMessage("reloaded"));
                    getLogger().info(languageManager.getMessage("reloaded"));
                } else {
                    sender.sendMessage(ChatColor.RED + languageManager.getMessage("nopermission"));
                }
                return true;
            }
        }
        return false;
    }
}