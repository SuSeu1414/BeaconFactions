package pl.suseu.bfactions.base.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.settings.RegionTier;

import java.util.UUID;

public class Region {

    private final UUID uuid;
    private Guild guild;

    private Location center;
    private RegionTier tier;

    public Region(UUID uuid, Location center, RegionTier tier) {
        this.uuid = uuid;
        this.center = center;
        this.tier = tier;
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
        return l1.distance(l2) < tier.getRadius();
    }

    public boolean isInDome(Location location) {
        if (location.getBlockY() < center.getBlockY()) {
            return false;
        }
        if (location.distance(center) > tier.getRadius()) {
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
        return tier.getRadius();
    }

    public RegionTier getTier() {
        return tier;
    }

    public void setTier(RegionTier tier) {
        this.tier = tier;
    }
}
