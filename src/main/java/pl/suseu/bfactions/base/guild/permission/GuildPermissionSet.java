package pl.suseu.bfactions.base.guild.permission;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GuildPermissionSet {

    private static final int MAX_BITS = 10;

    private Set<GuildPermission> permissions = ConcurrentHashMap.newKeySet();

    public GuildPermissionSet(int data) {
        for (int i = 0; i <= MAX_BITS; i++) {
            int bit = data &= (0b1 << i);
            GuildPermission permission = GuildPermission.getByBit(bit);
            if (permission == null) {
                continue;
            }

            this.permissions.add(permission);
        }
    }

    public void addPermission(GuildPermission permission) {
        permissions.add(permission);
    }

    public void removePermission(GuildPermission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(GuildPermission permission) {
        return permissions.contains(permission);
    }

    public int serialize() {
        int data = 0;
        for (GuildPermission perm : this.permissions) {
            data |= perm.getBit();
        }
        return data;
    }


}
