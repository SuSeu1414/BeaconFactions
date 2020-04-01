package pl.suseu.bfactions.item;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;

import java.io.File;
import java.io.IOException;

public class ItemSerializer {

    private final File file;
    private final BFactions plugin;
    private final YamlConfiguration yaml;

    public ItemSerializer(BFactions plugin, String filename) throws IOException {
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), filename);

        if (!this.file.exists()) {
            this.file.createNewFile();
        }

        this.yaml = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean saveItem(String name, ItemStack itemStack) {
        this.yaml.set(name, itemStack);
        try {
            this.yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ItemStack loadItem(String name) {
        if (!this.yaml.isItemStack("name")) {
            return null;
        }
        return this.yaml.getItemStack(name);
    }

    public YamlConfiguration getYaml() {
        return yaml;
    }
}
