package pl.suseu.bfactions.command.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.region.RegionRepository;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.command.BCommand;
import pl.suseu.bfactions.command.BCommandExecutor;
import pl.suseu.bfactions.gui.main.action.LeaveGuildAction;
import pl.suseu.bfactions.gui.main.factory.confirmation.ConfirmationGuiFactory;

import java.util.List;

public class LeaveCommandExecutor implements BCommandExecutor {

    private final BFactions plugin;
    private final LangAPI lang;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RegionRepository regionRepository;

    public LeaveCommandExecutor(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.regionRepository = plugin.getRegionRepository();
    }

    @Override
    public void execute(CommandSender sender, BCommand command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            this.lang.sendMessage("player-only", sender);
            return;
        }

        Player player = ((Player) sender);
        User user = this.userRepository.getUser(player.getUniqueId());
        Region region = this.regionRepository.nearestRegion(player.getLocation());

        if (region == null || !region.isInside(player.getLocation())) {
            this.lang.sendMessage("not-in-region", player);
            return;
        }

        Guild guild = region.getGuild();

        if (!guild.isMember(user)) {
            //should not happen
            return;
        }

        if (guild.isOwner(user)) {
            this.lang.sendMessage("owner-cannot-leave", sender);
            return;
        }

        ConfirmationGuiFactory confirmationGuiFactory = new ConfirmationGuiFactory(this.plugin);
        Inventory inv = confirmationGuiFactory.createGui(new LeaveGuildAction(this.plugin, user, guild));
        if (inv == null) {
            return;
        }

        player.openInventory(inv);
    }
}
