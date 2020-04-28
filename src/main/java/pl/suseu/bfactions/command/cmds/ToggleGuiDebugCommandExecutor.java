package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;

import java.util.List;

public class ToggleGuiDebugCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;

    public ToggleGuiDebugCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = this.plugin.getLang();
        this.userRepository = this.plugin.getUserRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        User user = this.userRepository.getUser(((Player) sender).getUniqueId());
        user.setDefaultItems(!user.isDefaultItems());
        this.lang.sendMessage("gui-debug-toggled", sender, "%status%", user.isDefaultItems() ? "ON" : "OFF");
    }

}
