package pl.suseu.bfactions.gui.main.action.permission;

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
import pl.suseu.bfactions.gui.main.factory.permission.ManageGuildPermissionsGuiFactory;

public class OpenManageGuildPermissionsGuiAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final Guild guild;
    private final LangAPI lang;
    private final ManageGuildPermissionsGuiFactory manageGuildPermissionsGuiFactory;

    public OpenManageGuildPermissionsGuiAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guild = guild;
        this.lang = plugin.getLang();
        this.manageGuildPermissionsGuiFactory = new ManageGuildPermissionsGuiFactory(plugin);
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE, true)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }


        Inventory inv = this.manageGuildPermissionsGuiFactory.createGui(this.guild);
        if (inv == null) {
            return;
        }
        whoClicked.openInventory(inv);
    }

}
