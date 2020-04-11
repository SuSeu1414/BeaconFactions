package pl.suseu.bfactions.settings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.suseu.bfactions.BFactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("ConstantConditions")
public class Settings {

    private final BFactions plugin;
    private final FileConfiguration cfg;
    private final Logger log;
    public int guildNameMaxLength;
    public int guildNameMinLength;
    public int guildTagMaxLength;
    public int guildTagMinLength;
    public int guildMembersMax;
    public int cuboidDistanceMin;
    public double fieldDomeDensity;
    public double fieldDomeDistance;
    public double fieldBorderDensity;
    public double fieldBorderDistance;
    public double fieldEnergyInitial;
    public int fieldPassiveDrainDelay;
    public double fieldDamageArrow;
    public double fieldDamageTNT;
    public final Map<Material, Double> fieldEnergyConversions = new HashMap<>();
    public final List<FieldTier> fieldTiers = new ArrayList<>();
    public final List<RegionTier> regionTiers = new ArrayList<>();
    public String guiMainTitle;

    public Settings(BFactions plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        this.log = plugin.getLogger();
    }

    public boolean loadConfig() {
        if (!checkIntegrity()) {
            return false;
        }

        guildNameMaxLength = cfg.getInt("guild.name-max-length");
        guildNameMinLength = cfg.getInt("guild.name-min-length");
        guildTagMaxLength = cfg.getInt("guild.tag-max-length");
        guildTagMinLength = cfg.getInt("guild.tag-min-length");
        guildMembersMax = cfg.getInt("guild.max-members");
        cuboidDistanceMin = cfg.getInt("guild.minimum-distance");

        fieldDomeDensity = cfg.getDouble("field.dome-particle-density");
        fieldDomeDistance = cfg.getDouble("field.dome-render-distance");
        fieldBorderDensity = cfg.getDouble("field.border-particle-density");
        fieldBorderDistance = cfg.getDouble("field.border-render-distance");
        fieldPassiveDrainDelay = cfg.getInt("field.passive-drain-delay");
        fieldDamageArrow = cfg.getDouble("field.arrow-damage");
        fieldDamageTNT = cfg.getDouble("field.tnt-damage");
        fieldEnergyConversions.clear();
        fieldTiers.clear();
        regionTiers.clear();

        ConfigurationSection conversionsSection = cfg.getConfigurationSection("field.energy-fuel");
        for (String key : conversionsSection.getKeys(false)) {
            Material material = Material.getMaterial(key);
            double energy = conversionsSection.getDouble(key);

            fieldEnergyConversions.put(material, energy);
        }

        ConfigurationSection energyUpgradesSection = cfg.getConfigurationSection("field.energy-upgrades");
        int i = 0;
        for (String key : energyUpgradesSection.getKeys(false)) {
            ConfigurationSection upgradeSection = energyUpgradesSection.getConfigurationSection(key);
            double maxEnergy = upgradeSection.getDouble("max-energy");
            double cost = upgradeSection.getDouble("cost");
            fieldTiers.add(new FieldTier(i, maxEnergy, cost));
            i++;
        }

        i = 0;
        ConfigurationSection sizeUpgradesSection = cfg.getConfigurationSection("field.size-upgrades");
        for (String key : sizeUpgradesSection.getKeys(false)) {
            ConfigurationSection upgradeSection = sizeUpgradesSection.getConfigurationSection(key);
            int radius = upgradeSection.getInt("radius");
            double drain = upgradeSection.getDouble("passive-drain-amount");
            double cost = upgradeSection.getDouble("cost");
            regionTiers.add(new RegionTier(i, radius, drain, cost));
            i++;
        }

        guiMainTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("gui.main-gui-title"));

        return true;
    }

    private boolean checkIntegrity() {
        boolean success = true;

        if (!cfg.isConfigurationSection("guild")) {
            log.warning("Configuration: Missing 'guild' section!");
            success = false;
        } else {
            ConfigurationSection guildSection = cfg.getConfigurationSection("guild");

            if (!guildSection.isInt("name-max-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'name-max-length' entry!");
                success = false;
            }
            if (!guildSection.isInt("name-min-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'name-min-length' entry!");
                success = false;
            }
            if (!guildSection.isInt("tag-max-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'tag-max-length' entry!");
                success = false;
            }
            if (!guildSection.isInt("tag-min-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'tag-min-length' entry!");
                success = false;
            }
            if (!guildSection.isInt("max-members")) {
                log.warning("Configuration (guild): Missing/Invalid 'max-members' entry!");
                success = false;
            }
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

            if (!fieldSection.isConfigurationSection("energy-upgrades")) {
                log.warning("Configuration (field): Missing 'energy-upgrades' section!");
                success = false;
            } else {
                ConfigurationSection energyUpgradesSection = fieldSection.getConfigurationSection("energy-upgrades");

                for (String key : energyUpgradesSection.getKeys(false)) {
                    ConfigurationSection upgradeSection = energyUpgradesSection.getConfigurationSection(key);

                    if (!upgradeSection.isDouble("max-energy")
                            && !upgradeSection.isInt("max-energy")
                            && !upgradeSection.isLong("max-energy")) {
                        log.warning("Configuration (field.energy-upgrades): Missing/Invalid 'max-energy' entry!");
                        success = false;
                    }
                    if (!upgradeSection.isDouble("cost")
                            && !upgradeSection.isInt("cost")
                            && !upgradeSection.isLong("cost")) {
                        log.warning("Configuration (field.energy-upgrades): Missing/Invalid 'cost' entry!");
                        success = false;
                    }
                }
            }

            if (!fieldSection.isConfigurationSection("size-upgrades")) {
                log.warning("Configuration (field): Missing 'size-upgrades' section!");
                success = false;
            } else {
                ConfigurationSection sizeUpgradesSection = fieldSection.getConfigurationSection("size-upgrades");

                for (String key : sizeUpgradesSection.getKeys(false)) {
                    ConfigurationSection upgradeSection = sizeUpgradesSection.getConfigurationSection(key);

                    if (!upgradeSection.isInt("radius")) {
                        log.warning("Configuration (field.size-upgrades): Missing/Invalid 'radius' entry!");
                        success = false;
                    }
                    if (!upgradeSection.isDouble("passive-drain-amount")
                            && !upgradeSection.isInt("passive-drain-amount")
                            && !upgradeSection.isLong("passive-drain-amount")) {
                        log.warning("Configuration (field.size-upgrades): Missing/Invalid 'passive-drain-amount' entry!");
                        success = false;
                    }
                    if (!upgradeSection.isDouble("cost")
                            && !upgradeSection.isInt("cost")
                            && !upgradeSection.isLong("cost")) {
                        log.warning("Configuration (field.size-upgrades): Missing/Invalid 'cost' entry!");
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
        }

        return success;
    }
}
