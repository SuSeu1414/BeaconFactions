package pl.suseu.bfactions.base.guild;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bukkit.Bukkit;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.guild.permission.GuildPermissionSet;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final UUID uuid;
    private String name;
    private User owner;
    private final Region region;
    private final Field field;
    private final Set<User> members = ConcurrentHashMap.newKeySet();
    private final Map<User, GuildPermissionSet> permissions = new ConcurrentHashMap<>();

    public Guild(UUID uuid, String name, User owner, Region region, Field field) {
        this.uuid = uuid;
        this.name = name;
        this.owner = owner;

        this.region = region;
        if (this.region != null) {
            this.region.setGuild(this);
        }

        this.field = field;
        if (this.field != null) {
            this.field.setGuild(this);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addMember(User user) {
        this.members.add(user);
        // todo add default permission set to permissions map
    }

    public void removeMember(User user) {
        this.members.remove(user);
        this.permissions.remove(user);
    }

    public void addMemberPermission(User member, GuildPermission permission) {
        GuildPermissionSet permissionSet = this.permissions.get(member);
        if (permissionSet == null) {
            return;
        }

        permissionSet.addPermission(permission);
    }

    public void removeMemberPermission(User member, GuildPermission permission) {
        GuildPermissionSet permissionSet = this.permissions.get(member);
        if (permissionSet == null) {
            return;
        }

        permissionSet.removePermission(permission);
    }

    public boolean hasPermission(User member, GuildPermission permission) {
        GuildPermissionSet permissionSet = this.permissions.get(member);
        if (permissionSet == null) {
            return false;
        }

        return permissionSet.hasPermission(permission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isOwner(User user) {
        return this.owner.equals(user);
    }

    public Region getRegion() {
        return region;
    }

    public Set<User> getMembers() {
        return new HashSet<>(this.members);
    }

    public Field getField() {
        return field;
    }

    public String getMembersSerialized() {
        if (plugin == null) {
            return "null";
        }

        List<UUID> uuids = new ArrayList<>();
        for (User member : this.members) {
            uuids.add(member.getUuid());
        }

        try {
            return plugin.getJsonMapper().writeValueAsString(uuids);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void setMembersFromJson(String json) {
        if (this.plugin == null) {
            return;
        }

        List<String> uuids;

        try {
            uuids = this.plugin.getJsonMapper().readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            this.plugin.getLogger().warning("Cannot deserialize members json (guild_uuid: " + this.uuid + ")");
            e.printStackTrace();
            return;
        }

        for (String memberUUIDString : uuids) {
            User member = plugin.getUserRepository().getUser(UUID.fromString(memberUUIDString));
            if (member == null) {
                this.plugin.getLogger().warning("Cannot get member! (guild: " + this.uuid + ", user: " + memberUUIDString + ")");
                continue;
            }
            members.add(member);
        }
    }

    public String getPermissionsSerialized() {
        if (plugin == null) {
            return "null";
        }

        Map<String, Integer> permissionMap = new HashMap<>();

        for (Map.Entry<User, GuildPermissionSet> entry : this.permissions.entrySet()) {
            permissionMap.put(entry.getKey().getUuid().toString(), entry.getValue().serialize());
        }

        try {
            return plugin.getJsonMapper().writeValueAsString(permissionMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void setPermissionsFromJson(String json) {
        if (this.plugin == null) {
            return;
        }

        Map<String, Integer> perms;

        try {
            perms = this.plugin.getJsonMapper().readValue(json, new TypeReference<Map<String, Integer>>() {
            });
        } catch (JsonProcessingException e) {
            this.plugin.getLogger().warning("Cannot deserialize permissions json (guild_uuid: " + this.uuid + ")");
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, Integer> entry : perms.entrySet()) {
            UUID memberUUID = UUID.fromString(entry.getKey());
            GuildPermissionSet permissionSet = new GuildPermissionSet(entry.getValue());

            User user = this.plugin.getUserRepository().getUser(memberUUID);

            if (user == null) {
                this.plugin.getLogger().warning("Cannot get member! (guild: " + this.uuid + ", user: " + entry.getKey() + ")");
                continue;
            }

            this.permissions.put(user, permissionSet);
        }
    }
}
