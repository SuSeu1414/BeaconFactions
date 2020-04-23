package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class RegionInteractionsListener implements Listener {

    private BFactions plugin;

    public RegionInteractionsListener(BFactions plugin) {
        this.plugin = plugin;
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
        if (!region.isInBorder(location)) {
            return;
        }
        if (!region.isInDome(location)) {
            event.setCancelled(true);
            return;
        }
        if (event.getPlayer().isOp()
                || event.getPlayer().hasPermission(GuildPermission.OPEN_CHESTS.getBypassPermission())) {
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

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Location location = event.getRightClicked().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(event.getPlayer().getUniqueId());
        if (region == null) {
            return;
        }
        if (!region.isInBorder(location)) {
            return;
        }
        if (!region.getGuild().isMember(user)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Animals)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Location location = event.getEntity().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(event.getDamager().getUniqueId());
        if (region == null) {
            return;
        }
        if (!region.isInBorder(location)) {
            return;
        }
        if (!region.isInDome(location)) {
            event.setCancelled(true);
            return;
        }
        if (event.getDamager().isOp()
                || event.getDamager().hasPermission(GuildPermission.KILL_ANIMALS.getBypassPermission())) {
            return;
        }
        if (region.getGuild().hasPermission(user, GuildPermission.KILL_ANIMALS, true)) {
            return;
        }
        if (region.getGuild().getField().getState() == FieldState.PERMISSIVE
                || region.getGuild().getField().getState() == FieldState.DISABLED) {
            return;
        }
        event.setCancelled(true);
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
        if (!region.isInBorder(location)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (region.getGuild().getField().getState() == FieldState.PERMISSIVE
                    || region.getGuild().getField().getState() == FieldState.DISABLED) {
                return;
            }
            if (block.getState() instanceof Container
                    && !region.getGuild().hasPermission(user, GuildPermission.OPEN_CHESTS, true)) {
                if (event.getPlayer().isOp()
                        || event.getPlayer().hasPermission(GuildPermission.OPEN_CHESTS.getBypassPermission())) {
                    return;
                }
                event.setCancelled(true);
                return;
            }
            if (block.getBlockData() instanceof Openable
                    && !region.getGuild().hasPermission(user, GuildPermission.OPEN_DOORS, true)) {
                if (event.getPlayer().isOp()
                        || event.getPlayer().hasPermission(GuildPermission.OPEN_DOORS.getBypassPermission())) {
                    return;
                }
                event.setCancelled(true);
                return;
            }
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (region.getGuild().getField().getState() == FieldState.PERMISSIVE) {
                return;
            }
            if (event.getPlayer().isOp()
                    || event.getPlayer().hasPermission(GuildPermission.MODIFY_TERRAIN.getBypassPermission())) {
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
        if (player.isOp() || player.hasPermission(GuildPermission.MODIFY_TERRAIN.getBypassPermission())) {
            return;
        }
        Location location = event.getBlock().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(player.getUniqueId());
        if (region == null) {
            return;
        }
        if (!region.isInBorder(location)) {
            return;
        }
        if (!region.isInDome(location)) {
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
