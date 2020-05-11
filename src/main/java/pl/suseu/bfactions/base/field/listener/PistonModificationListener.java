package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;

import java.util.List;
import java.util.stream.Collectors;

public class PistonModificationListener implements Listener {

    private final BFactions plugin;

    public PistonModificationListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.getBlocks().size() == 0) {
            return;
        }
        BlockFace mod = event.getDirection();
        List<Location> locations = event.getBlocks().stream()
                .map(block -> block.getLocation().clone().add(mod.getModX(), mod.getModY(), mod.getModZ()))
                .collect(Collectors.toList());
        Region region = this.plugin.getRegionRepository().nearestRegion(locations.get(0));
        if (region == null) {
            return;
        }
        if (locations.stream().anyMatch(region::isInPerimeter)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlocks().size() == 0) {
            return;
        }
        List<Location> locations = event.getBlocks().stream()
                .map(block -> block.getLocation().clone())
                .collect(Collectors.toList());
        Region region = this.plugin.getRegionRepository().nearestRegion(locations.get(0));
        if (region == null) {
            return;
        }
        if (locations.stream().anyMatch(region::isInPerimeter)) {
            event.setCancelled(true);
        }
    }
}
