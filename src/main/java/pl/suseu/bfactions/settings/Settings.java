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
                            && !section.isInt(materialName)
                            && !section.isLong(materialName))) {
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
