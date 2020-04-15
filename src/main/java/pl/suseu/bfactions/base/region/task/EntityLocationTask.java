package pl.suseu.bfactions.base.region.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.event.EntityRegionChangeEvent;
import pl.suseu.bfactions.util.EntityUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLocationTask implements Runnable {

    private final BFactions plugin;

    private final Map<UUID, Location> locationMap = new ConcurrentHashMap<>();
    private final Map<UUID, Region> regionMap = new ConcurrentHashMap<>();

    public EntityLocationTask(BFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Set<UUID> toRemove = new HashSet<>();
        for (UUID uuid : locationMap.keySet()) {
            if (EntityUtil.getByUUID(uuid) == null) {
                toRemove.add(uuid);
            }
        }

        for (UUID uuid : regionMap.keySet()) {
            if (EntityUtil.getByUUID(uuid) == null) {
                toRemove.add(uuid);
            }
        }

        for (UUID uuid : toRemove) {
            locationMap.remove(uuid);
            regionMap.remove(uuid);
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) {
                    continue;
                }

                UUID uuid = entity.getUniqueId();
                Region oldRegion = regionMap.get(uuid);
                Region newRegion = plugin.getRegionRepository().nearestRegion(entity.getLocation());
                Location oldLocation = locationMap.get(uuid);
                Location newLocation = entity.getLocation();

                if (newRegion != null && !newRegion.isInDome(newLocation)) {
                    newRegion = null;
                }

                if (oldLocation == null || newLocation.distance(oldLocation) != 0) {
                    oldLocation = newLocation;
                    locationMap.put(uuid, newLocation);
                }

                if (oldRegion != newRegion) {
                    regionMap.put(uuid, newRegion);
                    EntityRegionChangeEvent event = new EntityRegionChangeEvent(entity, newRegion, oldLocation, newLocation);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        Location finalOldLocation = oldLocation;
                        plugin.getServer().getScheduler().runTask(plugin, () -> entity.teleport(finalOldLocation));
                    }
                }
            }
        }
    }
}
