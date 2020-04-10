package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;

import java.util.Set;
import java.util.stream.Collectors;

public class EntityExplodeListener implements Listener {

    private final BFactions plugin;

    public EntityExplodeListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        if (region.isInDome(location)) {
            event.blockList().clear();
            return;
        }
        Set<Block> inRegion = event.blockList().stream()
                .filter(block -> region.isInBorder(block.getLocation()))
                .collect(Collectors.toSet());
        event.blockList().removeAll(inRegion);
    }
}
