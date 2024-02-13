package me.messdaniel.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    public static String translate(String message) {
        if (message == null) return message;
        final Pattern colorPattern = Pattern.compile("(#[A-Fa-f0-9]{6})|(&[0-9a-fk-or])");
        Matcher matcher = colorPattern.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        // Track the current active color
        String activeColor = null;
        while (matcher.find()) {
            String group = matcher.group();

            if (group.startsWith("#")) {
                String hexCode = group.substring(1);

                // Convert the hex color code to Minecraft formatting codes
                StringBuilder colorCode = new StringBuilder("§x");
                for (int i = 0; i < 6; i += 2) {
                    colorCode.append("§").append(hexCode.charAt(i)).append("§").append(hexCode.charAt(i + 1));
                }

                if (!colorCode.toString().equals(activeColor)) {
                    matcher.appendReplacement(buffer, colorCode.toString());
                    activeColor = colorCode.toString();
                }
            } else if (group.startsWith("&") || group.startsWith("§")) {
                // Use Minecraft color code as is
                String colorCode = group.substring(0, 2).replace('&', '§');
                if (!colorCode.equals(activeColor)) {
                    matcher.appendReplacement(buffer, colorCode);
                    activeColor = colorCode;
                }
            }
        }

        message = matcher.appendTail(buffer).toString();
        //if (message.contains("#") || message.contains("&") || message.contains("§")) message = translate(message);
        return message;
    }
    public static String removeColor(String message) {
        char[] chars = message.toCharArray();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '§' || ch == '&') {
                if (i + 1 < chars.length && isColorCodeCharacter(chars[i + 1])) {
                    i++;
                    continue;
                }
            } else if (ch == '#') {
                if (i + 6 < chars.length && isHexColorCode(chars, i + 1)) {
                    i += 6;
                    continue;
                }
            }
            result.append(ch);
        }

        return result.toString();
    }
    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }
    private static boolean isColorCodeCharacter(char ch) {
        return "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(ch) >= 0;
    }
    private static boolean isHexColorCode(char[] chars, int startIndex) {
        for (int i = 0; i < 6; i++) {
            char ch = chars[startIndex + i];
            if ("0123456789AaBbCcDdEeFf".indexOf(ch) == -1) {
                return false;
            }
        }
        return true;
    }
}