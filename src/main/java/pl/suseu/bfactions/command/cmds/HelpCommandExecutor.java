package pl.suseu.bfactions.command.cmds;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.command.BCommandMap;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommandExecutor implements BCommandExecutor {

    private final BCommandMap bCommandMap;
    private final LangAPI lang;

    public HelpCommandExecutor(BFactions plugin, BCommandMap bCommandMap) {
        this.bCommandMap = bCommandMap;
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

        int elementsPerPage = 5;

        List<String> help = this.bCommandMap.getCommands().stream()
                .filter(cmd -> sender.hasPermission(cmd.getPermission()))
                .sorted(Comparator.comparing(BCommand::getName))
                .map(cmd -> ""
                        + ChatColor.BOLD
                        + ChatColor.RED
                        + "/bf "
                        + cmd.getUsage()
                        + ChatColor.RESET
                        + ChatColor.ITALIC
                        + " - "
                        + cmd.getDescription())
                .collect(Collectors.toList());

        int pagesCount = (int) Math.ceil((double) help.size() / elementsPerPage);
        requestedPage = Math.max(requestedPage, 1);
        requestedPage = Math.min(requestedPage, pagesCount);

        this.lang.sendMessage("help-command-header", sender,
                "%requestedPage%", String.valueOf(requestedPage), "%maxPage%", String.valueOf(pagesCount));
        for (int i = elementsPerPage * (requestedPage - 1); i < elementsPerPage * requestedPage; i++) {
            if (i < 0) {
                i = 0;
            }
            if (i >= help.size()) {
                break;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&9[&bBFactions&9]")
                    + " " + (i + 1) + ". " + help.get(i));
        }
    }
}
