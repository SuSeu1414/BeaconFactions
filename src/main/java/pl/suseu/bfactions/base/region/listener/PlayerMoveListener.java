package pl.suseu.bfactions.base.region.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.event.PlayerMoveInRegionEvent;
import pl.suseu.bfactions.base.user.User;

public class PlayerMoveListener implements Listener {

    private final BFactions plugin;

    public PlayerMoveListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUserRepository().getUser(player.getUniqueId());
        Region region = user.getCurrentRegion();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (region == null) {
            return;
        }

        if (to == null || from.distance(to) == 0) {
            return;
        }

        PlayerMoveInRegionEvent playerMoveInRegionEvent = new PlayerMoveInRegionEvent(player, user, region, from, to);
        plugin.getServer().getPluginManager().callEvent(playerMoveInRegionEvent);
        if (playerMoveInRegionEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
