package pl.suseu.bfactions.base.guild;

import pl.suseu.bfactions.base.user.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {

    private final String name;
    private final User owner;

    private final Set<User> members = ConcurrentHashMap.newKeySet();
    private final Map<User, GuildPermissionSet> permissions = new ConcurrentHashMap<>();

    public Guild(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
}
