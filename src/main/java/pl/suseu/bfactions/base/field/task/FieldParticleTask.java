package pl.suseu.bfactions.base.field.task;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            Region closest = regionRepository.nearestRegion(location);

            if (closest == null) {
                continue;
            }

            Field field = closest.getGuild().getField();
            Set<Location> border = field.borderInRange(location, 10);
            Set<Location> dome = field.domeInRange(location, 30);

            Particle.DustOptions red = new Particle.DustOptions(Color.RED, 1);
            Particle.DustOptions green = new Particle.DustOptions(Color.GREEN, 1);
            Particle.DustOptions blue = new Particle.DustOptions(Color.BLUE, 1);

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                for (Location particle : border) {
                    player.spawnParticle(Particle.REDSTONE, particle, 1, red);
                }
                for (Location particle : dome) {
                    player.spawnParticle(Particle.REDSTONE, particle, 1, blue);
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
