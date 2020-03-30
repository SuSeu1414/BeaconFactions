package pl.suseu.bfactions.base.region;

import pl.suseu.bfactions.BFactions;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RegionRepository {

    private BFactions plugin;

    private Set<Region> regions = ConcurrentHashMap.newKeySet();

    public RegionRepository(BFactions plugin) {
        this.plugin = plugin;
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
