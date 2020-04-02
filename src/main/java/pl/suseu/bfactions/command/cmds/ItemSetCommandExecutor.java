package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.item.ItemRepository;

import java.util.List;

public class ItemSetCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final ItemRepository itemRepository;

    public ItemSetCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = this.plugin.getLang();
        this.itemRepository = this.plugin.getItemRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = (Player) sender;

        String itemId = args.get(0);
        this.itemRepository.addItem(itemId, player.getInventory().getItemInMainHand());
        this.itemRepository.save();
        sender.sendMessage("Item " + itemId + " set.");
    }
}
