package pl.suseu.bfactions.gui.main.factory.paginator;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.gui.main.action.paginator.ChangePageAction;
import pl.suseu.bfactions.item.ItemRepository;

import java.util.AbstractMap;
import java.util.List;
import java.util.logging.Logger;

public class PaginatorFactory {

    private final BFactions plugin;
    private final Logger logger;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public PaginatorFactory(BFactions plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.itemRepository = plugin.getItemRepository();
        this.userRepository = plugin.getUserRepository();
    }

    public CustomInventoryHolder createPaginator(Player player, String rawTitle, int rows, int page,
                                                 List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items) {

        User opener = this.userRepository.getUser(player.getUniqueId());

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

        holder.set(size - 3, this.itemRepository.getItem("next-page", opener.isDefaultItems()), nextPageAction);
        holder.set(size - 5, this.itemRepository.getItem("close-gui", opener.isDefaultItems()), closeAction);
        holder.set(size - 7, this.itemRepository.getItem("previous-page", opener.isDefaultItems()), prevPageAction);

        return holder;
    }

    private int maxPages(int itemsPerPage, int items) {
        int maxPages = (int) Math.ceil((double) items / itemsPerPage);
        if (maxPages == 0) {
            maxPages = 1;
        }
        return maxPages;
    }

}
