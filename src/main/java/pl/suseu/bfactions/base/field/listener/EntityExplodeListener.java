package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;

public class EntityExplodeListener implements Listener {

    private final BFactions plugin;

    public EntityExplodeListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null || !region.isInDome(location)) {
            return;
        }
        event.blockList().clear();
    }
}
