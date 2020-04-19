package pl.suseu.bfactions.data;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.field.FieldRepository;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;

import java.util.UUID;

public class DataIntegrator {

    private final BFactions plugin;
    private final GuildRepository guildRepository;
    private final FieldRepository fieldRepository;
    private final RegionRepository regionRepository;

    public DataIntegrator(BFactions plugin) {
        this.plugin = plugin;
        this.guildRepository = plugin.getGuildRepository();
        this.fieldRepository = plugin.getFieldRepository();
        this.regionRepository = plugin.getRegionRepository();
    }

    public void checkIntegrity() {
        for (Guild guild : guildRepository.getGuilds()) {
            Field field = fieldRepository.getField(guild.getUuid());
            Region region = regionRepository.getRegion(guild.getUuid());

            if (field == null || region == null) {
                remove(guild.getUuid());
                continue;
            }

            if (guild.getField() != field || guild.getRegion() != region) {
                remove(guild.getUuid());
                continue;
            }
        }

        for (Region region : regionRepository.getRegions()) {
            Guild guild = region.getGuild();

            if (guild == null) {
                remove(region.getUuid());
                continue;
            }

            Field field = guild.getField();

            if (field == null) {
                remove(guild.getUuid());
                continue;
            }

            if (guild.getField() != field || guild.getRegion() != region) {
                remove(guild.getUuid());
                continue;
            }
        }

        for (Field field : fieldRepository.getFields()) {
            Guild guild = field.getGuild();

            if (guild == null) {
                remove(field.getUuid());
                continue;
            }

            Region region = guild.getRegion();

            if (region == null) {
                remove(guild.getUuid());
                continue;
            }

            if (guild.getField() != field || guild.getRegion() != region) {
                remove(guild.getUuid());
                continue;
            }
        }
    }

    private void remove(UUID uuid) {
        this.guildRepository.removeGuild(uuid);
        this.fieldRepository.removeField(uuid);
        this.regionRepository.removeRegion(uuid);
        this.plugin.getLogger().warning("[Data Integrator] Removed guild from repositories (" + uuid.toString() + ")");
    }
}
