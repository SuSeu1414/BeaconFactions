package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.RegionTier;

import java.util.UUID;

public class Region {

    private final UUID uuid;
    private Guild guild;

    private Location center;
    private RegionTier tier;

    public Region(UUID uuid, Location center, RegionTier tier) {
        this.uuid = uuid;
        this.center = center;
        this.tier = tier;
    }

    public void teleportToSafety(Entity entity) {
        Location playerLocation = entity.getLocation();
        Location center = this.getCenter().clone();
        double r1 = this.flatDistance(playerLocation);
        double r2 = this.getSize() + 1;
        double x1 = playerLocation.getX() - center.getX();
        double z1 = playerLocation.getZ() - center.getZ();
        double x2 = center.getX() + (x1 * r2) / r1;
        double z2 = center.getZ() + (z1 * r2) / r1;
        World world = entity.getWorld();
        Location tpTarget = new Location(world, x2, 0, z2);
        double y;
        int i = 0;
        while (true) {
            y = world.getHighestBlockYAt(tpTarget);
            if (y != 0) {
                tpTarget.setY(y + 1);
                entity.teleport(tpTarget);
                return;
            }
            if (i++ > 3) {
                entity.teleport(world.getSpawnLocation());
                return;
            }
        }
    }

    public boolean isInPerimeter(Location location) {
        return this.flatDistance(location) < tier.getRadius();
    }

    public boolean isInside(Location location) {
        if (tier.getRegionType() == RegionType.ROLLER) {
            return isInPerimeter(location);
        }
        if (location.getBlockY() < center.getBlockY() - 1) {
            return false;
        }
        if (location.distance(center) > tier.getRadius()) {
            return false;
        }
        return true;
    }

    public double flatDistance(Location location) {
        if (location == null) {
            return Double.NaN;
        }
        Location l1 = this.getCenter().clone();
        Location l2 = location.clone();
        l1.setY(0);
        l2.setY(0);
        try {
            return l2.distance(l1);
        } catch (IllegalArgumentException exception) {
            return Double.NaN;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Location getCenter() {
        return center.clone();
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public int getSize() {
        return tier.getRadius();
    }

    public RegionTier getTier() {
        return tier;
    }

    public void setTier(RegionTier tier) {
        this.tier = tier;
    }

    public World getWorld() {
        return this.getCenter().getWorld();
    }
}
