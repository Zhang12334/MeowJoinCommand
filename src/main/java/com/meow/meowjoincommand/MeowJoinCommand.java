package com.meow.meowjoincommand;

import com.meow.meowjoincommand.config.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
}
