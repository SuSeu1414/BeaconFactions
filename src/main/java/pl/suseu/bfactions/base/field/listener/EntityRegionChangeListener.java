package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.region.event.EntityRegionChangeEvent;
import pl.suseu.bfactions.base.user.User;

public class EntityRegionChangeListener implements Listener {

    private final BFactions plugin;

    public EntityRegionChangeListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityRegionChange(EntityRegionChangeEvent event) {
        if (event.getRegion() == null) {
            return;
        }

        if (event.getEntity() instanceof TNTPrimed) {
            if (event.getFrom().equals(event.getTo())) {
                return;
            }
            TNTPrimed tnt = (TNTPrimed) event.getEntity();
            tnt.setFuseTicks(0);
        }

        if (event.getEntity() instanceof AbstractArrow) {
            AbstractArrow arrow = (AbstractArrow) event.getEntity();
            ProjectileSource source = arrow.getShooter();

            if (!(source instanceof Player)) {
                return;
            }

            Player shooter = (Player) source;
            User user = plugin.getUserRepository().getUser(shooter.getUniqueId());

            if (event.getRegion().getGuild().isMember(user)) {
                return;
            }

            Field field = event.getRegion().getGuild().getField();
            field.setUnrepairableUntil(System.currentTimeMillis() + plugin.getSettings().fieldHealDelay);
            field.addEnergy(-1 * plugin.getSettings().fieldDamageArrow);

            double range = plugin.getSettings().fieldDomeDistance;
            Location location = arrow.getLocation();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Entity entity : arrow.getNearbyEntities(range, range, range)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }

                    Player player = (Player) entity;
                    player.playSound(location, Sound.ITEM_SHIELD_BREAK, 1, 1);
                    player.spawnParticle(Particle.FLAME, location, 10);
                    player.spawnParticle(Particle.SMOKE_NORMAL, location, 10);
                    player.spawnParticle(Particle.EXPLOSION_NORMAL, location, 10);
                }
            });

            arrow.remove();
        }

    }
}
