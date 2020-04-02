package pl.suseu.bfactions.command;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.cmds.TestAddMemberCommandExecutor;
import pl.suseu.bfactions.command.cmds.TestCommandExecutor;
import pl.suseu.bfactions.util.StringArrayUtil;

import java.util.HashSet;
import java.util.Set;

public class BCommandMap {

    private final BFactions plugin;
    private final Set<BCommand> commands = new HashSet<>();

    private final TestCommandExecutor testCommandExecutor;
    private final TestAddMemberCommandExecutor testAddMemberCommandExecutor;

    public BCommandMap(BFactions plugin) {
        this.plugin = plugin;

        this.testCommandExecutor = new TestCommandExecutor();
        this.testAddMemberCommandExecutor = new TestAddMemberCommandExecutor(this.plugin);
    }

    public void addCommand(BCommand command) {
        commands.add(command);
    }

    public BCommand getCommand(String cmd) {
        for (BCommand command : this.commands) {
            if (command.getName().equalsIgnoreCase(cmd) || StringArrayUtil.containsIgnoreCase(command.getAliases(), cmd)) {
                return command;
            }
        }
        return null;
    }

    public void initCommands() {
        new BCommandBuilder("test")
                .setPermission("bfactions.false")
                .setExecutor(this.testCommandExecutor)
                .addAlias("t")
                .addAlias("tst")
                .setUsage("test <arg>")
                .build(this.commands);
        new BCommandBuilder("add")
                .setExecutor(this.testCommandExecutor)
                .setNeedsArguments(true)
                .setUsage("add <player>")
                .build(this.commands);
    }
}
