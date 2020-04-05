package pl.suseu.bfactions.gui.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.gui.paginator.PaginatorFactory;

import java.util.AbstractMap;
import java.util.List;

public class ChangePageAction implements ClickAction {

    private final BFactions plugin;
    private final PaginatorFactory paginatorFactory;

    private final String title;
    private final int rows;
    private final int page;
    private final List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items;

    public ChangePageAction(BFactions plugin,
                            PaginatorFactory paginatorFactory,
                            String title, int rows, int page,
                            List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items) {
        this.plugin = plugin;
        this.paginatorFactory = paginatorFactory;
        this.title = title;
        this.rows = rows;
        this.page = page;
        this.items = items;
    }

    @Override
    public void execute(Player whoClicked) {
//        whoClicked.closeInventory();
        InventoryHolder holder = this.paginatorFactory.createPaginator(this.title, this.rows, this.page, this.items);
        if (holder == null) {
            return;
        }

        Inventory inv = holder.getInventory();
        whoClicked.openInventory(inv);
    }
}
