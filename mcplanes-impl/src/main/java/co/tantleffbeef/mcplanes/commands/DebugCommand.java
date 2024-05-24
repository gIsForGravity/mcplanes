package co.tantleffbeef.mcplanes.commands;

import co.tantleffbeef.mcplanes.McPlanes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugCommand implements CommandExecutor {
    private final McPlanes plugin;

    public DebugCommand(McPlanes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Bukkit.broadcastMessage("debug command");
        Bukkit.broadcastMessage("poopy butt");
        return true;
    }
}
