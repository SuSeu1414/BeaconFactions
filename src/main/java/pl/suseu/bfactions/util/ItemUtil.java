package pl.suseu.bfactions.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

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

}
