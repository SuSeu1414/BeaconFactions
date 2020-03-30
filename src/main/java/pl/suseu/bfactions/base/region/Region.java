package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import org.bukkit.World;
import pl.suseu.bfactions.base.guild.Guild;

public class Region {

    private Guild guild;

    private Location center;
    private int size;

    private Location min;
    private Location max;

    public Region(Guild guild, Location center, int size) {
        this.guild = guild;
        this.center = center;
        this.size = size;
    }

    public void recalculate() {
        World world = this.center.getWorld();
        int x = this.center.getBlockX();
        int z = this.center.getBlockZ();

        this.min = new Location(world, x - this.size, 0, z - this.size);
        this.max = new Location(world, x + this.size, 256, z + this.size);
    }

    public boolean inIn(Location location) {
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
}
