package pl.suseu.bfactions.gui.main.action.upgrade;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.Tier;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.main.factory.upgrade.UpgradeGuiFactory;

import java.util.List;
import java.util.stream.Collectors;

public class OpenFieldUpgradeGuiAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final Guild guild;
    private final LangAPI lang;
    private final UpgradeGuiFactory upgradeGuiFactory;
    private final Tier.TierType tierType;

    public OpenFieldUpgradeGuiAction(BFactions plugin, Guild guild, Tier.TierType tierType) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guild = guild;
        this.lang = plugin.getLang();
        this.upgradeGuiFactory = new UpgradeGuiFactory(this.plugin, tierType);
        this.tierType = tierType;
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
//        if (!this.guild.hasPermission(user, GuildPermission.MANAGE)) {
        if (!this.guild.isOwner(user)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        List<Tier> tiers = null;

        if (tierType == Tier.TierType.FIELD) {
            tiers = this.plugin.getSettings().tierRepository.getFieldTiers().stream()
                    .map(fieldTier -> (Tier) fieldTier)
                    .collect(Collectors.toList());
        } else if (tierType == Tier.TierType.REGION) {
            tiers = this.plugin.getSettings().tierRepository.getRegionTiers().stream()
                    .map(fieldTier -> (Tier) fieldTier)
                    .collect(Collectors.toList());
        } else if (tierType == Tier.TierType.DISCOUNT) {
            tiers = this.plugin.getSettings().tierRepository.getDiscountTiers().stream()
                    .map(discountTier -> (Tier) discountTier)
                    .collect(Collectors.toList());
        }

        if (tiers == null) {
            return;
        }

        int tier = this.guild.getTier(tierType) == null ? -1 : this.guild.getTier(tierType).getTier();
        Inventory inv = this.upgradeGuiFactory.createGui(whoClicked, this.guild, tiers, tier);
        whoClicked.openInventory(inv);
    }
}
