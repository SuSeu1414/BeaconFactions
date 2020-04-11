package pl.suseu.bfactions.gui.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.rynbou.langapi3.LangAPI;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.guild.GuildRepository;
import pl.suseu.bfactions.base.guild.permission.GuildPermission;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.CustomInventoryHolder;
import pl.suseu.bfactions.item.ItemRepository;

public class OpenManageMemberPermissionsGuiAction implements ClickAction {

    private final BFactions plugin;
    private final Guild guild;
    private final User user;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final ItemRepository itemRepository;
    private final LangAPI lang;

    private final ItemStack buttonON;
    private final ItemStack buttonOFF;
    private final ItemStack buttonEnable;
    private final ItemStack buttonDisable;

    public OpenManageMemberPermissionsGuiAction(BFactions plugin, Guild guild, User user) {
        this.plugin = plugin;
        this.userRepository = plugin.getUserRepository();
        this.guildRepository = plugin.getGuildRepository();
        this.guild = guild;
        this.user = user;
        this.lang = plugin.getLang();
        this.itemRepository = plugin.getItemRepository();

        this.buttonON = this.itemRepository.getItem("button-on");
        this.buttonOFF = this.itemRepository.getItem("button-off");
        this.buttonEnable = this.itemRepository.getItem("button-enable");
        this.buttonDisable = this.itemRepository.getItem("button-disable");
    }

    @Override
    public void execute(Player whoClickedPlayer) {
        User whoClickedUser = this.userRepository.getUser(whoClickedPlayer.getUniqueId());
        if (!this.guild.hasPermission(whoClickedUser, GuildPermission.MANAGE)) {
            whoClickedPlayer.playSound(whoClickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
            this.lang.sendMessage("no-guild-permissions", whoClickedPlayer);
            return;
        }

        CustomInventoryHolder holder = new CustomInventoryHolder(this.user.getName(), 6 * 9);

        ItemStack memberInfoItem = this.itemRepository.getItem("member-info");
        holder.setItem(4, memberInfoItem);

        ItemStack managePermissionInfo = this.itemRepository.getItem("permission-manage-info");
        holder.setItem(12, managePermissionInfo);

        ItemStack openChestsPermissionInfo = this.itemRepository.getItem("permission-chests-info");
        holder.setItem(12 + 9, openChestsPermissionInfo);

        ItemStack openDoorPermissionInfo = this.itemRepository.getItem("permission-door-info");
        holder.setItem(12 + 18, openDoorPermissionInfo);

        ItemStack minePermissionInfo = this.itemRepository.getItem("permission-mine-info");
        holder.setItem(12 + 27, minePermissionInfo);

        ItemStack killPermissionInfo = this.itemRepository.getItem("permission-kill-info");
        holder.setItem(12 + 36, killPermissionInfo);

        displayPermissionButtons(holder, GuildPermission.MANAGE, 13);
        displayPermissionButtons(holder, GuildPermission.OPEN_CHESTS, 13 + 9);
        displayPermissionButtons(holder, GuildPermission.OPEN_DOORS, 13 + 18);
        displayPermissionButtons(holder, GuildPermission.MODIFY_TERRAIN, 13 + 27);
        displayPermissionButtons(holder, GuildPermission.KILL_ANIMALS, 13 + 36);

        Inventory inventory = holder.getInventory();
        whoClickedPlayer.openInventory(inventory);
    }

    private void displayPermissionButtons(CustomInventoryHolder holder, GuildPermission permission, int slot) {
        if (this.guild.hasPermission(this.user, permission)) {
            displayOnButtons(holder, permission, slot);
        } else {
            displayOffButtons(holder, permission, slot);
        }
    }

    public void displayOnButtons(CustomInventoryHolder holder, GuildPermission permission, int slot) {
        holder.setItem(slot, buttonON);
        holder.setItem(slot + 1, buttonDisable);
        holder.setAction(slot, null);
        holder.setAction(slot + 1, whoClicked -> {
            this.guild.removeMemberPermission(this.user, permission);
            displayOffButtons(holder, permission, slot);
//            this.guildRepository.addModifiedGuild(this.guild);
        });
    }

    public void displayOffButtons(CustomInventoryHolder holder, GuildPermission permission, int slot) {
        holder.setItem(slot, buttonEnable);
        holder.setItem(slot + 1, buttonOFF);
        holder.setAction(slot, whoClicked -> {
            this.guild.addMemberPermission(this.user, permission);
            displayOnButtons(holder, permission, slot);
//            this.guildRepository.addModifiedGuild(this.guild);
        });
        holder.setAction(slot + 1, null);

    }
}
