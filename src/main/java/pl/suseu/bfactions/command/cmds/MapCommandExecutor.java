package pl.suseu.bfactions.command.cmds;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;

public class MapCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;

    public MapCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }
        Player player = ((Player) sender);

        // todo check for map in inventory

        ItemStack mapItem = this.plugin.getFactionMapService().getMapItem();

        if (mapItem == null) {
            return;
        }

        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            player.getInventory().setItemInOffHand(mapItem);
        } else {
            player.getInventory().addItem(mapItem);
        }
    }


}
