package pl.suseu.bfactions.base.tier;

import pl.suseu.bfactions.base.region.RegionType;
import pl.suseu.bfactions.base.tier.cost.TierCost;

import java.util.List;

public class RegionTier extends Tier {

    private final int radius;
    private final RegionType regionType;
    private final double drainAmount;

    public RegionTier(int tier, String pathItem, String pathItemBuy, String pathItemOwned, List<TierCost> cost, TierType type, int radius, RegionType regionType, double drainAmount) {
        super(tier, pathItem, pathItemBuy, pathItemOwned, cost, type);
        this.radius = radius;
        this.regionType = regionType;
        this.drainAmount = drainAmount;
    }

    public int getRadius() {
        return radius;
    }

    public double getDrainAmount() {
        return drainAmount;
    }

    public RegionType getRegionType() {
        return regionType;
    }
}
