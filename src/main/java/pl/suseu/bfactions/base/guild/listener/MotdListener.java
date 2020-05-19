package pl.suseu.bfactions.base.guild.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;

public class MotdListener implements Listener {

    private final BFactions plugin;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final LangAPI langAPI;

    public MotdListener(BFactions plugin) {
        this.plugin = plugin;
        this.regionRepository = plugin.getRegionRepository();
        this.userRepository = plugin.getUserRepository();
        this.langAPI = plugin.getLang();
    }

    @EventHandler
    public void onRegionChange(PlayerRegionChangeEvent event) {
        Location location = event.getTo();
        Region closest = this.regionRepository.nearestRegion(location);

        if (closest == null) {
            return;
        }

        Guild guild = closest.getGuild();
        Player player = event.getPlayer();
        User user = this.userRepository.getUser(player.getUniqueId());
        if (!guild.isMember(user)) {
            return;
        }

        String motd = event.getRegion() != null ? guild.getEntryMOTD() : guild.getExitMOTD();

        if (motd != null) {
            if (event.getRegion() != null) {
                langAPI.sendMessage("guild-motd-entry", player, "%motd%", motd, "%guild%", guild.getName());
            } else {
                langAPI.sendMessage("guild-motd-exit", player, "%motd%", motd, "%guild%", guild.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Location location = event.getPlayer().getLocation();
        Region closest = this.regionRepository.nearestRegion(location);

        if (closest == null) {
            return;
        }

        Guild guild = closest.getGuild();
        Player player = event.getPlayer();
        User user = this.userRepository.getUser(player.getUniqueId());
        if (!guild.isMember(user)) {
            return;
        }

        String motd = guild.getEntryMOTD();

        if (motd != null) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                langAPI.sendMessage("guild-motd-entry", player, "%motd%", motd, "%guild%", guild.getName());
            }, 1);
        }
    }
}
