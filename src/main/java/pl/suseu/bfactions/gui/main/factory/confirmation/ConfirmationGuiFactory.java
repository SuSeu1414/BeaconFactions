package pl.suseu.bfactions.gui.main.factory.confirmation;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.item.ItemRepository;

public class ConfirmationGuiFactory {

    private final BFactions plugin;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ConfirmationGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.itemRepository = plugin.getItemRepository();
        this.userRepository = plugin.getUserRepository();
    }

    public Inventory createGui(ClickAction action) {
        CustomInventoryHolder holder = new CustomInventoryHolder(plugin.getSettings().guiConfirmationTitle, 9 * 3);

        ItemStack confirmItem = this.itemRepository.getItem("confirm", false);
        holder.set(13, confirmItem, action);

        return holder.getInventory();
    }
}
