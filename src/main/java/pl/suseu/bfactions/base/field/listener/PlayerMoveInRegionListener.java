package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.region.event.PlayerMoveInRegionEvent;
import pl.suseu.bfactions.base.user.User;

public class PlayerMoveInRegionListener implements Listener {

    private final BFactions plugin;

    public PlayerMoveInRegionListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveInRegionEvent event) {
        Player player = event.getPlayer();
        Entity toTeleport = player.isInsideVehicle() ? player.getVehicle() : player;
        User user = plugin.getUserRepository().getUser(player.getUniqueId());

        if (user.getCurrentRegion().getGuild().isMember(user)) {
            return;
        }
        if (player.isOp() || player.hasPermission("bfactions.bypass-entry")) {
            return;
        }
        if (event.getRegion().getGuild().getField().getState() != FieldState.ENABLED) {
            return;
        }
        if (System.currentTimeMillis() - user.getLastRegionChange() > 3000) {
            user.getCurrentRegion().teleportToSafety(toTeleport);
        }
        Location from = event.getRegion().getCenter().clone();
        Location to = event.getFrom();
        if (from.getX() == to.getX()) {
            to.add(1, 0, 0);
        }
        if (from.getZ() == to.getZ()) {
            to.add(0, 0, 1);
        }
        if (!user.getCurrentRegion().isInPerimeter(from)) {
            return;
        }
        from.setY(to.getY());
        Vector vector = from.toVector().subtract(to.toVector()).multiply(-1).normalize();
        toTeleport.setVelocity(vector);
    }
}
