package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;

public class SetHomeCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;

    public SetHomeCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
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

        guild.setHome(player.getLocation());
        this.lang.sendMessage("guild-home-set", player);
    }
}
