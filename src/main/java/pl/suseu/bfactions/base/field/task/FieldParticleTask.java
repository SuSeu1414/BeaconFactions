package pl.suseu.bfactions.base.field.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.settings.Settings;

import java.util.HashSet;
import java.util.Set;

public class FieldParticleTask implements Runnable {

    private final Settings settings;
    private final RegionRepository regionRepository;
    private final BFactions plugin;

    public FieldParticleTask(BFactions plugin) {
        this.plugin = plugin;
        this.regionRepository = this.plugin.getRegionRepository();
        this.settings = plugin.getSettings();
    }

    @Override
    public void run() {
        double density = settings.fieldParticleDensity;
        double range = settings.fieldParticleRange;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            Region closest = regionRepository.nearestRegion(location);
            Set<Location> particles = locationsInRange(closest.walls(density), location, range);

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                for (Location particle : particles) {
                    player.spawnParticle(Particle.REDSTONE, particle, 1);
                }
            });

        }
    }

    private Set<Location> locationsInRange(Set<Location> locations, Location location, double range) {
        Set<Location> toReturn = new HashSet<>();

        for (Location l : locations) {
            if (l.distance(location) < range) {
                toReturn.add(l);
            }
        }

        return toReturn;
    }
}
