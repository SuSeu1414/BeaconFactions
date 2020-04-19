package pl.suseu.bfactions.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.suseu.bfactions.BFactions;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    private static final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    public static void replace(ItemStack is, String rpl1, String rpl2) {
        ItemMeta im = is.getItemMeta();
        if (im == null) { // should never happen
            return;
        }
        String name = im.getDisplayName();
        List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();

        name = name.replace(rpl1, rpl2);
        //noinspection ConstantConditions
        lore = replaceList(lore, rpl1, rpl2);

        im.setDisplayName(name);
        im.setLore(lore);
        is.setItemMeta(im);
    }

    private static List<String> replaceList(List<String> list, String rpl1, String rpl2) {
        List<String> toReturn = new ArrayList<>();
        list.forEach(s -> toReturn.add(s.replace(rpl1, rpl2)));
        return toReturn;
    }

    public static void addBoostTags(ItemMeta itemMeta, String boost, String id) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey isBoostKey = new NamespacedKey(plugin, "is-boost-" + boost + "-item");
        NamespacedKey boostIdKey = new NamespacedKey(plugin, "boost-" + boost + "-item-id");
        container.set(isBoostKey, PersistentDataType.BYTE, (byte) 1);
        container.set(boostIdKey, PersistentDataType.STRING, id);
        if (boost.equals("undamageable")) {
            long time = plugin.getSettings().fieldBoostUndamageableItems.get(id);
            if (time != 0) {
                setBoostUndamageableRemainingTime(itemMeta, time);
            }
        }
    }


    public static long getBoostUndamageableRemainingTime(ItemMeta itemMeta) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "boost-undamageable-time");
        return container.getOrDefault(key, PersistentDataType.LONG, 0L);
    }

    public static void setBoostUndamageableRemainingTime(ItemMeta itemMeta, long time) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "boost-undamageable-time");
        container.set(key, PersistentDataType.LONG, time);
    }

    public static String getBoostItemId(ItemMeta itemMeta, String boost) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key2 = new NamespacedKey(plugin, "boost-" + boost + "-item-id");
        return container.getOrDefault(key2, PersistentDataType.STRING, "error");
    }

    public static boolean isBoostItem(ItemMeta itemMeta, String boost) {
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "is-boost-" + boost + "-item");
        return container.getOrDefault(key, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public static boolean isBoostItem(String boost, String id) {
        return id.startsWith("boost-" + boost + "-");
    }
}
