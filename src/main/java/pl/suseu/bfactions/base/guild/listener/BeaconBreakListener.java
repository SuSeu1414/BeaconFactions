package pl.suseu.bfactions.base.guild.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class BeaconBreakListener implements Listener {

    private final BFactions plugin;
    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final LangAPI lang;
    private final EventWaiter eventWaiter;


    public BeaconBreakListener(BFactions plugin) {
        this.plugin = plugin;
        this.guildRepository = plugin.getGuildRepository();
        this.userRepository = plugin.getUserRepository();
        this.lang = plugin.getLang();
        this.eventWaiter = plugin.getEventWaiter();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.BEACON) {
            return;
        }

        Guild guild = this.guildRepository.getGuildByBeaconLocation(event.getBlock().getLocation());

        if (guild == null) {
            return;
        }

        event.setCancelled(true);

        User user = this.userRepository.getUser(event.getPlayer().getUniqueId());
        if (!guild.isOwner(user)) {
            return;
        }

        if (guild.getDeleteCode() != -1) {
            return;
        }

        AtomicInteger number = new AtomicInteger(new Random().nextInt(8999) + 1000);
        guild.setDeleteCode(number.get());

        this.lang.sendMessage("confirm-guild-deletion", event.getPlayer(), "%number%", "" + number.get());
        this.eventWaiter.waitForEvent(AsyncPlayerChatEvent.class, EventPriority.NORMAL,
                ev -> ev.getPlayer().equals(event.getPlayer()) && ev.getMessage().equals("" + number.get()),
                ev -> {
                    ev.setCancelled(true);
                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                        guild.getRegion().getCenter().getWorld().dropItemNaturally(guild.getRegion().getCenter(),
                                this.plugin.getItemRepository().getItem("beacon", false));
                        //ev.getPlayer().getInventory().addItem(this.plugin.getItemRepository().getItem("beacon", false));
                        guild.delete();
                    });
                    this.lang.sendMessage("guild-deleted", event.getPlayer());
                }, 20 * 15, () -> {
                    this.lang.sendMessage("confirm-guild-deletion-listener-timeout", event.getPlayer());
                    guild.setDeleteCode(-1);
                });
    }

}
