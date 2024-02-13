package me.messdaniel.listener;

import me.messdaniel.DeathPower;
import me.messdaniel.managers.DeathPowerManger;
import me.messdaniel.playerdata.DeathPowerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionEvent implements Listener {

    private DeathPowerManger deathPowerManger = DeathPower.getInstance().getDeathPowerManger();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        DeathPowerPlayer.get(player); //Just to load the player data
        deathPowerManger.giveActionbar(player);
        deathPowerManger.checkPower(player);
    }
}
