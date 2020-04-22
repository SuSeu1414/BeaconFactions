package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

import java.util.UUID;

public class PlayerRegionTeleportListener implements Listener {

    private final BFactions plugin;

    public PlayerRegionTeleportListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("bfactions.bypass-entry")) {
            return;
        }
        if (to == null) {
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        Region region = plugin.getRegionRepository().nearestRegion(to);

        if (region == null) {
            return;
        }
        if (region.getGuild().getField().getState() != FieldState.ENABLED) {
            return;
        }
        if (!region.isInBorder(to)) {
            return;
        }

        User user = plugin.getUserRepository().getUser(uuid);
        if (!user.getGuilds().contains(region.getGuild())) {
            event.setCancelled(true);
        }
    }
}
