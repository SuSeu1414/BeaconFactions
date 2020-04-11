package pl.suseu.bfactions.settings;

public class RegionTier {

    private final int tier;
    private final int radius;
    private final double drainAmount;

    public RegionTier(int tier, int radius, double drainAmount) {
        this.tier = tier;
        this.radius = radius;
        this.drainAmount = drainAmount;
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
}
