package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;

public class PlayerRegionChangeListener implements Listener {

    @EventHandler
    public void onPlayerRegionChange(PlayerRegionChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getRegion() == null) {
            return;
        }
        if (event.getRegion().getGuild().getInvitedMembers().contains(event.getUser())) {
            event.getRegion().getGuild().addMember(event.getUser());
            event.getRegion().getGuild().removeInvitedMember(event.getUser());
//          guildRepository.addModifiedGuild(event.getRegion().getGuild());
            return;
        }
        if (player.isOp() || player.hasPermission("bfactions.bypass-entry")) {
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
        if (from.getBlockY() <= to.getBlockY()) {
            from.add(0, event.getRegion().getSize(), 0);
        }
//        from.setY(to.getY());
        Vector vector = from.toVector().subtract(to.toVector()).multiply(-1).normalize();
        player.setVelocity(vector);
    }
}
