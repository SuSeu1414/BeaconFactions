package pl.suseu.bfactions.command;

import java.util.List;

public class BCommand {

//    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String usage;
    private final boolean needsArguments;

    private final BCommandExecutor executor;

    public BCommand(String name, List<String> aliases, String permission, String usage, boolean needsArguments, BCommandExecutor executor) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.usage = usage;
        this.needsArguments = needsArguments;
        this.executor = executor;
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

    public BCommandExecutor getExecutor() {
        return executor;
    }
}
