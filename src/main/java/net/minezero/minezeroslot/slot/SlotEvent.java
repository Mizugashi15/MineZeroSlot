package net.minezero.minezeroslot.slot;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minezero.minezeroslot.utils.Permission.*;
import static net.minezero.minezeroslot.MineZeroSlot.*;
import static org.bukkit.Bukkit.getServer;

public class SlotEvent implements Listener {

    @EventHandler
    void spin(PlayerInteractEvent event) {

//        try {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if (!isUsePermission(event.getPlayer())) {

                    event.getPlayer().sendMessage(prefix + " §c権限がありません！");
                    return;
                }
                if (inputdatamap.containsKey(event.getClickedBlock().getState().getLocation())) {

                    String slotname = inputdatamap.get(event.getClickedBlock().getState().getLocation());
                    Player player = event.getPlayer();

                    if (!slotdatamap.get(slotname).flag) {
                        player.sendMessage(prefix + " §c現在回転中です！");
                        return;
                    }

                    if (!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                        player.sendMessage(prefix + " §cコインを持ってください！");
                        return;
                    }

                    if (player.getInventory().getItemInMainHand().getAmount() <= slotdatamap.get(slotname).coinamount) {
                        player.sendMessage(prefix + " §cコインが" + slotdatamap.get(slotname).coinamount + "§c枚必要です！");
                        return;
                    }

                    slotdatamap.get(slotname).flag = false;
                    removeCoins(player, slotname);
                    updateSign(slotname);
                    raiseSign(slotname);

                    for (String s : slotdatamap.get(slotname).onespinsounds) {
                        String[] sound = s.split("-");
                        player.playSound(player.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                    }
                    List<ItemStack>[] symbols = judge(slotname);

                    int stop1 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop1;
                    int stop2 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop2;
                    int stop3 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop3;

                    List<ItemStack> reel1 = symbols[0];
                    List<ItemStack> reel2 = symbols[1];
                    List<ItemStack> reel3 = symbols[2];

                    ItemFrame frame1 = frames.get(slotname).get(0);
                    ItemFrame frame2 = frames.get(slotname).get(1);
                    ItemFrame frame3 = frames.get(slotname).get(2);

                    int max = Math.max(stop1, Math.max(stop2, stop3));

                    BukkitScheduler scheduler = getServer().getScheduler();

                    long delay = slotdatamap.get(slotname).spindelay;

//                    for (ItemStack i : reel1) {
//
//                        player.sendMessage(i.getType().name());
//
//                    }

                    player.sendMessage(reel3.size() + " " + max);

                    for (int i = 0 ; i <= max ; i++) {

                        if (i > 0) {
                            delay += slotdatamap.get(slotname).spindelay;
                        }
                        int finalI = i;

                        scheduler.scheduleSyncDelayedTask(plugin, () -> {

                            if (slotdatamap.get(slotname).spinsounds != null) {
                                for (String s : slotdatamap.get(slotname).spinsounds) {

                                    String[] sound = s.split("-");

                                    player.playSound(player.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));

                                }
                            }

                            colorSign(slotname);

                            if (stop1 >= finalI) {
                                frame1.setItem(reel1.get(finalI));
                            }
                            if (stop2 >= finalI) {
                                frame2.setItem(reel2.get(finalI));
                            }
                            if (stop3 >= finalI) {
                                frame3.setItem(reel3.get(finalI));
                            }

                            }, delay);
                    }
                    scheduler.scheduleSyncDelayedTask(plugin, () -> spinend(slotname, player), delay);
                }
            }
    }

    private void spinend(String slotname, Player player) {
        if (slotdatamap.get(slotname).winflag) {
            Sign sign = (Sign) signdatamap.get(slotname).getBlock().getState();
            double money = sign.getPersistentDataContainer().get(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE);
            win(slotname, slotdatamap.get(slotname).winkey, money, player);
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(slotname).defaultstock);
        } else {
            lose(slotname, player);
        }
        slotdatamap.get(slotname).flag = true;
    }

    private List<ItemStack>[] judge(String s) {

        Random random = new Random();

        int prob = random.nextInt(slotdatamap.get(s).allProb);

        int plusprob = slotdatamap.get(s).probs.get(0);

        getServer().getLogger().info(String.valueOf(prob));
        getServer().getLogger().info(String.valueOf(plusprob));

        if (plusprob >= prob) {
            slotdatamap.get(s).winflag = false;
            return isLose(s);
        }

        for (String s1 : slotdatamap.get(s).win_name) {

            plusprob += slotdatamap.get(s).win_chance.get(s1);

            if (plusprob >= prob) {
                slotdatamap.get(s).winflag = true;
                return isWin(s, s1);
            }
        }
        return null;
    }

    private List<ItemStack>[] isLose(String s) {

        int reel1 = 0;
        int reel2 = 0;
        int reel3 = 0;
        Random random = new Random();
        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta itemMeta;
        String[] s1;

        for (int i = 0 ; i <= slotdatamap.get(s).stopcount ; i++) {

            reel1 = random.nextInt(slotdatamap.get(s).reel1.size());
            s1 = slotdatamap.get(s).reel1.get(reel1).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);


            reel2 = random.nextInt(slotdatamap.get(s).reel2.size());
            s1 = slotdatamap.get(s).reel2.get(reel2).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);


            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
            } while (reel2 == reel3);
            s1 = slotdatamap.get(s).reel3.get(reel3).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);

        }
        return new List[]{reel1(item1, s), reel2(item2, s), reel3(item3, s, reel2)};
    }

    private List<ItemStack>[] isWin(String slot, String s) {

        int reel1 = 0;
        int reel2 = 0;
        int reel3 = 0;
        Random random = new Random();
        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta itemMeta;
        String[] s1;

        for (int i = 0 ; i <= slotdatamap.get(slot).stopcount ; i++) {

            if (i == 0) {

                s1 = slotdatamap.get(slot).win_symbols.get(s).get(0).split("-");
                item.setType(Material.valueOf(s1[0]));
                itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
                item.setItemMeta(itemMeta);
                item1.add(item);

                Bukkit.getServer().broadcastMessage(item1.get(0).getType().name());

                s1 = slotdatamap.get(slot).win_symbols.get(s).get(1).split("-");
                item.setType(Material.valueOf(s1[0]));
                itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
                item.setItemMeta(itemMeta);
                item2.add(item);

                s1 = slotdatamap.get(slot).win_symbols.get(s).get(2).split("-");
                item.setType(Material.valueOf(s1[0]));
                itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
                item.setItemMeta(itemMeta);
                item3.add(item);

                continue;
            }

            reel1 = random.nextInt(slotdatamap.get(slot).reel1.size());
            s1 = slotdatamap.get(slot).reel1.get(reel1).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);

            reel2 = random.nextInt(slotdatamap.get(slot).reel2.size());
            s1 = slotdatamap.get(slot).reel2.get(reel2).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);
            do {
                reel3 = random.nextInt(slotdatamap.get(slot).reel3.size());
            } while (reel2 == reel3);
            s1 = slotdatamap.get(slot).reel3.get(reel3).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);

//            Bukkit.getServer().broadcastMessage(item1.get(i).getType().name());


        }
        slotdatamap.get(slot).winkey = s;
        return new List[]{reel1(item1, slot), reel2(item2, slot), reel3(item3, slot, reel2)};
    }

    private List<ItemStack> reel1(List<ItemStack> items , String s) {

        Random random = new Random();
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta itemMeta;
        String[] s1;
        List<ItemStack> reelitems = items;

        for (ItemStack itemStack : items) {

            Bukkit.getServer().broadcastMessage(itemStack.getType().name());

        }

        for (int i = 0 ; i <= slotdatamap.get(s).reelstop1 ; i++) {
            s1 = slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())).split("-");
            item.setType(Material.valueOf(s1[0]));
//            Bukkit.getServer().broadcastMessage(s1[0]);
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            items.add(item);
        }

        Bukkit.getServer().broadcastMessage(String.valueOf(slotdatamap.get(s).reelstop1));

//        for (ItemStack itemStack : items) {
//
//            Bukkit.getServer().broadcastMessage(itemStack.getType().name());
//
//        }

        return items;
    }

    private List<ItemStack> reel2(List<ItemStack> items , String s) {

        Random random = new Random();
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta itemMeta;
        String[] s1;

        for (int i = 0 ; i <= slotdatamap.get(s).reelstop2 ; i++) {
            s1 = slotdatamap.get(s).reel2.get(random.nextInt(slotdatamap.get(s).reel2.size())).split("-");
            item.setType(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            items.add(item);
        }
        return items;
    }

    private List<ItemStack> reel3(List<ItemStack> items , String s , int n) {

        Random random = new Random();
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta itemMeta;
        String[] s1;
        int reel3 = 0;

        for (int i = 0 ; i <= slotdatamap.get(s).reelstop3 ; i++) {
            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
                s1 = slotdatamap.get(s).reel3.get(random.nextInt(slotdatamap.get(s).reel3.size())).split("-");
                item.setType(Material.valueOf(s1[0]));
                itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
                item.setItemMeta(itemMeta);
                items.add(item);
            } while (n == reel3);
        }
        return items;
    }

    private void lose(String s, Player player) {

        player.sendMessage(prefix + " §c何も獲得できませんでした...");
        for (String s1 : slotdatamap.get(s).losesounds) {
            String[] sound = s1.split("-");
            player.playSound(player.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
        }
    }

    private void win(String slot, String s, double money, Player player) {

        Bukkit.getServer().broadcastMessage(slotdatamap.get(slot).win_message.get(s).replace("%player%", player.getName()).replace("%money%", String.valueOf(money)));

        for (String s1 : slotdatamap.get(slot).win_sounds.get(s)) {
            String[] sound = s1.split("-");
            player.playSound(player.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
        }

        if (slotdatamap.get(slot).win_pot.get(s)) {
            for (String s1 : slotdatamap.get(slot).win_actions.get(s)) {

                if (s1.contains("MULTI")) {
                    money = money * Double.parseDouble(s1.substring(s1.lastIndexOf(":") + 1));
                }
                if (s1.contains("RAISE")) {
                    money = money + Double.parseDouble(s1.substring(s1.lastIndexOf(":") + 1));
                }
            }
            vault.deposit(player, money);
        }

        for (String s1 : slotdatamap.get(slot).win_commands.get(s)) {

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s1.replace("%player%", player.getName()).replace("%money%", String.valueOf(money)));

        }

    }

    private void removeCoins(Player player, String s) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        int remaining = slotdatamap.get(s).coinamount;
        ItemStack[] invContents = player.getInventory().getContents();
        for (int i = 0; i < invContents.length; i++) {
            ItemStack item = invContents[i];
            if (item != null && item.getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                int amount = item.getAmount();
                if (amount > remaining) {
                    item.setAmount(amount - remaining);
                    break;
                } else if (amount == remaining) {
                    item.setType(Material.AIR);
                    break;
                }

                item.setType(Material.AIR);
                remaining -= amount;
                if (remaining == 0) {
                    break;
                }

                invContents[i] = item;
            }
        }
        player.getInventory().setContents(invContents);
    }

    private void raiseSign(String s) {

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        double moneypot = sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE);

        moneypot += slotdatamap.get(s).raise;

        slotdatamap.get(s).stock += slotdatamap.get(s).raise;

        sign.getSide(Side.FRONT).setLine(1, "§6" + moneypot);

        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, s + "-moneypot"), PersistentDataType.DOUBLE, moneypot);

        sign.update(true);
    }

    private void updateSign(String s) {

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        if (sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE) == null) {

            sign.getPersistentDataContainer().set(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(s).stock);

            sign.update(true);
            return;
        }

        if (!sign.isWaxed()) {
            sign.setWaxed(true);
            sign.update(true);
        }
        return;
    }

    private void colorSign(String s) {

        String line1 = "§f==========§1==§f==========";
        String line2 = "§f=========§1====§f=========";
        String line3 = "§f========§1======§f========";
        String line4 = "§f=======§1========§f=======";
        String line5 = "§f======§1==========§f======";
        String line6 = "§f=====§1============§f=====";
        String line7 = "§f====§1==============§f====";
        String line8 = "§f===§1================§f===";
        String line9 = "§f==§1==================§f==";
        String line10 = "§f=§1====================§f=";

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        if (sign.getSide(Side.FRONT).getLine(0).equals(line1) || sign.getSide(Side.FRONT).getLine(0).isEmpty()) {
            sign.getSide(Side.FRONT).setLine(0, line2);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line2)) {
            sign.getSide(Side.FRONT).setLine(0, line3);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line3)) {
            sign.getSide(Side.FRONT).setLine(0, line4);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line4)) {
            sign.getSide(Side.FRONT).setLine(0, line5);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line5)) {
            sign.getSide(Side.FRONT).setLine(0, line6);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line6)) {
            sign.getSide(Side.FRONT).setLine(0, line7);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line7)) {
            sign.getSide(Side.FRONT).setLine(0, line8);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line8)) {
            sign.getSide(Side.FRONT).setLine(0, line9);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line9)) {
            sign.getSide(Side.FRONT).setLine(0, line10);
        }
        if (sign.getSide(Side.FRONT).getLine(0).equals(line10)) {
            sign.getSide(Side.FRONT).setLine(0, line1);
        }

        sign.update(true);
    }
}
