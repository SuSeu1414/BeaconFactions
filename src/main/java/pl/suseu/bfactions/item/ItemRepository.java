package pl.suseu.bfactions.item;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.util.ItemUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRepository {

    private final BFactions plugin;
    private final Map<String, ItemStack> items = new ConcurrentHashMap<>();

    public ItemRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public void addItem(String name, ItemStack item) {
        ItemStack clone = item.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        // do stuff with item meta
        clone.setItemMeta(itemMeta);
        items.put(name, clone);

        if (name.equalsIgnoreCase("blank-item-boost-undamageable-gui")) {
            for (Field field : this.plugin.getFieldRepository().getFields()) {
                field.updateUndamageableItemInventory();
            }
        }
    }

    public ItemStack getItem(String name) {
        ItemStack itemStack = items.get(name);
        if (itemStack == null) {
            return getDefaultItem(name);
        }

        ItemStack clone = itemStack.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta == null) {
            return getDefaultItem("error"); // should never happen
        }

        if (ItemUtil.isBoostItem("undamageable", name)) {
            ItemUtil.addBoostTags(itemMeta, "undamageable", name);
            ItemUtil.replace(clone, "%time%", "some time"); // todo time
        }

        clone.setItemMeta(itemMeta);

        return clone;
    }

    private ItemStack getDefaultItem(String name) {
        ItemStack result = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            return result;
        }
        meta.setDisplayName("Default (" + name + ")");
        meta.setLore(Arrays.asList("To set this item use", "/bfactions setitem " + name));
        result.setItemMeta(meta);
        addItem(name, result);
        return result.clone();
    }

    public boolean save() {
        ItemSerializer serializer;

        try {
            serializer = new ItemSerializer(plugin, "items.yml");
        } catch (IOException e) {
            plugin.getLogger().warning("Cannot save items!");
            e.printStackTrace();
            return false;
        }

        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            serializer.saveItem(entry.getKey(), entry.getValue());
        }

        return true;
    }

    public boolean load() {
        ItemSerializer serializer;

        try {
            serializer = new ItemSerializer(plugin, "items.yml");
        } catch (IOException e) {
            plugin.getLogger().warning("Cannot load items!");
            e.printStackTrace();
            return false;
        }

        YamlConfiguration yaml = serializer.getYaml();
        for (String s : yaml.getKeys(false)) {
            if (!yaml.isItemStack(s)) {
                continue;
            }
            items.put(s, yaml.getItemStack(s));
        }

        return true;
    }
}
