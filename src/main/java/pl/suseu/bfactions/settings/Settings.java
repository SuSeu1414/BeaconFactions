package pl.suseu.bfactions.settings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldTier;

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
    public Map<Material, Double> fieldEnergyConversions = new HashMap<>();
    public List<FieldTier> fieldTiers = new ArrayList<>();
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

        ConfigurationSection conversionsSection = cfg.getConfigurationSection("field.energy-fuel");
        for (String materialName : conversionsSection.getKeys(false)) {
            Material material = Material.getMaterial(materialName);
            double energy = conversionsSection.getDouble(materialName);

            fieldEnergyConversions.put(material, energy);
        }

        ConfigurationSection upgradesSection = cfg.getConfigurationSection("field.field-upgrades");
        int i = 0;
        for (String tierName : upgradesSection.getKeys(false)) {
            ConfigurationSection tierSection = upgradesSection.getConfigurationSection(tierName);

            double maxEnergy = tierSection.getDouble("max-energy");
            int radius = tierSection.getInt("radius");
            double drain = tierSection.getDouble("passive-drain-amount");

            if (i == 0) {
                fieldEnergyInitial = tierSection.getDouble("initial-energy");
            }

            fieldTiers.add(new FieldTier(i, maxEnergy, radius, drain));
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
            ConfigurationSection section = cfg.getConfigurationSection("guild");

            if (!section.isInt("name-max-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'name-max-length' entry!");
                success = false;
            }
            if (!section.isInt("name-min-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'name-min-length' entry!");
                success = false;
            }
            if (!section.isInt("tag-max-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'tag-max-length' entry!");
                success = false;
            }
            if (!section.isInt("tag-min-length")) {
                log.warning("Configuration (guild): Missing/Invalid 'tag-min-length' entry!");
                success = false;
            }
            if (!section.isInt("max-members")) {
                log.warning("Configuration (guild): Missing/Invalid 'max-members' entry!");
                success = false;
            }
            if (!section.isInt("minimum-distance")) {
                log.warning("Configuration (guild): Missing/Invalid 'minimum-distance' entry!");
                success = false;
            }
        }

        if (!cfg.isConfigurationSection("field")) {
            log.warning("Configuration: Missing 'field' section!");
            success = false;
        } else {
            ConfigurationSection section = cfg.getConfigurationSection("field");

            if (!section.isDouble("dome-particle-density")
                    && !section.isInt("dome-particle-density")
                    && !section.isLong("dome-particle-density")) {
                log.warning("Configuration (field): Missing/Invalid 'dome-particle-density' entry!");
                success = false;
            }
            if (!section.isDouble("dome-render-distance")
                    && !section.isInt("dome-render-distance")
                    && !section.isLong("dome-render-distance")) {
                log.warning("Configuration (field): Missing/Invalid 'dome-render-distance' entry!");
                success = false;
            }
            if (!section.isDouble("border-particle-density")
                    && !section.isInt("border-particle-density")
                    && !section.isLong("border-particle-density")) {
                log.warning("Configuration (field): Missing/Invalid 'border-particle-density' entry!");
                success = false;
            }
            if (!section.isDouble("border-render-distance")
                    && !section.isInt("border-render-distance")
                    && !section.isLong("border-render-distance")) {
                log.warning("Configuration (field): Missing/Invalid 'border-render-distance' entry!");
                success = false;
            }
            if (!section.isInt("passive-drain-delay")) {
                log.warning("Configuration (field): Missing/Invalid 'passive-drain-delay' entry!");
                success = false;
            }
            if (!section.isDouble("arrow-damage")
                    && !section.isInt("arrow-damage")
                    && !section.isLong("arrow-damage")) {
                log.warning("Configuration (field): Missing/Invalid 'arrow-damage' entry!");
                success = false;
            }
            if (!section.isDouble("tnt-damage")
                    && !section.isInt("tnt-damage")
                    && !section.isLong("tnt-damage")) {
                log.warning("Configuration (field): Missing/Invalid 'tnt-damage' entry!");
                success = false;
            }

            if (!section.isConfigurationSection("energy-fuel")) {
                log.warning("Configuration (field): Missing 'energy-fuel' section!");
                success = false;
            } else {
                ConfigurationSection conversionsSection = section.getConfigurationSection("energy-fuel");

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

            if (!section.isConfigurationSection("field-upgrades")) {
                log.warning("Configuration (field): Missing 'field-upgrades' section!");
                success = false;
            } else {
                ConfigurationSection upgradesSection = section.getConfigurationSection("field-upgrades");
                int i = 0;

                if (upgradesSection.getKeys(false).size() < 1) {
                    log.warning("Configuration (field.field-upgrades): There must be at least one tier specified!");
                    log.warning("Unable to find initial configuration!");
                    success = false;
                }

                for (String tier : upgradesSection.getKeys(false)) {
                    ConfigurationSection tierSection = upgradesSection.getConfigurationSection(tier);

                    if (!tierSection.isDouble("initial-energy")
                            && !tierSection.isInt("initial-energy")
                            && !tierSection.isLong("initial-energy")) {
                        if (i == 0) {
                            log.warning("Configuration (field.field-upgrades): 'initial-energy' field not found in the first upgrade!");
                            log.warning("Unable to find initial configuration!");
                            success = false;
                        }
                    } else {
                        if (i != 0) {
                            log.warning("Configuration (field.field-upgrades): Unnecessary 'initial-energy' field in '" + tier + "'!");
                        }
                    }

                    if (!tierSection.isDouble("max-energy")
                            && !tierSection.isInt("max-energy")
                            && !tierSection.isLong("max-energy")) {
                        log.warning("Configuration (field.field-upgrades): Missing/Invalid 'max-energy' in tier '" + tier + "'!");
                        success = false;
                    }
                    if (!tierSection.isInt("radius")) {
                        log.warning("Configuration (field.field-upgrades): Missing/Invalid 'radius' in tier '" + tier + "'!");
                        success = false;
                    }
                    if (!tierSection.isDouble("passive-drain-amount")
                            && !tierSection.isInt("passive-drain-amount")
                            && !tierSection.isLong("passive-drain-amount")) {
                        log.warning("Configuration (field.field-upgrades): Missing/Invalid 'passive-drain-amount' in tier '" + tier + "'!");
                        success = false;
                    }
                    i++;
                }
            }
        }

        if (!cfg.isConfigurationSection("gui")) {
            log.warning("Configuration: Missing 'gui' section!");
            success = false;
        } else {
            ConfigurationSection section = cfg.getConfigurationSection("gui");

            if (!section.isString("main-gui-title")) {
                log.warning("Configuration (gui): Missing/Invalid 'main-gui-title' entry!");
                success = false;
            }
        }

        return success;
    }
}
