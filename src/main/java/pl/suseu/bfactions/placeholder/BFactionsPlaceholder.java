package pl.suseu.bfactions.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import pl.suseu.bfactions.BFactions;

public class BFactionsPlaceholder extends PlaceholderExpansion {

    private final BFactions plugin;
    private final PlaceholderService placeholderService;

    public BFactionsPlaceholder(BFactions plugin) {
        this.plugin = plugin;
        this.placeholderService = plugin.getPlaceholderService();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "bf";
    }

    @Override
    public String getAuthor() {
        return "SuSeu1414 & Rynbou";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        return this.placeholderService.getUserPlaceholders().getPlaceholder(player.getUniqueId(), params);
    }
}
