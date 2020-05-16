package pl.suseu.bfactions.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;

public class CraftingItem {

    private final BFactions plugin = ((BFactions) Bukkit.getServer().getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final String id;
    private final Material material;
    private final ItemType type;

    public CraftingItem(Material material) {
        this.id = null;
        this.material = material;
        this.type = ItemType.VANILLA;
    }

    public CraftingItem(String id) {
        this.id = id;
        this.material = null;
        this.type = ItemType.BFACTIONS;
    }

    public CraftingItem() {
        this.id = null;
        this.material = null;
        this.type = ItemType.NULL;
    }

    public static CraftingItem deserialize(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null!");
        }

        String[] split = id.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }

        if (split[0].equalsIgnoreCase("minecraft")) {
            Material material = Material.matchMaterial(split[1]);
            if (material == null) {
                throw new IllegalArgumentException("Invalid material: " + split[1]);
            }
            return new CraftingItem(material);
        } else if (split[0].equalsIgnoreCase("bfactions")) {
            return new CraftingItem(split[1]);
        } else {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
    }

    public ItemStack getItem() {
        if (this.type == ItemType.VANILLA) {
            return new ItemStack(this.material);
        } else if (this.type == ItemType.BFACTIONS) {
            return plugin.getItemRepository().getItem(this.id, false);
        } else {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public enum ItemType {
        VANILLA,
        BFACTIONS,
        NULL
    }
}
