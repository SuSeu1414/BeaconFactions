package pl.suseu.bfactions.gui.action;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.CustomInventoryHolder;
import pl.suseu.bfactions.gui.paginator.PaginatorFactory;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OpenManageGuildPermissionsGuiAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Guild guild;
    private final LangAPI lang;

    public OpenManageGuildPermissionsGuiAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guild = guild;
        this.lang = plugin.getLang();
        this.itemRepository = plugin.getItemRepository();
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        Set<User> members = this.guild.getMembers();
        List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items = new ArrayList<>();

        for (User member : members) {
            ItemStack itemStack = this.itemRepository.getItem("member-info");
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return;
            }

            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = ((SkullMeta) itemMeta);
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUuid()));
                itemStack.setItemMeta(skullMeta);
            }

            ItemUtil.replace(itemStack, "%name%", member.getName());

            OpenManageMemberPermissionsGuiAction action =
                    new OpenManageMemberPermissionsGuiAction(this.plugin, this.guild, member);
            items.add(new AbstractMap.SimpleEntry<>(itemStack, action));
        }

        PaginatorFactory paginatorFactory = new PaginatorFactory(this.plugin);
        CustomInventoryHolder paginator = paginatorFactory.createPaginator("Test %page%/%pages%", 6, 1, items);
        Inventory inv = paginator.getInventory();
        whoClicked.openInventory(inv);
    }

}
