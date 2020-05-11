package pl.suseu.bfactions.base.tier;

import pl.suseu.bfactions.base.tier.cost.TierCost;

import java.util.List;

public class FieldTier extends Tier {

    private final double maxEnergy;

    public FieldTier(int tier, String pathItem, String pathItemBuy, String pathItemOwned, List<TierCost> cost, TierType type, double maxEnergy) {
        super(tier, pathItem, pathItemBuy, pathItemOwned, cost, type);
        this.maxEnergy = maxEnergy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }
}
