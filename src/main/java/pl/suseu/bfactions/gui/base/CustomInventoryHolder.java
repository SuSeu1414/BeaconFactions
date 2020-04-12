package pl.suseu.bfactions.gui.base;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomInventoryHolder implements InventoryHolder {

    private final String title;
    private final int size;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, ClickAction> actions = new HashMap<>();

    private Inventory inventory;

    public CustomInventoryHolder(String title, int size) {
        this.title = title;
        this.size = size;
    }

    public void set(int slot, ItemStack itemStack, ClickAction action) {
        this.setItem(slot, itemStack);
        this.setAction(slot, action);
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.items.put(slot, itemStack);
        if (this.inventory != null) {
            this.inventory.setItem(slot, itemStack);
        }
    }

    public void setAction(int slot, ClickAction action) {
        this.actions.put(slot, action);
    }

    public ClickAction getAction(int slot) {
        ClickAction action = this.actions.get(slot);
        if (action != null) {
            return action;
        }

        return whoClicked -> {
        };
    }

    @Override
    public Inventory getInventory() {
        this.inventory = Bukkit.createInventory(this, this.size, this.title);
        for (Map.Entry<Integer, ItemStack> entry : this.items.entrySet()) {
            this.inventory.setItem(entry.getKey(), entry.getValue());
        }

        return this.inventory;
    }
}
