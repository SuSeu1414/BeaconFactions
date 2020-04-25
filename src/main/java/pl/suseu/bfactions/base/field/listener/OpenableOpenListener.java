package pl.suseu.bfactions.base.field.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class OpenableOpenListener implements Listener {

    private final BFactions plugin;

    public OpenableOpenListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Location location = block.getLocation();
        Region region = this.plugin.getRegionRepository().nearestRegion(location);
        User user = this.plugin.getUserRepository().getUser(event.getPlayer().getUniqueId());
        if (region == null) {
            return;
        }
        if (!region.isInBorder(location)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (region.getGuild().getField().getState() == FieldState.PERMISSIVE
                    || region.getGuild().getField().getState() == FieldState.DISABLED) {
                return;
            }
            if (block.getBlockData() instanceof Openable
                    && !region.getGuild().hasPermission(user, GuildPermission.OPEN_DOORS, true)) {
                event.setCancelled(true);
            }
        }
    }
}
