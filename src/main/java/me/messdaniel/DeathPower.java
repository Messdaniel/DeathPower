package me.messdaniel;

import me.messdaniel.commands.PowersCommand;
import me.messdaniel.listener.ConnectionEvent;
import me.messdaniel.listener.PlayerDamageEvent;
import me.messdaniel.managers.DeathPowerManger;
import me.messdaniel.powereffect.PowerEffect;
import me.messdaniel.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class DeathPower extends JavaPlugin {

    private static DeathPower instance;

    private DeathPowerManger deathPowerManger;
    @Override
    public void onEnable() {
        instance = this;
        createYml();
        MessagesUtils.load();
        PowerEffect.load();

        deathPowerManger = new DeathPowerManger();

        Bukkit.getPluginManager().registerEvents(new ConnectionEvent(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamageEvent(),this);

        getCommand("powers").setExecutor(new PowersCommand());
    }

    public void createYml() {
        saveDefaultResource("config");
        saveDefaultResource("playerdata");
    }

    void saveDefaultResource(String resource) {
        String path = resource + ".yml";
        File file= new File(getDataFolder(), path);
        if (!file.exists()) {
            this.saveResource(path, false);
            this.getLogger().log(Level.INFO, "Saved default resource " + path);
        }
    }

    public static DeathPower getInstance() {
        return instance;
    }

    public DeathPowerManger getDeathPowerManger() {
        return deathPowerManger;
    }
}
