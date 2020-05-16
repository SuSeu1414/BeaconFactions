package pl.suseu.bfactions.base.tier;

import pl.suseu.bfactions.base.tier.cost.TierCost;

import java.util.List;

public class DiscountTier extends Tier {

    private final double priceDiscount;
    private final double energyDiscount;

    public DiscountTier(int tier, String pathItem, String pathItemBuy, String pathItemOwned, List<TierCost> cost, double priceDiscount, double energyDiscount) {
        super(tier, pathItem, pathItemBuy, pathItemOwned, cost, TierType.DISCOUNT);
        this.priceDiscount = priceDiscount;
        this.energyDiscount = energyDiscount;
    }

    public double getPriceDiscount() {
        return priceDiscount;
    }

    public double getEnergyDiscount() {
        return energyDiscount;
    }
}
