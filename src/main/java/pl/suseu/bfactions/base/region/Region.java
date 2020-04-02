package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import org.bukkit.World;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.util.GeometryUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Region {

    private final UUID uuid;
    private Guild guild;

    private Location center;
    private int size;

    private Location min;
    private Location max;

    public Region(UUID uuid, Location center, int size) {
        this.uuid = uuid;
        this.center = center;
        this.size = size;
    }

    public Set<Location> walls(double density) {
        Set<Location> walls = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            Location corner1 = corners()[i];

            if (i == 3) {
                i = -1;
            }

            Location corner2 = corners()[i + 1];

            int n = (int) Math.round(density * 255);
            double dY = (float) 255 / n;

            for (int j = 0; j < n; j++) {
                corner1.setY(dY * n);
                corner2.setY(dY * n);
                walls.addAll(GeometryUtil.line(corner1, corner2, density));
            }
        }

        return walls;
    }

    public Location[] corners() {
        Location[] corners = new Location[4];

        corners[0] = this.center.clone().add(size, 0, size);
        corners[1] = this.center.clone().add(size, 0, -size);
        corners[2] = this.center.clone().add(-size, 0, -size);
        corners[3] = this.center.clone().add(-size, 0, size);

        return corners;
    }

    public void recalculate() {
        World world = this.center.getWorld();
        int x = this.center.getBlockX();
        int z = this.center.getBlockZ();

        this.min = new Location(world, x - this.size, 0, z - this.size);
        this.max = new Location(world, x + this.size, 256, z + this.size);
    }

    public boolean isIn(Location location) {
        if (location == null) {
            return false;
        }

        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        if (!world.equals(center.getWorld())) {
            return false;
        }

        int x = location.getBlockX();
        if (min.getBlockX() >= x || max.getBlockX() <= x) {
            return false;
        }

        int y = location.getBlockY();
        if (min.getBlockY() >= y || max.getBlockY() <= y) {
            return false;
        }

        int z = location.getBlockZ();
        if (min.getBlockZ() >= z || max.getBlockZ() <= z) {
            return false;
        }

        return true;
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
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }
}
