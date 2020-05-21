package pl.suseu.bfactions.base.field.task;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldRepository;
import pl.suseu.bfactions.base.field.FieldState;
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
            long now = System.currentTimeMillis();

            if (field.getCurrentEnergy() == 0 && field.getState() == FieldState.ENABLED) {
                field.setState(FieldState.PERMISSIVE);
            }
            if (field.getState() == FieldState.PERMISSIVE
                    && (now - field.getStateChangeTime()) >= settings.fieldKnockdownTimeout) {
                field.setState(FieldState.DISABLED);
            }
            if (field.getState() == FieldState.DISABLED
                    && field.getCurrentEnergy() > 0) {
                for (User user : userRepository.getUsers()) {
                    if (region.equals(user.getCurrentRegion())) {
                        user.setLastRegionChange(now);
                    }
                }
                field.setState(FieldState.ENABLED);
            }

            double progress;
            String title;

            if (field.getState() == FieldState.PERMISSIVE) {
                progress = ((double) settings.fieldKnockdownTimeout + field.getStateChangeTime() - now) / settings.fieldKnockdownTimeout;
                title = "The field was knocked out, " + (settings.fieldKnockdownTimeout + field.getStateChangeTime() - now) / 1000 + " seconds left";
            } else if (field.getState() == FieldState.ENABLED) {
                progress = field.getCurrentEnergy() / field.getTier().getMaxEnergy();
                title = String.format("%.0f (%.2f%%)", field.getCurrentEnergy(), progress * 100);
            } else if (field.getState() == FieldState.DISABLED) {
                progress = 0;
                title = "The field is disabled";
            } else {
                //State is Null?
                progress = 0;
                title = "";
            }

            BossBar alliedBar = field.getAlliedBar();
            BossBar enemyBar = field.getEnemyBar();

            if (progress > 1) {
                progress = 1;
            }

            alliedBar.setTitle(title);
            alliedBar.setProgress(progress);
            enemyBar.setTitle(title);
            enemyBar.setProgress(progress);
            if (progress < 0.33) {
                enemyBar.setColor(BarColor.WHITE);
            }
            if (progress >= 0.33) {
                enemyBar.setColor(BarColor.YELLOW);
            }
            if (progress > 0.66) {
                enemyBar.setColor(BarColor.RED);
            }

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
