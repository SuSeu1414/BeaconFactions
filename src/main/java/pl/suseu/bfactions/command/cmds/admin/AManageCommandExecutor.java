package pl.suseu.bfactions.command.cmds.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.factory.MainGuiFactory;
import pl.suseu.bfactions.gui.main.factory.paginator.GuildPaginatorFactory;

import java.util.List;

public class AManageCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final MainGuiFactory mainGuiFactory;

    public AManageCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.mainGuiFactory = new MainGuiFactory(plugin);
    }


    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player pSender = (Player) sender;
        new GuildPaginatorFactory(this.plugin).openGuildsGui(pSender, args.get(0),
                clickedGuild -> pSender.openInventory(mainGuiFactory.createGui(pSender, clickedGuild)));
    }
}
