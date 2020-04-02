package pl.suseu.bfactions.command;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.cmds.ItemSetCommandExecutor;
import pl.suseu.bfactions.command.cmds.TestCommandExecutor;
import pl.suseu.bfactions.util.StringArrayUtil;

import java.util.HashSet;
import java.util.Set;

public class BCommandMap {

    private final BFactions plugin;
    private final Set<BCommand> commands = new HashSet<>();

    private final TestCommandExecutor testCommandExecutor;
    private final ItemSetCommandExecutor itemSetCommandExecutor;

    public BCommandMap(BFactions plugin) {
        this.plugin = plugin;

        this.testCommandExecutor = new TestCommandExecutor();
        this.itemSetCommandExecutor = new ItemSetCommandExecutor(this.plugin);
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

        new BCommandBuilder("setitem")
                .setPermission("bfactions.setitem")
                .setUsage("setitem item-id")
                .setExecutor(this.itemSetCommandExecutor)
                .build(this.commands);
    }
}
