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
import pl.suseu.bfactions.base.region.RegionType;
import pl.suseu.bfactions.base.user.User;
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
        this.settings = this.plugin.getSettings();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            Region closest = regionRepository.nearestRegion(location);

            if (closest == null) {
                continue;
            }

            User user = plugin.getUserRepository().getUser(player.getUniqueId());
            Field field = closest.getGuild().getField();

            Particle.DustOptions borderOptions;
            if (closest.getGuild().isMember(user)) {
                borderOptions = new Particle.DustOptions(Color.GREEN, 1);
            } else if (closest.getGuild().getInvitedMembers().contains(user)) {
                borderOptions = new Particle.DustOptions(Color.PURPLE, 1);
            } else {
                borderOptions = new Particle.DustOptions(Color.RED, 1);
            }
            Particle.DustOptions domeOptions =
                    new Particle.DustOptions(Color.BLUE, 1);

            if (field.getGuild().getRegion().getTier().getRegionType() == RegionType.DOME) {
                Set<Location> dome = field.domeInRange(location, settings.fieldDomeDistance);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    for (Location particle : dome) {
                        player.spawnParticle(Particle.REDSTONE, particle, 1, domeOptions);
                    }
                });

                if (closest.isInside(location)) {
                    continue;
                }

                Set<Location> border = field.borderInRange(location, settings.fieldBorderDistance);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    for (Location particle : border) {
                        player.spawnParticle(Particle.REDSTONE, particle, 1, borderOptions);
                    }
                });
            }

            if (field.getGuild().getRegion().getTier().getRegionType() == RegionType.ROLLER) {
                Set<Location> border = field.borderInRange(location, settings.fieldDomeDistance);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    for (Location particle : border) {
                        player.spawnParticle(Particle.REDSTONE, particle, 1, borderOptions);
                    }
                });
            }
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
