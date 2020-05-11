package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.region.Region;

public class EntityExplodeListener implements Listener {

    private final BFactions plugin;

    public EntityExplodeListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBeaconBreak(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        event.blockList().removeIf(block -> {
            Location blockLocation = block.getLocation();
            Location centerLocation = region.getCenter();
            return blockLocation.getBlockX() == centerLocation.getBlockX()
                    && blockLocation.getBlockY() == centerLocation.getBlockY()
                    && blockLocation.getBlockZ() == centerLocation.getBlockZ();
        });
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }

        //check if need to apply field damage
        if (event.blockList().stream().anyMatch(block -> region.isInside(block.getLocation()))) {
            region.getGuild().getField().addEnergy(-1 * plugin.getSettings().fieldDamageTNT);
        }

        //remove damage in border, not in field
        event.blockList().removeIf(block -> region.isInPerimeter(block.getLocation()) && !region.isInside(block.getLocation()));

        //if field enabled remove damage in field
        if (region.getGuild().getField().getState() == FieldState.ENABLED) {
            event.blockList().removeIf(block -> region.isInside(block.getLocation()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTNTExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        if (event.getEntityType() != EntityType.PRIMED_TNT) {
            return;
        }
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        for (int x = -3; x < 3; x++) {
            for (int y = -3; y < 3; y++) {
                for (int z = -3; z < 3; z++) {
                    Location l = location.clone().add(x, y, z);
                    Block block = world.getBlockAt(l);
                    if (location.distance(l) > 3) {
                        continue;
                    }
                    if (block.getType() != Material.OBSIDIAN) {
                        continue;
                    }
                    if (region.getGuild().getField().getState() == FieldState.ENABLED
                            && region.isInside(l)) {
                        continue;
                    }
                    block.breakNaturally();
                }
            }
        }
    }
}
