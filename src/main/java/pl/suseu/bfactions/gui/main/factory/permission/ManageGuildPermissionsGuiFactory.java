package pl.suseu.bfactions.gui.main.factory.permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.gui.main.action.permission.OpenManageMemberPermissionsGuiAction;
import pl.suseu.bfactions.gui.main.factory.MainGuiFactory;
import pl.suseu.bfactions.gui.main.factory.paginator.PaginatorFactory;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ManageGuildPermissionsGuiFactory {

    private final BFactions plugin;
    private final LangAPI lang;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MainGuiFactory mainGuiFactory;

    public ManageGuildPermissionsGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.itemRepository = plugin.getItemRepository();
        this.userRepository = plugin.getUserRepository();
        this.mainGuiFactory = new MainGuiFactory(plugin);
    }

    public Inventory createGui(Player player, Guild guild, List<User> members) {
        if (members == null) {
            members = new ArrayList<>(guild.getMembers());
        }
        members = members.stream()
                .sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList());

        List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items = new ArrayList<>();

        User opener = this.userRepository.getUser(player.getUniqueId());

        for (User member : members) {
            ItemStack itemStack = this.itemRepository.getItem("member-info-list", opener.isDefaultItems());
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return null;
            }

            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = ((SkullMeta) itemMeta);
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUuid()));
                itemStack.setItemMeta(skullMeta);
            }

            ItemUtil.replace(itemStack, "%player%", member.getName());

            OpenManageMemberPermissionsGuiAction action =
                    new OpenManageMemberPermissionsGuiAction(this.plugin, guild, member, opener.isDefaultItems());
            items.add(new AbstractMap.SimpleEntry<>(itemStack, action));
        }

        PaginatorFactory paginatorFactory = new PaginatorFactory(this.plugin);
        String title = plugin.getSettings().guiManageMembersTitle;
        CustomInventoryHolder paginator = paginatorFactory.createPaginator(player, title, 6, 1, items);

        ItemStack backToMainItem = this.itemRepository.getItem("back-to-main", opener.isDefaultItems());
        ClickAction backToMainAction = whoClicked -> {
            Inventory gui = this.mainGuiFactory.createGui(whoClicked, guild);
            whoClicked.openInventory(gui);
        };

        paginator.set(53, backToMainItem, backToMainAction);

        ItemStack searchItem = this.itemRepository.getItem("search-members", opener.isDefaultItems());
        ClickAction searchAction = new SearchMembersAction(this.plugin, player, guild);
        paginator.set(4, searchItem, searchAction);

        ItemStack filler = this.itemRepository.getItem("permissions-filler", opener.isDefaultItems());
        for (int i : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 45, 46, 48, 50, 52}) {
            paginator.setItem(i, filler);
        }


        return paginator.getInventory();
    }

}
