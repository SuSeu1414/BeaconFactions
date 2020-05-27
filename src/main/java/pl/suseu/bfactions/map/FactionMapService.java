package pl.suseu.bfactions.map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.suseu.bfactions.BFactions;

import java.util.UUID;

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
        MapView mapView = Bukkit.getMap(0);
        if (mapView == null) {
            mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));
        }
        mapView.setTrackingPosition(true);
        mapView.setUnlimitedTracking(true);
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
        mapView.addRenderer(this.imageMapRenderer);
        mapMeta.setMapView(mapView);

        PersistentDataContainer pdc = mapMeta.getPersistentDataContainer();
        NamespacedKey factionMap = new NamespacedKey(plugin, "faction-map");
        NamespacedKey random = new NamespacedKey(plugin, "random");
        pdc.set(factionMap, PersistentDataType.BYTE, (byte) 1);
        pdc.set(random, PersistentDataType.STRING, UUID.randomUUID().toString());

        itemStack.setItemMeta(mapMeta);

        return itemStack;
    }

    public boolean isFactionMap(ItemStack itemStack) {
        ItemMeta im = itemStack.getItemMeta();
        if (im == null) {
            return false;
        }

        PersistentDataContainer pdc = im.getPersistentDataContainer();
        NamespacedKey factionMap = new NamespacedKey(plugin, "faction-map");
        return pdc.getOrDefault(factionMap, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public ItemStack getMapItem() {
        return mapItem;
    }
}
