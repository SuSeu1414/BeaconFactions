package pl.suseu.bfactions.gui.main.action.invite;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.eventwaiter.EventWaiter;

public class InviteMemberAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final Guild guild;
    private final LangAPI lang;
    private final EventWaiter eventWaiter;

    public InviteMemberAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guild = guild;
        this.eventWaiter = plugin.getEventWaiter();
        this.lang = plugin.getLang();
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE, true)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        whoClicked.closeInventory();
        lang.sendMessage("type-player-name-in-chat-to-invite", whoClicked);
        this.eventWaiter.waitForEvent(AsyncPlayerChatEvent.class, EventPriority.NORMAL,
                event -> event.getPlayer().equals(whoClicked),
                event -> {
                    onNameTyped(whoClicked, event);
                }, 20 * 30, () -> {
                    this.lang.sendMessage("guild-invite-listener-timed-out", whoClicked);
                });
    }

    private void onNameTyped(Player whoClicked, AsyncPlayerChatEvent nameTypedEvent) {
        nameTypedEvent.setCancelled(true);
        String name = nameTypedEvent.getMessage();
        User toInviteUser = this.userRepository.getUserByName(name);
        Player toInvitePlayer = Bukkit.getPlayer(name);
        if (toInvitePlayer == null) {
            this.lang.sendMessage("user-does-not-exist", nameTypedEvent.getPlayer());
            return;
        }

        if (this.guild.getInvitedMembers().contains(toInviteUser)) {
            this.lang.sendMessage("user-already-invited", nameTypedEvent.getPlayer());
            return;
        }

        if (this.guild.isMember(toInviteUser)) {
            this.lang.sendMessage("user-already-member", nameTypedEvent.getPlayer());
            return;
        }

        this.guild.addInvitedMember(toInviteUser);
        this.lang.sendMessage("you-have-been-invited-to-guild", toInvitePlayer,
                "%guild%", guild.getName());
        this.lang.sendMessage("user-invited-to-guild", whoClicked);
    }
}
