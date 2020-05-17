package pl.suseu.bfactions.base.tier.cost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;

public class TierItemCost extends TierCost {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final String item;
    private final int amount;

    public TierItemCost(String item, int amount) {
        super(TierCostType.ITEM);
        this.item = item;
        this.amount = amount;
    }

    @Override
    public boolean canBuy(Player player, Guild guild) {
        if (plugin == null) {
            return false;
        }
        ItemStack itemStack = plugin.getItemRepository().getItem(item, false);
        return player.getInventory().containsAtLeast(itemStack, this.amount);
    }

    @Override
    public void buy(Player player, Guild guild) {
        if (plugin == null) {
            return;
        }
        double discount = guild.getDiscountTier() != null ? guild.getDiscountTier().getPriceDiscount() : 0;
        discount /= 100.0;
        discount = 1.0 - discount;

        ItemStack itemStack = plugin.getItemRepository().getItem(item, false);
        itemStack.setAmount((int) Math.ceil(this.amount * discount));
        player.getInventory().removeItem(itemStack);
    }

    public int getAmount() {
        return amount;
    }

    public String getItem() {
        return item;
    }
}
