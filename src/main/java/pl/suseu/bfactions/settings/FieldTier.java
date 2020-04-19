package pl.suseu.bfactions.settings;

public class FieldTier extends Tier {

    private final double maxEnergy;

    public FieldTier(int tier, double maxEnergy, double cost) {
        super(tier, cost, TierType.FIELD);
        this.maxEnergy = maxEnergy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }
}
