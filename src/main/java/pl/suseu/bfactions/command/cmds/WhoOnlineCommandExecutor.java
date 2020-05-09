package pl.suseu.bfactions.command.cmds;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
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
                            Set<OfflinePlayer> members = clickedGuild.getMembers().stream()
                                    .map(user -> this.plugin.getServer().getOfflinePlayer(user.getUuid()))
                                    .collect(Collectors.toSet());
                            OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(clickedGuild.getOwner().getUuid());

                            List<String> players = members.stream()
                                    .filter(offlinePlayer -> offlinePlayer.getName() != null)
                                    .sorted(Comparator.comparing(OfflinePlayer::getName))
                                    .map(offlinePlayer -> (offlinePlayer.isOnline() ? ChatColor.GREEN : ChatColor.RED) + offlinePlayer.getName())
                                    .collect(Collectors.toList());

                            lang.sendMessage("guild-members-list", pSender, "%guild%", clickedGuild.getName());

                            ChatColor ownerColor = owner.isOnline() ? ChatColor.GREEN : ChatColor.RED;
                            pSender.sendMessage("OWNER: " + ownerColor + owner.getName());

                            if (!players.isEmpty()) {
                                pSender.sendMessage("MEMBERS: ");
                            }
                            players.forEach(player -> pSender.sendMessage(player + " "));
                            pSender.closeInventory();
                        });
    }
}
