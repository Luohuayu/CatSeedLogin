package cc.baka9.catseedlogin.bukkit;

import cc.baka9.catseedlogin.bukkit.command.CommandCatSeedReload;
import cc.baka9.catseedlogin.bukkit.command.CommandLogin;
import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.database.MySQL;
import cc.baka9.catseedlogin.bukkit.database.SQL;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.bukkit.task.Task;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CatSeedLogin extends JavaPlugin {

    public static CatSeedLogin instance;
    public static BukkitScheduler scheduler = Bukkit.getScheduler();
    public static SQL sql;
    public static boolean loadProtocolLib = false;

    @Override
    public void onEnable(){
        instance = this;
        //Config
        try {
            Config.load();
            Config.save();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getLogger().warning("加载配置文件时出错，请检查你的配置文件。");
        }
        sql = new MySQL(this);
        try {
            sql.init();

            Cache.refreshAll();
        } catch (Exception e) {
            getLogger().warning("§c加载数据库时出错");
            e.printStackTrace();
        }
        //Listeners
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        //ProtocolLibListeners
        try {
            Class.forName("com.comphenix.protocol.ProtocolLib");
            ProtocolLibListeners.enable();
            loadProtocolLib = true;
        } catch (ClassNotFoundException e) {
            getLogger().warning("服务器没有装载ProtocolLib插件，这将无法使用登录前隐藏背包");
        }

        //Commands
        getServer().getPluginCommand("login").setExecutor(new CommandLogin());
        getServer().getPluginCommand("login").setTabCompleter((commandSender, command, s, args)
                -> args.length == 1 ? Collections.singletonList("密码") : new ArrayList<>(0));

        getServer().getPluginCommand("catseedloginreload").setExecutor(new CommandCatSeedReload());

        //Task
        Task.runAll();
    }


    @Override
    public void onDisable(){
        Task.cancelAll();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!LoginPlayerHelper.isLogin(p.getName())) return;
            if (!p.isDead() || Config.Settings.DeathStateQuitRecordLocation) {
                Config.setOfflineLocation(p);
            }

        });
        try {
            sql.getConnection().close();
        } catch (Exception e) {
            getLogger().warning("获取数据库连接时出错");
            e.printStackTrace();
        }
        super.onDisable();
    }

    public void runTaskAsync(Runnable runnable){
        scheduler.runTaskAsynchronously(this, runnable);
    }

}
