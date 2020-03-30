package pl.suseu.bfactions.base.guild;

import pl.suseu.bfactions.base.user.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {

    private String name;
    private User owner;
    private Set<User> members = ConcurrentHashMap.newKeySet();
    private Map<User, GuildPermissionSet> permissions = new ConcurrentHashMap<>();

}
