package pl.suseu.bfactions.placeholder;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.user.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceholderService implements Runnable {


    private final BFactions plugin;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final Map<UUID, String> guildNamePlaceholder = new ConcurrentHashMap<>();
    private long counter = 0;

    public PlaceholderService(BFactions plugin) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
    }

    @Override
    public void run() {
        updateGuildNames();
        counter++;
    }

    private void updateGuildNames() {
        if (counter % (20 * 5) == 0) { // every 5 seconds
            for (Guild guild : guildRepository.getGuilds()) {
                this.guildNamePlaceholder.put(guild.getUuid(), guild.getName());
            }
        }
    }

    public String getGuildName(UUID uuid) {
        return this.guildNamePlaceholder.get(uuid);
    }
}
