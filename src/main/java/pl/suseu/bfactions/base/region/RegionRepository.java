package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import pl.suseu.bfactions.BFactions;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RegionRepository {

    private final BFactions plugin;

    private final Set<Region> regions = ConcurrentHashMap.newKeySet();

    public RegionRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public Region nearestRegion(Location location) {
        double min = Double.MAX_VALUE;
        Region nearest = null;

        for (Region region : getRegions()) {
            try {
                double dist = region.getCenter().distance(location);
                if (dist < min) {
                    min = dist;
                    nearest = region;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        return nearest;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public void removeRegion(Region region) {
        regions.remove(region);
    }

    /**
     * @return a copy of regions set
     */
    public Set<Region> getRegions() {
        return new HashSet<>(regions);
    }
}
