package pl.suseu.bfactions.gui.main.factory.permission;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;

public class ManageMemberPermissionGuiFactory {

    private final BFactions plugin;
    private final ItemRepository itemRepository;
    private final boolean defaultItems;

    private final ItemStack buttonON;
    private final ItemStack buttonOFF;
    private final ItemStack buttonEnable;
    private final ItemStack buttonDisable;
    private final ItemStack buttonBypasses;


    public ManageMemberPermissionGuiFactory(BFactions plugin, boolean defaultItems) {
        this.plugin = plugin;
        this.itemRepository = plugin.getItemRepository();
        this.defaultItems = defaultItems;

        this.buttonON = this.itemRepository.getItem("button-on", defaultItems);
        this.buttonOFF = this.itemRepository.getItem("button-off", defaultItems);
        this.buttonEnable = this.itemRepository.getItem("button-enable", defaultItems);
        this.buttonDisable = this.itemRepository.getItem("button-disable", defaultItems);
        this.buttonBypasses = this.itemRepository.getItem("button-bypasses", defaultItems);
    }

    public Inventory createGui(Guild guild, User user) {
        CustomInventoryHolder holder = new CustomInventoryHolder(user.getName(), 6 * 9);

        ItemStack memberInfoItem = this.itemRepository.getItem("member-info", defaultItems);
        holder.setItem(4, memberInfoItem);
        ItemUtil.replace(memberInfoItem, "%player%", user.getName());
        ItemMeta itemMeta = memberInfoItem.getItemMeta();

        if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = ((SkullMeta) itemMeta);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(user.getUuid()));
            memberInfoItem.setItemMeta(skullMeta);
        }

        ItemStack managePermissionInfo = this.itemRepository.getItem("permission-manage-info", defaultItems);
        holder.setItem(12, managePermissionInfo);

        ItemStack openChestsPermissionInfo = this.itemRepository.getItem("permission-chests-info", defaultItems);
        holder.setItem(12 + 9, openChestsPermissionInfo);

        ItemStack openDoorPermissionInfo = this.itemRepository.getItem("permission-door-info", defaultItems);
        holder.setItem(12 + 18, openDoorPermissionInfo);

        ItemStack minePermissionInfo = this.itemRepository.getItem("permission-mine-info", defaultItems);
        holder.setItem(12 + 27, minePermissionInfo);

        ItemStack killPermissionInfo = this.itemRepository.getItem("permission-kill-info", defaultItems);
        holder.setItem(12 + 36, killPermissionInfo);

        displayPermissionButtons(holder, GuildPermission.MANAGE, 13, guild, user);
        displayPermissionButtons(holder, GuildPermission.OPEN_CHESTS, 13 + 9, guild, user);
        displayPermissionButtons(holder, GuildPermission.OPEN_DOORS, 13 + 18, guild, user);
        displayPermissionButtons(holder, GuildPermission.MODIFY_TERRAIN, 13 + 27, guild, user);
        displayPermissionButtons(holder, GuildPermission.KILL_ANIMALS, 13 + 36, guild, user);

        return holder.getInventory();
    }

    private void displayPermissionButtons(CustomInventoryHolder holder, GuildPermission permission, int slot, Guild guild, User user) {
        if (guild.bypassesPermission(user, permission)) {
            displayBypassButtons(holder, permission, slot, guild, user);
            return;
        }

        if (guild.hasPermission(user, permission, false)) {
            displayOnButtons(holder, permission, slot, guild, user);
        } else {
            displayOffButtons(holder, permission, slot, guild, user);
        }
    }

    public void displayBypassButtons(CustomInventoryHolder holder, GuildPermission permission, int slot, Guild guild, User user) {
        holder.setItem(slot, buttonBypasses);
        holder.setAction(slot, null);
        holder.setItem(slot + 1, null);
        holder.setAction(slot + 1, null);
    }

    public void displayOnButtons(CustomInventoryHolder holder, GuildPermission permission, int slot, Guild guild, User user) {
        holder.setItem(slot, buttonON);
        holder.setItem(slot + 1, buttonDisable);
        holder.setAction(slot, null);
        holder.setAction(slot + 1, whoClicked -> {
            //todo check permission
            guild.removeMemberPermission(user, permission);
            displayOffButtons(holder, permission, slot, guild, user);
//            this.guildRepository.addModifiedGuild(this.guild);
        });
    }

    public void displayOffButtons(CustomInventoryHolder holder, GuildPermission permission, int slot, Guild guild, User user) {
        holder.setItem(slot, buttonEnable);
        holder.setItem(slot + 1, buttonOFF);
        holder.setAction(slot, whoClicked -> {
            //todo check permission
            guild.addMemberPermission(user, permission);
            displayOnButtons(holder, permission, slot, guild, user);
//            this.guildRepository.addModifiedGuild(this.guild);
        });
        holder.setAction(slot + 1, null);

    }

}
