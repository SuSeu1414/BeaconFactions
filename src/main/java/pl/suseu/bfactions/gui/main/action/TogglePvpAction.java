package pl.suseu.bfactions.gui.main.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.main.factory.MainGuiFactory;

public class TogglePvpAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final LangAPI lang;
    private final Guild guild;
    private final User member;

    public TogglePvpAction(BFactions plugin, Guild guild, User member) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.lang = plugin.getLang();
        this.guild = guild;
        this.member = member;
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
//        if (!this.guild.hasPermission(user, GuildPermission.MANAGE, true)) {
        if (!this.guild.isOwner(user)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        guild.setPvpEnabled(!guild.isPvpEnabled());
        whoClicked.openInventory(new MainGuiFactory(this.plugin).createGui(whoClicked, guild));
        whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

}
