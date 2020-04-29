package pl.suseu.bfactions.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;

public class InviteMemberCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;

    public InviteMemberCommandExecutor(BFactions plugin) {
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
        User user = this.userRepository.getUser(player.getUniqueId());
        Region region = this.regionRepository.nearestRegion(player.getLocation());

        if (region == null || !region.isInDome(player.getLocation())) {
            this.lang.sendMessage("not-in-region", player);
            return;
        }

        Guild guild = region.getGuild();
        if (!guild.hasPermission(user, GuildPermission.MANAGE, true)) {
            this.lang.sendMessage("no-guild-permissions", player);
            return;
        }

        if (args.size() != 1) {
            command.sendUsage(sender, label);
        }

        String name = args.get(0);

        User toInviteUser = this.userRepository.getUserByName(name);
        Player toInvitePlayer = Bukkit.getPlayer(name);
        if (toInviteUser == null) {
            this.lang.sendMessage("user-does-not-exist", player);
            return;
        }

        if (guild.getInvitedMembers().contains(toInviteUser)) {
            this.lang.sendMessage("user-already-invited", player);
            return;
        }

        if (guild.isMember(toInviteUser)) {
            this.lang.sendMessage("user-already-member", player);
            return;
        }

        guild.addInvitedMember(toInviteUser);
        this.lang.sendMessage("you-have-been-invited-to-guild", toInvitePlayer,
                "%guild%", guild.getName());
        this.lang.sendMessage("user-invited-to-guild", player);
    }
}
