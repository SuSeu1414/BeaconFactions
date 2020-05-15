package pl.suseu.bfactions.gui.main.factory;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.base.tier.Tier;
import pl.suseu.bfactions.base.user.User;
import pl.suseu.bfactions.base.user.UserRepository;
import pl.suseu.bfactions.gui.base.ClickAction;
import pl.suseu.bfactions.gui.base.CustomInventoryHolder;
import pl.suseu.bfactions.gui.main.action.ChangeGuildNameAction;
import pl.suseu.bfactions.gui.main.action.LeaveGuildAction;
import pl.suseu.bfactions.gui.main.action.TogglePvpAction;
import pl.suseu.bfactions.gui.main.action.invite.OpenGuildInvitesGuiAction;
import pl.suseu.bfactions.gui.main.action.permission.OpenManageGuildPermissionsGuiAction;
import pl.suseu.bfactions.gui.main.action.upgrade.OpenFieldUpgradeGuiAction;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.settings.Settings;
import pl.suseu.bfactions.util.ItemUtil;

public class MainGuiFactory {

    private final BFactions plugin;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Settings settings;

    public MainGuiFactory(BFactions plugin) {
        this.plugin = plugin;
        this.itemRepository = this.plugin.getItemRepository();
        this.userRepository = this.plugin.getUserRepository();
        this.settings = this.plugin.getSettings();
    }

    public Inventory createGui(Player player, Guild guild) {
        String title = this.settings.guiMainTitle;
        title = StringUtils.replace(title, "%guild%", guild.getName());
        int size = 6 * 9;

        User user = this.userRepository.getUser(player.getUniqueId());

        CustomInventoryHolder holder = new CustomInventoryHolder(title, size);

        ItemStack manageInvitesItem = this.itemRepository.getItem("manage-invites", user.isDefaultItems());
        holder.set(41, manageInvitesItem, new OpenGuildInvitesGuiAction(this.plugin, guild));

        ItemStack managePermissionsItem = this.itemRepository.getItem("manage-permissions", user.isDefaultItems());
        holder.set(39, managePermissionsItem, new OpenManageGuildPermissionsGuiAction(this.plugin, guild, null));

        ItemStack changeNameItem = this.itemRepository.getItem("change-name", user.isDefaultItems());
        holder.set(40, changeNameItem, new ChangeGuildNameAction(plugin, guild));

        ItemStack book1 = this.itemRepository.getItem("info-book", user.isDefaultItems());
        ItemUtil.replace(book1, "%guild_name%", guild.getName());
        ItemUtil.replace(book1, "%online%",
                "" + guild.getMembersAndOwner().stream().filter(u -> Bukkit.getPlayer(u.getUuid()) != null).count());
        ItemUtil.replace(book1, "%member-count%", guild.getMembersAndOwner().size() + "");
        ItemUtil.replace(book1, "%guild-name%", guild.getName());
        ItemUtil.replace(book1, "%owner%", guild.getOwner().getName());
        ItemUtil.replace(book1, "%region-size%", guild.getRegion().getSize() + "");
        ItemUtil.replace(book1, "%region-shape%", guild.getRegion().getTier().getRegionType().toString());
        ItemUtil.replace(book1, "%max-energy%", String.format("%.0f", guild.getField().getTier().getMaxEnergy()));
        holder.setItem(4, book1);

        ItemStack addFuelItem = this.itemRepository.getItem("add-fuel", user.isDefaultItems());
        ClickAction openAddFuelGuiAction = whoClicked -> whoClicked.openInventory(guild.getFuelInventory());
        holder.set(42, addFuelItem, openAddFuelGuiAction);

        ItemStack openFieldUpgradesItem = this.itemRepository.getItem("field-upgrades", user.isDefaultItems());
        ClickAction openFieldUpgradesAction = new OpenFieldUpgradeGuiAction(this.plugin, guild, Tier.TierType.FIELD);
        holder.set(21, openFieldUpgradesItem, openFieldUpgradesAction);

        ItemStack regionUpgradesItem = this.itemRepository.getItem("region-upgrades", user.isDefaultItems());
        ClickAction regionUpgradesAction = new OpenFieldUpgradeGuiAction(this.plugin, guild, Tier.TierType.REGION);
        holder.set(22, regionUpgradesItem, regionUpgradesAction);

        ItemStack openUndamageableItem = this.itemRepository.getItem("field-undamageable-inventory", user.isDefaultItems());
        ClickAction openUndamageableAction = whoClicked -> whoClicked.openInventory(guild.getField().getUndamageableItemInventory());
        holder.set(23, openUndamageableItem, openUndamageableAction);

        if (guild.isMember(user) && !guild.isOwner(user)) {
            ItemStack leaveItem = this.itemRepository.getItem("quit-guild", user.isDefaultItems());
            ClickAction leaveAction = new LeaveGuildAction(this.plugin, user, guild);
            holder.setItem(53, leaveItem);
            holder.setActionWithConfirmation(53, leaveAction);
        }

        ItemStack pvpItem;
        if (guild.isPvpEnabled()) {
            pvpItem = this.itemRepository.getItem("enable-pvp", user.isDefaultItems());
        } else {
            pvpItem = this.itemRepository.getItem("disable-pvp", user.isDefaultItems());
        }
        holder.set(38, pvpItem, new TogglePvpAction(this.plugin, guild, user));

        ItemStack fillerItem = this.itemRepository.getItem("main-filler", user.isDefaultItems());
        for (int slot = 0; slot < 6 * 9; slot++) {
            if (!holder.isSet(slot)) {
                holder.setItem(slot, fillerItem);
            }
        }

        return holder.getInventory();
    }

}
