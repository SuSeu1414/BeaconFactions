package pl.suseu.bfactions.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;

// this command exist only for tests purpose
public class TestAddMemberCommandExecutor implements BCommandExecutor {

    private BFactions plugin;

    public TestAddMemberCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, BCommand command, List<String> args) {
        User user = plugin.getUserRepository().getUser(((Player) sender).getUniqueId());
        Player target = Bukkit.getPlayer(args.get(0));
        Bukkit.broadcastMessage(target.getName());
        User userTarget = plugin.getUserRepository().getUser(target.getUniqueId());
        user.getOwnedGuild().addMember(user);
        plugin.getLang().sendMessage("member-added", sender);
    }
}
