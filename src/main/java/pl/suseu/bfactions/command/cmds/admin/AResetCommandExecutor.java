package pl.suseu.bfactions.command.cmds.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;
import java.util.UUID;

public class AResetCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;

    public AResetCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.regionRepository = plugin.getRegionRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        Guild guild = null;
        if (args.size() == 0) {
            if (!(sender instanceof Player)) {
                this.lang.sendMessage("player-only", sender);
                return;
            }

            Player player = ((Player) sender);
            Region region = this.regionRepository.nearestRegion(player.getLocation());

            if (region == null || !region.isInDome(player.getLocation())) {
                this.lang.sendMessage("not-in-region", player);
                return;
            }

            guild = region.getGuild();
        } else if (args.size() == 1) {
            try {
                UUID uuid = UUID.fromString(args.get(0));
                guild = this.guildRepository.getGuild(uuid);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("Invalid uuid!");
                return;
            }
        }

        if (guild == null) {
            sender.sendMessage("Guild does not exist!");
            return;
        }
        guild.getRegion().setTier(this.plugin.getSettings().regionTiers.get(0));
        guild.getField().setTier(this.plugin.getSettings().fieldTiers.get(0));
        guild.getField().recalculate();
        sender.sendMessage("Progress reset!");
    }
}
