package pl.suseu.bfactions.util;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class GeometryUtil {

    public static Set<Location> line(Location from, Location to, double density) {
        Set<Location> line = new HashSet<>();

        int n = (int) Math.round(density * from.distance(to));
        double x = from.getX(), y = from.getY(), z = from.getZ();
        double dX = (to.getX() - x) / n;
        double dY = (to.getY() - y) / n;
        double dZ = (to.getZ() - z) / n;

        line.add(from);
        for (int i = 1; i <= n; i++) {
            line.add(new Location(from.getWorld(), x + i * dX, y + i * dY, z + i * dZ));
        }

        return line;
    }

    public static Set<Location> circle(Plane plane, Location center, double radius, double density) {
        return arc(plane, center, radius, density, 0, Math.PI * 2);
    }

    public static Set<Location> arc(Plane plane, Location center, double radius, double density, double r1, double r2) {
        Set<Location> arc = new HashSet<>();

        int n = (int) Math.ceil(2 * Math.PI * radius * density);
        double rad = 2 * Math.PI / n;
        double x = 0, y = 0, z = 0;

        for (double angle = r1; angle < r2; angle += rad) {
            switch (plane) {
                case X:
                    y = Math.cos(angle) * radius;
                    z = Math.sin(angle) * radius;
                    break;
                case Y:
                    x = Math.cos(angle) * radius;
                    z = Math.sin(angle) * radius;
                    break;
                case Z:
                    x = Math.cos(angle) * radius;
                    y = Math.sin(angle) * radius;
                    break;
            }
            arc.add(center.clone().add(x, y, z));
        }

        return arc;
    }

    public static Set<Location> dome(Location center, double radius, double density) {
        Set<Location> dome = new HashSet<>();

        Set<Location> base = arc(Plane.Z, center, radius, density, 0, Math.PI / 2);
        for (Location l : base) {
            double r = Math.abs(center.getX() - l.getX());
            double y = Math.abs(center.getY() - l.getY());

            dome.addAll(circle(Plane.Y, center.clone().add(0, y, 0), r, density));
        }

        return dome;
    }

    public static Set<Location> roller(Location center, double radius, double minY, double maxY, double density) {
        Set<Location> roller = new HashSet<>();

        Location centerY0 = center.clone();
        centerY0.setY(minY);

        Set<Location> base = circle(Plane.Y, centerY0, radius, density);
        for (Location l1 : base) {
            Location l2 = l1.clone();
            l2.setY(maxY);

            roller.addAll(line(l1, l2, density));
        }

        return roller;
    }

    public static Set<Location> corners(Location block) {
        block = block.toCenterLocation();
        Set<Location> corners = new HashSet<>();
        for (double x = -0.49; x <= 0.49; x += 0.98) {
            for (double y = -0.49; y <= 0.49; y += 0.98) {
                for (double z = -0.49; z <= 0.49; z += 0.98) {
                    corners.add(block.clone().add(x, y, z));
                }
            }
        }

        return corners;
    }

    public enum Plane {
        X,
        Y,
        Z
    }
}
