package pl.suseu.bfactions.base.guild.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.gui.main.MainGuiFactory;

public class BeaconClickListener implements Listener {

    private final BFactions plugin;
    private final GuildRepository guildRepository;
    private final MainGuiFactory mainGuiFactory;

    public BeaconClickListener(BFactions plugin) {
        this.plugin = plugin;
        this.guildRepository = plugin.getGuildRepository();
        this.mainGuiFactory = new MainGuiFactory(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        if (clickedBlock.getType() != Material.BEACON) {
            return;
        }

        Guild guild = this.guildRepository.getGuildByBeaconLocation(clickedBlock.getLocation());

        if (guild == null) {
            return;
        }

        event.setCancelled(true);

        Inventory gui = this.mainGuiFactory.createGui(event.getPlayer(), guild);
        event.getPlayer().openInventory(gui);
    }


}
