package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.factory.paginator.GuildPaginatorFactory;

import java.util.List;

public class AcceptCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final LangAPI lang;

    public AcceptCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.lang = plugin.getLang();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = (Player) sender;
        User user = userRepository.getUser(player.getUniqueId());
        new GuildPaginatorFactory(this.plugin)
                .openGuildsGui(player, u -> true, guild -> guild.getInvitedMembers().contains(user),
                        clickedGuild -> {
                            clickedGuild.addMember(user);
                            clickedGuild.removeInvitedMember(user);
                            lang.sendMessage("accepted-invite", player, "%guild%", clickedGuild.getName());
                        });
    }
}
