package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;

public class LiquidSpreadListener implements Listener {

    private final BFactions plugin;

    public LiquidSpreadListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLiquidSpread(BlockFromToEvent event) {
        if (!event.getBlock().isLiquid()) {
            return;
        }
        Location from = event.getBlock().getLocation();
        Location to = event.getToBlock().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(from);
        if (region == null) {
            return;
        }
        if (region.isInPerimeter(from)) {
            return;
        }
        if (!region.isInPerimeter(to)) {
            return;
        }
        event.setCancelled(true);
    }
}
