package pl.suseu.bfactions.gui.main.factory.paginator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.GuildClickAction;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.AbstractMap;
import java.util.stream.Collectors;

public class GuildPaginatorFactory {

    private final BFactions plugin;
    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public GuildPaginatorFactory(BFactions plugin) {
        this.plugin = plugin;
        this.guildRepository = plugin.getGuildRepository();
        this.userRepository = plugin.getUserRepository();
        this.itemRepository = plugin.getItemRepository();
    }

    public void openGuildsGui(Player playerToOpen, String memberNickname, GuildClickAction guildClickAction) {
        User opener = this.userRepository.getUser(playerToOpen.getUniqueId());
        Inventory inv = new PaginatorFactory(this.plugin).createPaginator(playerToOpen, "Choose guild", 6, 1,
                this.guildRepository.getGuilds().stream()
                        .flatMap(guild -> guild.getMembersAndOwner().stream())
                        .distinct()
                        .filter(member -> member.getName().equalsIgnoreCase(memberNickname))
                        .flatMap(member -> member.getGuilds().stream())
                        .distinct()
                        .sorted()
                        .map(guild -> {
                            ItemStack itemStack = this.itemRepository.getItem("choose-guild", opener.isDefaultItems());
                            ItemUtil.replace(itemStack, "%name%", guild.getName());
                            ItemUtil.replace(itemStack, "%owner%", guild.getOwner().getName());
                            ClickAction action = whoClicked -> guildClickAction.execute(guild);
                            return new AbstractMap.SimpleEntry<>(itemStack, action);
                        })
                        .collect(Collectors.toList()))
                .getInventory();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> playerToOpen.openInventory(inv));
    }

}
