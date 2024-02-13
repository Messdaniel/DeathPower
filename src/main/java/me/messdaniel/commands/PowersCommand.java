package me.messdaniel.commands;

import com.google.common.eventbus.DeadEvent;
import me.messdaniel.DeathPower;
import me.messdaniel.managers.DeathPowerManger;
import me.messdaniel.playerdata.DeathPowerPlayer;
import me.messdaniel.powereffect.PowerEffect;
import me.messdaniel.utils.MessagesUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PowersCommand implements CommandExecutor, TabCompleter {

    private DeathPowerManger deathPowerManger = DeathPower.getInstance().getDeathPowerManger();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesUtils.getMessage("only-player-command"));
            return true;
        }

        Player player = (Player) sender;
        if (!sender.hasPermission("powers.command")) {
            MessagesUtils.sendMessage(player,"dont-have-perms");
            return true;
        }
        if (args.length == 0) {
            MessagesUtils.sendMessage(player,"usage-power-command");
            return true;
        }
        if (args[0].equalsIgnoreCase("Reload")) {
            DeathPower.getInstance().createYml();
            DeathPower.getInstance().reloadConfig();
            MessagesUtils.load();
            PowerEffect.load();
            DeathPower.getInstance().getDeathPowerManger().load();

            for (Player op : Bukkit.getOnlinePlayers()) {
                deathPowerManger.checkPower(op);
            }

            MessagesUtils.sendMessage(player,"reloaded-successfully");
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "revive": {
                    if (args.length < 2) {
                        MessagesUtils.sendMessage(player,"usage-revive-power-command");
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || !target.hasPlayedBefore()) {
                        MessagesUtils.sendMessage(player,"no-player-online", "name", args[1]);
                        return true;
                    }
                    DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(target);
                    if (!deathPowerPlayer.isEliminated()) {
                        MessagesUtils.sendMessage(player,"player-not-eliminated", "name", target.getName());
                        return true;
                    }
                    Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
                    if (deathPowerPlayer.getPower() == 0)
                        deathPowerPlayer.setPower(deathPowerManger.getStartPowerAmount());
                    deathPowerPlayer.setEliminated(false);
                    deathPowerPlayer.update();
                    MessagesUtils.sendMessage(player,"revive-successfully", "name", target.getName());
                    return true;
                }
                case "eliminate": {
                    if (args.length < 2) {
                        MessagesUtils.sendMessage(player,"usage-eliminate-power-command");
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || !target.hasPlayedBefore()) {
                        MessagesUtils.sendMessage(player,"no-player-online", "name", args[1]);
                        return true;
                    }
                    DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(target);
                    if (deathPowerPlayer.isEliminated()) {
                        MessagesUtils.sendMessage(player,"player-already-eliminated", "name", target.getName());
                        return true;
                    }
                    deathPowerPlayer.setEliminated(true);
                    deathPowerPlayer.update();
                    deathPowerManger.checkPower(target);
                    MessagesUtils.sendMessage(player,"eliminated-successfully", "name", target.getName());
                    return true;
                }
                case "info": {
                    if (args.length < 2) {
                        MessagesUtils.sendMessage(player,"usage-info-power-command");
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || !target.hasPlayedBefore()) {
                        MessagesUtils.sendMessage(player,"no-player-online", "name", args[1]);
                        return true;
                    }
                    DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(target);
                    MessagesUtils.sendMessage(player,"powers-info",
                            "name", target.getName(), "power", deathPowerPlayer.getPower(), "eliminated", deathPowerPlayer.isEliminated());
                    return true;
                }
                case "give": {
                    if (args.length < 3) {
                        MessagesUtils.sendMessage(player,"usage-give-power-command");
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || !target.hasPlayedBefore()) {
                        MessagesUtils.sendMessage(player,"no-player-online", "name", args[1]);
                        return true;
                    }
                    DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(target);
                    int number;
                    String numberString = args[2];
                    if (numberString.matches("\\d+")) {
                        number = Integer.parseInt(numberString);
                    } else {
                        MessagesUtils.sendMessage(player,"invalid-number-format", "maximum-number", deathPowerManger.getMaximumGiveOrTake());
                        return true;
                    }
                    if (number > deathPowerManger.getMaximumGiveOrTake()) {
                        MessagesUtils.sendMessage(player,"invalid-number-format", "maximum-number", deathPowerManger.getMaximumGiveOrTake());
                        return true;
                    }
                    deathPowerPlayer.setPower(deathPowerPlayer.getPower() + number);
                    deathPowerManger.checkPower(target);
                    MessagesUtils.sendMessage(player,"give-power-successfully",
                            "give", number, "name", target.getName(), "power", deathPowerPlayer.getPower());
                    return true;
                }
                case "take": {
                    if (args.length < 3) {
                        MessagesUtils.sendMessage(player,"usage-take-power-command");
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (target == null || !target.hasPlayedBefore()) {
                        MessagesUtils.sendMessage(player,"no-player-online", "name", args[1]);
                        return true;
                    }
                    DeathPowerPlayer deathPowerPlayer = DeathPowerPlayer.get(target);
                    int number;
                    String numberString = args[2];
                    if (numberString.matches("\\d+")) {
                        number = Integer.parseInt(numberString);
                    } else {
                        MessagesUtils.sendMessage(player,"invalid-number-format", "maximum-number", deathPowerManger.getMaximumGiveOrTake());
                        return true;
                    }
                    if (number > deathPowerManger.getMaximumGiveOrTake()) {
                        MessagesUtils.sendMessage(player,"invalid-number-format", "maximum-number", deathPowerManger.getMaximumGiveOrTake());
                        return true;
                    }
                    deathPowerPlayer.setPower(deathPowerPlayer.getPower() - number);
                    deathPowerManger.checkPower(target);
                    MessagesUtils.sendMessage(player,"take-power-successfully",
                            "take", number, "name", target.getName(), "power", deathPowerPlayer.getPower());
                    return true;
                }
                default: {
                    MessagesUtils.sendMessage(player,"usage-power-command");
                    return true;
                }
            }
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("Reload");
            completions.add("Info");
            completions.add("Take");
            completions.add("Give");
            completions.add("revive");
            completions.add("eliminate");
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("Reload")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        }
        completions.removeIf(completion -> !completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        Collections.sort(completions);
        return completions;
    }
}
