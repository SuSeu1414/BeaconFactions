package pl.suseu.bfactions.command.cmds.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;
import java.util.UUID;

public class AKickCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;

    public AKickCommandExecutor(BFactions plugin) {
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

        if (args.size() == 0) {
            command.sendUsage(sender, label);
            return;
        }

        User toKick;

        String arg = args.get(0);

        if (arg.length() > 16) {
            try {
                UUID uuid = UUID.fromString(arg);
                toKick = this.userRepository.getUser(uuid);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("Invalid uuid!");
                return;
            }
        } else {
            Player plr = Bukkit.getPlayer(arg);
            if (plr == null) {
                sender.sendMessage("Player is not online!");
                return;
            }
            toKick = this.userRepository.getUser(plr.getUniqueId());
        }

        Player player = ((Player) sender);
        Region region = this.regionRepository.nearestRegion(player.getLocation());

        if (region == null || !region.isInDome(player.getLocation())) {
            this.lang.sendMessage("not-in-region", player);
            return;
        }

        Guild guild = region.getGuild();

        if (!guild.isMember(toKick)) {
            sender.sendMessage("Player is not a guild member!");
            return;
        }

        if (guild.isOwner(toKick)) {
            sender.sendMessage("You cannot kick owner!");
            return;
        }

        guild.removeMember(toKick);
        sender.sendMessage("Player has been kicked from the guild");
    }
}
