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
    private final TransferCommandExecutor transferCommandExecutor;
    private final HelpCommandExecutor helpCommandExecutor;
    private final SetMotdCommandExecutor setMotdCommandExecutor;
    private final PotatoCommandExecutor potatoCommandExecutor;
    private final MapCommandExecutor mapCommandExecutor;
    private final AcceptCommandExecutor acceptCommandExecutor;

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
        this.transferCommandExecutor = new TransferCommandExecutor(this.plugin);
        this.helpCommandExecutor = new HelpCommandExecutor(this.plugin, this);
        this.setMotdCommandExecutor = new SetMotdCommandExecutor(this.plugin);
        this.potatoCommandExecutor = new PotatoCommandExecutor(this.plugin);
        this.mapCommandExecutor = new MapCommandExecutor(this.plugin);
        this.acceptCommandExecutor = new AcceptCommandExecutor(this.plugin);
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
                .setDescription("Installs held item as a custom item of that id")
                .setPermission("bfactions.command.setitem")
                .setNeedsArguments(true)
                .setUsage("setitem <item-id>")
                .setExecutor(this.itemSetCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("giveitem")
                .setPermission("bfactions.command.giveitem")
                .setDescription("Gives the custom item of that id")
                .setNeedsArguments(true)
                .setUsage("giveitem <item-id> <player>")
                .setExecutor(itemGiveCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("debuggui")
                .setPermission("bfactions.command.debuggui")
                .setDescription("Toggles the GUI debug mode, which shows IDs of custom items in every GUI")
                .setAsync(true)
                .setExecutor(toggleGuiDebugCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("invite")
                .setPermission("bfactions.command.invite")
                .setDescription("Invite a player to the guild you are standing in")
                .setExecutor(this.inviteMemberCommandExecutor)
                .setAsync(true)
                .setNeedsArguments(true)
                .setUsage("invite <player>")
                .build(this.commands);

        new BCommandBuilder("leave")
                .addAlias("quit")
                .setDescription("Leave the guild you are standing in")
                .setPermission("bfactions.command.quit")
                .setAsync(true)
                .setExecutor(this.leaveCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("reset")
                .setPermission("bfactions.command.reset")
                .setDescription("Reset the guild you are standing in")
                .setAsync(true)
                .setExecutor(this.aResetCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("delete")
                .setPermission("bfactions.command.delete")
                .setDescription("Delete the guild that you own")
                .setExecutor(this.deleteCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("kick")
                .setPermission("bfactions.command.kick")
                .setDescription("Kick a player from the guild that you own")
                .setUsage("kick <player>")
                .setAsync(true)
                .setNeedsArguments(true)
                .setExecutor(this.kickCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("rename")
                .setPermission("bfactions.command.rename")
                .setDescription("Rename the guild that you own")
                .setAsync(true)
                .setUsage("rename <new-name>")
                .setNeedsArguments(true)
                .setExecutor(this.renameCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("manage")
                .setPermission("bfactions.command.manage")
                .setDescription("Open the GUI of an another guild")
                .setAsync(true)
                .setNeedsArguments(true)
                .setUsage("manage <member>")
                .setExecutor(this.aManageCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("who")
                .setPermission("bfactions.command.who")
                .setDescription("List online players of a guild")
                .setAsync(true)
                .setUsage("who <member>")
                .setExecutor(this.whoOnlineCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("list")
                .setPermission("bfactions.command.list")
                .setDescription("List all the guilds")
                .setAsync(true)
                .setUsage("list <page>")
                .setExecutor(this.listCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("sethome")
                .setPermission("bfactions.command.sethome")
                .setDescription("Set your location as the home of your guild")
                .setExecutor(this.setHomeCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("home")
                .setPermission("bfactions.command.home")
                .setDescription("Teleport to the home location of your guild")
                .setExecutor(this.homeCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("transfer")
                .setPermission("bfactions.command.transfer")
                .setDescription("Transfer the ownership of your guild to another player")
                .setUsage("transfer <player>")
                .setAsync(true)
                .setNeedsArguments(true)
                .setExecutor(this.transferCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("help")
                .setPermission("bfactions.command.help")
                .setDescription("List of all the commands")
                .setAsync(true)
                .setUsage("help <page>")
                .setExecutor(this.helpCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("setmotd")
                .setPermission("bfactions.command.setmotd")
                .setDescription("Set the MOTD in your guild")
                .setUsage("setmotd entry/exit <motd>")
                .setNeedsArguments(true)
                .setExecutor(this.setMotdCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("potato")
                .setPermission("bfactions.command.potato")
                .setDescription("Decreases the amount of particles being displayed")
                .setAsync(true)
                .setExecutor(this.potatoCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("map")
                .setPermission("bfactions.command.map")
                .setDescription("todo")
//                .setAsync(true)
                .setExecutor(this.mapCommandExecutor)
                .build(this.commands);

        new BCommandBuilder("accept")
                .setPermission("bfactions.command.accept")
                .setDescription("Accept a guild invitation")
                .setAsync(true)
                .setExecutor(this.acceptCommandExecutor)
                .build(this.commands);
    }

    public Set<BCommand> getCommands() {
        return new HashSet<>(this.commands);
    }
}
