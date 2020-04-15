package pl.suseu.bfactions.gui.main.factory.upgrade;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.item.ItemRepository;


public class SizeUpgradeGuiFactory {

    private final BFactions plugin;
    private final ItemRepository itemRepository;

    public SizeUpgradeGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.itemRepository = plugin.getItemRepository();
    }
//
//    public Inventory createGui() {
//
//    }

}
