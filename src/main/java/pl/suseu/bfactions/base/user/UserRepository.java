package pl.suseu.bfactions.base.user;

import org.apache.commons.lang.StringUtils;
import pl.suseu.bfactions.BFactions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRepository {

    private final BFactions plugin;

    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Set<UUID> modifiedUsers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> projectileUsers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, User> onlineUsers = new ConcurrentHashMap<>();

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

        user = new User(uuid, null);
        addUser(user, true);
        return user;
    }

    public List<User> getUsers(String name) {
        return this.getUsers().stream()
                .filter(user -> StringUtils.containsIgnoreCase(user.getName(), name))
                .collect(Collectors.toList());
    }

    public User getUserByName(String name) {
        for (User user : this.getUsers()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    public void addOnlineUser(User user) {
        this.onlineUsers.put(user.getUuid(), user);
    }

    public void removeOnlineUser(User user) {
        this.removeOnlineUser(user.getUuid());
    }

    public void removeOnlineUser(UUID uuid) {
        this.onlineUsers.remove(uuid);
    }

    public Set<User> getOnlineUsers() {
        return new HashSet<>(this.onlineUsers.values());
    }

    public User getOnlineUser(UUID uuid) {
        if (!this.onlineUsers.containsKey(uuid)) {
            return null;
        }
        return this.onlineUsers.get(uuid);
    }

    public void addModifiedUser(User user) {
        this.addModifiedUser(user.getUuid());
    }

    public void addModifiedUser(UUID uuid) {
        this.modifiedUsers.add(uuid);
    }

    public Set<UUID> getModifiedUserUUID() {
        return new HashSet<>(this.modifiedUsers);
    }

    public Set<User> getModifiedUsers() {
        Set<User> users = new HashSet<>();
        for (UUID uuid : this.modifiedUsers) {
            users.add(this.getUser(uuid));
        }

        return users;
    }

    public void clearModifiedUsers() {
        this.modifiedUsers.clear();
    }

    public void addProjectileUser(User user) {
        this.addProjectileUser(user.getUuid());
    }

    public void addProjectileUser(UUID uuid) {
        this.projectileUsers.add(uuid);
    }

    public Set<UUID> getProjectileUserUUID() {
        return new HashSet<>(this.projectileUsers);
    }

    public Set<User> getProjectileUsers() {
        Set<User> users = new HashSet<>();
        for (UUID uuid : this.projectileUsers) {
            users.add(this.getUser(uuid));
        }

        return users;
    }

    /**
     * @return a copy of users set
     */
    public Set<User> getUsers() {
        return new HashSet<>(this.users.values());
    }
}
