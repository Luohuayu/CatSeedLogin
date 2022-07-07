package cc.baka9.catseedlogin.bukkit.command;

import cc.baka9.catseedlogin.bukkit.CatSeedLogin;
import cc.baka9.catseedlogin.bukkit.Config;
import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.database.MySQL;
import cc.baka9.catseedlogin.bukkit.database.SQLite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandCatSeedReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (!sender.isOp()) return false;

        Config.reload();
        CatSeedLogin.sql = Config.MySQL.Enable ? new MySQL(CatSeedLogin.instance) : new SQLite(CatSeedLogin.instance);
        try {

            CatSeedLogin.sql.init();

            Cache.refreshAll();
        } catch (Exception e) {
            CatSeedLogin.instance.getLogger().warning("§c加载数据库时出错");
            e.printStackTrace();
        }

        sender.sendMessage("配置已重载!");
        return true;
    }
}
