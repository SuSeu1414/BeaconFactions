package pl.suseu.bfactions.command;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.cmds.TestCommandExecutor;

import java.util.HashSet;
import java.util.Set;

public class BCommandMap {

    private BFactions plugin;
    private Set<BCommand> commands = new HashSet<>();

    private TestCommandExecutor testCommandExecutor;

    public BCommandMap(BFactions plugin) {
        this.plugin = plugin;

        this.testCommandExecutor = new TestCommandExecutor();
    }

    public void addCommand(BCommand command) {
        commands.add(command);
    }

    public void initCommands() {
        new BCommandBuilder("test")
                .setPermission("bfactions.false")
                .setExecutor(this.testCommandExecutor)
                .addAlias("t")
                .addAlias("tst")
                .setUsage("test <arg>")
                .build(this.commands);
    }
}
