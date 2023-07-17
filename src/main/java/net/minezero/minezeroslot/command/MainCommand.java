package net.minezero.minezeroslot.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;

import static net.minezero.minezeroslot.MineZeroSlot.*;
import static net.minezero.minezeroslot.utils.Permission.*;

public class MainCommand implements CommandExecutor {

    public static String slotname;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!isOpPermission((Player) sender)) {

            sender.sendMessage(prefix + " §c権限がありません！");
            return true;
        }

        if (args.length == 0) {

            sender.sendMessage(prefix);
            sender.sendMessage(" §7/slot §ebuild [スロット名] §7➡ §f[スロット名]の名前でスロットを設置します");
            sender.sendMessage(" §7/slot §eremove [スロット名] §7➡ §f[スロット名]のスロットを撤去します");
            sender.sendMessage(" §7/slot §elist §7➡ §fスロットの一覧を表示します");
            sender.sendMessage(" §7/coin §ebuy [枚数] §7➡ §fコインを[枚数]枚購入します");

            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list")) {

                sender.sendMessage(prefix);

                for (String s : filenames) {

                    sender.sendMessage(" §f- §e" + s);

                }
                return true;
            }
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("build")) {

                slotname = args[1];
                Player player = (Player) sender;

                player.setMetadata(metadata, new FixedMetadataValue(plugin, true));
                placewating.put(player, 0);
                player.sendMessage(prefix + " §a額縁を設置してください");
                player.sendMessage(prefix + " §a置いた順番から1 2 3と番号付けられます");

                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {

                File file = new File(plugin.getDataFolder().getPath() + "/slots/" + args[1] + ".yml");

                if (file.exists()) {

                    file.delete();
                    sender.sendMessage(prefix + " §a" + args[1] + "を削除しました");

                    return true;
                } else {
                    sender.sendMessage(prefix + " §cスロットが見つかりません！");
                    return false;
                }
            }
        }

        return false;
    }

}
