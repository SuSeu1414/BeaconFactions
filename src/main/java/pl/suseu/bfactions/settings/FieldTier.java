package pl.suseu.bfactions.settings;

public class FieldTier {

    private final int tier;
    private final double maxEnergy;

    public FieldTier(int tier, double maxEnergy) {
        this.tier = tier;
        this.maxEnergy = maxEnergy;
    }

    public int getTier() {
        return tier;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }
}
