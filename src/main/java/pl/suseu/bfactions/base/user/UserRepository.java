package pl.suseu.bfactions.base.user;

import pl.suseu.bfactions.BFactions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private final BFactions plugin;

    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Set<UUID> modifiedUsers = ConcurrentHashMap.newKeySet();

    public UserRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public void addUser(User user) {
        this.users.put(user.getUuid(), user);
        this.modifiedUsers.add(user.getUuid());
    }

    public User getUser(UUID uuid) {
        User user = this.users.get(uuid);
        if (user != null) {
            return user;
        }

        user = new User(uuid);
        addUser(user);
        return user;
    }

    public void addModifiedUser(UUID uuid) {
        modifiedUsers.add(uuid);
    }

    public Set<UUID> getModifiedUsers() {
        return modifiedUsers;
    }

    /**
     * @return a copy of users set
     */
    public Set<User> getUsers() {
        return new HashSet<>(this.users.values());
    }
}
