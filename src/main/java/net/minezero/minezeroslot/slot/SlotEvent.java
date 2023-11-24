package net.minezero.minezeroslot.slot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minezero.minezeroslot.utils.Permission.*;
import static net.minezero.minezeroslot.MineZeroSlot.*;
import static org.bukkit.Bukkit.getServer;

public class SlotEvent implements Listener {

    String line1 = "§e§m=======|§r §6§lMoney §e§m|=======";
    String line2 = "§3§m=======|§r §6§lMoney §3§m|=======";
    String line3 = "§f§m=======|§r §6§lMoney §f§m|=======";
    String line4 = "§d§m=======|§r §6§lMoney §d§m|=======";
    String line5 = "§c§m=======|§r §6§lMoney §c§m|=======";
    String line6 = "§f§m=======|§r §6§lCount §f§m|=======";
    String line7 = "§3§m=======|§r §6§lCount §3§m|=======";
    String line8 = "§c§m=======|§r §6§lCount §c§m|=======";
    String line9 = "§e§m=======|§r §6§lCount §e§m|=======";
    String line10 = "§d§m=======|§r §6§lCount §d§m|=======";

    @EventHandler
    void spin(PlayerInteractEvent event) throws IOException {

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
                        return;
                    }

                    if (!player.getInventory().getItemInMainHand().hasItemMeta()) {
                        return;
                    }

                    if (!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                        player.sendMessage(prefix + " §cコインを持ってください！");
                        return;
                    }

                    if (!hasEnoughCoins(player, slotname)) {
                        player.sendMessage(prefix + " §cコインが" + slotdatamap.get(slotname).coinamount + "§c枚必要です！");
                        return;
                    }

                    slotdatamap.get(slotname).flag = false;
                    removeCoins(player, slotname);
                    updateSign(slotname);
                    raiseSign(slotname);

                    if (slotdatamap.get(slotname).onespinsounds != null) {
                        for (String s : slotdatamap.get(slotname).onespinsounds) {
                            String[] sound = s.split("-");
                            player.getWorld().playSound(framedatamap.get(slotname).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                        }
                    }
                    List<ItemStack>[] symbols = judge(slotname);

                    int stop1 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop1 -1;
                    int stop2 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop2 -1;
                    int stop3 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop3 -1;

                    List<ItemStack> reel1 = symbols[0];
                    List<ItemStack> reel2 = symbols[1];
                    List<ItemStack> reel3 = symbols[2];

                    ItemFrame frame1 = frames.get(slotname).get(0);
                    ItemFrame frame2 = frames.get(slotname).get(1);
                    ItemFrame frame3 = frames.get(slotname).get(2);

                    if (!slotdatamap.get(slotname).framesound) {
                        if (!frame1.isSilent()) {
                            frame1.setSilent(true);
                        }
                        if (!frame2.isSilent()) {
                            frame2.setSilent(true);
                        }
                        if (!frame3.isSilent()) {
                            frame3.setSilent(true);
                        }
                    } else {
                        if (frame1.isSilent()) {
                            frame1.setSilent(false);
                        }
                        if (frame2.isSilent()) {
                            frame2.setSilent(false);
                        }
                        if (frame3.isSilent()) {
                            frame3.setSilent(false);
                        }
                    }

                    int max = Math.max(stop1, Math.max(stop2, stop3));

                    BukkitScheduler scheduler = getServer().getScheduler();

                    long delay = slotdatamap.get(slotname).spindelay;

                    for (int i = 0 ; i < max ; i++) {

                        if (i > 0) {
                            delay += slotdatamap.get(slotname).spindelay;
                        }

                        if (stop1 != 0) {
                            stop1--;
                        }
                        if (stop2 != 0) {
                            stop2--;
                        }
                        if (stop3 != 0) {
                            stop3--;
                        }
                        int finalStop1 = stop1;
                        int finalStop2 = stop2;
                        int finalStop3 = stop3;

                        scheduler.scheduleSyncDelayedTask(plugin, () -> {

                            if (slotdatamap.get(slotname).spinsounds != null) {
                                for (String s : slotdatamap.get(slotname).spinsounds) {
                                    String[] sound = s.split("-");
                                    player.getWorld().playSound(frame2.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                }
                            }
                            colorSign(slotname);

                            if (finalStop1 != 0) {
                                frame1.setItem(reel1.get(finalStop1));
                            }
                            if (finalStop2 != 0) {
                                frame2.setItem(reel2.get(finalStop2));
                            }
                            if (finalStop3 != 0) {
                                frame3.setItem(reel3.get(finalStop3));
                            }

//                            Bukkit.getServer().broadcastMessage(finalStop1 + " " + finalStop2 + " " + finalStop3);

                            }, delay);
                    }
                    scheduler.scheduleSyncDelayedTask(plugin, () -> {
                        try {
                            spinend(slotname, player);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, delay);
                }
            }
    }

    private void spinend(String slotname, Player player) throws IOException {
        if (slotdatamap.get(slotname).winflag) {
            Sign sign = (Sign) signdatamap.get(slotname).getBlock().getState();
            double money = sign.getPersistentDataContainer().get(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE);
            win(slotname, slotdatamap.get(slotname).winkey, money, player);
        } else {
            lose(slotname, player);
        }
        slotdatamap.get(slotname).flag = true;
    }

    private List<ItemStack>[] judge(String s) {

        Random random = new Random();

        double prob = random.nextDouble(slotdatamap.get(s).allProb);

        double plusprob = slotdatamap.get(s).probs.get(0);

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

        Random random = new Random();
        int reel2 = 0;
        int reel3;
        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();

        String[] s1;

        for (int i = 0 ; i < slotdatamap.get(s).stopcount ; i++) {

            s1 = slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);

            reel2 = random.nextInt(slotdatamap.get(s).reel2.size());
            s1 = slotdatamap.get(s).reel2.get(reel2).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);

            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
            } while (reel2 == reel3);
            s1 = slotdatamap.get(s).reel3.get(reel3).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);

        }
        return new List[]{reel1(item1, s), reel2(item2, s), reel3(item3, s, reel2)};
    }

    private List<ItemStack>[] isWin(String slot, String s) {

        Random random = new Random();
        int reel2 = 0;
        int reel3;
        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();
        String[] s1;

        for (int i = 0 ; i < slotdatamap.get(slot).stopcount ; i++) {

            s1 = slotdatamap.get(slot).reel1.get(random.nextInt(slotdatamap.get(slot).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);

            reel2 = random.nextInt(slotdatamap.get(slot).reel2.size());
            s1 = slotdatamap.get(slot).reel2.get(reel2).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);
            do {
                reel3 = random.nextInt(slotdatamap.get(slot).reel3.size());
            } while (reel2 == reel3);
            s1 = slotdatamap.get(slot).reel3.get(reel3).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);
        }

        if (slotdatamap.get(slot).win_symbolcustom.get(s)) {
            s1 = slotdatamap.get(slot).win_symbols.get(s).get(0).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.set(1, item);

            s1 = slotdatamap.get(slot).win_symbols.get(s).get(1).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.set(1, item);

            s1 = slotdatamap.get(slot).win_symbols.get(s).get(2).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.set(1, item);
        } else {
            s1 = slotdatamap.get(slot).reel1.get(random.nextInt(slotdatamap.get(slot).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);

            item1.set(1, item);
            item2.set(1, item);
            item3.set(1, item);
        }

        slotdatamap.get(slot).winkey = s;
        return new List[]{reel1(item1, slot), reel2(item2, slot), reel3(item3, slot, reel2)};
    }

    private List<ItemStack> reel1(List<ItemStack> items, String s) {

        Random random = new Random();
        String[] s1;

        for (int i = 0 ; i < slotdatamap.get(s).reelstop1 ; i++) {
            s1 = slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            items.add(item);
        }
        return items;
    }

    private List<ItemStack> reel2(List<ItemStack> items , String s) {

        Random random = new Random();
        String[] s1;

        for (int i = 0 ; i < slotdatamap.get(s).reelstop2 ; i++) {
            s1 = slotdatamap.get(s).reel2.get(random.nextInt(slotdatamap.get(s).reel2.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            items.add(item);
        }
        return items;
    }

    private List<ItemStack> reel3(List<ItemStack> items , String s , int n) {

        Random random = new Random();
        String[] s1;
        int reel3;

        for (int i = 0 ; i < slotdatamap.get(s).reelstop3 ; i++) {
            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
                s1 = slotdatamap.get(s).reel3.get(random.nextInt(slotdatamap.get(s).reel3.size())).split("-");
                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
                item.setItemMeta(itemMeta);
                items.add(item);
            } while (n == reel3);
        }
        return items;
    }

    private void lose(String s, Player player) {

        player.sendMessage(prefix + " §c何も獲得できませんでした...");
        if (slotdatamap.get(s).losesounds != null) {
            for (String s1 : slotdatamap.get(s).losesounds) {
                String[] sound = s1.split("-");
                player.getWorld().playSound(framedatamap.get(s).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            }
        }
    }

    private void win(String slot, String s, double money, Player player) throws IOException {

        try {
            for (String s1 : slotdatamap.get(slot).win_actions.get(s)) {

                double action = Double.parseDouble(s1.substring(s1.lastIndexOf(":") + 1));
                if (s1.contains("MULTI")) {
                    money = money * action;
                }
                if (s1.contains("RAISE")) {
                    money = money + action;
                }
            }
        } catch (NullPointerException ignore) {
        }

        try {
            for (String s1 : slotdatamap.get(slot).win_commands.get(s)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s1.replace("%player%", player.getName()).replace("%money%", String.valueOf(money)).replace("%spin%", String.valueOf(slotdatamap.get(slot).spincount)));
            }
        } catch (NullPointerException ignore) {
        }

        try {
            Bukkit.getServer().broadcastMessage(slotdatamap.get(slot).win_message.get(s).replace("%player%", player.getName()).replace("%money%", String.valueOf(money)).replace("&", "§").replace("%spin%", String.valueOf(slotdatamap.get(slot).spincount)));
        } catch (NullPointerException ignore) {
        }

        try {
            player.sendMessage(slotdatamap.get(slot).win_playermessage.get(s).replace("%player%", player.getName()).replace("%money%", String.valueOf(money)).replace("&", "§").replace("%spin%", String.valueOf(slotdatamap.get(slot).spincount)));
        } catch (NullPointerException ignore) {
        }

        if (slotdatamap.get(slot).win_sounds.get(s) != null) {
            for (String s1 : slotdatamap.get(slot).win_sounds.get(s)) {
                String[] sound = s1.split("-");
                player.getWorld().playSound(framedatamap.get(slot).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            }
        }

        if (slotdatamap.get(slot).win_pot.get(s)) {
            File file = new File(plugin.getDataFolder().getPath() + "/slots/" + slot + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("stock.nowstock", slotdatamap.get(slot).defaultstock);
            config.set("spincount", 0);
            config.save(file);
            vault.deposit(player, money);
            Sign sign = (Sign) signdatamap.get(slot).getBlock().getState();
            sign.getSide(Side.FRONT).setLine(1, "§e§l" + slotdatamap.get(slot).defaultstock);
            sign.getSide(Side.FRONT).setLine(2, "§f§l" + 0);
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slot + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(slot).defaultstock);
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slot + "-spincount"), PersistentDataType.INTEGER, 0);
            sign.update(true);
        }

    }

    public boolean hasEnoughCoins(Player player, String s) {
        int remaining = slotdatamap.get(s).coinamount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                if (remaining == 0 || item.getAmount() >= remaining) {
                    return true;
                }
                remaining -= item.getAmount();
            }
        }

        return false;
    }

    private void removeCoins(Player player, String s) {
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

    private void raiseSign(String s) throws IOException {

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();
        double moneypot = sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE);
        int spincount = sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-spincount"), PersistentDataType.INTEGER);
        File file = new File(plugin.getDataFolder().getPath() + "/slots/" + s + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        moneypot += slotdatamap.get(s).raise;

        spincount++;
        config.set("stock.nowstock", moneypot);
        config.set("spincount", spincount);
        config.save(file);
        sign.getSide(Side.FRONT).setLine(1, "§e§l" + moneypot);
        sign.getSide(Side.FRONT).setLine(2, "§f§l" + spincount);
        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, s + "-moneypot"), PersistentDataType.DOUBLE, moneypot);
        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, s + "-spincount"), PersistentDataType.INTEGER, spincount);
        sign.update();
    }

    private void updateSign(String s) {

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        if (sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE) == null) {
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(s).stock);
        }

        if (sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-spincount"), PersistentDataType.INTEGER) == null) {
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin , s + "-spincount"), PersistentDataType.INTEGER, slotdatamap.get(s).spincount);
        }

        if (!sign.isWaxed()) {
            sign.setWaxed(true);
        }
        sign.update(true);
    }

    private void colorSign(String s) {

        slotdatamap.get(s).signnum++;
        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        if (slotdatamap.get(s).signnum == 0 || sign.getSide(Side.FRONT).getLine(0).isEmpty()) {
            sign.getSide(Side.FRONT).setLine(0, line1.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line6.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 1) {
            sign.getSide(Side.FRONT).setLine(0, line2.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line7.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 2) {
            sign.getSide(Side.FRONT).setLine(0, line3.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line8.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 3) {
            sign.getSide(Side.FRONT).setLine(0, line4.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line9.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 4) {
            sign.getSide(Side.FRONT).setLine(0, line5.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line10.replace("a", ""));
            slotdatamap.get(s).signnum = 0;
        }

        sign.update(true);
    }
}
