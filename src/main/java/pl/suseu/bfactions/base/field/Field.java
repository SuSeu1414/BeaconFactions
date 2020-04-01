package pl.suseu.bfactions.base.field;

import pl.suseu.bfactions.base.guild.Guild;

import java.util.UUID;

public class Field {

    private final UUID uuid;
    private Guild guild;

    public Field(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
