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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minezero.minezeroslot.utils.Permission.*;
import static net.minezero.minezeroslot.MineZeroSlot.*;

public class SlotEvent implements Listener {

    private BukkitTask task;

    @EventHandler
    void spin(PlayerInteractEvent event) {

        try {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!isUsePermission(event.getPlayer())) {

                    event.getPlayer().sendMessage(prefix + " §c権限がありません！");
                    return;
                }

                if (inputdatamap.containsKey(event.getClickedBlock().getState())) {

                    String slotname = inputdatamap.get(event.getClickedBlock().getState());
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

                    ItemFrame frame1 = (ItemFrame) framedatamap.get(slotname).get(0).getBlock();
                    ItemFrame frame2 = (ItemFrame) framedatamap.get(slotname).get(1).getBlock();
                    ItemFrame frame3 = (ItemFrame) framedatamap.get(slotname).get(2).getBlock();

                    int max = Math.max(stop1, Math.max(stop2, stop3));

                    for (int i = 0 ; i <= max ; i++) {

                        int finalI = i;
                        task = new BukkitRunnable() {

                            @Override
                            public void run() {

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
                                    frame3.setItem(reel2.get(finalI));
                                }
                            }
                        }.runTaskTimer(plugin, slotdatamap.get(slotname).spindelay, slotdatamap.get(slotname).spindelay);
                    }
                    if (slotdatamap.get(slotname).winflag) {
                        Sign sign = (Sign) signdatamap.get(slotname).getBlock();
                        double money = sign.getPersistentDataContainer().get(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE);
                        win(slotname, slotdatamap.get(slotname).winkey, money, event.getPlayer());
                        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(slotname).defaultstock);
                    } else {
                        lose(slotname, event.getPlayer());
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }
    }

    private List<ItemStack>[] judge(String s) {

        Random random = new Random();

        int prob = random.nextInt(slotdatamap.get(s).allProb);

        int plusprob = slotdatamap.get(s).probs.get(0);

        if (plusprob >= prob) {
            return isLose(s);
        }

        for (String s1 : slotdatamap.get(s).win_name) {

            plusprob += slotdatamap.get(s).win_chance.get(s1);

            if (plusprob <= prob) {
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

        for (int i = 0 ; i <= slotdatamap.get(s).stopcount ; i++) {


            reel1 = random.nextInt(slotdatamap.get(s).reel1.size());
            item1.add(slotdatamap.get(s).reel1.get(reel1));


            reel2 = random.nextInt(slotdatamap.get(s).reel2.size());
            item2.add(slotdatamap.get(s).reel2.get(reel2));


            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
            } while (reel2 == reel3);
            item3.add(slotdatamap.get(s).reel3.get(reel3));

        }
        slotdatamap.get(s).winflag = false;
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

        for (int i = 0 ; i <= slotdatamap.get(slot).stopcount ; i++) {

            if (i == 0) {

                item1.add(slotdatamap.get(slot).win_symbols.get(s).get(0));
                item2.add(slotdatamap.get(slot).win_symbols.get(s).get(1));
                item3.add(slotdatamap.get(slot).win_symbols.get(s).get(2));

            }

            reel1 = random.nextInt(slotdatamap.get(slot).reel1.size());
            item1.add(slotdatamap.get(slot).reel1.get(reel1));
            reel2 = random.nextInt(slotdatamap.get(slot).reel2.size());
            item2.add(slotdatamap.get(slot).reel2.get(reel2));
            do {
                reel3 = random.nextInt(slotdatamap.get(slot).reel3.size());
            } while (reel2 == reel3);
            item3.add(slotdatamap.get(slot).reel3.get(reel3));

        }
        slotdatamap.get(slot).winkey = s;
        slotdatamap.get(slot).winflag = true;
        return new List[]{reel1(item1, slot), reel2(item2, slot), reel3(item3, slot, reel2)};
    }

    private List<ItemStack> reel1(List<ItemStack> items , String s) {

        Random random = new Random();

        for (int i = 0 ; i <= slotdatamap.get(s).reelstop1 ; i++) {
            items.add(slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())));
        }
        return items;
    }

    private List<ItemStack> reel2(List<ItemStack> items , String s) {

        Random random = new Random();

        for (int i = 0 ; i <= slotdatamap.get(s).reelstop2 ; i++) {
            items.add(slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())));
        }
        return items;
    }

    private List<ItemStack> reel3(List<ItemStack> items , String s , int n) {

        Random random = new Random();
        int reel3 = 0;

        for (int i = 0 ; i <= slotdatamap.get(s).reelstop3 ; i++) {
            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel1.size());
                items.add(slotdatamap.get(s).reel1.get(reel3));
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

        Sign sign = (Sign) signdatamap.get(s).getBlock();

        double moneypot = sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE);

        moneypot += slotdatamap.get(s).raise;

        sign.getSide(Side.FRONT).setLine(1, "§6" + moneypot);

        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, s + "-moneypot"), PersistentDataType.DOUBLE, moneypot);

        sign.update(true);
    }

    private void colorSign(String s) {

        Sign sign = (Sign) signdatamap.get(s).getBlock();

        sign.getSide(Side.FRONT).setLine(0, "a");

        sign.update(true);
    }
}
