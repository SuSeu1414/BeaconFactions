package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;

public class PlayerRegionChangeListener implements Listener {

    @EventHandler
    public void onPlayerRegionChange(PlayerRegionChangeEvent event) {
        if (event.getRegion() == null) {
            return;
        }
        if (event.getRegion().getGuild().getInvitedMembers().contains(event.getUser())) {
            event.getRegion().getGuild().addMember(event.getUser());
            event.getRegion().getGuild().removeInvitedMember(event.getUser());
//          guildRepository.addModifiedGuild(event.getRegion().getGuild());
            return;
        }
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("bfactions.bypass-entry")) {
            return;
        }
        if (event.getRegion().getGuild().getField().getState() != FieldState.ENABLED) {
            return;
        }

        if (event.getRegion().getGuild().isMember(event.getUser())) {
            return;
        }

        event.setCancelled(true);
        Location from = event.getRegion().getCenter().clone();
        Location to = event.getFrom();
//        from.setY(to.getY());
        Vector vector = from.toVector().subtract(to.toVector()).multiply(-1).normalize();
        event.getPlayer().setVelocity(vector);
    }
}
