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

public class OutlineCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;

    public OutlineCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = (Player) sender;
        User user = userRepository.getUser(player.getUniqueId());
        user.setOutline(!user.isOutline());
        lang.sendMessage("outlines-visibility-toggle", player, "%status%", user.isOutline() ? "ON" : "OFF");
    }
}
