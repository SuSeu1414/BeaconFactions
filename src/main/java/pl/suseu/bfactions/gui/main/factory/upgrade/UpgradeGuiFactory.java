package pl.suseu.bfactions.gui.main.factory.upgrade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.FieldTier;
import pl.suseu.bfactions.base.tier.RegionTier;
import pl.suseu.bfactions.base.tier.Tier;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.gui.main.action.upgrade.OpenFieldUpgradeGuiAction;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.Arrays;
import java.util.List;


public class UpgradeGuiFactory {

    private static final int[] route = new int[]{0, 9, 18, 27, 28, 29, 20, 11, 2, 3, 4, 13, 22, 31, 32, 33, 24, 15, 6, 7, 8, 17, 26, 35, 44, 53};
    private static final List<Integer> beacons = Arrays.asList(0, 29, 4, 33, 8);

    private final BFactions plugin;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Tier.TierType tierType;

    public UpgradeGuiFactory(BFactions plugin, Tier.TierType tierType) {
        this.plugin = plugin;
        this.itemRepository = plugin.getItemRepository();
        this.userRepository = plugin.getUserRepository();
        this.tierType = tierType;
    }

    // I have no idea what it does, but it somehow works so leave it pls
    // btw it's not copied from anywhere. I did it by trial and error
    private static int func(int x) {
        int size = route.length;
        if ((x + 1) % size == 0) {
            return (x + 1) / size;
        }
        if (x < size) {
            return 0;
        }

        return func(x - size) + 1;
    }

    public Inventory createGui(Player player, Guild guild, List<Tier> tiers, int currentTier) {
        User opener = this.userRepository.getUser(player.getUniqueId());

        String title = "";
        if (tierType == Tier.TierType.FIELD) {
            title = plugin.getSettings().guiFieldUpgradesTitle;
        } else if (tierType == Tier.TierType.REGION) {
            title = plugin.getSettings().guiRegionUpgradeTitle;
        }
        CustomInventoryHolder holder = new CustomInventoryHolder(title, 6 * 9);

        int routeIndex = 0;
        int tierIndex = calculateFirstTierIndex(currentTier);

        for (; routeIndex < route.length; routeIndex++) {
            if (tierIndex >= tiers.size()) {
                break;
            }

            if (tierIndex <= currentTier) {
                setObtainedItem(opener, holder, tiers, routeIndex, tierIndex);
            }

            if (tierIndex == currentTier + 1) {
                setBuyableItem(player, opener, guild, tiers, holder, routeIndex, tierIndex);
            }

            if (tierIndex > currentTier + 1) {
                setNotObtainedItem(opener, holder, tiers, routeIndex, tierIndex);
            }

            tierIndex++;
        }

        ItemStack fillerItem = this.itemRepository.getItem("upgrade-filler", opener.isDefaultItems());
        for (int slot = 0; slot < 6 * 9; slot++) {
            if (!holder.isSet(slot)) {
                holder.setItem(slot, fillerItem);
            }
        }

        return holder.getInventory();
    }

    private void setBuyableItem(Player player, User opener, Guild guild, List<Tier> tiers, CustomInventoryHolder holder, int routeIndex, int tierIndex) {
        ItemStack itemStack;
        ClickAction action;
        final Tier tier = tiers.get(tierIndex);
        final int slot = route[routeIndex];

//        if (beacons.contains(slot)) {
//            itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
//        } else {
//            itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
//        }
        itemStack = this.itemRepository.getItem(tier.getPathItemBuy(), opener.isDefaultItems());
        replaceItem(itemStack, tier);

        action = whoClicked -> {
            if (!tier.canAfford(player, guild)) {
                this.plugin.getLang().sendMessage("cannot-afford", whoClicked);
                return;
            }
            tier.buy(player, guild);
            guild.setTier(tier);
            if (tierType == Tier.TierType.FIELD) {
                this.plugin.getLang().sendMessage("field-upgraded", whoClicked);
            } else if (tierType == Tier.TierType.REGION) {
                this.plugin.getLang().sendMessage("region-upgraded", whoClicked);
            }
            new OpenFieldUpgradeGuiAction(this.plugin, guild, tierType).execute(whoClicked);
        };

        holder.set(slot, itemStack, action);
    }

    private void setObtainedItem(User opener, CustomInventoryHolder holder, List<Tier> tiers, int routeIndex, int tierIndex) {
        ItemStack itemStack;
        ClickAction action = null;
        int slot = route[routeIndex];
        final Tier tier = tiers.get(tierIndex);
//        if (beacons.contains(slot)) {
//            itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
//        } else {
//            itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
//        }
        itemStack = this.itemRepository.getItem(tier.getPathItemOwned(), opener.isDefaultItems());
        replaceItem(itemStack, tier);
        holder.set(slot, itemStack, action);
    }

    private void setNotObtainedItem(User opener, CustomInventoryHolder holder, List<Tier> tiers, int routeIndex, int tierIndex) {
        ItemStack itemStack;
        ClickAction action = null;
        int slot = route[routeIndex];
        final Tier tier = tiers.get(tierIndex);
//        if (beacons.contains(slot)) {
//            itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
//        } else {
//            itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
//        }
        itemStack = this.itemRepository.getItem(tier.getPathItem(), opener.isDefaultItems());
        replaceItem(itemStack, tier);
        holder.set(slot, itemStack, action);
    }

    private void replaceItem(ItemStack itemStack, Tier tier) {
        ItemUtil.replace(itemStack, "%tier%", String.valueOf(tier.getTier()));
        //todo placeholders
//        ItemUtil.replace(itemStack, "%cost%", String.valueOf(tier.getCost()));
        if (tier instanceof FieldTier) {
            ItemUtil.replace(itemStack, "%max_energy%", String.format("%.0f", ((FieldTier) tier).getMaxEnergy()));
        }
        if (tier instanceof RegionTier) {
            ItemUtil.replace(itemStack, "%radius%", String.valueOf(((RegionTier) tier).getRadius()));
            ItemUtil.replace(itemStack, "%energy_drain%", String.valueOf(((RegionTier) tier).getDrainAmount()));
        }
    }

    private int calculateFirstTierIndex(int x) {
        return func(x) * route.length;
    }

}
