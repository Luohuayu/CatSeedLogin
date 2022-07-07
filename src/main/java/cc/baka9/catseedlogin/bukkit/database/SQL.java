package cc.baka9.catseedlogin.bukkit.database;

import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class SQL {
    protected JavaPlugin plugin;

    public SQL(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public LoginPlayer get(String name) throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts WHERE name = ?",
                name).prepareStatement(getConnection());

        ResultSet resultSet = ps.executeQuery();
        LoginPlayer lp = null;
        if (resultSet.next()) {
            lp = new LoginPlayer(name, resultSet.getString("password"));
            lp.setLastAction(resultSet.getTimestamp("lastAction").getTime());
            lp.setEmail(resultSet.getString("email"));
            lp.setIps(resultSet.getString("ips"));
        }
        resultSet.close();
        ps.close();
        return lp;
    }

    public List<LoginPlayer> getAll() throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts").prepareStatement(getConnection());
        ResultSet resultSet = ps.executeQuery();
        List<LoginPlayer> lps = new ArrayList<>();
        LoginPlayer lp;
        while (resultSet.next()) {
            lp = new LoginPlayer(resultSet.getString("name"), resultSet.getString("password"));
            lp.setLastAction(resultSet.getTimestamp("lastAction").getTime());
            lp.setEmail(resultSet.getString("email"));
            lp.setIps(resultSet.getString("ips"));
            lps.add(lp);
        }
        return lps;

    }

    public abstract Connection getConnection() throws Exception;
}
