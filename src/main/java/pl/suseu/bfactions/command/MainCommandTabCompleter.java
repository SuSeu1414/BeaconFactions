package pl.suseu.bfactions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import pl.suseu.bfactions.BFactions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommandTabCompleter implements TabCompleter {

    private final BFactions plugin;
    private final BCommandMap commandMap;

    public MainCommandTabCompleter(BFactions plugin, BCommandMap commandMap) {
        this.plugin = plugin;
        this.commandMap = commandMap;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return this.commandMap.getCommands().stream()
                    .filter(cmd -> commandSender.hasPermission(cmd.getPermission()))
                    .map(BCommand::getName)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

}
