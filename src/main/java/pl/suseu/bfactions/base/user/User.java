package pl.suseu.bfactions.base.user;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    private final BFactions plugin = ((BFactions) Bukkit.getPluginManager().getPlugin(BFactions.PLUGIN_NAME));

    private final UUID uuid;
    private String name;
    private final Set<Guild> guilds = ConcurrentHashMap.newKeySet();

    private Region currentRegion;
    private Region nearestRegion;
    private Location currentLocation;
    private long lastRegionChange;

    private boolean defaultItems;
    private boolean potatoMode = false;

    private boolean mapNeedsRedraw;
    private Image mapImage;
    private int lastDrawnMap;
    private boolean recalculatingMap;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
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
        if (this.name == null || this.name.isEmpty()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName();
            if (name == null) {
                return "null";
            }
            return name;
        }

        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.plugin.getUserRepository().addModifiedUser(this);
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

    public boolean mapNeedsRedraw() {
        return mapNeedsRedraw;
    }

    public void setMapNeedsRedraw(boolean mapNeedsRedraw) {
        this.mapNeedsRedraw = mapNeedsRedraw;
    }

    public Image getMapImage() {
        return mapImage;
    }

    public void setMapImage(Image mapImage) {
        this.mapImage = mapImage;
    }

    public int getLastDrawnMap() {
        return lastDrawnMap;
    }

    public void setLastDrawnMap(int lastDrawnMap) {
        this.lastDrawnMap = lastDrawnMap;
    }

    public boolean isRecalculatingMap() {
        return recalculatingMap;
    }

    public void setRecalculatingMap(boolean recalculatingMap) {
        this.recalculatingMap = recalculatingMap;
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
