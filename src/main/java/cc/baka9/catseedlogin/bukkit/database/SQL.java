package cc.baka9.catseedlogin.bukkit.database;

import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import java.sql.Date;
import java.sql.Time;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class SQL {
    protected JavaPlugin plugin;

    public SQL(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public LoginPlayer get(String name) throws Exception{
        LoginPlayer lp = null;

        try (PreparedStatement ps = new BufferStatement("SELECT * FROM `blessingskin` WHERE nickname = ?",
                name).prepareStatement(getConnection())) {
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    lp = new LoginPlayer(name, resultSet.getString("password"));
                    lp.setUserid(resultSet.getInt("uid"));
                    lp.setEmail(resultSet.getString("email"));
                    lp.setVerified(resultSet.getInt("verified") == 1);
                }
            }
        }

        return lp;
    }

    public void log(LoginPlayer lp, String action, String ip) throws Exception{
        try (PreparedStatement ps = new BufferStatement("INSERT INTO `blessingskin` (user_id,player_id,action,parameters,ip,time) VALUES (?,?,?,?,?,?)",
                lp.getUserid(), 0, action, "", ip, new Time(System.currentTimeMillis())).prepareStatement(getConnection())) {
            ps.executeUpdate();
        }
    }

    public abstract Connection getConnection() throws Exception;
}
