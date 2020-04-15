package pl.suseu.bfactions.base.field.task;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldRepository;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.settings.Settings;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldBarTask implements Runnable {

    private final BFactions plugin;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final Settings settings;

    public FieldBarTask(BFactions plugin) {
        this.plugin = plugin;
        this.fieldRepository = this.plugin.getFieldRepository();
        this.userRepository = this.plugin.getUserRepository();
        this.settings = this.plugin.getSettings();
    }

    @Override
    public void run() {
        for (Field field : fieldRepository.getFields()) {
            Guild guild = field.getGuild();
            Region region = guild.getRegion();
            double range = region.getSize() + settings.fieldBarDistance;

            double progress = field.getCurrentEnergy() / field.getTier().getMaxEnergy();
            String title = String.format("%.0f (%.0f)", field.getCurrentEnergy(), progress);

            BossBar alliedBar = field.getAlliedBar();
            BossBar enemyBar = field.getEnemyBar();

            alliedBar.setTitle(title);
            alliedBar.setProgress(progress);
            enemyBar.setTitle(title);
            enemyBar.setProgress(progress);

            Set<Player> inRange = plugin.getServer().getOnlinePlayers().stream()
                    .filter(player -> region.flatDistance(player.getLocation()) < range)
                    .collect(Collectors.toSet());

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Player player : inRange) {
                    User user = userRepository.getUser(player.getUniqueId());
                    if (guild.isMember(user)) {
                        alliedBar.addPlayer(player);
                    } else {
                        enemyBar.addPlayer(player);
                    }
                }
            });

            Set<Player> outOufRange = new HashSet<>();
            outOufRange.addAll(alliedBar.getPlayers());
            outOufRange.addAll(enemyBar.getPlayers());
            outOufRange.removeAll(inRange);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Player player : outOufRange) {
                    alliedBar.removePlayer(player);
                    enemyBar.removePlayer(player);
                }
            });
        }
    }
}
