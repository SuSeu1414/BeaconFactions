package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
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
    public void onTNTExplosion(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        if (event.getEntityType() != EntityType.PRIMED_TNT
                && event.getEntityType() != EntityType.MINECART_TNT) {
            return;
        }

        //check if need to apply field damage
        if (event.blockList().stream().anyMatch(block -> region.isInDome(block.getLocation()))) {
            region.getGuild().getField().addEnergy(-1 * plugin.getSettings().fieldDamageTNT);
        }

        event.blockList().clear();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }

        //check if need to apply field damage
        if (event.blockList().stream().anyMatch(block -> region.isInDome(block.getLocation()))) {
            region.getGuild().getField().addEnergy(-1 * plugin.getSettings().fieldDamageTNT);
        }

        //remove damage in border, not in field
        event.blockList().removeIf(block -> region.isInBorder(block.getLocation()) && !region.isInDome(block.getLocation()));

        //if field enabled remove damage in field
        if (region.getGuild().getField().getState() == FieldState.ENABLED) {
            event.blockList().removeIf(block -> region.isInDome(block.getLocation()));
        }

    }
}
