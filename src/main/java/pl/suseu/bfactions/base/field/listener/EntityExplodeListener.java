package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
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
        if (region == null) {
            return;
        }
        if (event.getEntityType() == EntityType.CREEPER) {
            event.blockList().removeIf(block -> {
                Location blockLocation = block.getLocation();
                Location centerLocation = region.getCenter();
                return blockLocation.getBlockX() == centerLocation.getBlockX()
                        && blockLocation.getBlockY() == centerLocation.getBlockY()
                        && blockLocation.getBlockZ() == centerLocation.getBlockZ();
            });
            return;
        }
        if (region.isInDome(location)) {
            event.blockList().clear();
            region.getGuild().getField().addEnergy(-1 * plugin.getSettings().fieldDamageTNT);
            return;
        }
        event.blockList().clear();
//        Set<Block> inRegion = event.blockList().stream()
//                .filter(block -> region.isInBorder(block.getLocation()))
//                .collect(Collectors.toSet());
//        event.blockList().removeAll(inRegion);
    }
}
