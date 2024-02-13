package me.messdaniel.playerdata;

import me.messdaniel.DeathPower;
import me.messdaniel.managers.DeathPowerManger;
import me.messdaniel.powereffect.PowerEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeathPowerPlayer {

    private static HashMap<UUID,DeathPowerPlayer> allDeathPowerPlayer = new HashMap<>();
    private DeathPowerManger deathPowerManger = DeathPower.getInstance().getDeathPowerManger();

    private File file = new File(DeathPower.getInstance().getDataFolder(), "playerdata.yml");
    private YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

    private OfflinePlayer player;
    private UUID uuid;
    private int power = deathPowerManger.getStartPowerAmount();
    private boolean eliminated = false;
    private List<Integer> activePowerEffects = new ArrayList<>();

    public DeathPowerPlayer(OfflinePlayer player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        load();
    }

    private void load() {
        if (fc.get(uuid.toString()) == null) {
            fc.createSection(uuid.toString());
            fc.set(uuid.toString() + ".power",deathPowerManger.getStartPowerAmount());
            fc.set(uuid.toString() + ".eliminated",false);
            try {
                fc.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            power = fc.getInt(uuid.toString() + ".power");
            eliminated = fc.getBoolean(uuid.toString() + ".eliminated");
        }
    }

    public static DeathPowerPlayer get(OfflinePlayer player) {
        if (allDeathPowerPlayer.containsKey(player.getUniqueId())) return allDeathPowerPlayer.get(player.getUniqueId());
        DeathPowerPlayer deathPowerPlayer = new DeathPowerPlayer(player);
        allDeathPowerPlayer.put(player.getUniqueId(),deathPowerPlayer);
        return deathPowerPlayer;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        if (power < 0) power = 0;
        this.power = power;
        if (deathPowerManger.isSaveOnUpdateAmount()) {
            update();
        }
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
        if (deathPowerManger.isSaveOnUpdateAmount()) {
            update();
        }
    }

    public List<Integer> getActivePowerEffects() {
        return activePowerEffects;
    }

    public void setActivePowerEffects(List<PowerEffect> activePowerEffects) {
        this.activePowerEffects = new ArrayList<>();
        for (PowerEffect powerEffect : activePowerEffects) {
            this.activePowerEffects.add(powerEffect.getPower());
        }
    }

    public void update() {
        fc.set(uuid.toString() + ".power",power);
        fc.set(uuid.toString() + ".eliminated",eliminated);
        try {
            fc.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
