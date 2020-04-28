package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class LiquidPlaceListener implements Listener {

    private final BFactions plugin;

    public LiquidPlaceListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        checkBucketEvent(event);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        checkBucketEvent(event);
    }

    private void checkBucketEvent(PlayerBucketEvent event) {
        Location location = event.getBlock().getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
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
        User user = this.plugin.getUserRepository().getUser(event.getPlayer().getUniqueId());
        Field field = region.getGuild().getField();
        if (field.getState() == FieldState.DISABLED
                || field.getState() == FieldState.PERMISSIVE) {
            return;
        }
        if (region.getGuild().hasPermission(user, GuildPermission.MODIFY_TERRAIN, true)) {
            return;
        }
        event.setCancelled(true);
    }
}
