package pl.suseu.bfactions.base.tier.cost;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.Tier;

public class TierRequirementCost extends TierCost {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final Tier.TierType tierType;
    private final int tier;

    public TierRequirementCost(Tier.TierType tierType, int tier) {
        super(TierCostType.TIER_REQUIREMENT);
        this.tierType = tierType;
        this.tier = tier;
    }

    @Override
    public boolean canBuy(Player player, Guild guild) {
        return guild.getTier(this.tierType).getTier() >= this.tier;
    }

    @Override
    public void buy(Player player, Guild guild) {
    }

    public Tier.TierType getTierType() {
        return tierType;
    }

    public int getTier() {
        return tier;
    }
}
