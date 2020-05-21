package pl.suseu.bfactions.placeholder;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.user.UserRepository;

public class PlaceholderService implements Runnable {


    private final BFactions plugin;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private long counter = 0;

    public PlaceholderService(BFactions plugin) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
    }

    @Override
    public void run() {
        updateGuilds();
        counter++;
    }

    private void updateGuilds() {
        if (counter % (20 * 5) == 0) { // every 5 seconds
            for (Guild guild : guildRepository.getGuilds()) {
                guild.updatePlaceholders();
                guild.updateHologram();
            }
        }
    }
}
