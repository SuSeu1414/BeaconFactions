package pl.suseu.bfactions.settings;

public class RegionTier extends Tier {

    private final int radius;
    private final double drainAmount;

    public RegionTier(int tier, int radius, double drainAmount, double cost) {
        super(tier, cost, TierType.REGION);
        this.radius = radius;
        this.drainAmount = drainAmount;
    }

    public int getRadius() {
        return radius;
    }

    public double getDrainAmount() {
        return drainAmount;
    }
}
