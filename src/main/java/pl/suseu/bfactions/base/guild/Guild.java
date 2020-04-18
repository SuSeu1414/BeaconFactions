package pl.suseu.bfactions.base.guild;

import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.guild.permission.GuildPermissionSet;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.gui.base.FuelInventoryHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final UUID uuid;
    private final Region region;
    private final Field field;
    private final Set<User> members = ConcurrentHashMap.newKeySet();
    private final Set<User> invitedMembers = ConcurrentHashMap.newKeySet();
    private final Map<User, GuildPermissionSet> permissions = new ConcurrentHashMap<>();
    private String name;
    private User owner;

    private Inventory fuelInventory;

    public Guild(UUID uuid, String name, User owner, Region region, Field field) {
        this.uuid = uuid;
        this.name = name;

        this.owner = owner;
        if (this.owner != null) {
            this.owner.addGuild(this);
        }

        this.region = region;
        if (this.region != null) {
            this.region.setGuild(this);
        }

        this.field = field;
        if (this.field != null) {
            this.field.setGuild(this);
        }

        this.fuelInventory = new FuelInventoryHolder(this).getInventory();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addMember(User user) {
        if (user == null) {
            return;
        }
        this.members.add(user);
        this.permissions.put(user, GuildPermissionSet.DEFAULT_PERMISSIONS);
        user.addGuild(this);
        Player player = Bukkit.getPlayer(user.getUuid());
        if (player != null) {
            this.getField().getEnemyBar().removePlayer(player);
        }
    }

    public void removeMember(User user) {
        if (user == null) {
            return;
        }
        this.members.remove(user);
        this.permissions.remove(user);
        user.removeGuild(this);
        Player player = Bukkit.getPlayer(user.getUuid());
        if (player != null) {
            this.getField().getAlliedBar().removePlayer(player);
        }
    }

    public void addInvitedMember(User user) {
        this.invitedMembers.add(user);
    }

    public void removeInvitedMember(User user) {
        this.invitedMembers.remove(user);
    }

    public Set<User> getInvitedMembers() {
        return new HashSet<>(this.invitedMembers);
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
        if (member == null || permission == null) {
            return false;
        }
        Player player = Bukkit.getPlayer(member.getUuid());
        if (player != null && player.isOp()) {
            //TODO add bypass permissions
            return true;
        }
        GuildPermissionSet permissionSet = this.permissions.get(member);
        return this.owner.equals(member) || (permissionSet != null && permissionSet.hasPermission(permission));
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

    public boolean isMember(User user) {
        return user.getGuilds().contains(this);
    }

    public String getMembersSerialized() {
        if (plugin == null) {
            return "null";
        }

        List<String> uuids = new ArrayList<>();
        for (User member : this.members) {
            uuids.add(member.getUuid().toString());
        }

        try {
            return plugin.getGson().toJson(uuids);
        } catch (Exception e) {
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
            uuids = this.plugin.getGson().fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
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
            addMember(member);
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
            return plugin.getGson().toJson(permissionMap);
        } catch (Exception e) {
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
            perms = this.plugin.getGson().fromJson(json, new TypeToken<Map<String, Integer>>() {
            }.getType());
        } catch (Exception e) {
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

    public Inventory getFuelInventory() {
        return fuelInventory;
    }
}
