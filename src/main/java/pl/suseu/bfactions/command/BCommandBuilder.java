package pl.suseu.bfactions.command;

import java.util.ArrayList;
import java.util.List;

public class BCommandBuilder {
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private String permission = "bfactions.default";
    private String usage = null;
    private boolean needsArguments = false;
    private BCommandExecutor executor;

    public BCommandBuilder(String name) {
        this.name = name;
    }

    public BCommandBuilder addAlias(String aliases) {
        this.aliases.add(aliases);
        return this;
    }

    public BCommandBuilder setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public BCommandBuilder setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public BCommandBuilder setNeedsArguments(boolean needsArguments) {
        this.needsArguments = needsArguments;
        return this;
    }

    public BCommandBuilder setExecutor(BCommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public BCommand createBCommand() {
        return new BCommand(name, aliases, permission, usage, needsArguments, executor);
    }
}