package pl.suseu.bfactions.map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import pl.suseu.bfactions.BFactions;

public class FactionMapService {

    private final BFactions plugin;
    private final ImageMapRenderer imageMapRenderer;
    private final ItemStack mapItem;

    public FactionMapService(BFactions plugin) {
        this.plugin = plugin;
        this.imageMapRenderer = new ImageMapRenderer(plugin);
        this.mapItem = this.createMapItem();
    }

    private ItemStack createMapItem() {
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = ((MapMeta) itemStack.getItemMeta());
        MapView mapView = Bukkit.createMap(plugin.getServer().getWorlds().get(0));
        mapView.setTrackingPosition(true);
        mapView.setUnlimitedTracking(true);
        MapRenderer craftRenderer = mapView.getRenderers().get(0);
        for (MapRenderer renderer : mapView.getRenderers()) {
            mapView.removeRenderer(renderer);
        }
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                mapView.setCenterX(player.getLocation().getBlockX());
                mapView.setCenterZ(player.getLocation().getBlockZ());
            }
        });
        mapView.addRenderer(craftRenderer);
        mapView.addRenderer(this.imageMapRenderer);
        mapMeta.setMapView(mapView);
        itemStack.setItemMeta(mapMeta);

        return itemStack;
    }

    public ItemStack getMapItem() {
        return mapItem;
    }
}
