package pl.suseu.bfactions.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
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

    public static float normalizeYaw(float f) {
        while (f < 0) {
            f += 360;
        }

        return f % 360;
//        float f1 = f % 360.0F;
//
//        if (f1 >= 180.0F) {
//            f1 -= 360.0F;
//        }
//
//        if (f1 < -180.0F) {
//            f1 += 360.0F;
//        }
//
//        return f1;
    }

    private double map(double value, double istart, double istop, double ostart, double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        for (int i = 0; i < canvas.getCursors().size(); i++) {
            canvas.getCursors().removeCursor(canvas.getCursors().getCursor(i));
        }

        byte dir = (byte) map(normalizeYaw(player.getLocation().getYaw() + (float) 11.125), 0, 360, 0, 16);
        if (dir < 0) {
            dir = 0;
        }
        if (dir > 15) {
            dir = 15;
        }
        canvas.getCursors().addCursor(new MapCursor(((byte) 0), ((byte) 0), dir, MapCursor.Type.WHITE_POINTER, true));

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
}
