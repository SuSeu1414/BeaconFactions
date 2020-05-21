package pl.suseu.bfactions.placeholder;

import org.bukkit.Bukkit;
import pl.suseu.bfactions.base.region.Region;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserPlaceholders {

    private final Map<UUID, Placeholders> placeholders = new ConcurrentHashMap<>();

    public void updatePlaceHolder(UUID player, String placeholder, String value) {
        this.placeholders.computeIfAbsent(player, uuid -> new Placeholders()).setPlaceholder(placeholder, value);
    }

    public String getPlaceholder(UUID player, String placeholder) {
        if (!this.placeholders.containsKey(player)) {
            return "";
        }
        return this.placeholders.get(player).getPlaceholder(placeholder);
    }

    public void removePlayer(UUID player) {
        this.placeholders.remove(player);
    }

    public void removeOfflinePlayers() {
        this.placeholders.keySet().stream()
                .filter(uuid -> !Bukkit.getOfflinePlayer(uuid).isOnline())
                .forEach(this.placeholders::remove);
    }

    public void updatePlayerRegion(UUID player, Region region) {
        if (region != null) {
            this.updatePlaceHolder(player, "guild_name", region.getGuild().getName());
            this.updatePlaceHolder(player, "guild_online", region.getGuild().getMembersAndOwner().stream()
                    .filter(user -> Bukkit.getOfflinePlayer(user.getUuid()).isOnline())
                    .count() + "");
            this.updatePlaceHolder(player, "guild_members", region.getGuild().getMembersAndOwner().size() + "");
        } else {
            this.updatePlaceHolder(player, "guild_name", "None");
            this.updatePlaceHolder(player, "guild_online", "-1");
            this.updatePlaceHolder(player, "guild_members", "-1");
        }
    }
}
