package pl.suseu.bfactions.base.user;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final UUID uuid;
    private final Set<Guild> guilds = ConcurrentHashMap.newKeySet();

    private Region currentRegion;
    private Region nearestRegion;
    private Location currentLocation;
    private long lastRegionChange;

    private boolean defaultItems;
    private boolean potatoMode = false;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean ownsGuild() {
        return this.getOwnedGuild() != null;
    }

    public Guild getOwnedGuild() {
        for (Guild guild : this.guilds) {
            if (guild.getOwner().equals(this)) {
                return guild;
            }
        }
        return null;
    }

    public String getName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String name = player.getName();
        if (name == null) {
            return "null";
        }
        return name;
    }

    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guilds);
    }

    public void addGuild(Guild guild) {
        this.guilds.add(guild);
    }

    public void removeGuild(Guild guild) {
        this.guilds.remove(guild);
    }

    public Region getCurrentRegion() {
        return currentRegion;
    }

    public void setCurrentRegion(Region currentRegion) {
        this.currentRegion = currentRegion;
    }

    public Region getNearestRegion() {
        return nearestRegion;
    }

    public void setNearestRegion(Region nearestRegion) {
        this.nearestRegion = nearestRegion;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public long getLastRegionChange() {
        return lastRegionChange;
    }

    public void setLastRegionChange(long lastRegionChange) {
        this.lastRegionChange = lastRegionChange;
    }

    public boolean isDefaultItems() {
        return defaultItems;
    }

    public void setDefaultItems(boolean defaultItems) {
        this.defaultItems = defaultItems;
    }

    public boolean usesPotatoMode() {
        return potatoMode;
    }

    public void setPotatoMode(boolean potatoMode) {
        this.potatoMode = potatoMode;
        plugin.getUserRepository().addModifiedUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
