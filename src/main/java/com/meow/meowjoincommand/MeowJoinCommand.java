package com.meow.meowjoincommand;

import com.meow.meowjoincommand.config.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MeowJoinCommand extends JavaPlugin implements Listener {

    private ConfigHandler configHandler;

    @Override
    public void onEnable() {
        // 加载配置文件
        saveDefaultConfig();
        configHandler = new ConfigHandler(this);

        if (getConfig().getBoolean("enable_plugin")) {
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info(ChatColor.GREEN + "MeowJoinCommand 已启用!");
        } else {
            getLogger().info(ChatColor.RED + "MeowJoinCommand 未启用，请在配置文件中启用插件。");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        configHandler.checkAndExecuteConfigs(player);
    }

    @Override
    public void onDisable() {
        getLogger().info("MeowJoinCommand 已禁用!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("meowjoincommand")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                // 检查是否是具有权限的用户
                if (sender.hasPermission("meowjoincommand.reload")) {
                    reloadConfig();
                    configHandler.reload(); // 重新加载 ConfigHandler
                    sender.sendMessage(ChatColor.GREEN + "插件配置已重新加载。");
                    getLogger().info(ChatColor.GREEN + "插件配置已重新加载！");
                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行此命令！");
                }
                return true;
            }
        }
        return false;
    }
}
