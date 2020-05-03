package pl.suseu.bfactions.gui.main.factory.invite;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.gui.main.action.invite.InviteMemberAction;
import pl.suseu.bfactions.gui.main.factory.paginator.PaginatorFactory;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuildInvitesGuiFactory {

    private final BFactions plugin;
    private final LangAPI lang;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public GuildInvitesGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.itemRepository = plugin.getItemRepository();
        this.userRepository = plugin.getUserRepository();
    }

    public Inventory createGui(Player player, Guild guild) {
        Set<User> invitedMembers = guild.getInvitedMembers();
        List<AbstractMap.SimpleEntry<ItemStack, ClickAction>> items = new ArrayList<>();

        User opener = this.userRepository.getUser(player.getUniqueId());

        for (User invitedUser : invitedMembers) {
            ItemStack itemStack = this.itemRepository.getItem("invite-info", opener.isDefaultItems());
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return null;
            }

            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = ((SkullMeta) itemMeta);
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(invitedUser.getUuid()));
                itemStack.setItemMeta(skullMeta);
            }

            ItemUtil.replace(itemStack, "%player%", invitedUser.getName());

            ClickAction action = plr -> {
                guild.removeInvitedMember(invitedUser);
//                this.guildRepository.addModifiedGuild(this.guild);
                this.lang.sendMessage("invite-removed", plr);
                plr.closeInventory();
            };

            items.add(new AbstractMap.SimpleEntry<>(itemStack, action));
        }


        PaginatorFactory paginatorFactory = new PaginatorFactory(this.plugin);
        String title = plugin.getSettings().guiInvitesTitle;
        CustomInventoryHolder holder = paginatorFactory.createPaginator(player, title, 6, 1, items);
        holder.set(4, this.itemRepository.getItem("invite-player", opener.isDefaultItems()), new InviteMemberAction(this.plugin, guild));
        ItemStack filler = this.itemRepository.getItem("invites-filler", opener.isDefaultItems());
        for (int i : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 45, 46, 48, 50, 52, 53}) {
            holder.setItem(i, filler);
        }

        return holder.getInventory();
    }

}
