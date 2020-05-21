package pl.suseu.bfactions.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TransferCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final EventWaiter eventWaiter;

    public TransferCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
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
        User oldOwner = userRepository.getUser(player.getUniqueId());
        Guild guild = oldOwner.getOwnedGuild();
        if (guild == null) {
            lang.sendMessage("you-do-not-own-any-guild", player);
            return;
        }

        if (args.size() == 0) {
            command.sendUsage(sender, label);
            return;
        }

        String arg = args.get(0);
        Player plr = Bukkit.getPlayer(arg);
        if (plr == null) {
            lang.sendMessage("player-offline", player);
            return;
        }

        User newOwner = this.userRepository.getUser(plr.getUniqueId());
        if (!guild.isMember(newOwner)) {
            lang.sendMessage("player-is-not-a-member", player);
            return;
        }
        if (newOwner.getOwnedGuild() != null) {
            lang.sendMessage("player-is-already-owner", player);
            return;
        }

        if (guild.getTransferCode() != -1) {
            return;
        }

        AtomicInteger number = new AtomicInteger(new Random().nextInt(8999) + 1000);
        guild.setTransferCode(number.get());

        this.lang.sendMessage("confirm-guild-transfer", player, "%number%", "" + number.get());
        this.eventWaiter.waitForEvent(AsyncPlayerChatEvent.class, EventPriority.NORMAL,
                ev -> ev.getPlayer().equals(player) && ev.getMessage().equals("" + number.get()),
                ev -> {
                    ev.setCancelled(true);
                    guild.setOwner(newOwner);
                    guild.removeMember(newOwner, true);
                    guild.addMember(oldOwner);
                    this.lang.sendMessage("guild-transferred", player);
                }, 20 * 15, () -> {
                    this.lang.sendMessage("confirm-guild-transfer-listener-timeout", player);
                    guild.setTransferCode(-1);
                });
    }
}
