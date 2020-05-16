package pl.suseu.bfactions.base.guild.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.region.event.PlayerRegionChangeEvent;

public class MotdListener implements Listener {

    private final RegionRepository regionRepository;
    private final LangAPI langAPI;

    public MotdListener(BFactions plugin) {
        this.regionRepository = plugin.getRegionRepository();
        this.langAPI = plugin.getLang();
    }

    @EventHandler
    public void onRegionChange(PlayerRegionChangeEvent event) {
        Location location = event.getTo();
        Region closest = this.regionRepository.nearestRegion(location);

        if (closest == null) {
            return;
        }

        Guild guild = closest.getGuild();
        String motd = event.getRegion() != null ? guild.getEntryMOTD() : guild.getExitMOTD();

        if (motd != null && !motd.equals("null")) {
            if (event.getRegion() != null) {
                langAPI.sendMessage("guild-motd-entry", event.getPlayer(), "%motd%", motd, "%guild%", guild.getName());
            } else {
                langAPI.sendMessage("guild-motd-exit", event.getPlayer(), "%motd%", motd, "%guild%", guild.getName());
            }
        }
    }
}
