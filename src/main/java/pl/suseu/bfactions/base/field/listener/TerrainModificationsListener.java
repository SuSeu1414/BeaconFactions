package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class TerrainModificationsListener implements Listener {

    private final BFactions plugin;

    public TerrainModificationsListener(BFactions plugin) {
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
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (region.getGuild().getField().getState() == FieldState.PERMISSIVE) {
                return;
            }
            if (!region.getGuild().hasPermission(user, GuildPermission.MODIFY_TERRAIN, true)) {
                event.setCancelled(true);
                return;
            }
            return;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        checkBlockEvent(event, event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkBlockEvent(event, event.getPlayer());
    }

    private <T extends BlockEvent & Cancellable> void checkBlockEvent(T event, Player player) {
        if (!(event instanceof BlockBreakEvent || event instanceof BlockPlaceEvent)) {
            return;
        }
        Location location = event.getBlock().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(player.getUniqueId());
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
        if (region.getGuild().hasPermission(user, GuildPermission.MODIFY_TERRAIN, true)) {
            return;
        }
        if (region.getGuild().getField().getState() == FieldState.PERMISSIVE) {
            return;
        }
        event.setCancelled(true);
    }
}
