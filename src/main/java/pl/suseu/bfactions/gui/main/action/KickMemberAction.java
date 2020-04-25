package pl.suseu.bfactions.gui.main.action;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;

public class KickMemberAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final LangAPI lang;
    private final Guild guild;
    private final User member;

    public KickMemberAction(BFactions plugin, Guild guild, User member) {
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

        whoClicked.closeInventory();
        this.guild.removeMember(this.member);
        this.lang.sendMessage("kicked-member", whoClicked, "%player%", this.member.getName());
        Player player = Bukkit.getPlayer(this.member.getUuid());
        if (player != null) {
            this.lang.sendMessage("you-have-been-kicked-from-guild", player, "%guild%", guild.getName());
        }
    }
}
