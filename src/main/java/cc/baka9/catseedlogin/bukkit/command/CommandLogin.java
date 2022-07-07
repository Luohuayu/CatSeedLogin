package cc.baka9.catseedlogin.bukkit.command;

import cc.baka9.catseedlogin.bukkit.Config;
import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.event.CatSeedPlayerLoginEvent;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CommandLogin implements CommandExecutor {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length == 0 || !(sender instanceof Player)) return false;
        Player player = (Player) sender;
        String name = player.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            sender.sendMessage(Config.Language.LOGIN_REPEAT);
            return true;
        }
        LoginPlayer lp = Cache.getIgnoreCase(name);
        if (lp == null || !lp.isVerified()) {
            sender.sendMessage(Config.Language.LOGIN_NOREGISTER);
            return true;
        }
        LoginPlayerHelper.recordCurrentIP(lp, "authenticate", player.getAddress().getAddress().getHostAddress());
        if (bCryptPasswordEncoder.matches(args[0], lp.getPassword())) {
            LoginPlayerHelper.recordCurrentIP(lp, "join", player.getAddress().getAddress().getHostAddress());
            LoginPlayerHelper.add(lp);
            CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.SUCCESS);
            Bukkit.getServer().getPluginManager().callEvent(loginEvent);
            sender.sendMessage(Config.Language.LOGIN_SUCCESS);
            player.updateInventory();
            LoginPlayerHelper.recordCurrentIP(lp, "has_joined", "127.0.0.1");
            if (Config.Settings.AfterLoginBack && Config.Settings.CanTpSpawnLocation) {
                Config.getOfflineLocation(player).ifPresent(player::teleport);
            }
        } else {
            sender.sendMessage(Config.Language.LOGIN_FAIL);
            CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.FAIL);
            Bukkit.getServer().getPluginManager().callEvent(loginEvent);
        }
        return true;
    }
}
