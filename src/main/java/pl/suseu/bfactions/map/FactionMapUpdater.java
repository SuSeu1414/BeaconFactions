package pl.suseu.bfactions.map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

public class FactionMapUpdater implements Runnable {

    private final BFactions plugin;

    public FactionMapUpdater(BFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            User user = this.plugin.getUserRepository().getOnlineUser(player.getUniqueId());
            if (user == null) {
                continue;
            }

            this.recalculateMap(user, player.getLocation(), 3);
        }
    }

    private void recalculateMap(User user, Location location, int scale) {
        if (user.isRecalculatingMap()) {
            return;
        }
        user.setRecalculatingMap(true);
        location.add(-64 * scale, 0, -64 * scale);
        BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB_PRE);

        Graphics2D graphics = img.createGraphics();

        Set<Region> regions = this.plugin.getRegionRepository().regionsInRange(location.clone().add(64 * scale, 0, 64 * scale), 1280 * scale);
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                Color c;
                c = new Color(33, 114, 5);
                img.setRGB(x, y, c.getRGB());
            }
        }

        Color c;
        for (Region region : regions) {
            if (region.getGuild().isMember(user)) {
                c = new Color(128, 255, 0);
            } else {
                c = new Color(255, 0, 0);
            }
            graphics.setColor(c);
            Location center = region.getCenter();
            int radius = region.getSize() / scale * 2 + 1;
            if (radius < 3) {
                radius = 3;
            }
            graphics.fillOval((center.getBlockX() / scale - location.getBlockX() / scale - radius / 2),
                    (center.getBlockZ() / scale - location.getBlockZ() / scale - radius / 2),
                    radius, radius);
        }

        graphics.dispose();

        user.setMapImage(img);
        user.setMapNeedsRedraw(true);
        user.setRecalculatingMap(false);
    }

}
