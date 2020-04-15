package pl.suseu.bfactions.gui.main.action.invite;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.main.factory.invite.GuildInvitesGuiFactory;

public class OpenGuildInvitesGuiAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final Guild guild;
    private final LangAPI lang;
    private final GuildInvitesGuiFactory guildInvitesGuiFactory;

    public OpenGuildInvitesGuiAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guild = guild;
        this.lang = plugin.getLang();
        this.guildInvitesGuiFactory = new GuildInvitesGuiFactory(this.plugin);
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        Inventory inventory = this.guildInvitesGuiFactory.createGui(this.guild);
        whoClicked.openInventory(inventory);
    }
}
