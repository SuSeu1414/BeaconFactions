package pl.suseu.bfactions.base.guild;

public enum GuildPermission {

    MANAGE(1),
    OPEN_CHESTS(2),
    OPEN_DOORS(4),
    MODIFY_TERRAIN(8),
    KILL_ANIMALS(16);

    final int bit;

    GuildPermission(int bit) {
        this.bit = bit;
    }

    public static GuildPermission getByBit(int bit) {
        for (GuildPermission permission : GuildPermission.values()) {
            if (permission.getBit() == bit) {
                return permission;
            }
        }
        return null;
    }

    public int getBit() {
        return bit;
    }
}
