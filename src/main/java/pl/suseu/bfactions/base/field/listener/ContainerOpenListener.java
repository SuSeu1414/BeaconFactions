package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class ContainerOpenListener implements Listener {

    private final BFactions plugin;

    public ContainerOpenListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Location location = block.getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(event.getPlayer().getUniqueId());
        if (region == null) {
            return;
        }
        if (!region.inInPerimeter(location)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (region.getGuild().getField().getState() == FieldState.PERMISSIVE
                    || region.getGuild().getField().getState() == FieldState.DISABLED) {
                return;
            }
            if (block.getState() instanceof Container
                    && !region.getGuild().hasPermission(user, GuildPermission.OPEN_CHESTS, true)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof BlockInventoryHolder)) {
            return;
        }
        BlockInventoryHolder holder = (BlockInventoryHolder) event.getInventory().getHolder();
        Location location = holder.getBlock().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(event.getPlayer().getUniqueId());
        if (region == null) {
            return;
        }
        if (!region.inInPerimeter(location)) {
            return;
        }
        if (!region.isInside(location)) {
            event.setCancelled(true);
            return;
        }
        if (region.getGuild().hasPermission(user, GuildPermission.OPEN_CHESTS, true)) {
            return;
        }
        if (region.getGuild().getField().getState() == FieldState.PERMISSIVE
                || region.getGuild().getField().getState() == FieldState.DISABLED) {
            return;
        }
        event.setCancelled(true);
    }
}
