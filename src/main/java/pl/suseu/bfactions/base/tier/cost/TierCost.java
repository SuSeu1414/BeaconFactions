package pl.suseu.bfactions.base.tier.cost;

import org.bukkit.entity.Player;
import pl.suseu.bfactions.base.guild.Guild;

public abstract class TierCost {
    private TierCostType type;

    public TierCost(TierCostType type) {
        this.type = type;
    }

    public abstract boolean canBuy(Player player, Guild guild);

    public abstract void buy(Player player, Guild guild);

    public enum TierCostType {
        MONEY, ENERGY, ITEM, TIER_REQUIREMENT
    }
}
