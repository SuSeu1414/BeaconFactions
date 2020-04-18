package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class RegionInteractionsListener implements Listener {

    private BFactions plugin;

    public RegionInteractionsListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        Player player = event.getPlayer();
        User user = plugin.getUserRepository().getUser(player.getUniqueId());
        if (!region.getGuild().isMember(user)) {
            event.setCancelled(true);
            return;
        }
        if (region.isInDome(location)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getType().toString().contains("DOOR")
                        && !region.getGuild().hasPermission(user, GuildPermission.OPEN_DOORS)) {
                    event.setCancelled(true);
                }
                if (event.getClickedBlock().getType().toString().contains("CHEST")
                        && !region.getGuild().hasPermission(user, GuildPermission.OPEN_CHESTS)) {
                    event.setCancelled(true);
                }
            }
        } else if (region.isInDome(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        Player player = event.getPlayer();
        User user = plugin.getUserRepository().getUser(player.getUniqueId());
        if (region.isInDome(location) && region.getGuild().hasPermission(user, GuildPermission.MODIFY_TERRAIN)) {
            return;
        }
        if (region.isInBorder(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        Player player = event.getPlayer();
        User user = plugin.getUserRepository().getUser(player.getUniqueId());
        if (region.isInDome(location) && region.getGuild().hasPermission(user, GuildPermission.MODIFY_TERRAIN)) {
            return;
        }
        if (region.isInBorder(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Animals)) {
            return;
        }
        Location location = event.getEntity().getLocation();
        Region region = plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        User user = plugin.getUserRepository().getUser(player.getUniqueId());
        if (!region.getGuild().hasPermission(user, GuildPermission.KILL_ANIMALS)) {
            event.setCancelled(true);
        }
    }
}
