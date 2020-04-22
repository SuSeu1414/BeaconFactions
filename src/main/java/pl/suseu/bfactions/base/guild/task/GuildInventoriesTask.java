package pl.suseu.bfactions.base.guild.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;
import pl.suseu.bfactions.item.ItemRepository;
import pl.suseu.bfactions.util.ItemUtil;
import pl.suseu.bfactions.util.TimeUtil;

import java.util.Map;

public class GuildInventoriesTask implements Runnable {

    private final BFactions plugin;
    private final ItemRepository itemRepository;
    private final Map<Material, Double> conversions;
    private long ticks = 0;

    public GuildInventoriesTask(BFactions plugin) {
        this.plugin = plugin;
        this.itemRepository = plugin.getItemRepository();
        this.conversions = this.plugin.getSettings().fieldEnergyConversions;
    }

    @Override
    public void run() {
        for (Guild guild : this.plugin.getGuildRepository().getGuilds()) {
            handleFuel(guild);
            if (ticks % 20 == 0) {
                handleUndamageableTime(guild);
            }
        }
        ticks++;
    }

    private void handleFuel(Guild guild) {
        final Location center = guild.getRegion().getCenter();
        for (final ItemStack itemStack : guild.getFuelInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            Double itemEnergy = conversions.get(itemStack.getType());

            if (itemEnergy == null) {
                guild.getFuelInventory().remove(itemStack);
                dropItem(center, itemStack);
                continue;
            }

            if (itemStack.getAmount() < 1) {
                guild.getFuelInventory().remove(itemStack);
                continue;
            }

            if (guild.getField().getCurrentEnergy() + itemEnergy > guild.getField().getTier().getMaxEnergy()) {
                dropItem(center, itemStack);
                guild.getFuelInventory().remove(itemStack);
                continue;
            }

            guild.getField().addEnergy(itemEnergy);
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
//            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
//                guild.getFuelInventory().clear();
//            });

    }

    private void handleUndamageableTime(Guild guild) {
        final Location center = guild.getRegion().getCenter();
        Inventory inv = guild.getField().getUndamageableItemInventory();
        ItemStack itemStack = inv.getItem(13);
        if (itemStack == null) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        if (ItemUtil.isBoostItem(itemMeta, "undamageable")) {
            long time = ItemUtil.getBoostUndamageableRemainingTime(itemMeta);
            time--;
            if (time < 1) {
                guild.getField().setUndamageableTime(0);
                inv.setItem(13, null);
                return;
            }
            itemStack = this.itemRepository.getItem(ItemUtil.getBoostItemId(itemMeta, "undamageable"));
            ItemUtil.replace(itemStack, "%time%", TimeUtil.timePhrase(time * 1000L, false));
            itemMeta = itemStack.getItemMeta();
            guild.getField().setUndamageableTime(time);
            ItemUtil.setBoostUndamageableRemainingTime(itemMeta, time);
        } else {
            guild.getField().setUndamageableTime(0);
            dropItem(center, itemStack);
            inv.setItem(13, null);
        }

        itemStack.setItemMeta(itemMeta);
        inv.setItem(13, itemStack);
    }

    private void dropItem(Location center, ItemStack itemStack) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            World world = center.getWorld();
            if (world != null) {
                center.getWorld().dropItemNaturally(center.clone().add(0, 1, 0), itemStack);
            }
        });
    }
}
