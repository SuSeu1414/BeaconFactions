package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;

public class RegionListener implements Listener {

    private final BFactions plugin;

    public RegionListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }
        if (from.getBlockZ() == to.getBlockZ() && from.getBlockX() == to.getBlockX()) {
            return;
        }

        Player player = event.getPlayer();
        User user = plugin.getUserRepository().getUser(player.getUniqueId());
        if (user.isInSafeLocation() == 1) {
            Location lastSafe = user.getLastSafeLocation();
            if (lastSafe == null) {
                //TODO teleport to spawn
                return;
            }
            player.teleport(lastSafe);
        }
    }
}
