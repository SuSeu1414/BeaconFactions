package pl.suseu.bfactions.item;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.suseu.bfactions.BFactions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRepository {

    private final BFactions plugin;
    private final Map<String, ItemStack> items = new ConcurrentHashMap<>();

    public ItemRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public void addItem(String name, ItemStack item) {
        items.put(name, item);
    }

    public ItemStack getItem(String name) {
        items.computeIfAbsent(name, item -> {
            ItemStack result = new ItemStack(Material.COMMAND_BLOCK);
            ItemMeta meta = result.getItemMeta();
            if (meta == null) {
                return result;
            }
            meta.setDisplayName(name);
            result.setItemMeta(meta);
            addItem(name, result);
            return result;
        });

        return items.get(name);
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
