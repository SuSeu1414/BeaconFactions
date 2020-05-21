package pl.suseu.bfactions.base.region.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.event.PlayerEnterRegionEvent;
import pl.suseu.bfactions.base.region.event.PlayerLeaveRegionEvent;
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

            if (newRegion != null && !newRegion.isInside(newLocation.toBlockLocation())) {
                newRegion = null;
            }

            if (oldLocation == null
                    || oldLocation.getWorld() == null
                    || !oldLocation.getWorld().equals(newLocation.getWorld())
                    || newLocation.distance(oldLocation) != 0) {
                oldLocation = newLocation;
                user.setCurrentLocation(newLocation);
            }

            if (oldRegion != newRegion) {
                user.setCurrentRegion(newRegion);
                user.setLastRegionChange(System.currentTimeMillis());
                PlayerRegionChangeEvent playerRegionChangeEvent = new PlayerRegionChangeEvent(player, user, newRegion, oldLocation, newLocation);
                plugin.getServer().getPluginManager().callEvent(playerRegionChangeEvent);
                boolean cancel = playerRegionChangeEvent.isCancelled();
                if (newRegion != null) {
                    PlayerEnterRegionEvent playerEnterRegionEvent = new PlayerEnterRegionEvent(player, user, newRegion, oldLocation, newLocation);
                    plugin.getServer().getPluginManager().callEvent(playerEnterRegionEvent);
                    if (playerEnterRegionEvent.isCancelled()) {
                        cancel = true;
                    }
                } else {
                    PlayerLeaveRegionEvent playerLeaveRegionEvent = new PlayerLeaveRegionEvent(player, user, oldRegion, oldLocation, newLocation);
                    plugin.getServer().getPluginManager().callEvent(playerLeaveRegionEvent);
                    if (playerLeaveRegionEvent.isCancelled()) {
                        cancel = true;
                    }
                }

                if (cancel) {
                    Location finalOldLocation = oldLocation;
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(finalOldLocation));
                }
            }
        }
    }
}
