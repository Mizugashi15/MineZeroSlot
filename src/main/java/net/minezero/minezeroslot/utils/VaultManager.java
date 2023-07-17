package net.minezero.minezeroslot.utils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public class VaultManager {

    String prefix = "§e[§bMZN§e]§e";
    private final JavaPlugin plugin;

    public VaultManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    public static Economy economy = null;

    private void setupEconomy() {

        plugin.getLogger().info("Vaultの連携を開始します");

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {

            plugin.getLogger().warning("Vaultが入っていません！");

            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {

            plugin.getLogger().warning("Can't get vault service");

            return;
        }

        economy = rsp.getProvider();
        plugin.getLogger().info("Vaultの連携が完了しました");

    }

    //get balance 所持金を取得
    public double getBal(UUID uuid) {
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid).getPlayer());
    }

    //get balance 所持金を取得 (日本円表記) ※ 返り血 String ※
    public String getJpyBal(UUID uuid) {
        double money = getBal(uuid);

        if (money < 10000){
            return String.valueOf(money);
        }

        if (money < 100000000){

            long man = (long) (money/10000);
            String left = String.valueOf(money).substring(String.valueOf(money).length()-4);

            if (Long.parseLong(left) == 0){
                return man + "万";
            }

            return man + "万" + Long.parseLong(left);
        }

        if (money < 100000000000L){

            long oku = (long) (money/100000000);
            String man = String.valueOf(money).substring(String.valueOf(money).length() -8);
            String te = man.substring(0, 4);
            String left = String.valueOf(money).substring(String.valueOf(money).length() -4);

            if (Long.parseLong(te)  == 0){

                if (Long.parseLong(left) == 0){
                    return oku + "億";
                } else {
                    return oku + "億"+ Long.parseLong(left);
                }

            } else {

                if (Long.parseLong(left) == 0){
                    return oku + "億" + Long.parseLong(te) + "万";
                }
            }

            return oku + "億" + Long.parseLong(te) + "万" + Long.parseLong(left);
        }

        return "null";
    }

    //show balance 所持金をプレイヤーに通知 (日本円表記)
    public void showJpyBal(UUID uuid) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid).getPlayer();
        String money = getJpyBal(uuid);
        Objects.requireNonNull(Objects.requireNonNull(p).getPlayer()).sendMessage(prefix + " あなたの所持金は " + money + "円です");

    }

    //show balance 所持金をプレイヤーに通知
    public void showBal(UUID uuid) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid).getPlayer();
        double money = getBal(uuid);
        Objects.requireNonNull(Objects.requireNonNull(p).getPlayer()).sendMessage(prefix + " あなたの所持金は " + (int) money + "円です");

    }

    //withdraw 金額を引く (ログ無し)
    public Boolean nologwithdraw(Player player, double money) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(player.getUniqueId());
        EconomyResponse resp = economy.withdrawPlayer(p, money);

        return resp.transactionSuccess();
    }

    //withdraw 金額を引く
    public Boolean withdraw(Player player, double money) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(player.getUniqueId());
        EconomyResponse resp = economy.withdrawPlayer(p, money);

        if (resp.transactionSuccess()) {

            if (p.isOnline()) {
                Objects.requireNonNull(p.getPlayer()).sendMessage(prefix + " " + (int) money + "円支払いました");
            }

            return true;
        }
        return false;
    }

    //deposit 金額を与える (ログなし)
    public Boolean nologdeposit(Player player, double money) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(player.getUniqueId());
        EconomyResponse resp = economy.depositPlayer(p, money);

        return resp.transactionSuccess();
    }

    //deposit 金額を与える
    public Boolean deposit(Player player, double money) {

        OfflinePlayer p = Bukkit.getOfflinePlayer(player.getUniqueId());
        EconomyResponse resp = economy.depositPlayer(p, money);

        if (resp.transactionSuccess()) {

            if (p.isOnline()) {
                Objects.requireNonNull(p.getPlayer()).sendMessage(prefix + " " + (int) money + "円受取りました");
            }

            return true;
        }
        return false;
    }
}
