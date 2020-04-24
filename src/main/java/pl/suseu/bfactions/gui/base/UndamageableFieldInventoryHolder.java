package pl.suseu.bfactions.gui.base;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.item.ItemRepository;

public class UndamageableFieldInventoryHolder implements InventoryHolder {

    private final BFactions plugin;
    private final ItemRepository itemRepository;

    public UndamageableFieldInventoryHolder(BFactions plugin) {
        this.plugin = plugin;
        this.itemRepository = plugin.getItemRepository();
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27); // todo title

        ItemStack item = this.itemRepository.getItem("blank-item-boost-undamageable-gui", false);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, item);
        }
        inv.setItem(13, null);
        return inv;
    }
}
