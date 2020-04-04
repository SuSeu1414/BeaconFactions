package pl.suseu.bfactions.gui.action;

import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;

public class OpenManagePermissionsGuiAction implements ClickAction {

    private final BFactions plugin;
    private final Guild guild;

    public OpenManagePermissionsGuiAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.guild = guild;
    }

    @Override
    public void execute(Player whoClicked) {
        whoClicked.closeInventory();
        whoClicked.sendMessage("TODO: open members inventory");
    }

}
