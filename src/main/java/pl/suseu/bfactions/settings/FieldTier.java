package pl.suseu.bfactions.settings;

public class FieldTier {

    private final int tier;
    private final double maxEnergy;
    private final double cost;

    public FieldTier(int tier, double maxEnergy, double cost) {
        this.tier = tier;
        this.maxEnergy = maxEnergy;
        this.cost = cost;
    }

    public int getTier() {
        return tier;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public double getCost() {
        return cost;
    }
}
