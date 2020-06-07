package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;

public class PlayerRegionChangeListener implements Listener {

    private final LangAPI lang;

    public PlayerRegionChangeListener(BFactions plugin) {
        this.lang = plugin.getLang();
    }

    @EventHandler
    public void onPlayerRegionChange(PlayerRegionChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getRegion() == null) {
            return;
        }
        Region region = event.getRegion();
        Guild guild = region.getGuild();
        if (guild.getInvitedMembers().contains(event.getUser())) {
            guild.addMember(event.getUser());
            guild.removeInvitedMember(event.getUser());
//          guildRepository.addModifiedGuild(event.getRegion().getGuild());
            lang.sendMessage("accepted-invite", player, "%guild%", guild.getName());
            return;
        }
        if (player.isOp() || player.hasPermission("bfactions.bypass-entry")) {
            return;
        }
        if (guild.getField().getState() != FieldState.ENABLED) {
            return;
        }

        if (guild.isMember(event.getUser())) {
            return;
        }

        event.setCancelled(true);
        Location from = region.getCenter().clone();
        Location to = event.getFrom();
        if (from.getBlockY() <= to.getBlockY()) {
            from.add(0, region.getSize(), 0);
        }
//        from.setY(to.getY());
        Vector vector = from.toVector().subtract(to.toVector()).multiply(-1).normalize();
        player.setVelocity(vector);
    }
}
