package pl.suseu.bfactions.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.action.KickMemberAction;

import java.util.List;

public class KickCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;

    public KickCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.regionRepository = plugin.getRegionRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = ((Player) sender);
        User user = userRepository.getUser(player.getUniqueId());
        Guild guild = user.getOwnedGuild();
        if (guild == null) {
            lang.sendMessage("you-do-not-own-any-guild", player);
            return;
        }

        if (args.size() == 0) {
            command.sendUsage(sender, label);
            return;
        }

        String arg = args.get(0);
        Player plr = Bukkit.getPlayer(arg);
        if (plr == null) {
            lang.sendMessage("player-offline", player);
            return;
        }

        User toKick = this.userRepository.getUser(plr.getUniqueId());
        if (!guild.isMember(toKick)) {
            lang.sendMessage("player-is-not-a-member", player);
            return;
        }

        new KickMemberAction(plugin, guild, toKick).execute(player);
    }
}
