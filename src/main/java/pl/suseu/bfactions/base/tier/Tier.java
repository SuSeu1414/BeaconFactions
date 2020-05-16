package pl.suseu.bfactions.base.tier;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.cost.TierCost;
import pl.suseu.bfactions.base.tier.cost.TierEnergyCost;
import pl.suseu.bfactions.base.tier.cost.TierItemCost;
import pl.suseu.bfactions.base.tier.cost.TierMoneyCost;

import java.util.ArrayList;
import java.util.List;

public abstract class Tier {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final int tier;
    private final String pathItem;
    private List<String> lore;
    private final String pathItemBuy;
    private List<String> loreBuy;
    private final String pathItemOwned;
    private List<String> loreOwned;
    private final List<TierCost> cost;
    private final TierType type;

    public Tier(int tier, String pathItem, String pathItemBuy, String pathItemOwned, List<TierCost> cost, TierType type) {
        this.tier = tier;
        this.pathItem = pathItem;
        this.pathItemBuy = pathItemBuy;
        this.pathItemOwned = pathItemOwned;
        this.cost = cost;
        this.type = type;
    }

    public int getTier() {
        return tier;
    }

    public boolean canAfford(Player player, Guild guild) {
        for (TierCost cost : this.cost) {
            if (!cost.canBuy(player, guild)) {
                return false;
            }
        }
        return true;
    }

    public void buy(Player player, Guild guild) {
        for (TierCost cost : this.cost) {
            cost.buy(player, guild);
        }
        guild.setTier(this);
    }

    public TierType getType() {
        return type;
    }

    public String getPathItem() {
        return pathItem;
    }

    public String getPathItemBuy() {
        return pathItemBuy;
    }

    public String getPathItemOwned() {
        return pathItemOwned;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<String> getLoreBuy() {
        return loreBuy;
    }

    public void setLoreBuy(List<String> loreBuy) {
        this.loreBuy = loreBuy;
    }

    public List<String> getLoreOwned() {
        return loreOwned;
    }

    public void setLoreOwned(List<String> loreOwned) {
        this.loreOwned = loreOwned;
    }

    public List<String> getCostPlaceholder(boolean checkCosts, Player player, Guild guild) {
        List<String> price = new ArrayList<>();
        price.add("" + ChatColor.BLUE + ChatColor.BOLD + ">> Cost: ");
        for (TierCost cost : this.cost) {
            if (cost instanceof TierMoneyCost) {
                double amount = ((TierMoneyCost) cost).getAmount();
//                String amountS = String.format("%.0f (%.2f%%)", amount);
                String amountS = String.format("%.1f", amount);
                if (checkCosts) {
                    price.add("  "
                            + (cost.canBuy(player, guild) ? ChatColor.GREEN + "\u2713" : ChatColor.RED + "\u2717")
                            + ChatColor.BOLD + " $" + amountS);
                } else {
                    price.add("  " + ChatColor.BLUE + ChatColor.BOLD + ">> $" + amountS);
                }
            }
            if (cost instanceof TierEnergyCost) {
                double amount = ((TierEnergyCost) cost).getAmount();
                String amountS = String.format("%.1f", amount);
                if (checkCosts) {
                    price.add("  "
                            + (cost.canBuy(player, guild) ? ChatColor.GREEN + "\u2713" : ChatColor.RED + "\u2717")
                            + ChatColor.BOLD + " " + amountS + " energy");
                } else {
                    price.add("  " + ChatColor.BLUE + ChatColor.BOLD + ">> " + amountS + " energy");
                }
            }
            if (cost instanceof TierItemCost) {
                if (plugin == null) {
                    continue;
                }
                ItemStack item = this.plugin.getItemRepository().getItem(((TierItemCost) cost).getItem(), false);
                ItemMeta im = item.getItemMeta();
                if (im == null) {
                    continue;
                }
                int amount = ((TierItemCost) cost).getAmount();
                String itemName = (im.hasDisplayName()) ? im.getDisplayName()
                        : StringUtils.capitalize(item.getType().toString().replace("_", " ").toLowerCase());
                if (checkCosts) {
                    price.add("  "
                            + (cost.canBuy(player, guild) ? ChatColor.GREEN + "\u2713" : ChatColor.RED + "\u2717")
                            + ChatColor.BOLD + " " + amount + "x " + itemName);
                } else {
                    price.add("  " + ChatColor.BLUE + ChatColor.BOLD + ">> " + amount + "x " + im.getDisplayName());
                }
            }
        }
        return price;
    }

    public enum TierType {
        FIELD, REGION, DISCOUNT,
    }
}
