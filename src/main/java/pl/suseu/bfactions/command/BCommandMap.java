package pl.suseu.bfactions.command;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.cmds.*;
import pl.suseu.bfactions.command.cmds.admin.AManageCommandExecutor;
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
    private final KickCommandExecutor kickCommandExecutor;
    private final AManageCommandExecutor aManageCommandExecutor;
    private final BCommandExecutor deleteCommandExecutor;
    private final RenameCommandExecutor renameCommandExecutor;
    private final WhoOnlineCommandExecutor whoOnlineCommandExecutor;
    private final ListCommandExecutor listCommandExecutor;
    private final HomeCommandExecutor homeCommandExecutor;
    private final SetHomeCommandExecutor setHomeCommandExecutor;

    public BCommandMap(BFactions plugin) {
        this.plugin = plugin;

        this.testCommandExecutor = new TestCommandExecutor();
        this.itemSetCommandExecutor = new ItemSetCommandExecutor(this.plugin);
        this.itemGiveCommandExecutor = new ItemGiveCommandExecutor(this.plugin);
        this.toggleGuiDebugCommandExecutor = new ToggleGuiDebugCommandExecutor(this.plugin);
        this.inviteMemberCommandExecutor = new InviteMemberCommandExecutor(this.plugin);
        this.leaveCommandExecutor = new LeaveCommandExecutor(this.plugin);
        this.aResetCommandExecutor = new AResetCommandExecutor(this.plugin);
        this.kickCommandExecutor = new KickCommandExecutor(this.plugin);
        this.deleteCommandExecutor = new DeleteCommandExecutor(this.plugin);
        this.aManageCommandExecutor = new AManageCommandExecutor(this.plugin);
        this.renameCommandExecutor = new RenameCommandExecutor(this.plugin);
        this.whoOnlineCommandExecutor = new WhoOnlineCommandExecutor(this.plugin);
        this.listCommandExecutor = new ListCommandExecutor(this.plugin);
        this.homeCommandExecutor = new HomeCommandExecutor(this.plugin);
        this.setHomeCommandExecutor = new SetHomeCommandExecutor(this.plugin);
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
                .setPermission("bfactions.command.false")
                .setExecutor(this.testCommandExecutor)
                .addAlias("t")
                .addAlias("tst")
                .setUsage("test <arg>")
                .build(this.commands);

        new BCommandBuilder("setitem")
                .setPermission("bfactions.command.setitem")
                .setNeedsArguments(true)
                .setUsage("setitem <item-id>")
                .setExecutor(this.itemSetCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("giveitem")
                .setPermission("bfactions.command.giveitem")
                .setNeedsArguments(true)
                .setUsage("giveitem <item-id> <optional player>")
                .setExecutor(itemGiveCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("debuggui")
                .setPermission("bfactions.command.debuggui")
                .setExecutor(toggleGuiDebugCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("invite")
                .setPermission("bfactions.command.invite")
                .setExecutor(this.inviteMemberCommandExecutor)
                .setNeedsArguments(true)
                .setUsage("invite <player>")
                .build(this.commands);

        new BCommandBuilder("leave")
                .addAlias("quit")
                .setPermission("bfactions.command.quit")
                .setExecutor(this.leaveCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("reset")
                .setPermission("bfactions.command.reset")
                .setExecutor(this.aResetCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("delete")
                .setPermission("bfactions.command.delete")
                .setExecutor(this.deleteCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("kick")
                .setPermission("bfactions.command.kick")
                .setUsage("kick <player>")
                .setNeedsArguments(true)
                .setExecutor(this.kickCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("rename")
                .setPermission("bfactions.command.rename")
                .setUsage("rename <new name>")
                .setNeedsArguments(true)
                .setExecutor(this.renameCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("manage")
                .setPermission("bfactions.command.manage")
                .setAsync(true)
                .setNeedsArguments(true)
                .setUsage("manage <member>")
                .setExecutor(this.aManageCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("who")
                .setPermission("bfactions.command.who")
                .setAsync(true)
                .setUsage("who <member>")
                .setExecutor(this.whoOnlineCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("list")
                .setPermission("bfactions.command.list")
                .setAsync(true)
                .setUsage("list <page>")
                .setExecutor(this.listCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("sethome")
                .setPermission("bfactions.command.sethome")
                .setExecutor(this.setHomeCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("home")
                .setPermission("bfactions.command.home")
                .setExecutor(this.homeCommandExecutor)
                .build(this.commands);
    }

    public Set<BCommand> getCommands() {
        return new HashSet<>(this.commands);
    }
}
