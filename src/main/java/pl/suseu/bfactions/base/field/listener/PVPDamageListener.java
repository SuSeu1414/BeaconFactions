package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class PVPDamageListener implements Listener {

    private final BFactions plugin;

    public PVPDamageListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAlliedPVP(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (!(damaged instanceof Player) || !(damager instanceof Player)) {
            return;
        }
        Location location = damaged.getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        if (region == null) {
            return;
        }
        Guild guild = region.getGuild();
        User damagedUser = this.plugin.getUserRepository().getUser(damaged.getUniqueId());
        User damagerUser = this.plugin.getUserRepository().getUser(damager.getUniqueId());
        if (!region.isInside(location)) {
            return;
        }
        if (!guild.isMember(damagedUser) || !guild.isMember(damagerUser)) {
            return;
        }
        if (guild.isPvpEnabled()) {
            return;
        }
        event.setCancelled(true);
    }
}
