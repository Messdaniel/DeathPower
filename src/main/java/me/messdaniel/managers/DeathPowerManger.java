package me.messdaniel.managers;

import com.sun.tools.jconsole.JConsoleContext;
import me.messdaniel.DeathPower;
import me.messdaniel.playerdata.DeathPowerPlayer;
import me.messdaniel.powereffect.PowerEffect;
import me.messdaniel.utils.ColorUtils;
import me.messdaniel.utils.MessagesUtils;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DeathPowerManger {

    private FileConfiguration fc;
    private int startPowerAmount = 5;
    private int powerPerKill = 1;
    private int powerPerDeath = 1;
    private int maximumGiveOrTake = 100;
    private boolean saveOnUpdateAmount = true;
    private String actionbar = "{power}";
    private int actionbarRefreshRate = 30;
    private String banMessage = "You ran out of power and got banned because of it";
    private int banDuration = 10;

    public DeathPowerManger() {
        load();
    }

    public void load() {
        fc = DeathPower.getInstance().getConfig();
        startPowerAmount = fc.getInt("start-power-amount");
        powerPerKill = fc.getInt("power-per-kill");
        powerPerDeath = fc.getInt("power-per-death");
        maximumGiveOrTake = fc.getInt("maximum-give-or-take-power");
        saveOnUpdateAmount = fc.getBoolean("save-everytime-new-power");
        actionbar = fc.getString("actionbar.message");
        actionbarRefreshRate = fc.getInt("actionbar.refresh-rate");
        banMessage = fc.getString("ban.message");
        banDuration = fc.getInt("ban.duration") == -1 ? Integer.MAX_VALUE : fc.getInt("ban.duration");
    }

    public void giveActionbar(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(player);
                String message = ColorUtils.translate(replacePlaceholdersFromActionBar("power",deathPowerPlayer.getPower()));

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ColorUtils.translate(message)));
            }
        }.runTaskTimer(DeathPower.getInstance(),actionbarRefreshRate,actionbarRefreshRate);
    }

    public void checkPower(OfflinePlayer offlinePlayer) {
        DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(offlinePlayer);
        int power = deathPowerPlayer.getPower();
        if (power == 0 || deathPowerPlayer.isEliminated()) {
            banPlayer(offlinePlayer);
            return;
        }
        Player player = offlinePlayer.getPlayer();
        if (player == null) return;
        List<Integer> activePowerEffects = deathPowerPlayer.getActivePowerEffects();
        Iterator<Integer> iterator = activePowerEffects.iterator();
        while (iterator.hasNext()) {
            PowerEffect powerEffect = PowerEffect.get(iterator.next()).get(0);
            if (powerEffect.getPower() <= power) continue;
            iterator.remove();
            player.removePotionEffect(powerEffect.getEffect());
        }
        List<PowerEffect> powerEffects = PowerEffect.get(power);
        if (powerEffects.isEmpty()) return;
        for (PowerEffect powerEffect : powerEffects) {
            player.addPotionEffect(new PotionEffect(powerEffect.getEffect(), powerEffect.getDuration(), powerEffect.getAmplifier() - 1,
                    powerEffect.isAmbient(),powerEffect.isParticle(),powerEffect.isIcon()));
            if (activePowerEffects.contains(powerEffect.getPower())) continue;
            MessagesUtils.sendMessage(player,"get-power-effect","effect",powerEffect.getEffectName(),"power",powerEffect.getPower());
        }
        deathPowerPlayer.setActivePowerEffects(powerEffects);
    }

    public void banPlayer(OfflinePlayer offlinePlayer) {
        DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(offlinePlayer);
        Date expirationDate = Date.from(Instant.now().plus(Duration.ofHours(getBanDuration())));
        Bukkit.getBanList(BanList.Type.NAME).addBan(offlinePlayer.getName(), getBanMessage(), expirationDate, "Console");
        deathPowerPlayer.setEliminated(true);
        if (offlinePlayer.isOnline()) offlinePlayer.getPlayer().kickPlayer(getBanMessage());
    }

    private void handleActivePowerEffects(DeathPowerPlayer deathPowerPlayer, Player player, int power) {
        List<Integer> activePowerEffects = deathPowerPlayer.getActivePowerEffects();
        Iterator<Integer> iterator = activePowerEffects.iterator();

        while (iterator.hasNext()) {
            PowerEffect powerEffect = PowerEffect.get(iterator.next()).get(0);
            if (powerEffect.getPower() <= power) continue;
            iterator.remove();
            player.removePotionEffect(powerEffect.getEffect());
        }

        List<PowerEffect> powerEffects = PowerEffect.get(power);
        if (powerEffects.isEmpty()) return;
        for (PowerEffect powerEffect : powerEffects) {
            player.addPotionEffect(new PotionEffect(powerEffect.getEffect(), powerEffect.getDuration(), powerEffect.getAmplifier() - 1,
                    powerEffect.isAmbient(),powerEffect.isParticle(),powerEffect.isIcon()));
            if (activePowerEffects.contains(powerEffect.getPower())) continue;
            MessagesUtils.sendMessage(player,"get-power-effect","effect",powerEffect.getEffectName(),"power",powerEffect.getPower());
        }
        deathPowerPlayer.setActivePowerEffects(powerEffects);
    }

    public String replacePlaceholdersFromActionBar(Object... replacers) {
        if (replacers.length == 1 && replacers[0] instanceof Object[]) {
            replacers = (Object[]) replacers[0];
        }

        String message = actionbar;

        for (int i = 0; i < replacers.length; i += 2) {
            if (i + 1 >= replacers.length) {
                break;
            }

            message = message.replace("{" + replacers[i].toString() + "}", String.valueOf(replacers[i + 1]));
        }

        return message;
    }

    public int getStartPowerAmount() {
        return startPowerAmount;
    }

    public int getMaximumGiveOrTake() {
        return maximumGiveOrTake;
    }

    public boolean isSaveOnUpdateAmount() {
        return saveOnUpdateAmount;
    }

    public String getActionbar() {
        return actionbar;
    }

    public int getActionbarRefreshRate() {
        return actionbarRefreshRate;
    }

    public String getBanMessage() {
        return banMessage;
    }

    public int getBanDuration() {
        return banDuration;
    }

    public int getPowerPerKill() {
        return powerPerKill;
    }

    public int getPowerPerDeath() {
        return powerPerDeath;
    }
}
