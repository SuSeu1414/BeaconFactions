package pl.suseu.bfactions.command.cmds;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.List;

public class SetMotdCommandExecutor implements BCommandExecutor {

    private final LangAPI lang;
    private final UserRepository userRepository;
    private final EventWaiter eventWaiter;

    public SetMotdCommandExecutor(BFactions plugin) {
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.eventWaiter = plugin.getEventWaiter();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = ((Player) sender);
        User user = this.userRepository.getUser(player.getUniqueId());
        Guild guild = user.getOwnedGuild();
        if (guild == null) {
            this.lang.sendMessage("you-do-not-own-any-guild", player);
            return;
        }
        Region region = guild.getRegion();

        if (region == null || !region.isInside(player.getLocation())) {
            this.lang.sendMessage("not-in-region", player);
            return;
        }

        if (args.size() != 1) {
            command.sendUsage(sender, label);
            return;
        }

        String arg = args.get(0);
        if (!arg.equalsIgnoreCase("entry")
                && !arg.equalsIgnoreCase("exit")) {
            command.sendUsage(sender, label);
            return;
        }

        this.lang.sendMessage("guild-motd-change", player);
        eventWaiter.waitForEvent(AsyncPlayerChatEvent.class,
                EventPriority.NORMAL,
                event -> event.getPlayer().equals(player),
                event -> {
                    if (arg.equalsIgnoreCase("entry")) {
                        guild.setEntryMOTD(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                    } else if (arg.equalsIgnoreCase("exit")) {
                        guild.setExitMOTD(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                    }
                    this.lang.sendMessage("guild-motd-set", player);
                    event.setCancelled(true);
                }, 30 * 20, () -> {
                    this.lang.sendMessage("guild-motd-timeout", player);
                });
    }
}
