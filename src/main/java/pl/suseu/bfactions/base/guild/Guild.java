package pl.suseu.bfactions.base.guild;

import pl.suseu.bfactions.base.user.User;

import java.util.Map;
import java.util.Set;

public class Guild {

    private String name;
    private User owner;
    private Set<User> members;
    private Map<User, GuildPermissionSet> permissions;

}
