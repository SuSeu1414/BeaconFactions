package pl.suseu.bfactions.base.guild.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldState;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

import java.util.UUID;

public class BeaconPlaceListener implements Listener {

    private final BFactions plugin;

    public BeaconPlaceListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();

        if (!event.getItemInHand().isSimilar(this.plugin.getItemRepository().getItem("beacon"))) {
            return;
        }

        Player player = event.getPlayer();
        User user = this.plugin.getUserRepository().getUser(player.getUniqueId());

        Region nearestRegion = this.plugin.getRegionRepository().nearestRegion(blockPlaced.getLocation());
        double distance;
        if (nearestRegion == null) {
            distance = Double.MAX_VALUE;
        } else {
            distance = nearestRegion.getCenter().distance(blockPlaced.getLocation());
        }

        if (this.plugin.getSettings().cuboidDistanceMin > distance) {
            this.plugin.getLang().sendMessage("too-close-to-other-guild", player);
            event.setCancelled(true);
            return;
        }

        if (user.ownsGuild()) {
            this.plugin.getLang().sendMessage("you-already-own-a-guild", player);
            event.setCancelled(true);
            return;
        }

        UUID uuid = UUID.randomUUID();
        Region region = new Region(uuid, event.getBlock().getLocation().clone().add(0.5, 0, 0.5),
                plugin.getSettings().regionTiers.get(0));
        Field field = new Field(uuid, plugin.getSettings().fieldTiers.get(0));
        field.setCurrentEnergy(plugin.getSettings().fieldEnergyInitial);
        Guild guild = new Guild(uuid, player.getName() + "'s guild", user, region, field);
        field.recalculate();
        field.setState(FieldState.ENABLED);
        this.plugin.getGuildRepository().addGuild(guild, true);
        this.plugin.getRegionRepository().addRegion(region);
        this.plugin.getFieldRepository().addField(field);

        this.plugin.getLang().sendMessage("guild-created", player);

        for (Player p : event.getBlockPlaced().getWorld().getPlayers()) {
            if (!p.equals(event.getPlayer()) && region.isInBorder(p.getLocation())) {
                region.teleportToSafety(p);
            }
        }
    }

}
