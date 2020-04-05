package pl.suseu.bfactions.base.guild;

import org.bukkit.Location;
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

    public Guild getGuildByBeaconLocation(Location location) {
        for (Guild g : this.getGuilds()) {
            if (g.getRegion() == null) {
                continue;
            }
            Location beacon = g.getRegion().getCenter();
            if (location.getBlockX() == beacon.getBlockX()
                    && location.getBlockY() == beacon.getBlockY()
                    && location.getBlockZ() == beacon.getBlockZ()) {
                return g;
            }
        }
        return null;
    }

    public void addModifiedGuild(Guild guild) {
        this.addModifiedGuild(guild.getUuid());
    }

    public void addModifiedGuild(UUID uuid) {
        this.modifiedGuilds.add(uuid);
    }

    public void clearModifiedGuilds() {
        this.modifiedGuilds.clear();
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
