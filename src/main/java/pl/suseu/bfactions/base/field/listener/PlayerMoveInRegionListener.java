package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import pl.suseu.bfactions.BFactions;
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
        User user = plugin.getUserRepository().getUser(player.getUniqueId());

        if (user.getCurrentRegion().getGuild().isMember(user)) {
            return;
        }

        Location from = event.getRegion().getCenter().clone();
        Location to = event.getFrom();
        from.setY(to.getY());
        Vector vector = from.toVector().subtract(to.toVector()).multiply(-1).normalize();
        event.getPlayer().setVelocity(vector);
    }
}