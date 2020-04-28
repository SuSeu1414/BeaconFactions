package pl.suseu.bfactions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;

import java.util.Arrays;

public class MainCommand implements CommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final BCommandMap commandMap;

    public MainCommand(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.commandMap = new BCommandMap(plugin);
        this.commandMap.initCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            lang.sendMessage("base-command-info", sender);
            return true;
        }

        BCommand bCommand = commandMap.getCommand(args[0]);

        if (bCommand == null) {
            lang.sendMessage("unknown-command", sender);
            return true;
        }

        if (!sender.hasPermission(bCommand.getPermission())) {
            lang.sendMessage("no-permission", sender);
            return true;
        }

        if (bCommand.isNeedsArguments() && args.length == 1) {
            bCommand.sendUsage(sender, label);
            return true;
        }

        bCommand.getExecutor().execute(sender, bCommand, label, Arrays.asList(args).subList(1, args.length));

        return true;
    }
}
