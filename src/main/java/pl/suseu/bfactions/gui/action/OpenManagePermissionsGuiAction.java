package pl.suseu.bfactions.gui.action;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.CustomInventoryHolder;
import pl.suseu.bfactions.gui.paginator.PaginatorFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class OpenManagePermissionsGuiAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final LangAPI lang;
    private final PaginatorFactory paginatorFactory;

    private final Guild guild;

    public OpenManagePermissionsGuiAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.lang = plugin.getLang();
        this.paginatorFactory = new PaginatorFactory(plugin);
        this.guild = guild;
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        whoClicked.closeInventory();

        List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items = new ArrayList<>();

        for (int i = 0; i <= 108; i++) {
            ItemStack is = new ItemStack(Material.PAPER, 1);
            ItemMeta itemMeta = is.getItemMeta();
            if (itemMeta == null) {
                continue;
            }
            itemMeta.setDisplayName("Item " + i);
            is.setItemMeta(itemMeta);
            int finalI = i;
            items.add(new AbstractMap.SimpleEntry<>(is, whoClicked1 -> whoClicked1.sendMessage(finalI + "")));
        }

        CustomInventoryHolder paginator = this.paginatorFactory.createPaginator("Test %page%/%pages%", 6, 1, items);
        Inventory inv = paginator.getInventory();
        whoClicked.openInventory(inv);
    }

}
