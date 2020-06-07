package pl.suseu.bfactions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.suseu.bfactions.BFactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        if (args.length <= 1) {
            return this.commandMap.getCommands().stream()
                    .filter(cmd -> commandSender.hasPermission(cmd.getPermission()))
                    .filter(cmd -> cmd.getName().startsWith(args[0]))
                    .map(BCommand::getName)
                    .collect(Collectors.toList());
        }

        String cmd = args[0];
        BCommand bCommand = commandMap.getCommand(cmd);
        if (bCommand == null || !commandSender.hasPermission(bCommand.getPermission())) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            if (cmd.equalsIgnoreCase("setitem") || cmd.equalsIgnoreCase("giveitem")) {
                return new ArrayList<>(this.plugin.getItemRepository().getKeys());
            }
            if (cmd.equalsIgnoreCase("setmotd")) {
                return Arrays.asList("entry", "exit");
            }
            if (cmd.equalsIgnoreCase("invite")
                    || cmd.equalsIgnoreCase("kick")
                    || cmd.equalsIgnoreCase("manage")
                    || cmd.equalsIgnoreCase("who")
                    || cmd.equalsIgnoreCase("transfer")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            if (cmd.equalsIgnoreCase("giveitem")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

}
