package pl.suseu.bfactions.base.guild.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.guild.Guild;

import java.util.Map;

public class FuelConsumeTask implements Runnable {

    private final BFactions plugin;

    public FuelConsumeTask(BFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Map<Material, Double> conversions = this.plugin.getSettings().fieldEnergyConversions;
        for (final Guild guild : this.plugin.getGuildRepository().getGuilds()) {
            final Location center = guild.getRegion().getCenter();
            for (final ItemStack itemStack : guild.getFuelInventory().getContents()) {
                if (itemStack == null) {
                    continue;
                }

                Double itemEnergy = conversions.get(itemStack.getType());

                if (itemEnergy == null) {
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
