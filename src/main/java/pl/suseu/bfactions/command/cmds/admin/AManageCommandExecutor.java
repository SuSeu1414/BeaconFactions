package pl.suseu.bfactions.command.cmds.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.main.factory.MainGuiFactory;
import pl.suseu.bfactions.gui.main.factory.paginator.PaginatorFactory;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

public class AManageCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final ItemRepository itemRepository;
    private final MainGuiFactory mainGuiFactory;

    public AManageCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.itemRepository = plugin.getItemRepository();
        this.mainGuiFactory = new MainGuiFactory(plugin);
    }


    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player pSender = (Player) sender;
        User uSender = this.userRepository.getUser((pSender.getUniqueId()));

        Inventory inv = new PaginatorFactory(this.plugin).createPaginator(pSender, "Choose guild", 6, 1,
                this.guildRepository.getGuilds().stream()
                        .flatMap(guild -> guild.getMembersAndOwner().stream())
                        .distinct()
                        .filter(member -> member.getName().equalsIgnoreCase(args.get(0)))
                        .flatMap(member -> member.getGuilds().stream())
                        .distinct()
                        .sorted()
                        .map(guild -> {
                            ItemStack itemStack = this.itemRepository.getItem("guild-info-list", uSender.isDefaultItems());
                            ItemUtil.replace(itemStack, "%name%", guild.getName());
                            ItemUtil.replace(itemStack, "%owner%", guild.getOwner().getName());
                            ClickAction action = whoClicked -> whoClicked.openInventory(mainGuiFactory.createGui(pSender, guild));
                            return new AbstractMap.SimpleEntry<>(itemStack, action);
                        })
                        .collect(Collectors.toList()))
                .getInventory();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> pSender.openInventory(inv));

    }
}
