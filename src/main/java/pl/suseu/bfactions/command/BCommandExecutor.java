package pl.suseu.bfactions.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface BCommandExecutor {

    void execute(CommandSender sender, BCommand command, List<String> args);

}
