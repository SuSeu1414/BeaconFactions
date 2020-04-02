package pl.suseu.bfactions.util;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class GeometryUtil {

    public static Set<Location> line(Location from, Location to, double density) {
        Set<Location> locations = new HashSet<>();

        int n = (int) Math.round(density * from.distance(to));
        double x = from.getX(), y = from.getY(), z = from.getZ();
        double dX = (to.getX() - x) / n;
        double dY = (to.getY() - y) / n;
        double dZ = (to.getZ() - z) / n;

        locations.add(from);
        for (int i = 1; i < n; i++) {
            locations.add(new Location(from.getWorld(), x + i * dX, y + i * dY, z + i * dZ));
        }

        return locations;
    }
}
