package pl.suseu.bfactions.command.cmds;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final GuildRepository guildRepository;
    private final LangAPI lang;

    public ListCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.guildRepository = plugin.getGuildRepository();
        this.lang = plugin.getLang();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (args.size() > 1) {
            command.sendUsage(sender, label);
            return;
        }

        int requestedPage = 1;
        if (args.size() == 1) {
            try {
                requestedPage = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
                command.sendUsage(sender, label);
                return;
            }
        }

        int linesPerPage = 10;

        List<String> guilds = this.guildRepository.getGuilds().stream()
                .sorted(Comparator.comparing(o -> ChatColor.stripColor(o.getName())))
                .map(guild -> ("\"" + guild.getName() + "\", OWNER: " + guild.getOwner().getName()))
                .collect(Collectors.toList());

        int pagesCount = (int) Math.ceil((double) guilds.size() / linesPerPage);
        requestedPage = Math.max(requestedPage, 1);
        requestedPage = Math.min(requestedPage, pagesCount);

        sender.sendMessage("Page " + requestedPage + "/" + pagesCount);
        this.lang.sendMessage("list-command-header", sender,
                "%requestedPage%", String.valueOf(requestedPage), "%maxPage%", String.valueOf(pagesCount));
        for (int i = linesPerPage * (requestedPage - 1); i < linesPerPage * requestedPage; i++) {
            if (i >= guilds.size()) {
                break;
            }
            sender.sendMessage(i + ". " + guilds.get(i));
        }
    }
}
