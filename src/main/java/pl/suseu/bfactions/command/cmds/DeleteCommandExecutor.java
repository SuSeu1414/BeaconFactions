package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldRepository;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class DeleteCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;
    private final FieldRepository fieldRepository;
    private final EventWaiter eventWaiter;

    public DeleteCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.regionRepository = plugin.getRegionRepository();
        this.fieldRepository = plugin.getFieldRepository();
        this.eventWaiter = plugin.getEventWaiter();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = ((Player) sender);
        User user = userRepository.getUser(player.getUniqueId());
        Guild guild = user.getOwnedGuild();
        if (guild == null) {
            lang.sendMessage("you-do-not-own-any-guild", player);
            return;
        }

        if (guild.getDeleteCode() != -1) {
            return;
        }

        AtomicInteger number = new AtomicInteger(new Random().nextInt(8999) + 1000);
        guild.setDeleteCode(number.get());

        this.lang.sendMessage("confirm-guild-deletion", player, "%number%", "" + number.get());
        this.eventWaiter.waitForEvent(AsyncPlayerChatEvent.class, EventPriority.NORMAL,
                ev -> ev.getPlayer().equals(player) && ev.getMessage().equals("" + number.get()),
                ev -> {
                    ev.setCancelled(true);
                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                        player.getInventory().addItem(this.plugin.getItemRepository().getItem("beacon", false));
                        guild.delete();
                    });
                    this.lang.sendMessage("guild-deleted", player);
                }, 20 * 15, () -> {
                    this.lang.sendMessage("confirm-guild-deletion-listener-timeout", player);
                    guild.setDeleteCode(-1);
                });
    }
}
