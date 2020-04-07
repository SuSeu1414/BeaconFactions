package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.base.guild.Guild;

import java.util.UUID;

public class Region {

    private final UUID uuid;
    private Guild guild;

    private Location center;
    private int size;

    public Region(UUID uuid, Location center, int size) {
        this.uuid = uuid;
        this.center = center;
        this.size = size;
    }

    public void teleportToSafety(Player player) {
        //TODO find safe location outside the region
        player.teleport(player.getWorld().getSpawnLocation());
    }

    public boolean isInBorder(Location location) {
        Location l1 = location.clone();
        Location l2 = this.center.clone();
        l1.setY(0);
        l2.setY(0);
        return l1.distance(l2) < size;
    }

    public boolean isInDome(Location location) {
        if (location.getBlockY() < center.getBlockY()) {
            return false;
        }
        if (location.distance(center) > size) {
            return false;
        }
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
