package pl.suseu.bfactions.base.user.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;

public class PlayerJoinListener implements Listener {

    private final BFactions plugin;

    public PlayerJoinListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUserRepository().getOnlineUser(player.getUniqueId());
        if (user == null) {
            user = this.plugin.getUserRepository().getUser(player.getUniqueId());
        }
        user.setName(event.getPlayer().getName());
    }

}
