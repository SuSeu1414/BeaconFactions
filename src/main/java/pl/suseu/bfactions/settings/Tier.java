package pl.suseu.bfactions.settings;

public abstract class Tier {

    private final int tier;
    private final double cost;
    private final TierType type;

    public Tier(int tier, double cost, TierType type) {
        this.tier = tier;
        this.cost = cost;
        this.type = type;
    }

    public int getTier() {
        return tier;
    }

    public double getCost() {
        return cost;
    }

    public TierType getType() {
        return type;
    }

    public enum TierType {
        FIELD, REGION,
    }
}
