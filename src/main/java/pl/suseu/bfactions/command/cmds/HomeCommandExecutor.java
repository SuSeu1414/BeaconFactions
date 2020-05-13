package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.factory.paginator.GuildPaginatorFactory;
import pl.suseu.bfactions.settings.Settings;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.List;

public class HomeCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final EventWaiter eventWaiter;
    private final Settings settings;

    public HomeCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.eventWaiter = plugin.getEventWaiter();
        this.settings = plugin.getSettings();
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
                            this.lang.sendMessage("teleportation-started", sender, "%delay%", settings.guildHomeDelay + "");
                            pSender.closeInventory();
                            this.eventWaiter.waitForEvent(PlayerMoveEvent.class, EventPriority.NORMAL, event -> {
                                return event.getTo() == null
                                        || (event.getPlayer().equals(pSender)
                                        && event.getFrom().getX() != event.getTo().getX()
                                        && event.getFrom().getY() != event.getTo().getY()
                                        && event.getFrom().getZ() != event.getTo().getZ());
                            }, event -> {
                                this.lang.sendMessage("teleportation-cancelled", sender);
                            }, settings.guildHomeDelay * 20, () -> {
                                this.lang.sendMessage("teleportation-success", sender);
                                pSender.teleport(clickedGuild.getHome());
                            });


                        });
    }
}
