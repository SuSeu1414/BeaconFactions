package pl.suseu.bfactions.gui.main;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.gui.CustomInventoryHolder;
import pl.suseu.bfactions.gui.action.ChangeGuildNameAction;
import pl.suseu.bfactions.gui.action.OpenGuildInvitesGuiAction;
import pl.suseu.bfactions.gui.action.OpenManageGuildPermissionsGuiAction;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.settings.Settings;

public class MainGuiFactory {

    private final BFactions plugin;
    private final ItemRepository itemRepository;
    private final Settings settings;

    public MainGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.itemRepository = this.plugin.getItemRepository();
        this.settings = this.plugin.getSettings();
    }

    public Inventory createGui(Player player, Guild guild) {
        String title = this.settings.guiMainTitle;
        title = StringUtils.replace(title, "%guild%", guild.getName());
        int size = 6 * 9;

        CustomInventoryHolder holder = new CustomInventoryHolder(title, size);

        ItemStack manageInvitesItem = this.itemRepository.getItem("manage-invites");
        holder.set(14, manageInvitesItem, new OpenGuildInvitesGuiAction(this.plugin, guild));

        ItemStack managePermissionsItem = this.itemRepository.getItem("manage-permissions");
        holder.set(23, managePermissionsItem, new OpenManageGuildPermissionsGuiAction(this.plugin, guild));

        ItemStack changeNameItem = this.itemRepository.getItem("change-name");
        holder.set(32, changeNameItem, new ChangeGuildNameAction(plugin, guild));

        ItemStack book1 = this.itemRepository.getItem("main-book-1");
        ItemStack book2 = this.itemRepository.getItem("main-book-2");
        ItemStack book3 = this.itemRepository.getItem("main-book-3");
        ItemStack book4 = this.itemRepository.getItem("main-book-4");


        return holder.getInventory();
    }

}
