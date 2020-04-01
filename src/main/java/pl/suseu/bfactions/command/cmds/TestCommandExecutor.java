package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;

public class TestCommandExecutor implements BCommandExecutor {

    @Override
    public void execute(CommandSender sender, List<String> args) {
        sender.sendMessage("uwu");
    }
}
