package pl.suseu.bfactions.base.user;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
    private final Set<UUID> projectiles = ConcurrentHashMap.newKeySet();
    private Location lastSafeLocation;

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

    /**
     * @return -1 if player is offline
     * 0 if safe
     * 1 if unsafe
     */
    public int isInSafeLocation() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return -1;
        }
        Location location = player.getLocation();

        for (Region region : plugin.getRegionRepository().getRegions()) {
            if (!region.isIn(location)) {
                continue;
            }

            if (this.guilds.contains(region.getGuild())) {
                return 1;
            } else {
                return 0;
            }
        }

        return 1;
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

    public Location getLastSafeLocation() {
        return lastSafeLocation;
    }

    public void setLastSafeLocation(Location lastSafeLocation) {
        this.lastSafeLocation = lastSafeLocation;
    }

    public Set<UUID> getProjectiles() {
        return new HashSet<>(this.projectiles);
    }

    public void addProjectile(UUID uuid) {
        this.projectiles.add(uuid);
    }

    public void removeProjectile(UUID uuid) {
        this.projectiles.remove(uuid);
    }
}
