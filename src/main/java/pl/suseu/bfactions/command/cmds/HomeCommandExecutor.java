package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.factory.paginator.GuildPaginatorFactory;

import java.util.List;

public class HomeCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;

    public HomeCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player pSender = (Player) sender;
        new GuildPaginatorFactory(this.plugin)
                .openGuildsGui(pSender, pSender.getName(),
                        clickedGuild -> {
                            if (clickedGuild.getHome() == null) {
                                this.lang.sendMessage("guild-home-not-set", sender);
                                return;
                            }
                            pSender.teleport(clickedGuild.getHome());
                            pSender.closeInventory();
                        });
    }
}
