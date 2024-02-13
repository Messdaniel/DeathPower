package me.messdaniel.powereffect;

import me.messdaniel.DeathPower;
import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerEffect {

    private static ArrayList<PowerEffect> allPowerEffects;
    public static void load() {
        allPowerEffects = new ArrayList<>();
        FileConfiguration fc = DeathPower.getInstance().getConfig();
        ConfigurationSection cs = fc.getConfigurationSection("powerEffect");
        for (String key : cs.getKeys(false)) {
            if (!key.matches("\\d+")) continue;
            int power = Integer.parseInt(key);
            PotionEffectType effectType = getPotionEffectTypeFromName(cs.getString(key + ".effect"));
            int duration = cs.getInt(key + ".duration");
            int amplifier = cs.getInt(key + ".amplifier");
            boolean ambient = cs.getBoolean(key + ".ambient");
            boolean particle = cs.getBoolean(key + ".particle");
            boolean icon = cs.getBoolean(key + ".icon");
            String effectName = cs.getString(key + ".effect") + " " + amplifier;
            PowerEffect powerEffect = new PowerEffect(power,effectName,effectType,duration,amplifier,ambient,particle,icon);
            allPowerEffects.add(powerEffect);
        }
    }

    public static List<PowerEffect> get(int power) {
        List<PowerEffect> allReachedPowerEffects = new ArrayList<>();
        for (PowerEffect powerEffect : allPowerEffects) {
            if (powerEffect.getPower() > power) continue;
            allReachedPowerEffects.add(powerEffect);
        }
        return allReachedPowerEffects;
    }

    private int power;
    private String effectName;
    private PotionEffectType effect;
    private int duration;
    private int amplifier;
    boolean ambient;
    boolean particle;
    boolean icon;

    public PowerEffect(int power,String effectName,PotionEffectType effect,int duration,int amplifier,boolean ambient,boolean particle,boolean icon) {
        this.power = power;
        this.effectName = effectName;
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particle = particle;
        this.icon = icon;
    }

    public int getPower() {
        return power;
    }

    public String getEffectName() {
        return effectName;
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isParticle() {
        return particle;
    }

    public boolean isIcon() {
        return icon;
    }

    public static PotionEffectType getPotionEffectTypeFromName(String name) {
        switch (name.toLowerCase()) {
            case "swiftness":
            case "speed":
                return PotionEffectType.SPEED;
            case "slow":
            case "slowness":
                return PotionEffectType.SLOW;
            case "fast_digging":
            case "haste":
                return PotionEffectType.FAST_DIGGING;
            case "mining fatigue":
            case "slow_digging":
                return PotionEffectType.SLOW_DIGGING;
            case "strength":
            case "increase_damage":
                return PotionEffectType.INCREASE_DAMAGE;
            case "heal":
            case "instant health":
                return PotionEffectType.HEAL;
            case "harm":
            case "instant damage":
                return PotionEffectType.HARM;
            case "jump":
            case "jump boost":
                return PotionEffectType.JUMP;
            case "confusion":
            case "nausea":
                return PotionEffectType.CONFUSION;
            case "regeneration":
                return PotionEffectType.REGENERATION;
            case "damage_resistance":
            case "resistance":
                return PotionEffectType.DAMAGE_RESISTANCE;
            case "fire_resistance":
            case "fire resistance":
                return PotionEffectType.FIRE_RESISTANCE;
            case "water_breathing":
            case "water breathing":
                return PotionEffectType.WATER_BREATHING;
            case "invisibility":
                return PotionEffectType.INVISIBILITY;
            case "blindness":
                return PotionEffectType.BLINDNESS;
            case "night_vision":
            case "night vision":
                return PotionEffectType.NIGHT_VISION;
            case "hunger":
                return PotionEffectType.HUNGER;
            case "weakness":
                return PotionEffectType.WEAKNESS;
            case "poison":
                return PotionEffectType.POISON;
            case "wither":
                return PotionEffectType.WITHER;
            case "health_boost":
            case "health boost":
                return PotionEffectType.HEALTH_BOOST;
            case "absorption":
                return PotionEffectType.ABSORPTION;
            case "saturation":
                return PotionEffectType.SATURATION;
            case "glowing":
                return PotionEffectType.GLOWING;
            case "levitation":
                return PotionEffectType.LEVITATION;
            case "luck":
                return PotionEffectType.LUCK;
            case "unluck":
            case "bad luck":
                return PotionEffectType.UNLUCK;
            case "slow_falling":
            case "slow falling":
                return PotionEffectType.SLOW_FALLING;
            case "conduit_power":
            case "conduit power":
                return PotionEffectType.CONDUIT_POWER;
            case "dolphins_grace":
            case "dolphin's grace":
                return PotionEffectType.DOLPHINS_GRACE;
            case "bad_omen":
            case "bad omen":
                return PotionEffectType.BAD_OMEN;
            case "hero of the village":
            case "hero_of_the_village":
                return PotionEffectType.HERO_OF_THE_VILLAGE;
            case "darkness":
                return PotionEffectType.DARKNESS;
            default:
                return null;
        }
    }
}
