package pl.suseu.bfactions.data.serializer;

import com.google.gson.reflect.TypeToken;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.permission.GuildPermissionSet;
import pl.suseu.bfactions.base.user.User;

import java.util.*;

public class DataSerializer {

    private final BFactions plugin;

    public DataSerializer(BFactions plugin) {
        this.plugin = plugin;
    }

    public String getMembersSerialized(Set<User> members) {
        List<String> uuids = new ArrayList<>();
        for (User member : members) {
            uuids.add(member.getUuid().toString());
        }

        try {
            return plugin.getGson().toJson(uuids);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void setMembersFromJson(String json, Guild guild) {
        if (this.plugin == null) {
            return;
        }

        List<String> uuids;

        try {
            uuids = this.plugin.getGson().fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
            this.plugin.getLogger().warning("Cannot deserialize members json (guild_uuid: " + guild.getUuid() + ")");
            e.printStackTrace();
            return;
        }

        for (String memberUUIDString : uuids) {
            User member = plugin.getUserRepository().getUser(UUID.fromString(memberUUIDString));
            if (member == null) {
                this.plugin.getLogger().warning("Cannot get member! (guild: " + guild.getUuid() + ", user: " + memberUUIDString + ")");
                continue;
            }
            guild.addMember(member);
        }
    }

    public String getPermissionsSerialized(Map<User, GuildPermissionSet> permissions) {
        if (plugin == null) {
            return "null";
        }

        Map<String, Integer> permissionMap = new HashMap<>();

        for (Map.Entry<User, GuildPermissionSet> entry : permissions.entrySet()) {
            permissionMap.put(entry.getKey().getUuid().toString(), entry.getValue().serialize());
        }

        try {
            return plugin.getGson().toJson(permissionMap);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void setPermissionsFromJson(String json, Guild guild) {
        if (this.plugin == null) {
            return;
        }

        Map<String, Integer> perms;

        try {
            perms = this.plugin.getGson().fromJson(json, new TypeToken<Map<String, Integer>>() {
            }.getType());
        } catch (Exception e) {
            this.plugin.getLogger().warning("Cannot deserialize permissions json (guild_uuid: " + guild.getUuid() + ")");
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, Integer> entry : perms.entrySet()) {
            UUID memberUUID = UUID.fromString(entry.getKey());
            GuildPermissionSet permissionSet = new GuildPermissionSet(entry.getValue());

            User user = this.plugin.getUserRepository().getUser(memberUUID);

            if (user == null) {
                this.plugin.getLogger().warning("Cannot get member! (guild: " + guild.getUuid() + ", user: " + entry.getKey() + ")");
                continue;
            }

            guild.setMemberPermissionSet(user, permissionSet);
        }
    }

}
