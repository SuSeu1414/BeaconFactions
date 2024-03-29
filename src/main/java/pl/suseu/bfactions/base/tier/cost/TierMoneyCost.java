package pl.suseu.bfactions.base.tier.cost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;

public class TierMoneyCost extends TierCost {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private double amount;

    public TierMoneyCost(double amount) {
        super(TierCostType.MONEY);
        this.amount = amount;
    }

    @Override
    public boolean canBuy(Player player, Guild guild) {
        if (plugin == null) {
            return false;
        }
        return this.plugin.getEconomy().has(player, this.amount);
    }

    @Override
    public void buy(Player player, Guild guild) {
        if (plugin == null) {
            return;
        }
        double discount = guild.getDiscountTier() != null ? guild.getDiscountTier().getPriceDiscount() : 0;
        discount /= 100.0;
        discount = 1.0 - discount;
        this.plugin.getEconomy().withdrawPlayer(player, this.amount * discount);
    }

    public double getAmount() {
        return amount;
    }
}
