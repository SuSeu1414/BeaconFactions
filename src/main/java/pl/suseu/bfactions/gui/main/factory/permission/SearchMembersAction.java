package pl.suseu.bfactions.gui.main.factory.permission;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.stream.Collectors;

public class SearchMembersAction implements ClickAction {

    private final BFactions plugin;
    private final Player player;
    private final Guild guild;
    private final EventWaiter eventWaiter;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final ManageGuildPermissionsGuiFactory manageGuildPermissionsGuiFactory;


    public SearchMembersAction(BFactions plugin, Player player, Guild guild) {
        this.plugin = plugin;
        this.player = player;
        this.guild = guild;
        this.eventWaiter = plugin.getEventWaiter();
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.manageGuildPermissionsGuiFactory = new ManageGuildPermissionsGuiFactory(plugin);
    }

    @Override
    public void execute(Player whoClicked) {
        whoClicked.closeInventory();
        this.lang.sendMessage("type-player-name-in-chat", whoClicked);
        this.eventWaiter.waitForEvent(AsyncPlayerChatEvent.class, EventPriority.NORMAL,
                event -> event.getPlayer().equals(whoClicked),
                event -> {
                    event.setCancelled(true);
                    Inventory inv = this.manageGuildPermissionsGuiFactory
                            .createGui(whoClicked, this.guild, this.userRepository.getUsers(event.getMessage()).stream()
                                    .filter(user -> this.guild.getMembers().contains(user))
                                    .collect(Collectors.toList()));
                    if (inv == null) {
                        return;
                    }
                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> whoClicked.openInventory(inv));
                }, 20 * 30, () -> {
                    this.lang.sendMessage("search-listener-timed-out", whoClicked);
                });
    }
}
