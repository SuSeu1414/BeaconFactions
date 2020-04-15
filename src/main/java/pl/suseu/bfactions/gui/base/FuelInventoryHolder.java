package pl.suseu.bfactions.gui.base;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.suseu.bfactions.base.guild.Guild;

public class FuelInventoryHolder implements InventoryHolder {

    private final Guild guild;

    public FuelInventoryHolder(Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, InventoryType.HOPPER, "Fuel"); // todo title
    }
}
