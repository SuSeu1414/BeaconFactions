package pl.suseu.bfactions.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;

import java.awt.*;

public class ImageMapRenderer extends MapRenderer {

    private final BFactions plugin;

    public ImageMapRenderer(BFactions plugin) {
        super(true);
        this.plugin = plugin;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        User user = this.plugin.getUserRepository().getOnlineUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        int mapId = map.getId();

        if (user.mapNeedsRedraw() || user.getLastDrawnMap() != mapId) {
            Image image = user.getMapImage();
            if (image == null) {
                return;
            }
            canvas.drawImage(0, 0, image);
            user.setLastDrawnMap(mapId);
            user.setMapNeedsRedraw(false);
        }
    }

    private double map(double value, double istart, double istop, double ostart, double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }
}
