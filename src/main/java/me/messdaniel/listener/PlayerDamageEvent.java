package me.messdaniel.listener;

import me.messdaniel.DeathPower;
import me.messdaniel.managers.DeathPowerManger;
import me.messdaniel.playerdata.DeathPowerPlayer;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDamageEvent implements Listener {

    private DeathPowerManger deathPowerManger = DeathPower.getInstance().getDeathPowerManger();


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (player.getHealth() > event.getFinalDamage()) return;
        if (player.getInventory().getItemInMainHand().equals(new ItemStack(Material.TOTEM_OF_UNDYING))) return;
        if (player.getInventory().getItemInOffHand().equals(new ItemStack(Material.TOTEM_OF_UNDYING))) return;
        DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(damager);
        int previousPower = deathPowerPlayer.getPower();
        deathPowerPlayer.setPower(previousPower + deathPowerManger.getPowerPerKill());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(player);
        int previousPower = deathPowerPlayer.getPower();
        deathPowerPlayer.setPower(previousPower - deathPowerManger.getPowerPerDeath());

        deathPowerManger.checkPower(player);
    }
}
