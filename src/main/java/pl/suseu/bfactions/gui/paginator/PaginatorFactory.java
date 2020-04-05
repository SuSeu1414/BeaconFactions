package pl.suseu.bfactions.gui.paginator;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.gui.CustomInventoryHolder;
import pl.suseu.bfactions.gui.action.ChangePageAction;
import pl.suseu.bfactions.gui.action.ClickAction;
import pl.suseu.bfactions.item.ItemRepository;

import java.util.AbstractMap;
import java.util.List;
import java.util.logging.Logger;

public class PaginatorFactory {

    private final BFactions plugin;
    private final Logger logger;
    private final ItemRepository itemRepository;

    public PaginatorFactory(BFactions plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.itemRepository = plugin.getItemRepository();
    }

    public CustomInventoryHolder createPaginator(String rawTitle, int rows, int page,
                                                 List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items) {
        if (rows < 3) {
            logger.warning("Cannot create paginator with less than 3 rows!");
            return null;
        }

        int itemsPerPage = (rows - 2) * 9;
        int maxPages = maxPages(itemsPerPage, items.size());
        int firstIndex = (page - 1) * itemsPerPage;

        String title = StringUtils.replace(rawTitle, "%page%", "" + page);
        title = StringUtils.replace(title, "%pages%", "" + maxPages);
        int size = rows * 9;
        CustomInventoryHolder holder = new CustomInventoryHolder(title, size);

        boolean isLastPage = page >= maxPages;
        int lastIndex;
        if (isLastPage) {
            lastIndex = items.size() - 1;
        } else {
            lastIndex = firstIndex + itemsPerPage - 1;
        }
        int slot = 9;
        int index = firstIndex;
        for (; slot + firstIndex - 9 <= lastIndex; slot++) {
            holder.set(slot, items.get(index).getKey(), items.get(index).getValue());
            index++;
        }


        ClickAction nextPageAction;
        ClickAction prevPageAction;
        ClickAction closeAction = HumanEntity::closeInventory;

        if (page + 1 > maxPages) {
            nextPageAction = whoClicked -> whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
        } else {
            nextPageAction = new ChangePageAction(plugin, this, rawTitle, rows, page + 1, items);
        }

        if (page - 1 < 1) {
            prevPageAction = whoClicked -> whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
        } else {
            prevPageAction = new ChangePageAction(plugin, this, rawTitle, rows, page - 1, items);
        }

        holder.set(size - 3, this.itemRepository.getItem("next-page"), nextPageAction);
        holder.set(size - 5, this.itemRepository.getItem("close-gui"), closeAction);
        holder.set(size - 7, this.itemRepository.getItem("previous-page"), prevPageAction);

        return holder;
    }

    private int firstIndex(int page, int itemsPerPage) {
        if (page == 1) {
            return 9;
        }

        return itemsPerPage * page + 9;
    }

    private int maxPages(int itemsPerPage, int items) {
        return (int) Math.ceil((double) items / itemsPerPage);
    }

}
