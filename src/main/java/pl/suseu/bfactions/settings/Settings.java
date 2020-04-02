package pl.suseu.bfactions.settings;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.suseu.bfactions.BFactions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Settings {

    public int guildNameMaxLength;
    public int guildNameMinLength;
    public int guildTagMaxLength;
    public int guildTagMinLength;
    public int guildMembersMax;

    public int cuboidSizeInitial;
    public int cuboidDistanceMin;

    public double fieldParticleDensity;
    public double fieldParticleRange;
    public double fieldEnergyInitial;
    public double fieldPassiveDrainAmount;
    public int fieldPassiveDrainDelay;
    public double fieldDamageArrow;
    public double fieldDamageTNT;
    public Map<Material, Double> fieldEnergyConversions = new HashMap<>();

    private BFactions plugin;
    private FileConfiguration cfg;
    private Logger log;

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

        cuboidSizeInitial = cfg.getInt("cuboid.initial-size");
        cuboidDistanceMin = cfg.getInt("cuboid.minimum-distance");

        fieldParticleDensity = cfg.getDouble("field.particle-density");
        fieldParticleRange = cfg.getDouble("field.particle-visibility");
        fieldEnergyInitial = cfg.getDouble("field.initial-energy");
        fieldPassiveDrainAmount = cfg.getDouble("field.passive-drain-amount");
        fieldPassiveDrainDelay = cfg.getInt("field.passive-drain-delay");
        fieldDamageArrow = cfg.getDouble("field.arrow-damage");
        fieldDamageTNT = cfg.getDouble("field.tnt-damage");
        fieldEnergyConversions.clear();

        ConfigurationSection conversionsSection = cfg.getConfigurationSection("field.energy-fuel");
        for (String materialName : conversionsSection.getKeys(false)) {
            Material material = Material.getMaterial(materialName);
            double energy = conversionsSection.getDouble(materialName);

            fieldEnergyConversions.put(material, energy);
        }

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
        }

        if (!cfg.isConfigurationSection("cuboid")) {
            log.warning("Configuration: Missing 'cuboid' section!");
            success = false;
        } else {
            ConfigurationSection section = cfg.getConfigurationSection("cuboid");

            if (!section.isInt("initial-size")) {
                log.warning("Configuration (cuboid): Missing/Invalid 'initial-size' entry!");
                success = false;
            }
            if (!section.isInt("minimum-distance")) {
                log.warning("Configuration (cuboid): Missing/Invalid 'minimum-distance' entry!");
                success = false;
            }
        }

        if (!cfg.isConfigurationSection("field")) {
            log.warning("Configuration: Missing 'field' section!");
            success = false;
        } else {
            ConfigurationSection section = cfg.getConfigurationSection("field");

            if (!section.isDouble("particle-density")
                    && !section.isInt("particle-density")
                    && !section.isLong("particle-density")) {
                log.warning("Configuration (field): Missing/Invalid 'particle-density' entry!");
                success = false;
            }
            if (!section.isDouble("particle-visibility")
                    && !section.isInt("particle-visibility")
                    && !section.isLong("particle-visibility")) {
                log.warning("Configuration (field): Missing/Invalid 'particle-visibility' entry!");
                success = false;
            }
            if (!section.isDouble("initial-energy")
                    && !section.isInt("initial-energy")
                    && !section.isLong("initial-energy")) {
                log.warning("Configuration (field): Missing/Invalid 'initial-energy' entry!");
                success = false;
            }
            if (!section.isDouble("passive-drain-amount")
                    && !section.isInt("passive-drain-amount")
                    && !section.isLong("passive-drain-amount")) {
                log.warning("Configuration (field): Missing/Invalid 'passive-drain-amount' entry!");
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
                        continue;
                    }
                }
            }
        }

        return success;
    }
}
