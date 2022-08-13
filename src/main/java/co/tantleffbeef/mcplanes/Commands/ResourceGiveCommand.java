package co.tantleffbeef.mcplanes.Commands;

import co.tantleffbeef.mcplanes.Plugin;
import co.tantleffbeef.mcplanes.ResourceManager;
import co.tantleffbeef.mcplanes.Tool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static co.tantleffbeef.mcplanes.Tool.getNamespacePlugin;

public class ResourceGiveCommand implements CommandExecutor, TabCompleter {
    private final ResourceManager resourceManager;

    public ResourceGiveCommand(PluginCommand command, ResourceManager resourceManager) {
        this.resourceManager = resourceManager;

        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Invalid number of arguments");
            return false;
        }

        final var player = Bukkit.getPlayerExact(args[0]);

        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Invalid player name");
            return false;
        }

        final String namespacedKeyString = args[1];
        final String[] namespacedKeyStringParts = namespacedKeyString.split(":", 2);

        if (namespacedKeyStringParts.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Invalid item Id");
            return false;
        }

        final String namespace = namespacedKeyStringParts[0];
        final String value = namespacedKeyStringParts[1];

        var itemPlugin = Tool.getNamespacePlugin(namespace);

        if (itemPlugin == null) {
            commandSender.sendMessage(ChatColor.RED + "Invalid item Id");
            return false;
        }

        final NamespacedKey itemKey = new NamespacedKey(itemPlugin, value);
        ItemStack item;

        try {
            item = resourceManager.getCustomItem(itemKey);
        } catch (NullPointerException e) {
            commandSender.sendMessage(ChatColor.RED + "Invalid item Id");
            return false;
        }

        Inventory playerInventory = player.getInventory();
        var addedItems = playerInventory.addItem(item);

        for (ItemStack i : addedItems.values()) {
            Objects.requireNonNull(player.getLocation().getWorld()).dropItemNaturally(player.getLocation(), i);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 2)
            return null;

        var namespacedKeys = resourceManager.getItemIdList();
        final List<String> itemIds = new ArrayList<>(namespacedKeys.size());

        for (var key: namespacedKeys)
            itemIds.add(key.toString());

        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(args[1], itemIds, completions);
        return completions;
    }


}
