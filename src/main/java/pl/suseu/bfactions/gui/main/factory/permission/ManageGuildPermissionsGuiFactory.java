package pl.suseu.bfactions.gui.main.factory.permission;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.gui.main.action.permission.OpenManageMemberPermissionsGuiAction;
import pl.suseu.bfactions.gui.main.factory.paginator.PaginatorFactory;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ManageGuildPermissionsGuiFactory {

    private final BFactions plugin;
    private final LangAPI lang;
    private final ItemRepository itemRepository;

    public ManageGuildPermissionsGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.itemRepository = plugin.getItemRepository();
    }

    public Inventory createGui(Guild guild) {
        Set<User> members = guild.getMembers();
        List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items = new ArrayList<>();

        for (User member : members) {
            ItemStack itemStack = this.itemRepository.getItem("member-info");
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
                    new OpenManageMemberPermissionsGuiAction(this.plugin, guild, member);
            items.add(new AbstractMap.SimpleEntry<>(itemStack, action));
        }

        PaginatorFactory paginatorFactory = new PaginatorFactory(this.plugin);
        CustomInventoryHolder paginator = paginatorFactory.createPaginator("Test %page%/%pages%", 6, 1, items);
        ItemStack filler = this.itemRepository.getItem("permissions-filler");
        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 48, 50, 52, 53}) {
            paginator.setItem(i, filler);
        }


        return paginator.getInventory();
    }

}
