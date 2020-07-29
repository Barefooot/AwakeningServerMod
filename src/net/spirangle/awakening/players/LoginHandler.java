package net.spirangle.awakening.players;

import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.steam.SteamId;
import com.wurmonline.server.utils.DbUtilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static net.spirangle.awakening.AwakeningConstants.DAY_AS_MILLIS;


@SuppressWarnings("unused")
public class LoginHandler {

    private static final Logger logger = Logger.getLogger(LoginHandler.class.getName());

   private static LoginHandler instance = null;

   public static LoginHandler getInstance() {
      if(instance==null) instance = new LoginHandler();
      return instance;
   }

   private Map<Long,PlayerInfo> mainBySteamId;

   private LoginHandler() {
       mainBySteamId = new HashMap<>();
   }

    @SuppressWarnings("unused")
    public static void sendPvPList(final Player player) {
        if(player.isUndead()) return;
        final Communicator comm = player.getCommunicator();
        String localServerName = Servers.localServer.name;
        if(localServerName.length()>1) {
            localServerName = localServerName.toLowerCase();
            localServerName = java.lang.Character.toUpperCase(localServerName.charAt(0))+localServerName.substring(1);
        }
        long now = System.currentTimeMillis();
        List<PlayerData> pvp = PlayersData.getInstance().getPlayerData().stream().filter(pd -> {
            if(pd.isPvP()) {
                PlayerInfo pi = PlayerInfoFactory.getPlayerInfoWithWurmId(pd.wurmId);
                if(pi!=null) {
                    try {
                        pi.load();
                        if(pi.lastLogin >= now-60L*DAY_AS_MILLIS || (pi.lastLogin==0L && pi.lastLogout >= now-60L*DAY_AS_MILLIS)) {
                            pd.setKingdom(pi.getChaosKingdom());
                            return true;
                        }
                    } catch(IOException ioe) {}
                }
            }
            return false;
        }).sorted((p1,p2) -> {
            if(p1.getKingdom()!=p2.getKingdom()) return p1.getKingdom()-p2.getKingdom();
            return p1.name.compareTo(p2.name);
        }).collect(Collectors.toList());
        if(!pvp.isEmpty()) {
            comm.sendSafeServerMessage("These are active PvP players on "+localServerName+":");
            StringBuilder list = new StringBuilder();
            int i = 0,n = 0,k = -1;
            Kingdom kingdom = Kingdoms.getKingdom(player.getKingdomId());
            for(final PlayerData pd : pvp) {
                if(pd.getKingdom()!=k) {
                    if(list.length()>0) {
                        comm.sendSafeServerMessage(list.toString());
                        list = new StringBuilder();
                    }
                    list.append(Kingdoms.getNameFor(pd.getKingdom()));
                    if(kingdom.getId()==pd.getKingdom()) list.append(" (home)");
                    else if(kingdom.isAllied(pd.getKingdom())) list.append(" (allied)");
                    else list.append(" (enemy)");
                    list.append(": ");
                    k = pd.getKingdom();
                    n = 0;
                }
                if(n>0 && list.length()+pd.name.length()+2>200) {
                    comm.sendSafeServerMessage(list.toString());
                    list = new StringBuilder();
                    n = 0;
                }
                if(n>0 && list.length()>0) list.append(", ");
                list.append(pd.name);
                ++n;
                ++i;
            }
            if(n>0 && list.length()>0) {
                comm.sendSafeServerMessage(list.toString());
            }
        } else {
            comm.sendSafeServerMessage("There are currently no active PvP players on "+localServerName+".");
        }
    }

    public PlayerInfo getMainPlayerInfo(final SteamId steamId) {
       return getMainPlayerInfo(null,steamId);
    }

    private PlayerInfo getMainPlayerInfo(final PlayerInfo playerInfo,final SteamId steamId) {
        if(playerInfo==null) return null;
        PlayerInfo main = mainBySteamId.get(steamId.getSteamID64());
        if(main!=null) return main;
        else main = playerInfo;
        Connection db = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            db = DbConnector.getPlayerDbCon();
            ps = db.prepareStatement("SELECT DISTINCT PLAYER_ID FROM STEAM_IDS WHERE STEAM_ID=? AND PLAYER_ID!=?");
            ps.setLong(1,steamId.getSteamID64());
            ps.setLong(2,playerInfo.wurmId);
            rs = ps.executeQuery();
            while(rs.next()) {
                long wurmId = rs.getLong("PLAYER_ID");
                PlayerInfo pi = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
                if(pi==null) continue;
                try {
                    pi.load();
                    if(main==null || pi.getPower()>main.getPower() ||
                       (pi.getPower()==main.getPower() && pi.playingTime>main.playingTime)) main = pi;
                } catch(IOException ioe) {}
            }
        } catch(SQLException e) {
        } finally {
            DbUtilities.closeDatabaseObjects(ps,rs);
            DbConnector.returnConnection(db);
        }
        if(main!=null) mainBySteamId.put(steamId.getSteamID64(),main);
        return main;
    }
}

