package pl.suseu.bfactions.base.guild.permission;

public enum GuildPermission {

    MANAGE(1, "bfactions.bypass-manage"),
    OPEN_CHESTS(2, "bfactions.bypass-chests"),
    OPEN_DOORS(4, "bfactions.bypass-doors"),
    MODIFY_TERRAIN(8, "bfactions.bypass-terrain"),
    KILL_ANIMALS(16, "bfactions.bypass-animals");

    final int bit;
    final String bypassPermission;

    GuildPermission(int bit, String bukkitPermission) {
        this.bit = bit;
        this.bypassPermission = bukkitPermission;
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

    public String getBypassPermission() {
        return bypassPermission;
    }
}
