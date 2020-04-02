package pl.suseu.bfactions.base.guild;

import pl.suseu.bfactions.BFactions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GuildRepository {

    private final BFactions plugin;

    private final Map<UUID, Guild> guilds = new ConcurrentHashMap<>();
    private final Set<UUID> modifiedGuilds = ConcurrentHashMap.newKeySet();

    public GuildRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public void addGuild(Guild guild, boolean isNew) {
        this.guilds.put(guild.getUuid(), guild);
        if (isNew) {
            this.addModifiedGuild(guild);
        }
    }

    public Guild getGuild(UUID uuid) {
        return this.guilds.get(uuid);
    }

    public void addModifiedGuild(Guild guild) {
        this.addModifiedGuild(guild.getUuid());
    }

    public void addModifiedGuild(UUID uuid) {
        this.modifiedGuilds.add(uuid);
    }

    /**
     * @return a copy of guilds set
     */
    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guilds.values());
    }

    public Set<Guild> getModifiedGuilds() {
        Set<Guild> modified = new HashSet<>();
        for (UUID uuid : this.modifiedGuilds) {
            modified.add(this.getGuild(uuid));
        }
        return modified;
    }
}
