package pl.suseu.bfactions.command.cmds;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.factory.paginator.GuildPaginatorFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WhoOnlineCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;

    public WhoOnlineCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    public static void sendWhoMessage(Player receiver, Guild guild, BFactions plugin) {
        Set<OfflinePlayer> members = guild.getMembers().stream()
                .map(user -> plugin.getServer().getOfflinePlayer(user.getUuid()))
                .collect(Collectors.toSet());
        OfflinePlayer owner = plugin.getServer().getOfflinePlayer(guild.getOwner().getUuid());

        List<String> players = members.stream()
                .filter(offlinePlayer -> offlinePlayer.getName() != null)
                .sorted(Comparator.comparing(OfflinePlayer::getName))
                .map(offlinePlayer -> (offlinePlayer.isOnline() ? ChatColor.GREEN : ChatColor.RED) + offlinePlayer.getName())
                .collect(Collectors.toList());

        plugin.getLang().sendMessage("guild-members-list", receiver, "%guild%", guild.getName());

        ChatColor ownerColor = owner.isOnline() ? ChatColor.GREEN : ChatColor.RED;
        receiver.sendMessage("OWNER: " + ownerColor + owner.getName());

        if (!players.isEmpty()) {
            receiver.sendMessage("MEMBERS: ");
        }
        players.forEach(p -> receiver.sendMessage(p + " "));
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player pSender = (Player) sender;
        new GuildPaginatorFactory(this.plugin)
                .openGuildsGui(pSender, args.size() == 0 ? pSender.getName() : args.get(0),
                        clickedGuild -> {
                            sendWhoMessage(pSender, clickedGuild, this.plugin);
                            pSender.closeInventory();
                        });
    }
}
