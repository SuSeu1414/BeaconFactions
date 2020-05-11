package pl.suseu.bfactions.base.tier;

import java.util.ArrayList;
import java.util.List;

public class TierRepository {

    private final List<FieldTier> fieldTiers = new ArrayList<>();
    private final List<RegionTier> regionTiers = new ArrayList<>();

    public TierRepository() {
    }

    public void clearTiers() {
        this.fieldTiers.clear();
        this.regionTiers.clear();
    }

    public void addFieldTier(FieldTier fieldTier) {
        this.fieldTiers.add(fieldTier);
    }

    public void addRegionTier(RegionTier regionTier) {
        this.regionTiers.add(regionTier);
    }

    public List<FieldTier> getFieldTiers() {
        return new ArrayList<>(fieldTiers);
    }

    public List<RegionTier> getRegionTiers() {
        return new ArrayList<>(regionTiers);
    }
}
