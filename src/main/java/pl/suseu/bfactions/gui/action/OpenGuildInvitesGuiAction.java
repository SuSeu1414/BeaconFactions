package pl.suseu.bfactions.gui.action;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.CustomInventoryHolder;
import pl.suseu.bfactions.gui.paginator.PaginatorFactory;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;
import pl.suseu.eventwaiter.EventWaiter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OpenGuildInvitesGuiAction implements ClickAction {

    private final BFactions plugin;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final ItemRepository itemRepository;
    private final Guild guild;
    private final LangAPI lang;
    private final EventWaiter eventWaiter;

    public OpenGuildInvitesGuiAction(BFactions plugin, Guild guild) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.guild = guild;
        this.eventWaiter = plugin.getEventWaiter();
        this.lang = plugin.getLang();
        this.itemRepository = plugin.getItemRepository();
    }

    @Override
    public void execute(Player whoClicked) {
        User user = this.userRepository.getUser(whoClicked.getUniqueId());
        if (!this.guild.hasPermission(user, GuildPermission.MANAGE)) {
            whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClicked);
            return;
        }

        Set<User> invitedMembers = this.guild.getInvitedMembers();
        List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items = new ArrayList<>();

        for (User invitedUser : invitedMembers) {
            ItemStack itemStack = this.itemRepository.getItem("invite-info");
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return;
            }

            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = ((SkullMeta) itemMeta);
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(invitedUser.getUuid()));
                itemStack.setItemMeta(skullMeta);
            }

            ItemUtil.replace(itemStack, "%name%", invitedUser.getName());

            ClickAction action = plr -> {
                this.guild.removeInvitedMember(invitedUser);
//                this.guildRepository.addModifiedGuild(this.guild);
                this.lang.sendMessage("invite-removed", plr);
                plr.closeInventory();
            };

            items.add(new AbstractMap.SimpleEntry<>(itemStack, action));
        }


        PaginatorFactory paginatorFactory = new PaginatorFactory(this.plugin);
        // todo configurable title
        CustomInventoryHolder holder = paginatorFactory.createPaginator("Invites %page%/%pages%", 6, 1, items);
        holder.set(4, this.itemRepository.getItem("invite-player"), new InviteMemberAction(this.plugin, this.guild));
        //todo set fancy items

        Inventory inventory = holder.getInventory();
        whoClicked.openInventory(inventory);
    }
}
