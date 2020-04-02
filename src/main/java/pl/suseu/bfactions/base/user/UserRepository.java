package pl.suseu.bfactions.base.user;

import pl.suseu.bfactions.BFactions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private final BFactions plugin;

    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Set<UUID> modifiedUsers = ConcurrentHashMap.newKeySet();

    public UserRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public void addUser(User user, boolean newUser) {
        this.users.put(user.getUuid(), user);
        if (newUser) {
            this.modifiedUsers.add(user.getUuid());
        }
    }

    public User getUser(UUID uuid) {
        User user = this.users.get(uuid);
        if (user != null) {
            return user;
        }

        user = new User(uuid);
        addUser(user, true);
        return user;
    }

    public void addModifiedUser(User user) {
        this.addModifiedUser(user.getUuid());
    }

    public void addModifiedUser(UUID uuid) {
        modifiedUsers.add(uuid);
    }

    public Set<UUID> getModifiedUserUUID() {
        return new HashSet<>(this.modifiedUsers);
    }

    public Set<User> getModifiedUsers() {
        Set<User> modified = new HashSet<>();
        for (UUID uuid : this.modifiedUsers) {
            modified.add(this.getUser(uuid));
        }

        return modified;
    }

    /**
     * @return a copy of users set
     */
    public Set<User> getUsers() {
        return new HashSet<>(this.users.values());
    }
}
