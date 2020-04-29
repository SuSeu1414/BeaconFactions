package pl.suseu.bfactions.gui.main.action;

import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.gui.base.ClickAction;

public class LeaveGuildAction implements ClickAction {

    private final BFactions plugin;
    private final User user;
    private final Guild guild;
    private final LangAPI lang;

    public LeaveGuildAction(BFactions plugin, User user, Guild guild) {
        this.plugin = plugin;
        this.user = user;
        this.guild = guild;
        this.lang = plugin.getLang();
    }

    @Override
    public void execute(Player whoClicked) {
        whoClicked.closeInventory();
        this.guild.removeMember(user);
        this.guild.getRegion().teleportToSafety(whoClicked);
        this.lang.sendMessage("guild-quit", whoClicked);
    }
}
