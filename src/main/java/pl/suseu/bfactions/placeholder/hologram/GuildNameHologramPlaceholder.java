package pl.suseu.bfactions.placeholder.hologram;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.placeholder.PlaceholderService;

import java.util.UUID;

public class GuildNameHologramPlaceholder implements PlaceholderReplacer {

    private final UUID guild;

    private final BFactions plugin;
    private final PlaceholderService placeholderService;

    public GuildNameHologramPlaceholder(UUID guild, BFactions plugin) {
        this.guild = guild;
        this.plugin = plugin;
        this.placeholderService = plugin.getPlaceholderService();
    }

    @Override
    public String update() {
        return this.placeholderService.getGuildName(guild);
    }
}
