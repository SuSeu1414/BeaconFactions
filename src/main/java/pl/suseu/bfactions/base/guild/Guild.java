package pl.suseu.bfactions.base.guild;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.Field;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.guild.permission.GuildPermissionSet;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.tier.DiscountTier;
import pl.suseu.bfactions.base.tier.FieldTier;
import pl.suseu.bfactions.base.tier.RegionTier;
import pl.suseu.bfactions.base.tier.Tier;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.gui.base.FuelInventoryHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Guild implements Comparable<Guild> {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final UUID uuid;
    private final Region region;
    private final Field field;
    private Location home;
    private final Set<User> members = ConcurrentHashMap.newKeySet();
    private final Set<User> invitedMembers = ConcurrentHashMap.newKeySet();
    private final Map<User, GuildPermissionSet> permissions = new ConcurrentHashMap<>();
    private String name;
    private User owner;
    private String entryMOTD = null;
    private String exitMOTD = null;
    private boolean outline = false;

    private Inventory fuelInventory;
    private int deleteCode = -1;
    private int transferCode = -1;
    private boolean pvpEnabled;
    private DiscountTier discountTier;

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
            this.home = region.getCenter().clone().add(0, 1, 0);
        }

        this.field = field;
        if (this.field != null) {
            this.field.setGuild(this);
        }

        this.fuelInventory = new FuelInventoryHolder(this).getInventory();
        this.discountTier = null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addMember(User user) {
        if (user == null) {
            return;
        }
        this.members.add(user);
        this.permissions.put(user, GuildPermissionSet.getDefaultPermissionSet());
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

    public void setMemberPermissionSet(User member, GuildPermissionSet set) {
        if (set == null) {
            return;
        }
        this.permissions.put(member, set);
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

    public boolean hasPermission(User member, GuildPermission permission, boolean checkBypass) {
        if (member == null || permission == null) {
            return false;
        }
        if (checkBypass && bypassesPermission(member, permission)) {
            return true;
        }
        GuildPermissionSet permissionSet = this.permissions.get(member);
        return this.owner.equals(member) || (permissionSet != null && permissionSet.hasPermission(permission));
    }

    public boolean bypassesPermission(User member, GuildPermission permission) {
        if (member == null || permission == null) {
            return false;
        }
        Player player = Bukkit.getPlayer(member.getUuid());
        if (player != null) {
            if (player.isOp()) {
                return true;
            }
            if (player.hasPermission(permission.getBypassPermission())) {
                return true;
            }
        }
        return false;
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

    public Set<User> getMembersAndOwner() {
        Set<User> members = new HashSet<>(this.members);
        members.add(this.owner);
        return members;
    }

    public Field getField() {
        return field;
    }

    public boolean isMember(User user) {
        return user.getGuilds().contains(this);
    }

    public Tier getTier(Tier.TierType tierType) {
        if (tierType == Tier.TierType.FIELD) {
            return this.field.getTier();
        }

        if (tierType == Tier.TierType.REGION) {
            return this.region.getTier();
        }

        if (tierType == Tier.TierType.DISCOUNT) {
            return this.discountTier;
        }

        return null;
    }

    public void setTier(Tier tier) {
        if (tier instanceof FieldTier) {
            this.field.setTier(((FieldTier) tier));
        }

        if (tier instanceof RegionTier) {
            this.region.setTier(((RegionTier) tier));
            if (this.plugin != null) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, this.field::recalculate);
            }
        }

        if (tier instanceof DiscountTier) {
            this.setDiscountTier(((DiscountTier) tier));
        }
    }


    public String getMembersSerialized() {
        if (plugin == null) {
            return "[]";
        }
        return plugin.getDataSerializer().getMembersSerialized(this.getMembers());
    }

    public void setMembersFromJson(String json) {
        if (plugin == null) {
            return;
        }
        plugin.getDataSerializer().setMembersFromJson(json, this);
    }

    public String getPermissionsSerialized() {
        if (plugin == null) {
            return "{}";
        }
        return plugin.getDataSerializer().getPermissionsSerialized(this.permissions);
    }

    public void setPermissionsFromJson(String json) {
        if (plugin == null) {
            return;
        }
        plugin.getDataSerializer().setPermissionsFromJson(json, this);
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public String getHomeSerialized() {
        if (plugin == null) {
            return "{}";
        }
        return plugin.getDataSerializer().serializeLocation(this.home);
    }

    public void setHomeSerialized(String json) {
        if (plugin == null) {
            return;
        }
        if (json == null) {
            return;
        }
        this.home = this.plugin.getDataSerializer().deserializeLocation(json);
    }

    public Inventory getFuelInventory() {
        return fuelInventory;
    }

    public int getDeleteCode() {
        return deleteCode;
    }

    public void setDeleteCode(int deleteCode) {
        this.deleteCode = deleteCode;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public DiscountTier getDiscountTier() {
        return discountTier;
    }

    public void setDiscountTier(DiscountTier discountTier) {
        this.discountTier = discountTier;
    }

    public String getEntryMOTD() {
        return entryMOTD;
    }

    public void setEntryMOTD(String entryMOTD) {
        this.entryMOTD = entryMOTD;
    }

    public String getExitMOTD() {
        return exitMOTD;
    }

    public void setExitMOTD(String exitMOTD) {
        this.exitMOTD = exitMOTD;
    }

    public boolean isOutline() {
        return outline;
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    public void delete() {
        if (this.plugin == null) {
            return;
        }

        for (User member : this.getMembers()) {
            this.removeMember(member);
        }
        this.removeMember(this.getOwner());
        this.getField().getAlliedBar().removeAll();
        this.getField().getEnemyBar().removeAll();
        this.getField().getEnemyBar().setVisible(false);
        this.getField().getAlliedBar().setVisible(false);
        this.getRegion().getCenter().getBlock().setType(Material.AIR);
        this.plugin.getRegionRepository().removeRegion(this.getRegion());
        this.plugin.getFieldRepository().removeField(this.getField());
        this.plugin.getGuildRepository().removeGuild(this.getUuid());
        this.plugin.getGuildRepository().addDeletedGuild(this.getUuid());
    }

    public int getTransferCode() {
        return transferCode;
    }

    public void setTransferCode(int transferCode) {
        this.transferCode = transferCode;
    }

    @Override
    public int compareTo(Guild o) {
        if (o == null) {
            return 0;
        }
        return this.name.compareTo(o.getName());
    }
}
