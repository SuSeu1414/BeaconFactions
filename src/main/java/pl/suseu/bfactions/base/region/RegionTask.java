package pl.suseu.bfactions.base.region;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;

public class RegionTask implements Runnable {

    private final BFactions plugin;

    public RegionTask(BFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (User user : plugin.getUserRepository().getUsers()) {
            if (user.isInSafeLocation() != 0) {
                return;
            }

            Player player = Bukkit.getPlayer(user.getUuid());
            if (player == null) {
                return;
            }

            user.setLastSafeLocation(player.getLocation());
        }
    }
}
