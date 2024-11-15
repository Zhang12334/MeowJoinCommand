package com.meow.meowjoincommand;

import com.meow.meowjoincommand.config.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

public class MeowJoinCommand extends JavaPlugin implements Listener {

    private ConfigHandler configHandler;

    // 存储语言消息的变量
    private String startupMessage;
    private String shutdownMessage;
    private String notenableMessage;
    private String nowusingversionMessage;
    private String checkingupdateMessage;
    private String checkfailedMessage;
    private String updateavailableMessage;
    private String updateurlMessage;
    private String oldversionmaycauseproblemMessage;
    private String nowusinglatestversionMessage;
    private String reloadedMessage;
    private String nopermissionMessage;
    private String cannotfindvaultMessage;
    private String cannotreloadecoserviceMessage;
    private String unknowntypeMessage;
    private String executenotcorrectMessage;
    private String tickdelaynotintMessage;
    private String unknowncommandMessage;
    private String commandnotcorrectMessage;

    // 获取语言变量的方法
    public String getLanguage(String variableName) {
        try {
            // 使用反射根据变量名获取字段
            Field field = this.getClass().getDeclaredField(variableName);
            field.setAccessible(true);  // 使私有字段可以访问
            return (String) field.get(this);  // 返回该字段的值
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;  // 如果出现错误，返回null
        }
    }

    @Override
    public void onEnable() {
        int pluginId = 23901;
        Metrics metrics = new Metrics(this, pluginId);
        
        // 加载配置和语言设置
        loadLanguage();
        saveDefaultConfig();
        configHandler = new ConfigHandler(this);

        if (getConfig().getBoolean("enable_plugin")) {
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info(startupMessage);
        } else {
            getLogger().warning(notenableMessage);
        }
        
        String currentVersion = getDescription().getVersion();
        getLogger().info(nowusingversionMessage + " v" + currentVersion);
        getLogger().info(checkingupdateMessage);
        
        // 异步检查更新
        new BukkitRunnable() {
            @Override
            public void run() {
                check_update();
            }
        }.runTaskAsynchronously(this);
    }

    // 加载语言设置
    private void loadLanguage() {
        FileConfiguration config = getConfig();
        String language = config.getString("language", "zh_cn");

        if ("zh_cn".equalsIgnoreCase(language)) {
            // 中文消息
            startupMessage = "MeowJoinCommand 已加载！";
            shutdownMessage = "MeowJoinCommand 已卸载！";
            notenableMessage = "插件未启用，请前往配置文件中设置！";
            nowusingversionMessage = "当前使用版本:";
            checkingupdateMessage = "正在检查更新...";
            checkfailedMessage = "检查更新失败，请检查你的网络状况！";
            updateavailableMessage = "发现新版本:";
            updateurlMessage = "新版本下载地址:";
            oldversionmaycauseproblemMessage = "旧版本可能会导致问题，请尽快更新！";
            nowusinglatestversionMessage = "您正在使用最新版本！";
            reloadedMessage = "配置文件已重载！";
            nopermissionMessage = "你没有权限执行此命令！";
            cannotfindvaultMessage = "没有找到 Vault 插件或经济服务无法正常工作，将无法使用经济系统相关功能！";
            cannotreloadecoserviceMessage = "经济服务无法重载，请确保 Vault 插件存在";
            unknowntypeMessage = "未知的条件类型:";
            executenotcorrectMessage = "条件配置格式不正确:";
            tickdelaynotintMessage = "tick_delay 必须是整数，当前值:";
            unknowncommandMessage = "未知的命令类型:";
            commandnotcorrectMessage = "命令配置格式不正确:";
        } else {
            // English message
            startupMessage = "MeowJoinCommand has been loaded!";
            shutdownMessage = "MeowJoinCommand has been disabled!";
            notenableMessage = "Plugin not enabled, please set it in the configuration file!";
            nowusingversionMessage = "Currently using version:";
            checkingupdateMessage = "Checking for updates...";
            checkfailedMessage = "Update check failed, please check your network!";
            updateavailableMessage = "A new version is available: ";
            updateurlMessage = "Download update at:";
            oldversionmaycauseproblemMessage = "Old versions may cause problems!";
            nowusinglatestversionMessage = "You are using the latest version!";
            reloadedMessage = "Configuration file has been reloaded!";
            nopermissionMessage = "You do not have permission to execute this command!";
            cannotfindvaultMessage = "Vault plugin not found or the economy service is not working properly, the economy system related functions will not work!";
            cannotreloadecoserviceMessage = "Economy service cannot be reloaded, please ensure that the Vault plugin exists";
            unknowntypeMessage = "Unknown condition type:";
            executenotcorrectMessage = "Condition configuration format is incorrect:";
            tickdelaynotintMessage = "tick_delay must be an integer, current value:";
            unknowncommandMessage = "Unknown command type:";
            commandnotcorrectMessage = "Command configuration format is incorrect:";
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("reload");
        }
        return suggestions;
    }

    private void check_update() {
        String currentVersion = getDescription().getVersion();
        String[] githubUrls = {
            "https://ghp.ci/",
            "https://raw.fastgit.org/",
            ""
        };
        String latestVersionUrl = "https://github.com/Zhang12334/MeowJoinCommand/releases/latest";

        try {
            String latestVersion = null;
            for (String url : githubUrls) {
                HttpURLConnection connection = (HttpURLConnection) new URL(url + latestVersionUrl).openConnection();
                connection.setInstanceFollowRedirects(false); // 不自动跟随重定向
                int responseCode = connection.getResponseCode();
                if (responseCode == 302) { // 如果是 302 重定向
                    String redirectUrl = connection.getHeaderField("Location");
                    if (redirectUrl != null && redirectUrl.contains("tag/")) {
                        latestVersion = extractVersionFromUrl(redirectUrl);
                        break;
                    }
                }
                connection.disconnect();
            }

            if (latestVersion == null) {
                getLogger().warning(checkfailedMessage);
                return;
            }

            if (isVersionGreater(latestVersion, currentVersion)) {
                getLogger().warning(updateavailableMessage + " v" + latestVersion);
                getLogger().warning(updateurlMessage + " https://github.com/Zhang12334/MeowJoinCommand/releases/latest");
                getLogger().warning(oldversionmaycauseproblemMessage);
            } else {
                getLogger().info(nowusinglatestversionMessage);
            }
        } catch (Exception e) {
            getLogger().warning(checkfailedMessage);
        }
    }

    private boolean isVersionGreater(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        for (int i = 0; i < Math.max(v1Parts.length, v2Parts.length); i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;
            if (v1Part > v2Part) return true;
            else if (v1Part < v2Part) return false;
        }
        return false;
    }

    private String extractVersionFromUrl(String url) {
        int tagIndex = url.indexOf("tag/");
        if (tagIndex != -1) {
            int endIndex = url.indexOf('/', tagIndex + 4);
            if (endIndex == -1) {
                endIndex = url.length();
            }
            return url.substring(tagIndex + 4, endIndex);
        }
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        configHandler.checkAndExecuteConfigs(player);  // 执行配置
    }

    @Override
    public void onDisable() {
        getLogger().info(shutdownMessage);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("meowjoincommand")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("meowjoincommand.reload")) {
                    reloadConfig();
                    loadLanguage(); // 加载语言配置
                    configHandler.reloadConfig();  // 热加载配置
                    sender.sendMessage(ChatColor.GREEN + reloadedMessage);
                } else {
                    sender.sendMessage(ChatColor.RED + nopermissionMessage);
                }
                return true;
            }
        }
        return false;
    }
}
