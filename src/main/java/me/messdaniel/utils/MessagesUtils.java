package me.messdaniel.utils;

import me.messdaniel.DeathPower;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesUtils {
    private static FileConfiguration fc;
    private static Map<String, String> messages;

    public static void load() {
        fc = DeathPower.getInstance().getConfig();
        loadMessages();
    }

    private static void loadMessages() {
        messages = new HashMap<>();
        ConfigurationSection cs = fc.getConfigurationSection("messages");
        for (String key : cs.getKeys(true)) {
            if (key.startsWith(".")) {
                key = key.substring(1);
            }

            Object value = cs.get(key);
            if (value == null || value instanceof MemorySection) continue;
            String message = value instanceof List ? fromList((List<?>) value) : value.toString();

            messages.put(key, message);
        }
    }
    private static String fromList(final List<?> list) {
        StringBuilder builder = new StringBuilder();

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).toString()).append(i + 1 != list.size() ? "\n" : "");
            }
        }

        return builder.toString();
    }
    private static String replace(String message, Object... replacers) {
        if (replacers.length == 1 && replacers[0] instanceof Object[]) {
            replacers = (Object[]) replacers[0];
        }

        for (int i = 0; i < replacers.length; i += 2) {
            if (i + 1 >= replacers.length) {
                break;
            }

            message = message.replace("{" + replacers[i].toString() + "}", String.valueOf(replacers[i + 1]));
        }

        return message;
    }
    public static String getRawMessage(String key) {
        String message = messages.get(key);

        if (message == null) {
            DeathPower.getInstance().getLogger().warning(getErrorMessage(key));
            return getErrorMessage(key);
        }

        return !message.isEmpty() ? message : getErrorMessage(key);
    }
    public static String getMessage(String key) {
        String message = getRawMessage(key);
        return ColorUtils.translate(message);
    }

    public static String getMessage(String key,Object... replaces) {
        String message = getRawMessage(key);

        message = ColorUtils.translate(replace(message, replaces));

        return message;
    }

    public static void sendMessage(Player player,String key,Object... replaces) {
        String message = getMessage(key,replaces);
        if (message.equals(" ")) return;
        player.sendMessage(message);
    }

    public static String getErrorMessage(String key) {
        return "Failed to load message: '" + key + "' found no assigned value";
    }
}
