package pl.suseu.bfactions.base.guild.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;

import java.util.UUID;

public class BeaconPlaceListener implements Listener {

    private final BFactions plugin;

    public BeaconPlaceListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        //todo multiple checks (weather player has a guild etc)

        if (!event.getBlock().getType().equals(Material.BEACON)) { //todo check name, lore etc
            return;
        }

        Player player = event.getPlayer();
        User user = this.plugin.getUserRepository().getUser(player.getUniqueId());

        Guild guild = new Guild(UUID.randomUUID(), player.getName() + "'s guild", user, null, null);
        this.plugin.getGuildRepository().addGuild(guild, true);

        this.plugin.getLang().sendMessage("guild-created", player);
    }

}