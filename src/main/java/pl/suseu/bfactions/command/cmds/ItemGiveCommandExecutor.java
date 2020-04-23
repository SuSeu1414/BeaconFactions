package pl.suseu.bfactions.command.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.settings.Settings;
import pl.suseu.bfactions.util.ItemUtil;
import pl.suseu.bfactions.util.TimeUtil;

import java.util.List;

public class ItemGiveCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final ItemRepository itemRepository;
    private final Settings settings;

    public ItemGiveCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = this.plugin.getLang();
        this.itemRepository = this.plugin.getItemRepository();
        this.settings = plugin.getSettings();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, List<String> args) {
        Player player = null;
        String id = args.get(0);

        if (args.size() == 1) {
            if (!(sender instanceof Player)) {
                this.lang.sendMessage("player-only", sender);
                return;
            }

            player = ((Player) sender);
        }

        if (args.size() == 2) {
            player = Bukkit.getPlayer(args.get(1));
        }

        if (player == null) {
            this.lang.sendMessage("player-offline", sender);
            return;
        }

        ItemStack itemStack = this.itemRepository.getItem(id);
        if (this.settings.fieldBoostUndamageableItems.containsKey(id)) {
            Long time = this.settings.fieldBoostUndamageableItems.get(id);
            ItemUtil.replace(itemStack, "%time%", TimeUtil.timePhrase(time * 1000L, false));
        }
        player.getInventory().addItem(itemStack);
        sender.sendMessage("Given " + id + " to " + player.getName());
    }
}
