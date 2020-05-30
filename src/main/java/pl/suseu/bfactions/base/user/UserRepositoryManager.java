package pl.suseu.bfactions.base.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.suseu.bfactions.BFactions;

public class UserRepositoryManager implements Listener, Runnable {

    private final BFactions plugin;
    private final UserRepository userRepository;

    public UserRepositoryManager(BFactions plugin) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
    }

    @Override
    public void run() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            User user = this.userRepository.getOnlineUser(player.getUniqueId());
            if (user == null) {
                this.userRepository.addOnlineUser(this.userRepository.getUser(player.getUniqueId()));
            }
        }

        for (User user : this.userRepository.getOnlineUsers()) {
            if (this.plugin.getServer().getPlayer(user.getUuid()) == null) {
                this.userRepository.removeOnlineUser(user);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            User user = this.userRepository.getUser(event.getPlayer().getUniqueId());
            this.userRepository.addOnlineUser(user);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        this.quit(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onKick(PlayerKickEvent event) {
        this.quit(event.getPlayer());
    }

    private void quit(Player player) {
        User user = this.userRepository.getOnlineUser(player.getUniqueId());
        this.userRepository.removeOnlineUser(player.getUniqueId());

        if (user != null) {
            user.setMapImage(null);
        }
    }
}
