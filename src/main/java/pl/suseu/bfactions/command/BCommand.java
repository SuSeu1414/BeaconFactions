package pl.suseu.bfactions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.suseu.bfactions.BFactions;

import java.util.List;

public class BCommand {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String usage;
    private final boolean needsArguments;
    private final boolean async;

    private final BCommandExecutor executor;

    public BCommand(String name, List<String> aliases, String permission, String usage, boolean needsArguments, boolean async, BCommandExecutor executor) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.usage = usage;
        this.needsArguments = needsArguments;
        this.async = async;
        this.executor = executor;
    }

    public void sendUsage(CommandSender sender, String label) {
        if (this.plugin == null) {
            return;
        }
        this.sendUsage(sender, label, this.getUsage());
    }

    public void sendUsage(CommandSender sender, String label, String usage) {
        if (this.plugin == null) {
            return;
        }
        usage = "/" + label + usage;
        this.plugin.getLang().sendMessage("command-usage", sender, "%usage%", usage);
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public String getUsage() {
        return usage;
    }

    public boolean isNeedsArguments() {
        return needsArguments;
    }

    public boolean isAsync() {
        return async;
    }

    public BCommandExecutor getExecutor() {
        return executor;
    }
}
