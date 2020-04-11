package pl.suseu.bfactions.settings;

public class RegionTier {

    private final int tier;
    private final int radius;
    private final double drainAmount;
    private final double cost;

    public RegionTier(int tier, int radius, double drainAmount, double cost) {
        this.tier = tier;
        this.radius = radius;
        this.drainAmount = drainAmount;
        this.cost = cost;
    }

    public int getTier() {
        return tier;
    }

    public int getRadius() {
        return radius;
    }

    public double getDrainAmount() {
        return drainAmount;
    }

    public double getCost() {
        return cost;
    }
}
