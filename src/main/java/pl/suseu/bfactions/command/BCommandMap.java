package pl.suseu.bfactions.command;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.cmds.*;
import pl.suseu.bfactions.command.cmds.admin.AKickCommandExecutor;
import pl.suseu.bfactions.command.cmds.admin.AResetCommandExecutor;
import pl.suseu.bfactions.util.StringArrayUtil;

import java.util.HashSet;
import java.util.Set;

public class BCommandMap {

    private final BFactions plugin;
    private final Set<BCommand> commands = new HashSet<>();

    private final TestCommandExecutor testCommandExecutor;
    private final ItemSetCommandExecutor itemSetCommandExecutor;
    private final ItemGiveCommandExecutor itemGiveCommandExecutor;
    private final ToggleGuiDebugCommandExecutor toggleGuiDebugCommandExecutor;
    private final InviteMemberCommandExecutor inviteMemberCommandExecutor;
    private final LeaveCommandExecutor leaveCommandExecutor;
    private final AResetCommandExecutor aResetCommandExecutor;
    private final AKickCommandExecutor aKickCommandExecutor;

    public BCommandMap(BFactions plugin) {
        this.plugin = plugin;

        this.testCommandExecutor = new TestCommandExecutor();
        this.itemSetCommandExecutor = new ItemSetCommandExecutor(this.plugin);
        this.itemGiveCommandExecutor = new ItemGiveCommandExecutor(this.plugin);
        this.toggleGuiDebugCommandExecutor = new ToggleGuiDebugCommandExecutor(this.plugin);
        this.inviteMemberCommandExecutor = new InviteMemberCommandExecutor(this.plugin);
        this.leaveCommandExecutor = new LeaveCommandExecutor(this.plugin);
        this.aResetCommandExecutor = new AResetCommandExecutor(this.plugin);
        this.aKickCommandExecutor = new AKickCommandExecutor(this.plugin);
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
                .setNeedsArguments(true)
                .setUsage("setitem item-id")
                .setExecutor(this.itemSetCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("giveitem")
                .setPermission("bfactions.giveitem")
                .setExecutor(itemGiveCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("debuggui")
                .setPermission("bfactions.debuggui")
                .setExecutor(toggleGuiDebugCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("invite")
                .setPermission("bfactions.invite")
                .setExecutor(this.inviteMemberCommandExecutor)
                .setNeedsArguments(true)
                .setUsage("invite <player>")
                .build(this.commands);

        new BCommandBuilder("leave")
                .addAlias("quit")
                .setPermission("bfactions.quit")
                .setExecutor(this.leaveCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("reset")
                .setPermission("bfactions.reset")
                .setExecutor(this.aResetCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("kick")
                .setPermission("bfactions.kick")
                .setExecutor(this.aKickCommandExecutor)
                .build(this.commands);
    }

    public Set<BCommand> getCommands() {
        return new HashSet<>(this.commands);
    }
}
