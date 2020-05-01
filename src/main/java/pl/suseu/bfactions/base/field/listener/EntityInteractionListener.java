package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class EntityInteractionListener implements Listener {

    private final BFactions plugin;

    public EntityInteractionListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntered();
        Location location = event.getVehicle().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(player.getUniqueId());
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
        if ((!(event.getEntity() instanceof Animals) || !(event.getDamager() instanceof Player))
                && (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof TNTPrimed))) {
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
        if (region.getGuild().hasPermission(user, GuildPermission.KILL_ANIMALS, true)) {
            return;
        }
        if (region.getGuild().getField().getState() == FieldState.PERMISSIVE
                || region.getGuild().getField().getState() == FieldState.DISABLED) {
            return;
        }
        event.setCancelled(true);
    }
}
