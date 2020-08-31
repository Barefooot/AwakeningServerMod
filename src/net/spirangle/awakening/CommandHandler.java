package net.spirangle.awakening;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.server.*;
import com.wurmonline.server.behaviours.Crops;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.questions.BMLEditorQuestion;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.spirangle.awakening.actions.ActionStack;
import net.spirangle.awakening.players.PlayerData;
import net.spirangle.awakening.players.PlayersData;
import net.spirangle.awakening.time.Scheduler;
import net.spirangle.awakening.util.StringUtils;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandHandler {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    private static final Pattern configPattern = Pattern.compile("^#config\\s+([a-zA-Z0-9]+)(?:\\s+(\\d+)\\s+(\\d+))?");
    private static final Pattern restorePattern = Pattern.compile("^#restore\\s+(rock|biomes|cave|mycelium)\\s+(?:"+
                                                                  "(load)\\s+(.+)|"+
                                                                  "(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+))");
    private static final Pattern altsPattern = Pattern.compile("^#alts\\s+([a-zA-Z0-9]+)");
    private static final Pattern monitorPattern = Pattern.compile("^#monitor\\s+([a-zA-Z0-9]+)\\s+(\\d+)");
    private static final Pattern pvpPattern = Pattern.compile("^#pvp\\s+([a-zA-Z0-9]+)\\s+(remove)");
    private static final Pattern karmaPattern = Pattern.compile("^#karma\\s+([a-zA-Z0-9]+)\\s+([\\-0-9]+)");
    private static final Pattern rotatePattern = Pattern.compile("^#rotate\\s+(\\d+)\\s+([0-9\\.]+)");
    private static final Pattern inventoryPattern = Pattern.compile("^#inventory\\s+([a-zA-Z0-9]+)\\s+(clear|give)(?:\\s+(\\d+)\\s+(\\d+))?");
    private static final Pattern transferPattern = Pattern.compile("^#transfer\\s+(\\w+)\\s+([\\w\\.\\/-]+)\\s+(\\w+)");
    private static final Pattern schedulePattern = Pattern.compile("^#schedule\\s+(?:"+
                                                                   "(stop|list)(?:\\s+([a-zA-Z0-9_]+))?|"+
                                                                   "broadcast\\s+([a-zA-Z0-9]+)\\s+(\\d+)\\/(\\d+)\\s+(#[0-9a-fA-F]{6}|\\d+)\\s+(.+)"+
                                                                   ")");
    private static final Pattern tilePattern = Pattern.compile("^#tile\\s+(\\w+)");
    private static final Pattern setResourcePattern = Pattern.compile("^#setresource\\s+(\\w+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)(?:\\s+(\\d+))?");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static CommandHandler instance = null;

    public static CommandHandler getInstance() {
        if(instance==null) instance = new CommandHandler();
        return instance;
    }

    private Field serverLag = null;

    private CommandHandler() {
    }

    private boolean hasPowerForCommand(Player performer,int power,String command) {
        return hasPowerForCommand(performer,power,false,command);
    }

    private boolean hasPowerForCommand(Player performer,int power,boolean cm,String command) {
        if(performer.getPower() >= power || (cm && performer.mayMute())) return true;
        logger.log(Level.WARNING,"Player "+performer.getName()+" tried to use #"+command+" command.");
        return false;
    }

    public MessagePolicy handleGmCommand(Communicator communicator,String message,String title) {
        if(Config.handleCommandConfig && message.startsWith("#config"))
            return handleCommandConfig(communicator,message);
        else if(Config.handleCommandRestore && message.startsWith("#restore"))
            return handleCommandRestore(communicator,message);
        else if(Config.handleCommandAlts && message.startsWith("#alts"))
            return handleCommandAlts(communicator,message);
        else if(Config.handleCommandMonitor && message.startsWith("#monitor"))
            return handleCommandMonitor(communicator,message);
        else if(Config.handleCommandPvP && message.startsWith("#pvp"))
            return handleCommandPvP(communicator,message);
        else if(Config.handleCommandKarma && message.startsWith("#karma"))
            return handleCommandKarma(communicator,message);
        else if(Config.handleCommandRotate && message.startsWith("#rotate"))
            return handleCommandRotate(communicator,message);
        else if(Config.handleCommandInventory && message.startsWith("#inventory"))
            return handleCommandInventory(communicator,message);
        else if(Config.handleCommandTransfer && message.startsWith("#transfer"))
            return handleCommandTransfer(communicator,message);
        else if(Config.handleCommandSQL && message.startsWith("#sql"))
            return handleCommandSQL(communicator,message);
        else if(Config.handleCommandSchedule && message.startsWith("#schedule"))
            return handleCommandSchedule(communicator,message);
        else if(Config.handleCommandTile && message.startsWith("#tile"))
            return handleCommandTile(communicator,message);
        else if(Config.handleCommandSetResource && message.startsWith("#setresource"))
            return handleCommandSetResource(communicator,message);
        else if(Config.handleCommandFlowersFix && message.startsWith("#flowersfix"))
            return handleCommandFlowersFix(communicator,message);

        return MessagePolicy.PASS;
    }

    public MessagePolicy handleCommand(Communicator communicator,String message,String title) {
        if(Config.handleCommandLag && message.equals("/lag"))
            return handleCommandLag(communicator,message);
        else if(Config.handleCommandBank && message.equals("/bank"))
            return handleCommandBank(communicator,message);
        else if(Config.handleCommandPvPList && message.startsWith("/pvp"))
            return handleCommandPvPList(communicator,message);
        return MessagePolicy.PASS;
    }

    public MessagePolicy handleCommandConfig(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,"config")) return MessagePolicy.PASS;
        try {
            Matcher m = configPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null) {
                    String cmd = m.group(1);
                    if(cmd.equals("reloadAwakening")) reloadConfig(performer,"awakening");
                    else if(cmd.equals("bml")) {
                        int w = 550;
                        int h = 550;
                        if(m.group(2)!=null) w = Integer.parseInt(m.group(2));
                        if(m.group(3)!=null) h = Integer.parseInt(m.group(3));
                        BMLEditorQuestion question = new BMLEditorQuestion(performer,"",w,h,false);
                        question.sendQuestion();
                    }
                }
            } else {
                communicator.sendSafeServerMessage("Config command usage:");
                communicator.sendSafeServerMessage("#config reloadAwakening  -- reload the awakening.properties file");
                communicator.sendSafeServerMessage("#config bml  -- open a BML editor");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Config command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandRestore(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_IMPLEMENTOR,"restore")) return MessagePolicy.PASS;
        try {
            Matcher m = restorePattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null) {
                    String type = m.group(1);
                    if(m.group(2)!=null && m.group(3)!=null) {
                        String cmd = m.group(2);
                        String path = m.group(3);
                        if(path!=null && net.spirangle.awakening.zones.Tiles.loadMesh("map.mesh."+type,path)!=null) {
                            communicator.sendSafeServerMessage("Loaded mesh for restoring map. ["+path+"]");
                        }
                    } else if(m.group(4)!=null && m.group(5)!=null && m.group(6)!=null && m.group(7)!=null) {
                        communicator.sendSafeServerMessage("Restoring map to original state...");
                        int n = 0;
                        int sx = Integer.parseInt(m.group(4));
                        int sy = Integer.parseInt(m.group(5));
                        int ex = Integer.parseInt(m.group(6));
                        int ey = Integer.parseInt(m.group(7));
                        if("biomes".equals(type)) {
                            n = net.spirangle.awakening.zones.Tiles.restoreBiomes(sx,sy,ex,ey);
                        } else if("mycelium".equals(type)) {
                            n = net.spirangle.awakening.zones.Tiles.restoreMycelium(sx,sy,ex,ey);
                        }
                        if(n<0) {
                            communicator.sendAlertServerMessage("Original map data has not been loaded, or cached out.");
                        } else {
                            communicator.sendSafeServerMessage("Updated "+n+" tiles with original data.");
                        }
                    }
                }
            } else {
                communicator.sendSafeServerMessage("Restore command usage:");
                communicator.sendSafeServerMessage("#restore <rock|biomes|cave> load <path>  -- load the mesh data for the original map");
                communicator.sendSafeServerMessage("#restore <rock|biomes|cave|mycelium> <startx> <starty> <endx> <endy>  -- restore map for the given area using loaded mesh data");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Restore command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandAlts(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,true,"alts")) return MessagePolicy.PASS;
        try {
            Matcher m = altsPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null) {
                    String name = m.group(1);
                    PlayerInfo playerInfo = PlayerInfoFactory.getPlayerInfoWithName(com.wurmonline.server.LoginHandler.raiseFirstLetter(name));
                    if(playerInfo==null) communicator.sendNormalServerMessage("No such player.");
                    else {
                        try {
                            int n = 0;
                            playerInfo.load();
                            Connection db = null;
                            PreparedStatement ps = null;
                            ResultSet rs = null;
                            List<String> steamIds = new ArrayList<>();
                            Set<PlayerInfo> alts = new HashSet<>();
                            alts.add(playerInfo);
                            try {
                                db = DbConnector.getPlayerDbCon();
                                try {
                                    ps = db.prepareStatement("SELECT DISTINCT STEAM_ID FROM STEAM_IDS WHERE PLAYER_ID=?");
                                    ps.setLong(1,playerInfo.wurmId);
                                    rs = ps.executeQuery();
                                    while(rs.next()) {
                                        steamIds.add(rs.getString("STEAM_ID"));
                                    }
                                } catch(SQLException e) {
                                } finally {
                                    DbUtilities.closeDatabaseObjects(ps,rs);
                                }
                                communicator.sendSafeServerMessage("Listing possible alt characters for "+name+
                                                                   " [IP: "+playerInfo.getIpaddress()+", SteamID: "+String.join(",",steamIds)+"]:");
                                try {
                                    StringBuilder sql = new StringBuilder();
                                    sql.append("SELECT DISTINCT PLAYER_ID,STEAM_ID FROM STEAM_IDS WHERE PLAYER_ID!=? AND STEAM_ID");
                                    if(steamIds.size()==1) sql.append("=").append(steamIds.get(0));
                                    else {
                                        sql.append(" IN(").append(steamIds.get(0));
                                        for(int i=1; i<steamIds.size(); ++i) sql.append(",").append(steamIds.get(i));
                                        sql.append(")");
                                    }
                                    ps = db.prepareStatement(sql.toString());
                                    ps.setLong(1,playerInfo.wurmId);
                                    rs = ps.executeQuery();
                                    while(rs.next()) {
                                        long wurmId = rs.getLong("PLAYER_ID");
                                        long steamId = rs.getLong("STEAM_ID");
                                        PlayerInfo pi = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
                                        if(pi==null || alts.contains(pi)) continue;
                                        if(sendAltPlayerInfo(communicator,pi,steamId)) {
                                            alts.add(pi);
                                            ++n;
                                        }
                                    }
                                } catch(SQLException e) {
                                } finally {
                                    DbUtilities.closeDatabaseObjects(ps,rs);
                                }
                            } catch(SQLException e) {
                            } finally {
                                DbConnector.returnConnection(db);
                            }
                            PlayerInfo[] playerInfos = PlayerInfoFactory.getPlayerInfos();
                            for(PlayerInfo pi : playerInfos) {
                                if(alts.contains(pi)) continue;
                                try {
                                    pi.load();
                                } catch(IOException e) {}
                                if(!pi.getIpaddress().equals(playerInfo.getIpaddress())) continue;
                                if(sendAltPlayerInfo(communicator,pi,-10L)) {
                                    alts.add(pi);
                                    ++n;
                                }
                            }
                            if(n==0) communicator.sendSafeServerMessage("No other characters found.");
                        } catch(IOException ioe) {}
                    }
                }
            } else {
                communicator.sendSafeServerMessage("Alts command usage:");
                communicator.sendSafeServerMessage("#alts <player>  -- list player alts, or possible alts.");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Alts command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandMonitor(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,true,"monitor")) return MessagePolicy.PASS;
        try {
            Matcher m = monitorPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null && m.group(2)!=null) {
                    String name = m.group(1);
                    Player player = Players.getInstance().getPlayerOrNull(com.wurmonline.server.LoginHandler.raiseFirstLetter(name));
                    if(player==null) communicator.sendNormalServerMessage("No such player.");
                    else if(player.getSteamId()==performer.getSteamId() && player.getPower()<MiscConstants.POWER_DEMIGOD) {
                        communicator.sendNormalServerMessage("You cannot monitor your own characters.");
                    } else {
                        long time = Long.parseLong(m.group(2));
                        ActionStack.Pattern pattern = ActionStack.getInstance().getPattern(player);
                        if(pattern==null) {
                            communicator.sendNormalServerMessage("You cannot monitor "+player.getName()+", there is no action pattern registered yet.");
                        } else {
                            communicator.sendAlertServerMessage("You monitor "+player.getName()+" for "+time+" minutes.");
                            logger.info(performer.getName()+" monitors "+player.getName()+" for "+time+" minutes.");
                            pattern.setLogActionsTime(performer,player,time);
                        }
                    }
                }
            } else {
                communicator.sendSafeServerMessage("Monitor command usage:");
                communicator.sendSafeServerMessage("#monitor <name> <time>  -- monitor player's actions, writes actions with timestamps to log-file; time=minutes");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Monitor command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandPvP(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,true,"pvp")) return MessagePolicy.PASS;
        try {
            Matcher m = pvpPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null && m.group(2)!=null) {
                    String name = m.group(1);
                    Player player = Players.getInstance().getPlayerOrNull(com.wurmonline.server.LoginHandler.raiseFirstLetter(name));
                    if(player==null) communicator.sendNormalServerMessage("No such player.");
                    else {
                        String cmd = m.group(2);
                        if(cmd.equals("remove")) {
                            PlayerData pd = PlayersData.getInstance().get(player);
                            if(!pd.isPvP()) communicator.sendSafeServerMessage(name+" does not have the PvP setting.");
                            else {
                                pd.setPvP(false);
                                communicator.sendSafeServerMessage("You removed PvP setting from "+name+".");
                                player.getCommunicator().sendSafeServerMessage("PvP was unchecked in your settings.");
                            }
                        }
                    }
                }
            } else {
                communicator.sendSafeServerMessage("PvP command usage:");
                communicator.sendSafeServerMessage("#pvp <name> remove  -- remove PvP setting from a player");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"PvP command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandKarma(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,"karma")) return MessagePolicy.PASS;
        try {
            Matcher m = karmaPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null && m.group(2)!=null) {
                    String name = m.group(1);
                    Player player = Players.getInstance().getPlayerOrNull(com.wurmonline.server.LoginHandler.raiseFirstLetter(name));
                    if(player==null) communicator.sendNormalServerMessage("No such player.");
                    else {
                        int karma = Integer.parseInt(m.group(2));
                        if(karma!=0) {
                            int k = player.getKarma();
                            k += karma;
                            if(k<0) k = 0;
                            player.setKarma(k);
                            communicator.sendSafeServerMessage("You have "+(karma>0? "given to" : "taken from")+" "+name+" an amount of "+(karma>0? karma : -karma)+" karma.");
                        }
                    }
                }
            } else {
                communicator.sendSafeServerMessage("Karma command usage:");
                communicator.sendSafeServerMessage("#karma <name> <amount>  -- give an amount of karma to player (use negative number to take instead)");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Karma command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandRotate(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,"rotate")) return MessagePolicy.PASS;
        try {
            Matcher m = rotatePattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null && m.group(2)!=null) {
                    long wurmId = Long.parseLong(m.group(1));
                    Creature creature = Creatures.getInstance().getCreature(wurmId);
                    float rot = Float.parseFloat(m.group(2));
                    creature.setRotation(rot);
                    creature.moved(0,0,0,0,0);
                    communicator.sendSafeServerMessage(StringUtils.format("Set rotatation of %d to %.2f degrees.",wurmId,rot));
                }
            } else {
                communicator.sendSafeServerMessage("Rotate command usage:");
                communicator.sendSafeServerMessage("#rotate <wurmId> <degrees>  -- set rotation of a creature");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Rotate command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandInventory(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,"inventory")) return MessagePolicy.PASS;
        try {
            Matcher m = inventoryPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null && m.group(2)!=null) {
                    String name = m.group(1);
                    Creature creature = Players.getInstance().getPlayerOrNull(com.wurmonline.server.LoginHandler.raiseFirstLetter(name));
                    if(creature==null) {
                        long wurmId = Long.parseLong(name);
                        creature = Creatures.getInstance().getCreature(wurmId);
                    }
                    String cmd = m.group(2);
                    if(cmd.equals("clear")) {
                        Set<Item> items = creature.getInventory().getItems();
                        Item[] itarr = items.toArray(new Item[items.size()]);
                        int i;
                        for(i = 0; i<itarr.length; ++i)
                            Items.destroyItem(itarr[i].getWurmId());
                        communicator.sendSafeServerMessage("Cleared inventory of "+i+" items from "+name+".");
                        return MessagePolicy.DISCARD;
                    } else if(cmd.equals("give") && m.group(3)!=null && m.group(4)!=null) {
                        if(hasPowerForCommand(performer,MiscConstants.POWER_IMPLEMENTOR,"inventory")) {
                            int itemId = Integer.parseInt(m.group(3));
                            float ql = (float)Integer.parseInt(m.group(4));
                            Item item = ItemFactory.createItem(itemId,ql,performer.getName());
                            creature.getInventory().insertItem(item);
                            communicator.sendSafeServerMessage("Created "+item.getName()+" and gave to "+creature.getName()+".");
                            return MessagePolicy.DISCARD;
                        }
                    }
                }
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Inventory command error: "+e.getMessage(),e);
            return MessagePolicy.DISCARD;
        }
        communicator.sendSafeServerMessage("Inventory command usage:");
        communicator.sendSafeServerMessage("#inventory <wurmId> clear  -- destroy all items in the creature's inventory");
        if(performer.getPower() >= MiscConstants.POWER_IMPLEMENTOR) {
            communicator.sendSafeServerMessage("#inventory <wurmId> give <itemId> <qualityLevel>  -- create item and place in creature's inventory");
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandTransfer(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_IMPLEMENTOR,"transfer")) return MessagePolicy.PASS;
        try {
            Matcher m = transferPattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null && m.group(2)!=null && m.group(3)!=null) {
                    String name = m.group(1);
                    Player player = Players.getInstance().getPlayerOrNull(com.wurmonline.server.LoginHandler.raiseFirstLetter(name));
                    if(player==null) {
                        communicator.sendAlertServerMessage("No such player.");
                        return MessagePolicy.DISCARD;
                    }
                    try(Connection con = AwakeningDb.getConnection(m.group(2))) {
                        if(con==null) {
                            communicator.sendAlertServerMessage("No such database file.");
                            return MessagePolicy.DISCARD;
                        }
                        String transferPlayer = m.group(3);
                        long transferId = 0L, money = 0L;
                        int karma = 0;
                        try(PreparedStatement ps = con.prepareStatement("SELECT WURMID,IPADDRESS,MONEY,KARMA FROM PLAYERS WHERE NAME=?")) {
                            ps.setString(1,transferPlayer);
                            try(ResultSet rs = ps.executeQuery()) {
                                if(rs.next()) {
                                    transferId = rs.getLong(1);
                                    money = rs.getLong(3);
                                    karma = rs.getInt(4);
                                }
                            }
                        }
                        if(transferId!=0L) {
                            int n = 0;
                            player.addMoney(money);
                            player.getCommunicator().sendSafeServerMessage("You gain "+new Change(money).getChangeString()+".");
                            player.setKarma(player.getKarma()+karma);
                            player.getCommunicator().sendSafeServerMessage("You gain "+karma+" karma.");
                            try(PreparedStatement ps = con.prepareStatement("SELECT NUMBER,VALUE FROM SKILLS WHERE OWNER=?")) {
                                ps.setLong(1,transferId);
                                try(ResultSet rs = ps.executeQuery()) {
                                    while(rs.next()) {
                                        int number = rs.getInt(1);
                                        double value = rs.getDouble(2);
                                        Skill skill = player.getSkills().getSkillOrLearn(number);
                                        if(skill.getKnowledge()<value) {
                                            skill.setKnowledge(value,false);
                                            ++n;
                                        }
                                    }
                                    if(n>0) {
                                        player.getCommunicator().sendSafeServerMessage("You transfer "+n+" skills.");
                                    }
                                }
                            }
                            communicator.sendAlertServerMessage("You transfer "+player.getName()+", who gained: "+new Change(money).getChangeString()+
                                                                ", "+karma+" karma, and changed "+n+" skills.");
                        }
                    } catch(SQLException e) {
                        logger.log(Level.SEVERE,"Failed to transfer player data: "+e.getMessage(),e);
                    }
                }
            } else {
                communicator.sendSafeServerMessage("Transfer player usage:");
                communicator.sendSafeServerMessage("#transfer <player> <dbFile> <dbPlayer>  -- transfer a player's money, karma and skills from an external db");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Transfer command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandSQL(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,"sql")) return MessagePolicy.PASS;
        if(message.startsWith("#sql ")) {
            String sql = message.substring(5).trim();
            logger.info("Execute SQL: "+sql);
            communicator.sendNormalServerMessage("Execute SQL: "+sql);
            try(Connection con = ModSupportDb.getModSupportDb();
                Statement st = con.createStatement()) {
                st.execute(sql);
                communicator.sendNormalServerMessage("Done");
            } catch(SQLException e) {
                logger.log(Level.SEVERE,"SQL error.",e);
                communicator.sendNormalServerMessage("SQL error (see server log).");
            }
            return MessagePolicy.DISCARD;
        } else if(message.startsWith("#sql-")) {
            int i = message.indexOf(' ');
            if(i >= 0) {
                String db = message.substring(5,i).trim();
                String sql = message.substring(i+1).trim();
                logger.info("Execute SQL: "+sql);
                communicator.sendNormalServerMessage("Execute SQL: "+sql);
                try(Connection con = AwakeningDb.getConnection("sqlite/"+db);
                    Statement st = con.createStatement()) {
                    st.execute(sql);
                    communicator.sendNormalServerMessage("Done");
                } catch(SQLException e) {
                    logger.log(Level.SEVERE,"SQL error.",e);
                    communicator.sendNormalServerMessage("SQL error (see server log).");
                }
                return MessagePolicy.DISCARD;
            }
        }
        communicator.sendAlertServerMessage("Usage: #sql[-<database>] <sql>");
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandSchedule(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        boolean gm = performer.getPower()>=MiscConstants.POWER_DEMIGOD;
        if(!hasPowerForCommand(performer,MiscConstants.POWER_DEMIGOD,true,"schedule")) return MessagePolicy.PASS;
        try {
            Matcher m = schedulePattern.matcher(message);
            if(m.find()) {
                if(m.group(1)!=null) {
                    String command = m.group(1);
                    String name = m.group(2);

                    if(command.equals("list")) {
                        Scheduler.getInstance().sendList(communicator);
                    } else if(command.equals("stop")) {
                        Scheduler.getInstance().stop(communicator,name);
                    }

                } else if(m.group(3)!=null) {
                    String name = m.group(3);
                    int start = Integer.parseInt(m.group(4),10);
                    int delay = Integer.parseInt(m.group(5),10);
                    String s = m.group(6);
                    int color = 0xffffff;
                    if(s.charAt(0)=='#') color = Integer.parseInt(s.substring(1),16);
                    else color = Integer.parseInt(s,10);
                    String text = m.group(7);
                    Scheduler.getInstance().startBroadCast(communicator,name,start,delay,color,text);
                }
                //				communicator.sendSafeServerMessage("1:"+m.group(1)+", 2:"+m.group(2)+", 3:"+m.group(3)+", 4:"+m.group(4)+
                //					", 5:"+m.group(5)+", 6:"+m.group(6)+", 7:"+m.group(7)+", 8:"+m.group(8)+", 9:"+m.group(9)+", 10:"+m.group(10)+
                //					", 11:"+m.group(11)+", 12:"+m.group(12));
            } else {
                communicator.sendSafeServerMessage("Schedule command usage:");
                communicator.sendSafeServerMessage("#schedule list  -- print a list of all active tasks");
                communicator.sendSafeServerMessage("#schedule stop [command or id]  -- stop all tasks, or by command or id");
                communicator.sendSafeServerMessage("#schedule broadcast <id> <start/delay> <color> <message>  -- broadcast message, start=minute of the hour, delay=minutes");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Schedule command error.",e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandTile(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_IMPLEMENTOR,"tile")) return MessagePolicy.PASS;
        try {
            Matcher m = tilePattern.matcher(message);
            if(m.find()) {
                String command = m.group(1).toLowerCase();
                int x = performer.getTileX();
                int y = performer.getTileY();
                final int currTileId = Server.surfaceMesh.getTile(x,y);
                final byte type = Tiles.decodeType(currTileId);
                final byte data = Tiles.decodeData(currTileId);
                if("checkfarmgrowth".equals(command)) {
                    VolaTile tile = Zones.getOrCreateTile(x,y,true);
                    final boolean farmed = Crops.decodeFieldState(data);
                    final boolean growth = net.spirangle.awakening.zones.Tiles.checkForFarmGrowth(tile,farmed);
                    communicator.sendNormalServerMessage("Test checkForFarmGrowth(x="+x+", y="+y+", farmed="+farmed+") = "+growth);
                } else if("checktreesprout".equals(command)) {
                    final boolean sprout = net.spirangle.awakening.zones.Tiles.checkForTreeSprout(x,y,type,data);
                    communicator.sendNormalServerMessage("Test checkForTreeSprout(x="+x+", y="+y+") = "+sprout);
                } else {
                    communicator.sendNormalServerMessage("Unknown tile command: "+command);
                }
            } else {
                communicator.sendAlertServerMessage("Usage: #tile <checkFarmGrowth>");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Tile command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandSetResource(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_IMPLEMENTOR,"setresource")) return MessagePolicy.PASS;
        try {
            Matcher m = setResourcePattern.matcher(message);
            if(m.find()) {
                String resource = m.group(1).toLowerCase();
                int type;
                switch(resource) {
                    case "copper":
                        type = Tile.TILE_CAVE_WALL_ORE_COPPER.id;
                        break;
                    case "gold":
                        type = Tile.TILE_CAVE_WALL_ORE_GOLD.id;
                        break;
                    case "sandstone":
                        type = Tile.TILE_CAVE_WALL_SANDSTONE.id;
                        break;
                    case "silver":
                        type = Tile.TILE_CAVE_WALL_ORE_SILVER.id;
                        break;
                    case "slate":
                        type = Tile.TILE_CAVE_WALL_SLATE.id;
                        break;
                    default:
                        communicator.sendNormalServerMessage("This is not a supported resource.");
                        return MessagePolicy.DISCARD;
                }

                int l = Integer.parseInt(m.group(2));
                int t = Integer.parseInt(m.group(3));
                int r = Integer.parseInt(m.group(4));
                int b = Integer.parseInt(m.group(5));
                int min = Integer.parseInt(m.group(6));
                int max = min;
                if(m.group(7)!=null)
                    max = Integer.parseInt(m.group(7));
                if(min<1 || min>0xffff) {
                    communicator.sendAlertServerMessage("Minimum number value out of range");
                } else if(max<min || max<1 || max>0xffff) {
                    communicator.sendAlertServerMessage("Maximum number value out of range");
                } else {

                    logger.info("#setresource by "+performer.getName()+", resource="+resource+", min="+min+", max="+max);
                    communicator.sendNormalServerMessage("Setting resource  ["+(max==min? ""+min : min+"<=>"+max)+"]...");

                    int x, y, n = 0;
                    int w = Zones.worldTileSizeX;
                    int h = Zones.worldTileSizeY;
                    if(l>r) {
                        x = r;
                        r = l;
                        l = x;
                    }
                    if(t>b) {
                        y = b;
                        b = t;
                        t = y;
                    }
                    if(l<0) l = 0;
                    if(t<0) t = 0;
                    if(r >= w) r = w-1;
                    if(b >= h) b = h-1;
                    for(x = l; x<=r; ++x)
                        for(y = t; y<=b; ++y) {
                            if(Tiles.decodeType(Server.caveMesh.getTile(x,y))==type) {
                                //			            	res = Server.getCaveResource(x,y);
                                Server.setCaveResource(x,y,min+(max>min? Server.rand.nextInt(max-min) : 0));
                                ++n;
                            }
                        }

                    communicator.sendNormalServerMessage("Updated resource value for "+n+" "+resource+" tiles.");
                }
            } else {
                communicator.sendAlertServerMessage("Usage: #setresource <resource> <startx> <starty> <endx> <endy> <minimum> [maximum]");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Set resource command error: "+e.getMessage(),e);
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandFlowersFix(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        if(!hasPowerForCommand(performer,MiscConstants.POWER_IMPLEMENTOR,"flowersfix")) return MessagePolicy.PASS;

        logger.info("#flowersfix by "+performer.getName());
        communicator.sendNormalServerMessage("Fixing flowers...");

        int n = net.spirangle.awakening.zones.Tiles.fixFlowers(0,0,Zones.worldTileSizeX-1,Zones.worldTileSizeY-1);
        if(n >= 0) {
            communicator.sendNormalServerMessage("Updated flowers for "+n+" tiles.");
        }
        return MessagePolicy.DISCARD;
    }

    public boolean reloadConfig(Player performer,String mod) {
        Path path = Paths.get("mods/"+mod+".properties");
        if(!Files.exists(path)) {
            performer.getCommunicator().sendAlertServerMessage("The config file seems to be missing.");
            return true;
        }
        InputStream stream = null;
        try {
            performer.getCommunicator().sendAlertServerMessage("Opening the config file.");
            stream = Files.newInputStream(path);
            Properties properties = new Properties();
            performer.getCommunicator().sendAlertServerMessage("Reading from the config file.");
            properties.load(stream);
            logger.info("Reloading configuration.");
            performer.getCommunicator().sendAlertServerMessage("Loading all options.");
            Config.getInstance().configure(properties);
            logger.info("Configuration reloaded.");
            performer.getCommunicator().sendAlertServerMessage("The config file has been reloaded.");
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Error while reloading properties file.",e);
            performer.getCommunicator().sendAlertServerMessage("Error reloading the config file, check the server log.");
        } finally {
            try {
                if(stream!=null) stream.close();
            } catch(Exception e) {
                logger.log(Level.SEVERE,"Properties file not closed, possible file lock.",e);
                performer.getCommunicator().sendAlertServerMessage("Error closing the config file, possible file lock.");
            }
        }
        return true;
    }

    public MessagePolicy handleCommandLag(Communicator communicator,String message) {
        if(serverLag==null) {
            try {
                serverLag = Server.class.getDeclaredField("secondsLag");
                serverLag.setAccessible(true);
            } catch(NoSuchFieldException e) {
                logger.log(Level.SEVERE,"Could not retrieve towers field in class Kingdoms: "+e.getMessage(),e);
            }
        }
        if(serverLag!=null) {
            Player performer = communicator.getPlayer();
            try {
                int lag = (int)serverLag.get(null);
                performer.getCommunicator().sendSafeServerMessage("Total lag in seconds since server start: "+lag);
            } catch(IllegalAccessException e) {}
        }
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandBank(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        long money = performer.getMoney();
        performer.getCommunicator().sendSafeServerMessage("Your bank account currently contains: "+new Change(money).getChangeString());
        return MessagePolicy.DISCARD;
    }

    public MessagePolicy handleCommandPvPList(Communicator communicator,String message) {
        Player performer = communicator.getPlayer();
        net.spirangle.awakening.players.LoginHandler.sendPvPList(performer);
        return MessagePolicy.DISCARD;
    }

    private boolean sendAltPlayerInfo(Communicator communicator,PlayerInfo pi,long steamId) {
        try {
            pi.load();
            String text = pi.getName()+" [IP: "+pi.getIpaddress()+", Steam ID: "+
                          (steamId!=-10L? steamId :(pi.getSteamId()!=null? pi.getSteamId().getSteamID64() : "missing"))+"]";
            Player player = Players.getInstance().getPlayerOrNull(pi.wurmId);
            if(player!=null) text += " - online";
            else text += " - last seen: "+dateFormat.format(new Date(pi.lastLogout));
            communicator.sendSafeServerMessage(text);
            return true;
        } catch(IOException ioe) {}
        return false;
    }
}
