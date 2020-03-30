package pl.suseu.bfactions.base.guild;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GuildPermissionSet {

    private Set<GuildPermission> permissions = ConcurrentHashMap.newKeySet();

    public void addPermission(GuildPermission permission) {
        permissions.add(permission);
    }

    public void removePermission(GuildPermission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(GuildPermission permission) {
        return permissions.contains(permission);
    }

}
