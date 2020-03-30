package pl.suseu.bfactions.base.user;

import pl.suseu.bfactions.base.guild.Guild;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    private UUID uuid;

    private Set<Guild> guilds = ConcurrentHashMap.newKeySet();

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
