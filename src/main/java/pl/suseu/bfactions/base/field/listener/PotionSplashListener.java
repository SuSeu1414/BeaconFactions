package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

import java.util.Set;
import java.util.stream.Collectors;

public class PotionSplashListener implements Listener {

    private final BFactions plugin;

    public PotionSplashListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (event.getAffectedEntities().isEmpty()) {
            return;
        }

        Region region = this.plugin.getRegionRepository().nearestRegion(potion.getLocation());
        if (region == null) {
            return;
        }
        Set<LivingEntity> inRegion = event.getAffectedEntities().stream()
                .filter(entity -> region.isInDome(entity.getLocation()))
                .collect(Collectors.toSet());

        ProjectileSource source = potion.getShooter();
        if (!(source instanceof Player)) {
            return;
        }

        Player shooter = (Player) source;
        User user = this.plugin.getUserRepository().getUser(shooter.getUniqueId());

        for (LivingEntity affectedEntity : inRegion) {
            if (affectedEntity instanceof Animals) {
                if (region.getGuild().hasPermission(user, GuildPermission.KILL_ANIMALS, true)) {
                    continue;
                }
                if (region.getGuild().getField().getState() == FieldState.DISABLED
                        || region.getGuild().getField().getState() == FieldState.PERMISSIVE) {
                    continue;
                }
                event.setIntensity(affectedEntity, 0);
            }

            if (affectedEntity instanceof Player) {
                if (region.getGuild().getField().getState() == FieldState.DISABLED
                        || region.getGuild().getField().getState() == FieldState.PERMISSIVE) {
                    continue;
                }
                //TODO PVP inside guild?
                event.setIntensity(affectedEntity, 0);
            }
        }
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        Location location = event.getAreaEffectCloud().getLocation();

        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        if (region == null || !region.isInDome(location)) {
            return;
        }

        ProjectileSource source = potion.getShooter();
        if (!(source instanceof Player)) {
            return;
        }

        if (region.getGuild().getField().getState() == FieldState.DISABLED
                || region.getGuild().getField().getState() == FieldState.PERMISSIVE) {
            return;
        }
        event.setCancelled(true);
    }
}
