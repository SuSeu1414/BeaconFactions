package pl.suseu.bfactions.base.field;

public class FieldTier {

    private final int tier;
    private final double maxEnergy;
    private final int radius;
    private final double drainAmount;

    public FieldTier(int tier, double maxEnergy, int radius, double drainAmount) {
        this.tier = tier;
        this.maxEnergy = maxEnergy;
        this.radius = radius;
        this.drainAmount = drainAmount;
    }

    public int getTier() {
        return tier;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public int getRadius() {
        return radius;
    }

    public double getDrainAmount() {
        return drainAmount;
    }
}
