package pl.suseu.bfactions.settings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.region.RegionType;
import pl.suseu.bfactions.base.tier.FieldTier;
import pl.suseu.bfactions.base.tier.RegionTier;
import pl.suseu.bfactions.base.tier.Tier;
import pl.suseu.bfactions.base.tier.TierRepository;
import pl.suseu.bfactions.base.tier.cost.TierCost;
import pl.suseu.bfactions.base.tier.cost.TierEnergyCost;
import pl.suseu.bfactions.base.tier.cost.TierItemCost;
import pl.suseu.bfactions.base.tier.cost.TierMoneyCost;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("ConstantConditions")
public class Settings {

    public final Map<Material, Double> fieldEnergyConversions = new HashMap<>();
    //    public final List<FieldTier> fieldTiers = new ArrayList<>();
//    public final List<RegionTier> regionTiers = new ArrayList<>();
    public final TierRepository tierRepository = new TierRepository();
    public final Map<String, Long> fieldBoostUndamageableItems = new HashMap<>();
    private final BFactions plugin;
    private final FileConfiguration cfg;
    private final YamlConfiguration tiersCfg;
    private final Logger log;
    //    public int guildNameMaxLength;
//    public int guildNameMinLength;
//    public int guildTagMaxLength;
//    public int guildTagMinLength;
//    public int guildMembersMax;
    public int cuboidDistanceMin;
    public double fieldBarDistance;
    public double fieldDomeDensity;
    public double fieldDomeDistance;
    public double fieldBorderDensity;
    public double fieldBorderDistance;
    public int fieldKnockdownTimeout;
    public double fieldEnergyInitial;
    public int fieldPassiveDrainDelay;
    public double fieldDamageArrow;
    public double fieldDamageTNT;
    public String guiMainTitle;
    public String guiInvitesTitle;
    public String guiManageMembersTitle;
    public String guiManageMemberTitle;
    public String guiFieldUpgradesTitle;
    public String guiRegionUpgradeTitle;
    public String guiBoostUndamageableTitle;
    public String guiConfirmationTitle;
    public String timeDays;
    public String timeHours;
    public String timeMinutes;
    public String timeSeconds;
    public String timeMilliseconds;

    public Settings(BFactions plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        this.tiersCfg = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "upgrades.yml"));
        this.log = plugin.getLogger();
    }

    public boolean loadConfig() {
        if (!checkIntegrity()) {
            return false;
        }

//        guildNameMaxLength = cfg.getInt("guild.name-max-length");
//        guildNameMinLength = cfg.getInt("guild.name-min-length");
//        guildTagMaxLength = cfg.getInt("guild.tag-max-length");
//        guildTagMinLength = cfg.getInt("guild.tag-min-length");
//        guildMembersMax = cfg.getInt("guild.max-members");
        cuboidDistanceMin = cfg.getInt("guild.minimum-distance");

        fieldBarDistance = cfg.getDouble("field.hp-bar-render-distance");
        fieldDomeDensity = cfg.getDouble("field.dome-particle-density");
        fieldDomeDistance = cfg.getDouble("field.dome-render-distance");
        fieldBorderDensity = cfg.getDouble("field.border-particle-density");
        fieldBorderDistance = cfg.getDouble("field.border-render-distance");
        fieldKnockdownTimeout = cfg.getInt("field.field-knockdown-timeout") * 1000;
        fieldPassiveDrainDelay = cfg.getInt("field.passive-drain-delay");
        fieldDamageArrow = cfg.getDouble("field.arrow-damage");
        fieldDamageTNT = cfg.getDouble("field.tnt-damage");
        fieldEnergyConversions.clear();
//        fieldTiers.clear();
//        regionTiers.clear();
        this.tierRepository.clearTiers();

        ConfigurationSection conversionsSection = cfg.getConfigurationSection("field.energy-fuel");
        for (String key : conversionsSection.getKeys(false)) {
            Material material = Material.getMaterial(key);
            double energy = conversionsSection.getDouble(key);

            fieldEnergyConversions.put(material, energy);
        }

        double initialMaxEnergy = cfg.getDouble("field.initial-configuration.max-energy");
        int initialRadius = cfg.getInt("field.initial-configuration.radius");
        double initialDrain = cfg.getDouble("field.initial-configuration.passive-drain-amount");
        fieldEnergyInitial = cfg.getDouble("field.initial-configuration.initial-energy");
//        fieldTiers.add(new FieldTier(0, initialMaxEnergy, 0));
//        regionTiers.add(new RegionTier(0, initialRadius, initialDrain, 0));

//        ConfigurationSection energyUpgradesSection = cfg.getConfigurationSection("field.energy-upgrades");
//        int i = 1;
//        for (String key : energyUpgradesSection.getKeys(false)) {
//            ConfigurationSection upgradeSection = energyUpgradesSection.getConfigurationSection(key);
//            double maxEnergy = upgradeSection.getDouble("max-energy");
//            double cost = upgradeSection.getDouble("cost");
//            fieldTiers.add(new FieldTier(i, maxEnergy, cost));
//            i++;
//        }
//
//        i = 1;
//        ConfigurationSection sizeUpgradesSection = cfg.getConfigurationSection("field.size-upgrades");
//        for (String key : sizeUpgradesSection.getKeys(false)) {
//            ConfigurationSection upgradeSection = sizeUpgradesSection.getConfigurationSection(key);
//            int radius = upgradeSection.getInt("radius");
//            double drain = upgradeSection.getDouble("passive-drain-amount");
//            double cost = upgradeSection.getDouble("cost");
//            regionTiers.add(new RegionTier(i, radius, drain, cost));
//            i++;
//        }

        ConfigurationSection itemsSection = cfg.getConfigurationSection("field.boost-undamageable-items");

        for (String key : itemsSection.getKeys(false)) {
            long time = itemsSection.getLong(key);
            this.fieldBoostUndamageableItems.put("boost-undamageable-" + key, time);
        }

        guiMainTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.main-gui-title"));
        guiInvitesTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.invites-gui-title"));
        guiManageMembersTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.manage-members-gui-title"));
        guiManageMemberTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.manage-member-gui-title"));
        guiFieldUpgradesTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.field-upgrade-gui-title"));
        guiRegionUpgradeTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.region-upgrade-gui-title"));
        guiBoostUndamageableTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.boost-undamageable-gui-title"));
        guiConfirmationTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.confirmation-gui-title"));

        timeDays = cfg.getString("time-format.days");
        timeHours = cfg.getString("time-format.hours");
        timeMinutes = cfg.getString("time-format.minutes");
        timeSeconds = cfg.getString("time-format.seconds");
        timeMilliseconds = cfg.getString("time-format.milliseconds");

        // load tiers
        int i = 0;
        ConfigurationSection sizeTiersSection = tiersCfg.getConfigurationSection("size");
        for (String s : sizeTiersSection.getKeys(false)) {
            ConfigurationSection tierSection = sizeTiersSection.getConfigurationSection(s);
            RegionType regionType = RegionType.valueOf(tierSection.getString("type").toUpperCase());
            int radius = tierSection.getInt("radius");
            double passiveDrain = tierSection.getDouble("passive-drain-amount");
            String guiItem = tierSection.getString("gui-item");
            String guiItemBuy = tierSection.getString("gui-item-buy");
            String guiItemOwned = tierSection.getString("gui-item-owned");
            List<String> sCost = tierSection.getStringList("price");
            List<TierCost> cost = new ArrayList<>();
            for (String c : sCost) {
                String[] split = c.split(":");
                if (split[0].equalsIgnoreCase("money")) {
                    cost.add(new TierMoneyCost(Double.parseDouble(split[1])));
                } else if (split[0].equalsIgnoreCase("energy")) {
                    cost.add(new TierEnergyCost(Double.parseDouble(split[1])));
                } else if (split[0].equalsIgnoreCase("item")) {
                    cost.add(new TierItemCost(split[1], Integer.parseInt(split[2])));
                }
            }
            RegionTier tier = new RegionTier(i, guiItem, guiItemBuy, guiItemOwned, cost, Tier.TierType.REGION, radius, regionType, passiveDrain);
            this.tierRepository.addRegionTier(tier);
            i++;
        }

        i = 0;
        ConfigurationSection energyTiersSection = tiersCfg.getConfigurationSection("energy");
        for (String s : energyTiersSection.getKeys(false)) {
            ConfigurationSection tierSection = energyTiersSection.getConfigurationSection(s);
            double maxEnergy = tierSection.getDouble("max-energy");
            String guiItem = tierSection.getString("gui-item");
            String guiItemBuy = tierSection.getString("gui-item-buy");
            String guiItemOwned = tierSection.getString("gui-item-owned");
            List<String> sCost = tierSection.getStringList("price");
            List<TierCost> cost = new ArrayList<>();
            for (String c : sCost) {
                String[] split = c.split(":");
                if (split[0].equalsIgnoreCase("money")) {
                    cost.add(new TierMoneyCost(Double.parseDouble(split[1])));
                } else if (split[0].equalsIgnoreCase("energy")) {
                    cost.add(new TierEnergyCost(Double.parseDouble(split[1])));
                } else if (split[0].equalsIgnoreCase("item")) {
                    cost.add(new TierItemCost(split[1], Integer.parseInt(split[2])));
                }
            }
            FieldTier tier = new FieldTier(i, guiItem, guiItemBuy, guiItemOwned, cost, Tier.TierType.FIELD, maxEnergy);
            this.tierRepository.addFieldTier(tier);
            i++;
        }

        return true;
    }

    private boolean checkIntegrity() {
        boolean success = true;

        if (!cfg.isConfigurationSection("guild")) {
            log.warning("Configuration: Missing 'guild' section!");
            success = false;
        } else {
            ConfigurationSection guildSection = cfg.getConfigurationSection("guild");

//            if (!guildSection.isInt("name-max-length")) {
//                log.warning("Configuration (guild): Missing/Invalid 'name-max-length' entry!");
//                success = false;
//            }
//            if (!guildSection.isInt("name-min-length")) {
//                log.warning("Configuration (guild): Missing/Invalid 'name-min-length' entry!");
//                success = false;
//            }
//            if (!guildSection.isInt("tag-max-length")) {
//                log.warning("Configuration (guild): Missing/Invalid 'tag-max-length' entry!");
//                success = false;
//            }
//            if (!guildSection.isInt("tag-min-length")) {
//                log.warning("Configuration (guild): Missing/Invalid 'tag-min-length' entry!");
//                success = false;
//            }
//            if (!guildSection.isInt("max-members")) {
//                log.warning("Configuration (guild): Missing/Invalid 'max-members' entry!");
//                success = false;
//            }
            if (!guildSection.isInt("minimum-distance")) {
                log.warning("Configuration (guild): Missing/Invalid 'minimum-distance' entry!");
                success = false;
            }
        }

        if (!cfg.isConfigurationSection("field")) {
            log.warning("Configuration: Missing 'field' section!");
            success = false;
        } else {
            ConfigurationSection fieldSection = cfg.getConfigurationSection("field");
            if (!fieldSection.isDouble("hp-bar-render-distance")
                    && !fieldSection.isInt("hp-bar-render-distance")
                    && !fieldSection.isLong("hp-bar-render-distance")) {
                log.warning("Configuration (field): Missing/Invalid 'hp-bar-render-distance' entry!");
                success = false;
            }
            if (!fieldSection.isDouble("dome-particle-density")
                    && !fieldSection.isInt("dome-particle-density")
                    && !fieldSection.isLong("dome-particle-density")) {
                log.warning("Configuration (field): Missing/Invalid 'dome-particle-density' entry!");
                success = false;
            }
            if (!fieldSection.isDouble("dome-render-distance")
                    && !fieldSection.isInt("dome-render-distance")
                    && !fieldSection.isLong("dome-render-distance")) {
                log.warning("Configuration (field): Missing/Invalid 'dome-render-distance' entry!");
                success = false;
            }
            if (!fieldSection.isDouble("border-particle-density")
                    && !fieldSection.isInt("border-particle-density")
                    && !fieldSection.isLong("border-particle-density")) {
                log.warning("Configuration (field): Missing/Invalid 'border-particle-density' entry!");
                success = false;
            }
            if (!fieldSection.isDouble("border-render-distance")
                    && !fieldSection.isInt("border-render-distance")
                    && !fieldSection.isLong("border-render-distance")) {
                log.warning("Configuration (field): Missing/Invalid 'border-render-distance' entry!");
                success = false;
            }
            if (!fieldSection.isInt("field-knockdown-timeout")) {
                log.warning("Configuration (field): Missing/Invalid 'field-knockdown-timeout' entry!");
                success = false;
            }
            if (!fieldSection.isInt("passive-drain-delay")) {
                log.warning("Configuration (field): Missing/Invalid 'passive-drain-delay' entry!");
                success = false;
            }
            if (!fieldSection.isDouble("arrow-damage")
                    && !fieldSection.isInt("arrow-damage")
                    && !fieldSection.isLong("arrow-damage")) {
                log.warning("Configuration (field): Missing/Invalid 'arrow-damage' entry!");
                success = false;
            }
            if (!fieldSection.isDouble("tnt-damage")
                    && !fieldSection.isInt("tnt-damage")
                    && !fieldSection.isLong("tnt-damage")) {
                log.warning("Configuration (field): Missing/Invalid 'tnt-damage' entry!");
                success = false;
            }

            if (!fieldSection.isConfigurationSection("energy-fuel")) {
                log.warning("Configuration (field): Missing 'energy-fuel' section!");
                success = false;
            } else {
                ConfigurationSection conversionsSection = fieldSection.getConfigurationSection("energy-fuel");

                for (String materialName : conversionsSection.getKeys(false)) {
                    Material material = Material.getMaterial(materialName);

                    if (material == null || (!conversionsSection.isDouble(materialName)
                            && !conversionsSection.isInt(materialName)
                            && !conversionsSection.isLong(materialName))) {
                        log.warning("Configuration (field.energy-fuel): Invalid '" + materialName + "'");
                        success = false;
                    }
                }
            }

            if (!fieldSection.isConfigurationSection("initial-configuration")) {
                log.warning("Configuration (field): Missing 'initial-configuration' section!");
                success = false;
            } else {
                ConfigurationSection initialCfgSection = fieldSection.getConfigurationSection("initial-configuration");

                if (!initialCfgSection.isDouble("max-energy")
                        && !initialCfgSection.isInt("max-energy")
                        && !initialCfgSection.isLong("max-energy")) {
                    log.warning("Configuration (field.initial-configuration): Missing/Invalid 'max-energy' entry!");
                    success = false;
                }
                if (!initialCfgSection.isDouble("initial-energy")
                        && !initialCfgSection.isInt("initial-energy")
                        && !initialCfgSection.isLong("initial-energy")) {
                    log.warning("Configuration (field.initial-configuration): Missing/Invalid 'initial-energy' entry!");
                    success = false;
                }
                if (!initialCfgSection.isInt("radius")) {
                    log.warning("Configuration (field.initial-configuration): Missing/Invalid 'radius' entry!");
                    success = false;
                }
                if (!initialCfgSection.isDouble("passive-drain-amount")
                        && !initialCfgSection.isInt("passive-drain-amount")
                        && !initialCfgSection.isLong("passive-drain-amount")) {
                    log.warning("Configuration (field.initial-configuration): Missing/Invalid 'passive-drain-amount' entry!");
                    success = false;
                }
            }

//            if (!fieldSection.isConfigurationSection("energy-upgrades")) {
//                log.warning("Configuration (field): Missing 'energy-upgrades' section!");
//                success = false;
//            } else {
//                ConfigurationSection energyUpgradesSection = fieldSection.getConfigurationSection("energy-upgrades");
//
//                for (String key : energyUpgradesSection.getKeys(false)) {
//                    ConfigurationSection upgradeSection = energyUpgradesSection.getConfigurationSection(key);
//
//                    if (!upgradeSection.isDouble("max-energy")
//                            && !upgradeSection.isInt("max-energy")
//                            && !upgradeSection.isLong("max-energy")) {
//                        log.warning("Configuration (field.energy-upgrades): Missing/Invalid 'max-energy' entry!");
//                        success = false;
//                    }
//                    if (!upgradeSection.isDouble("cost")
//                            && !upgradeSection.isInt("cost")
//                            && !upgradeSection.isLong("cost")) {
//                        log.warning("Configuration (field.energy-upgrades): Missing/Invalid 'cost' entry!");
//                        success = false;
//                    }
//                }
//            }
//
//            if (!fieldSection.isConfigurationSection("size-upgrades")) {
//                log.warning("Configuration (field): Missing 'size-upgrades' section!");
//                success = false;
//            } else {
//                ConfigurationSection sizeUpgradesSection = fieldSection.getConfigurationSection("size-upgrades");
//
//                for (String key : sizeUpgradesSection.getKeys(false)) {
//                    ConfigurationSection upgradeSection = sizeUpgradesSection.getConfigurationSection(key);
//
//                    if (!upgradeSection.isInt("radius")) {
//                        log.warning("Configuration (field.size-upgrades): Missing/Invalid 'radius' entry!");
//                        success = false;
//                    }
//                    if (!upgradeSection.isDouble("passive-drain-amount")
//                            && !upgradeSection.isInt("passive-drain-amount")
//                            && !upgradeSection.isLong("passive-drain-amount")) {
//                        log.warning("Configuration (field.size-upgrades): Missing/Invalid 'passive-drain-amount' entry!");
//                        success = false;
//                    }
//                    if (!upgradeSection.isDouble("cost")
//                            && !upgradeSection.isInt("cost")
//                            && !upgradeSection.isLong("cost")) {
//                        log.warning("Configuration (field.size-upgrades): Missing/Invalid 'cost' entry!");
//                        success = false;
//                    }
//                }
//            }

            if (!fieldSection.isConfigurationSection("boost-undamageable-items")) {
                log.warning("Configuration (field): Missing 'boost-undamageable-items' section!");
                success = false;
            } else {
                ConfigurationSection itemsSection = fieldSection.getConfigurationSection("boost-undamageable-items");

                for (String key : itemsSection.getKeys(false)) {
                    if (!itemsSection.isInt(key) && itemsSection.isLong(key)) {
                        log.warning("Configuration (field.boost-undamageable-items." + key + "): Invalid '" + key + "' entry!");
                        success = false;
                    }
                }
            }
        }

        if (!cfg.isConfigurationSection("gui")) {
            log.warning("Configuration: Missing 'gui' section!");
            success = false;
        } else {
            ConfigurationSection guiSection = cfg.getConfigurationSection("gui");

            if (!guiSection.isString("main-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'main-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("invites-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'invites-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("manage-members-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'manage-members-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("manage-member-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'manage-member-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("field-upgrade-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'field-upgrade-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("region-upgrade-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'region-upgrade-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("boost-undamageable-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'boost-undamageable-gui-title' entry!");
                success = false;
            }

            if (!guiSection.isString("confirmation-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'confirmation-gui-title' entry!");
                success = false;
            }
        }

        if (!cfg.isConfigurationSection("time-format")) {
            log.warning("Configuration: Missing 'time-format' section!");
            success = false;
        }

        for (String t : new String[]{"days", "minutes", "hours", "seconds", "milliseconds"}) {
            if (!cfg.isString("time-format." + t)) {
                log.warning("Configuration (time-format): Missing/Invalid '" + t + "' entry!");
                success = false;
            }
        }

        // upgrades.yml, size
        if (!this.tiersCfg.isConfigurationSection("size")) {
            log.warning("Configuration (upgrades.yml): Missing 'size' section!");
            success = false;
        } else {
            ConfigurationSection sizeSection = this.tiersCfg.getConfigurationSection("size");
            for (String s : sizeSection.getKeys(false)) {
                ConfigurationSection tierSection = sizeSection.getConfigurationSection(s);
                if (!tierSection.isString("type") || !(tierSection.getString("type").equalsIgnoreCase("DOME") || tierSection.getString("type").equalsIgnoreCase("ROLLER"))) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'type' entry. Possible values: DOME, ROLLER");
                    success = false;
                }
                if (!tierSection.isInt("radius")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'radius' entry!");
                    success = false;
                }
                if (!tierSection.isDouble("passive-drain-amount") &&
                        !tierSection.isInt("passive-drain-amount") &&
                        !tierSection.isLong("passive-drain-amount")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'passive-drain-amount' entry!");
                    success = false;
                }
                if (!tierSection.isString("gui-item")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'gui-item' entry!");
                    success = false;
                }
                if (!tierSection.isString("gui-item-buy")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'gui-item-buy' entry!");
                    success = false;
                }
                if (!tierSection.isString("gui-item-owned")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'gui-item-owned' entry!");
                    success = false;
                }
                if (!tierSection.isList("price")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'price' entry!");
                    success = false;
                } else {
                    List<String> price = tierSection.getStringList("price");
                    for (String p : price) {
                        String[] split = p.split(":");
                        if (split.length < 1) {
                            log.warning("Configuration (upgrades.yml, size." + s + ".price): Invalid price: " + p);
                            success = false;
                            continue;
                        }
                        if (split[0].equalsIgnoreCase("money") || split[0].equalsIgnoreCase("energy")) {
                            if (split.length != 2) {
                                log.warning("Configuration (upgrades.yml, size." + s + ".price): Invalid price: " + p);
                                success = false;
                                continue;
                            }
                            try {
                                Double.parseDouble(split[1]);
                            } catch (NumberFormatException e) {
                                log.warning("Configuration (upgrades.yml, size." + s + ".price): Invalid price: " + p);
                                success = false;
                            }
                        } else if (split[0].equalsIgnoreCase("item")) {
                            if (split.length != 3) {
                                log.warning("Configuration (upgrades.yml, size." + s + ".price): Invalid price: " + p);
                                success = false;
                                continue;
                            }
                            try {
                                Integer.parseInt(split[2]);
                            } catch (NumberFormatException e) {
                                log.warning("Configuration (upgrades.yml, size." + s + ".price): Invalid price: " + p);
                                success = false;
                            }
                        }
                    }
                }
            }
        }


        // upgrades.yml, energy
        if (!this.tiersCfg.isConfigurationSection("energy")) {
            log.warning("Configuration (upgrades.yml): Missing 'energy' section!");
            success = false;
        } else {
            ConfigurationSection sizeSection = this.tiersCfg.getConfigurationSection("energy");
            for (String s : sizeSection.getKeys(false)) {
                ConfigurationSection tierSection = sizeSection.getConfigurationSection(s);
                if (!tierSection.isDouble("max-energy") &&
                        !tierSection.isInt("max-energy") &&
                        !tierSection.isLong("max-energy")) {
                    log.warning("Configuration (upgrades.yml, energy." + s + "): Missing/Invalid 'max-energy' entry!");
                    success = false;
                }
                if (!tierSection.isString("gui-item")) {
                    log.warning("Configuration (upgrades.yml, energy." + s + "): Missing/Invalid 'gui-item' entry!");
                    success = false;
                }
                if (!tierSection.isString("gui-item-buy")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'gui-item-buy' entry!");
                    success = false;
                }
                if (!tierSection.isString("gui-item-owned")) {
                    log.warning("Configuration (upgrades.yml, size." + s + "): Missing/Invalid 'gui-item-owned' entry!");
                    success = false;
                }
                if (!tierSection.isList("price")) {
                    log.warning("Configuration (upgrades.yml, energy." + s + "): Missing/Invalid 'price' entry!");
                    success = false;
                } else {
                    List<String> price = tierSection.getStringList("price");
                    for (String p : price) {
                        String[] split = p.split(":");
                        if (split.length < 1) {
                            log.warning("Configuration (upgrades.yml, energy." + s + ".price): Invalid price: " + p);
                            success = false;
                            continue;
                        }
                        if (split[0].equalsIgnoreCase("money") || split[0].equalsIgnoreCase("energy")) {
                            if (split.length != 2) {
                                log.warning("Configuration (upgrades.yml, energy." + s + ".price): Invalid price: " + p);
                                success = false;
                                continue;
                            }
                            try {
                                Double.parseDouble(split[1]);
                            } catch (NumberFormatException e) {
                                log.warning("Configuration (upgrades.yml, energy." + s + ".price): Invalid price: " + p);
                                success = false;
                            }
                        } else if (split[0].equalsIgnoreCase("item")) {
                            if (split.length != 3) {
                                log.warning("Configuration (upgrades.yml, energy." + s + ".price): Invalid price: " + p);
                                success = false;
                                continue;
                            }
                            try {
                                Integer.parseInt(split[2]);
                            } catch (NumberFormatException e) {
                                log.warning("Configuration (upgrades.yml, energy." + s + ".price): Invalid price: " + p);
                                success = false;
                            }
                        }
                    }
                }
            }
        }

        return success;
    }
}
