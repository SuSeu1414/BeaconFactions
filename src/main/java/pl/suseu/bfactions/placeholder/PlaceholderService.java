package pl.suseu.bfactions.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;

import java.util.UUID;
import java.util.stream.Collectors;

public class PlaceholderService implements Runnable, Listener {


    private final BFactions plugin;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;
    private final UserPlaceholders userPlaceholders = new UserPlaceholders();
    private long counter = 0;

    public PlaceholderService(BFactions plugin) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.regionRepository = plugin.getRegionRepository();
    }

    @Override
    public void run() {
        updateGuilds();
        updatePlayers();
        counter++;
    }

    private void updateGuilds() {
        if (counter % (20 * 5) == 0) { // every 5 seconds
            for (Guild guild : guildRepository.getGuilds()) {
                guild.updatePlaceholders();
                guild.updateHologram();
            }
        }
    }

    private void updatePlayers() {
        if (counter % (20 * 5) == 0) { // every 5 seconds
            for (UUID uuid : Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet())) {
                User user = this.userRepository.getUser(uuid);
                this.userPlaceholders.updatePlayerRegion(uuid, user.getCurrentRegion());
            }
        }
        if (counter % (20 * 60) == 0) { // every 60 seconds
            this.userPlaceholders.removeOfflinePlayers();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        User user = this.userRepository.getUser(uuid);
        this.userPlaceholders.updatePlayerRegion(uuid, user.getCurrentRegion());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.getUserPlaceholders().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.getUserPlaceholders().removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRegionChange(PlayerRegionChangeEvent event) {
        this.getUserPlaceholders().updatePlayerRegion(event.getPlayer().getUniqueId(), event.getRegion());
    }

    public UserPlaceholders getUserPlaceholders() {
        return userPlaceholders;
    }
}
