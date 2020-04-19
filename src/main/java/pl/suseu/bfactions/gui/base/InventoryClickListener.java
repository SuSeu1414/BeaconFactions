package pl.suseu.bfactions.gui.base;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.suseu.bfactions.BFactions;

public class InventoryClickListener implements Listener {

    private final BFactions plugin;

    public InventoryClickListener(BFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

//        if (inv == null) {
//            return;
//        }

        InventoryHolder inventoryHolder = inv.getHolder();

        if (inventoryHolder == null) {
            return;
        }

        if (inventoryHolder instanceof CustomInventoryHolder) {
            event.setCancelled(true);

            CustomInventoryHolder holder = (CustomInventoryHolder) inventoryHolder;

            holder.getAction(event.getRawSlot()).execute(((Player) event.getWhoClicked()));
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        inventoryHolder = clickedInventory.getHolder();

        if (inventoryHolder == null) {
            return;
        }

        if (inventoryHolder instanceof UndamageableFieldInventoryHolder) {
            if (event.getRawSlot() != 13) {
                event.setCancelled(true);
            }
        }


    }

}
