package pl.suseu.bfactions.gui.main.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.eventwaiter.EventWaiter;

public class ChangeGuildNameAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final Guild guild;
    private final LangAPI lang;
    private final EventWaiter eventWaiter;

    public ChangeGuildNameAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.guild = guild;
        this.eventWaiter = plugin.getEventWaiter();
        this.lang = plugin.getLang();
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        whoClicked.closeInventory();
        lang.sendMessage("type-new-guild-name-in-chat", whoClicked);
        this.eventWaiter.waitForEvent(AsyncPlayerChatEvent.class, EventPriority.NORMAL,
                event -> event.getPlayer().equals(whoClicked),
                event -> {
                    String newName = event.getMessage();
                    this.guild.setName(newName);
//                    this.guildRepository.addModifiedGuild(this.guild);
                    this.lang.sendMessage("guild-name-changed", whoClicked);
                    event.setCancelled(true);
                }, 20 * 30, () -> {
                    this.lang.sendMessage("guild-name-listener-timed-out", whoClicked);
                });

    }

}
