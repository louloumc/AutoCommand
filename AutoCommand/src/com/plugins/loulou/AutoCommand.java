package com.plugins.loulou;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AutoCommand extends JavaPlugin {
    private List<String> customCmds;
    private String customTime;
    private FileConfiguration config;
    
    @Override
    public void onEnable() {
    	config = getConfig();
    	saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        this.customCmds = config.getStringList("custom-cmds");
        this.customTime = config.getString("custom-time");
        getCommand("acm").setExecutor(this);
        
        // Schedule task to check time
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                checkCustomTime();
            }
        }, 0L, 1000L); // Check time every 20 ticks (1 second)
    }
    private void reloadCustomConfigs() {
        // reload the config file
        reloadConfig();
        // read the custom-cmds and custom-time values from the config file
        List<String> customCmds = getConfig().getStringList("custom-cmds");
        String customTime = getConfig().getString("custom-time");
        // update plugin properties
        this.customCmds = customCmds;
        this.customTime = customTime;
        saveDefaultConfig();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("acm")) {
            if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "你不能使用该指令！");
                return false;
            }
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                // 重载配置文件的代码
            	reloadCustomConfigs();
                List<String> customCmds = getConfig().getStringList("custom-cmds");
                int customTime = getConfig().getInt("custom-time");
                sender.sendMessage(ChatColor.GREEN + "已重载配置文件！");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Clean up
    }
    
    private void checkCustomTime() {
        // Get current time
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(date);
        
        // Compare with custom time
        if (currentTime.equals(customTime)) {
            for (String cmd : customCmds) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }       
    }
}