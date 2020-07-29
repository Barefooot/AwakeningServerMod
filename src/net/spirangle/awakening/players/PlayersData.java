package net.spirangle.awakening.players;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PlayersData {

    private static final Logger logger = Logger.getLogger(PlayersData.class.getName());

    private static PlayersData instance = null;

    public static PlayersData getInstance() {
        if(instance==null) instance = new PlayersData();
        return instance;
    }

    private HashMap<Long,PlayerData> players;
    private HashMap<String,PlayerData> playersByName;

    private PlayersData() {
        players = new HashMap<>();
        playersByName = new HashMap<>();
    }

    public void loadPlayersData() {
        try(Connection con = ModSupportDb.getModSupportDb()) {
            try(PreparedStatement ps = con.prepareStatement("SELECT WURMID,NAME,KINGDOM,FLAGS,DATA,CREATED,CHANGED FROM AWA_PLAYERSDATA");
                ResultSet rs = ps.executeQuery()) {
                long wurmId,flags;
                String name,data;
                byte kingdom;
                PlayerData pd;
                while(rs.next()) {
                    wurmId = rs.getLong(1);
                    name = rs.getString(2);
                    kingdom = rs.getByte(3);
                    flags = rs.getLong(4);
                    data = rs.getString(5);
                    logger.info("Loading player data with id "+wurmId+" with name "+name+".");
                    pd = new PlayerData(wurmId,name,kingdom,flags,data);
                    players.put(wurmId,pd);
                    playersByName.put(name,pd);
                }
            }
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to load players data: "+e.getMessage(),e);
        }
    }

    @SuppressWarnings("unused")
    public void saveAllPlayersData() {
        if(players.entrySet().stream().noneMatch(entry -> entry.getValue().changed)) return;
        try(Connection con = ModSupportDb.getModSupportDb()) {
            while(!savePlayersData(con));
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to save players data: "+e.getMessage(),e);
        }
    }

    @SuppressWarnings("unused")
    public boolean savePlayersData() {
        if(players.entrySet().stream().noneMatch(entry -> entry.getValue().changed)) return true;
        try(Connection con = ModSupportDb.getModSupportDb()) {
            return savePlayersData(con);
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to save players data: "+e.getMessage(),e);
        }
        return true;
    }

    public boolean savePlayersData(Connection con) {
        if(players.entrySet().stream().noneMatch(entry -> entry.getValue().changed)) return true;
        boolean done = true;
        try(PreparedStatement ps = con.prepareStatement("UPDATE AWA_PLAYERSDATA SET NAME=?,KINGDOM=?,FLAGS=?,DATA=?,CHANGED=? WHERE WURMID=?")) {
            List<PlayerData> saved = new ArrayList<>(20);
            for(Map.Entry<Long,PlayerData> entry : players.entrySet()) {
                PlayerData pd = entry.getValue();
                Player player = null;
                try {
                    player = Players.getInstance().getPlayer(pd.wurmId);
                } catch(NoSuchPlayerException e) {}
                if(pd.changed) {
                    ps.setString(1,player!=null? player.getName() : pd.name);
                    ps.setByte(2,player!=null? player.getKingdomId() : pd.getKingdom());
                    ps.setLong(3,pd.getFlags());
                    ps.setString(4,pd.toJSONString());
                    ps.setLong(5,System.currentTimeMillis());
                    ps.setLong(6,pd.wurmId);
                    ps.addBatch();
                    saved.add(pd);
                    if(saved.size()>=20) {
                        done = false;
                        break;
                    }
                }
            }
            if(!saved.isEmpty()) {
                ps.executeBatch();
                logger.info("Saved data for "+saved.size()+" players.");
                saved.stream().forEach(pd -> pd.changed = false);
            }
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to save players data: "+e.getMessage(),e);
        }
        return done;
    }

    public Collection<PlayerData> getPlayerData() {
        return players.values();
    }

    public void onPlayerLogin(Player player) {
        PlayerData pd = players.get(player.getWurmId());
        if(pd==null) pd = addPlayer(player);
    }

    public PlayerData addPlayer(Player player) {
        PlayerData pd = new PlayerData(player);
        try(Connection con = ModSupportDb.getModSupportDb();
            PreparedStatement ps = con.prepareStatement("INSERT INTO AWA_PLAYERSDATA (WURMID,NAME,KINGDOM,FLAGS,DATA,CREATED,CHANGED) VALUES(?,?,?,?,?,?,?)")) {
            ps.setLong(1,pd.wurmId);
            ps.setString(2,pd.name);
            ps.setByte(3,pd.getKingdom());
            ps.setLong(4,pd.getFlags());
            ps.setString(5,pd.toJSONString());
            ps.setLong(6,System.currentTimeMillis());
            ps.setLong(7,System.currentTimeMillis());
            ps.execute();
            logger.info("Inserted players data for "+pd.name+".");
            players.put(pd.wurmId,pd);
            playersByName.put(pd.name,pd);
            pd.changed = false;
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to insert players data: "+e.getMessage(),e);
        }
        return pd;
    }

    public PlayerData get(Player player) {
        PlayerData pd = players.get(player.getWurmId());
        if(pd==null) pd = addPlayer(player);
        return pd;
    }

    public PlayerData get(long wurmId) {
        return players.get(wurmId);
    }

    public PlayerData get(String name) {
        return playersByName.get(name);
    }

    public LeaderBoard getLeaderBoard(Player player) {
        PlayerData pd = get(player);
        LeaderBoard lb = pd.getLeaderBoard();
        return lb;
    }
}
