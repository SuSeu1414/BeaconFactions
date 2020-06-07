package pl.suseu.bfactions.command.cmds;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.List;
import java.util.stream.Collectors;

public class SetMotdCommandExecutor implements BCommandExecutor {

    private final LangAPI lang;
    private final UserRepository userRepository;
    private final EventWaiter eventWaiter;

    public SetMotdCommandExecutor(BFactions plugin) {
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.eventWaiter = plugin.getEventWaiter();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = ((Player) sender);
        User user = this.userRepository.getUser(player.getUniqueId());
        Guild guild = user.getOwnedGuild();
        if (guild == null) {
            this.lang.sendMessage("you-do-not-own-any-guild", player);
            return;
        }
        Region region = guild.getRegion();

        if (region == null || !region.isInside(player.getLocation())) {
            this.lang.sendMessage("not-in-region", player);
            return;
        }

        if (args.size() < 1) {
            command.sendUsage(sender, label);
            return;
        }

        String type = args.get(0);
        if (!type.equalsIgnoreCase("entry")
                && !type.equalsIgnoreCase("exit")) {
            command.sendUsage(sender, label);
            return;
        }

        String motd = args.stream().skip(1).collect(Collectors.joining(" "));
        if (type.equalsIgnoreCase("entry")) {
            guild.setEntryMOTD(ChatColor.translateAlternateColorCodes('&', motd));
        } else if (type.equalsIgnoreCase("exit")) {
            guild.setExitMOTD(ChatColor.translateAlternateColorCodes('&', motd));
        }
        this.lang.sendMessage("guild-motd-set", player);
    }
}
