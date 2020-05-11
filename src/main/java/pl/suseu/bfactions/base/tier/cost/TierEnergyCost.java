package pl.suseu.bfactions.base.tier.cost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;

public class TierEnergyCost extends TierCost {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private double amount;

    public TierEnergyCost(double amount) {
        super(TierCostType.ENERGY);
        this.amount = amount;
    }

    @Override
    public boolean canBuy(Player player, Guild guild) {
        if (plugin == null) {
            return false;
        }
        return guild.getField().getCurrentEnergy() > this.amount;
    }

    @Override
    public void buy(Player player, Guild guild) {
        if (plugin == null) {
            return;
        }
        guild.getField().addEnergy(-1 * this.amount);
    }
}
