package pl.suseu.bfactions.base.region.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;
import pl.suseu.bfactions.base.user.User;

public class UserLocationTask implements Runnable {

    private final BFactions plugin;

    public UserLocationTask(BFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = plugin.getUserRepository().getUser(player.getUniqueId());
            Region oldRegion = user.getCurrentRegion();
            Region nearestRegion = plugin.getRegionRepository().nearestRegion(player.getLocation());
            Region newRegion = plugin.getRegionRepository().nearestRegion(player.getLocation());
            Location oldLocation = user.getCurrentLocation();
            Location newLocation = player.getLocation();

            if (nearestRegion != user.getNearestRegion()) {
                user.setNearestRegion(nearestRegion);
            }

            if (newRegion != null && !newRegion.isInDome(newLocation)) {
                newRegion = null;
            }

            if (oldLocation == null || newLocation.distance(oldLocation) != 0) {
                oldLocation = newLocation;
                user.setCurrentLocation(newLocation);
            }

            if (oldRegion != newRegion) {
                user.setCurrentRegion(newRegion);
                user.setLastRegionChange(System.currentTimeMillis());
                PlayerRegionChangeEvent event = new PlayerRegionChangeEvent(player, user, newRegion, oldLocation, newLocation);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    Location finalOldLocation = oldLocation;
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(finalOldLocation));
                }
            }
        }
    }
}
