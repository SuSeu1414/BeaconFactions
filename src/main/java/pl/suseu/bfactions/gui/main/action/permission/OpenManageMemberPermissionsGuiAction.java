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
import pl.suseu.bfactions.gui.main.factory.permission.ManageMemberPermissionGuiFactory;

public class OpenManageMemberPermissionsGuiAction implements ClickAction {

    private final BFactions plugin;
    private final Guild guild;
    private final User user;
    private final UserRepository userRepository;
    private final LangAPI lang;
    private final ManageMemberPermissionGuiFactory manageMemberPermissionGuiFactory;

    public OpenManageMemberPermissionsGuiAction(BFactions plugin, Guild guild, User user) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guild = guild;
        this.user = user;
        this.lang = plugin.getLang();
        this.manageMemberPermissionGuiFactory = new ManageMemberPermissionGuiFactory(plugin);
    }

    @Override
    public void execute(Player whoClickedPlayer) {
        User whoClickedUser = this.userRepository.getUser(whoClickedPlayer.getUniqueId());
        if (!this.guild.hasPermission(whoClickedUser, GuildPermission.MANAGE)) {
            whoClickedPlayer.playSound(whoClickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClickedPlayer);
            return;
        }

        Inventory inv = this.manageMemberPermissionGuiFactory.createGui(guild, user);
        if (inv == null) {
            return;
        }
        whoClickedPlayer.openInventory(inv);
    }

}
