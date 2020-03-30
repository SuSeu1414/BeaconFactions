package pl.suseu.bfactions.base.user;

import pl.suseu.bfactions.base.guild.Guild;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    private final UUID uuid;

    private final Set<Guild> guilds = ConcurrentHashMap.newKeySet();

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean ownsGuild(){
        return this.getOwnedGuild() != null;
    }

    public Guild getOwnedGuild() {
        for (Guild guild : this.guilds) {
            if (guild.getOwner().equals(this)) {
                return guild;
            }
        }
        return null;
    }

    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guilds);
    }

    public void addGuild(Guild guild) {
        this.guilds.add(guild);
    }

    public void removeGuild(Guild guild) {
        this.guilds.remove(guild);
    }
}
