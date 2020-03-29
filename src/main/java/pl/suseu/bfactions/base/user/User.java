package pl.suseu.bfactions.base.user;

import java.util.UUID;

public class User {

    private UUID uuid;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
