package com.wurmonline.server.questions;

import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.shared.util.StringUtilities;
import net.spirangle.awakening.AwakeningDb;
import net.spirangle.awakening.players.LeaderBoard;
import net.spirangle.awakening.players.PlayerData;
import net.spirangle.awakening.players.PlayersData;
import net.spirangle.awakening.util.Cache;
import net.spirangle.awakening.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.spirangle.awakening.AwakeningConstants.LEADERBOARD_QUESTION_TYPE;


public class LeaderBoardQuestion extends Question {

    private static final Logger logger = Logger.getLogger(LeaderBoardQuestion.class.getName());

    private class TopTenPlayer {
        long wurmId;
        int score;
        String name;
        int kingdomId;
        int skillId;
        long lastLogout;
    }

    private Skill skill;
    private long board;

    public LeaderBoardQuestion(Creature responder,Skill skill) {
        super(responder,"Leaderboard","",LEADERBOARD_QUESTION_TYPE,responder.getWurmId());
        this.skill = skill;
        this.board = skill==null? 0 : skill.getNumber();
    }

    @Override
    public void sendQuestion() {
        Player responder = (Player)this.getResponder();
        StringBuilder bml = new StringBuilder();
        int w = 450;
        int h = 350;
        long lastLogoutAfter = System.currentTimeMillis()-(2L*30L*24L*60L*60L*1000L);
        try {
            if(this.board==0) {
                bml.append("border{\n")
                   .append(" varray{rescale='true';\n")
                   .append("  text{type='bold';text=\"Top ten players:\"}\n")
                   .append(" };\n")
                   .append(" null;\n")
                   .append(" scroll{horizontal='false';vertical='true';\n")
                   .append("  varray{rescale='true';\n")
                   .append("   passthrough{id='id';text='").append(getId()).append("'};\n")
                   .append("   text{text=''}\n")
                   .append("   table{cols='5';\n")
                   .append("    label{type='bold';text='Rank'};\n")
                   .append("    label{type='bold';text='Score'};\n")
                   .append("    label{type='bold';text='Name'};\n")
                   .append("    label{type='bold';text='Kingdom'};\n")
                   .append("    label{type='bold';text='Top Skill'};\n");

                List<TopTenPlayer> list = (List<TopTenPlayer>)Cache.getInstance().get("leaderboard.top.ten");
                TopTenPlayer ttp;
                if(list==null) {
                    list = new ArrayList<>(10);
                    Connection db = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        db = DbConnector.getPlayerDbCon();
                        ps = db.prepareStatement("SELECT P.WURMID,(SELECT SUM(VALUE) FROM (SELECT S.VALUE FROM SKILLS S WHERE S.OWNER=P.WURMID ORDER BY S.VALUE DESC LIMIT 10)) AS SCORE,"+
                                                 "P.NAME,P.KINGDOM,(SELECT S.NUMBER FROM SKILLS S WHERE S.OWNER=P.WURMID ORDER BY S.VALUE DESC LIMIT 1) AS SKILL,P.LASTLOGOUT "+
                                                 "FROM PLAYERS P WHERE P.POWER<=0 AND P.LASTLOGOUT>=? ORDER BY SCORE DESC LIMIT 10");
                        ps.setLong(1,lastLogoutAfter);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                            ttp = new TopTenPlayer();
                            ttp.wurmId = rs.getLong(1);
                            ttp.score = rs.getInt(2);
                            ttp.name = StringUtils.bmlString(rs.getString(3));
                            ttp.kingdomId = rs.getInt(4);
                            ttp.skillId = rs.getInt(5);
                            ttp.lastLogout = rs.getLong(6);
                            list.add(ttp);
                        }
                    } catch(SQLException e) {
                    } finally {
                        DbUtilities.closeDatabaseObjects(ps,rs);
                        DbConnector.returnConnection(db);
                    }
                    Cache.getInstance().put("leaderboard.top.ten",list,3600L);
                }
                String name, color, kingdomName;
                Kingdom kingdom;
                Skill skill;
                PlayerData pd;
                LeaderBoard lb;
                boolean hide, rankedHidden = false;
                for(int rank = 1; rank<=list.size(); ++rank) {
                    ttp = list.get(rank-1);
                    pd = PlayersData.getInstance().get(ttp.wurmId);
                    if(pd==null) hide = false;
                    else {
                        lb = pd.getLeaderBoard();
                        hide = lb.isHidden(this.board);
                    }
                    name = hide? (ttp.wurmId==responder.getWurmId()? "*"+ttp.name : "<hidden>") : ttp.name;
                    if(hide && ttp.wurmId==responder.getWurmId()) rankedHidden = true;
                    kingdom = ttp.kingdomId>0? Kingdoms.getKingdomOrNull((byte)ttp.kingdomId) : null;
                    kingdomName = kingdom!=null? (!hide || ttp.wurmId==responder.getWurmId()? StringUtils.bmlString(kingdom.getName()) : "<hidden>") : "-";
                    color = ttp.wurmId==responder.getWurmId()? "color='127,255,127';" : "";
                    try {
                        skill = responder.getSkills().getSkill(ttp.skillId);
                    } catch(NoSuchSkillException e) {
                        skill = null;
                    }
                    bml.append("    label{").append(color).append("text='#").append(rank).append("'};\n")
                       .append("    label{").append(color).append("text='").append(StringUtils.decimalFormat.format((double)ttp.score/10.0d)).append("'};\n")
                       .append("    label{").append(color).append("text=\"").append(name).append("\"};\n")
                       .append("    label{").append(color).append("text=\"").append(kingdomName).append("\"};\n")
                       .append("    label{").append(color).append("text=\"").append(skill!=null? StringUtils.bmlString(skill.getName()) : "-").append("\"};\n");
                }
                bml.append("   }\n")
                   .append("   text{text=''}\n")
                   .append("   text{text=\"The top ten leaderboard lists the heroes on the server with the highest average score of their top ten skills. The skill with the highest score for each player is listed to the right.\"}\n");
                if(rankedHidden) {
                    bml.append("   text{text=''}\n")
                       .append("   text{color='127,255,127';text=\"*Your name is visible to you but not to other players.\"}\n");
                }
                bml.append("  }\n")
                   .append(" };\n");
            } else if(this.skill!=null) {
                int rank = 0,realRank = 0,playerRank = 0;
                boolean rankedHidden = false;
                bml.append("border{\n")
                   .append(" varray{rescale='true';\n")
                   .append("  text{type='bold';text=\"Leaderboard for ").append(this.skill.getName()).append(":\"}\n")
                   .append(" };\n")
                   .append(" null;\n")
                   .append(" scroll{horizontal='false';vertical='true';\n")
                   .append("  varray{rescale='true';\n")
                   .append("   passthrough{id='id';text='").append(getId()).append("'};\n")
                   .append("   text{text=''}\n");

                Connection con = null;
                try {
                    String db = "";
                    if(this.board >= SkillList.BODY && this.board<=SkillList.RESTORATION) {
                        con = AwakeningDb.getConnection("sqlite/wurmplayers");
                    } else {
                        return;
                    }
                    try(PreparedStatement ps = con.prepareStatement("SELECT P.WURMID,S.VALUE,P.NAME,P.KINGDOM,P.LASTLOGOUT FROM SKILLS S INNER JOIN "+db+"PLAYERS P ON P.WURMID=S.OWNER "+
                                                                    "WHERE S.NUMBER=? AND P.POWER<=0 AND P.LASTLOGOUT>=? ORDER BY S.VALUE DESC LIMIT 100")) {
                        ps.setLong(1,this.board);
                        ps.setLong(2,lastLogoutAfter);
                        try(ResultSet rs = ps.executeQuery()) {
                            long wurmId, lastLogout;
                            double skill;
                            int kingdomId;
                            String name, color, kingdomName;
                            Kingdom kingdom;
                            PlayerData pd;
                            LeaderBoard lb;
                            boolean hide;
                            while(rs.next()) {
                                ++realRank;
                                wurmId = rs.getLong(1);
                                if(wurmId==responder.getWurmId()) playerRank = realRank;
                                if(rank >= 10) {
                                    if(playerRank!=0) break;
                                    continue;
                                }
                                pd = PlayersData.getInstance().get(wurmId);
                                if(pd==null) hide = false;
                                else {
                                    lb = pd.getLeaderBoard();
                                    hide = lb.isHidden(this.board);
                                }
                                if(hide) continue;
                                ++rank;
                                skill = rs.getDouble(2);
                                name = StringUtils.bmlString(rs.getString(3));
                                name = hide? (wurmId==responder.getWurmId()? "*"+name : "<hidden>") : name;
                                if(hide && wurmId==responder.getWurmId()) rankedHidden = true;
                                kingdomId = rs.getInt(4);
                                kingdom = kingdomId>0? Kingdoms.getKingdomOrNull((byte)kingdomId) : null;
                                kingdomName = kingdom!=null? (!hide || wurmId==responder.getWurmId()? StringUtils.bmlString(kingdom.getName()) : "<hidden>") : "-";
                                lastLogout = rs.getLong(5);
                                color = wurmId==responder.getWurmId()? "color='127,255,127';" : "";
                                if(rank==1) {
                                    bml.append("   table{cols='4';\n")
                                       .append("    label{type='bold';text='Rank'};\n")
                                       .append("    label{type='bold';text='Skill'};\n")
                                       .append("    label{type='bold';text='Name'};\n")
                                       .append("    label{type='bold';text='Kingdom'};\n");
                                }
                                bml.append("    label{").append(color).append("text='#").append(rank).append("(").append(realRank).append(")'};\n")
                                   .append("    label{").append(color).append("text='").append(StringUtils.decimalFormat.format(skill)).append("'};\n")
                                   .append("    label{").append(color).append("text=\"").append(name).append("\"};\n")
                                   .append("    label{").append(color).append("text=\"").append(kingdomName).append("\"};\n");
                            }
                        }
                    }
                } finally {
                    if(con!=null) con.close();
                }
                if(rank==0) {
                    bml.append("   text{text=\"There are no heroes yet who have attained any skill in this field.\"}");
                } else {
                    bml.append("   }\n")
                       .append("   text{text=''}\n")
                       .append("   text{text=\"This list shows the ").append(StringUtilities.getWordForNumber(rank))
                       .append(" heroes with the highest ").append(this.skill.getName())
                       .append(" skill, among non-hidden players (number in parenthesis is the rank including hidden players).");
                    if(playerRank!=0) {
                        bml.append(" Your rank among the top 100 players is #").append(playerRank).append(".");
                    }
                    bml.append("\"}\n");
                    if(rankedHidden) {
                        bml.append("   text{text=''}\n")
                           .append("   text{color='127,255,127';text=\"*Your name is visible to you but not to other players.\"}\n");
                    }
                }
                bml.append("  }\n")
                   .append(" };\n");
            }
            boolean hide = PlayersData.getInstance().getLeaderBoard(responder).isHidden(this.board);
            bml.append(" null;\n")
               .append(" right{\n")
               .append("  harray{\n")
               .append("   checkbox{id='hide';size='").append(w-6-160).append(",1';").append(hide? "selected='true'" : "").append("text=\"Hide my name in this leaderboard\"}\n")
               .append("   button{id='cancel';size='80,20';text='Cancel'}\n")
               .append("   button{id='submit';size='80,20';text='Update'}\n")
               .append("  }\n")
               .append(" }\n")
               .append("}\n");
            responder.getCommunicator().sendBml(w,h,false,true,bml.toString(),200,200,200,this.title);
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to load leaderboard: "+e.getMessage(),e);
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Failed to show leaderboard: "+e.getMessage(),e);
        }
    }

    @Override
    public void answer(Properties properties) {
        Creature responder = this.getResponder();

        if("true".equals(properties.getProperty("cancel"))) return;

        if(responder.isPlayer()) {
            LeaderBoard lb = PlayersData.getInstance().getLeaderBoard((Player)responder);
            boolean hide = "true".equals(properties.getProperty("hide"));
            if(lb.setHidden(this.board,hide)) {
                responder.getCommunicator().sendNormalServerMessage("Your settings has been updated.");
            }
            LeaderBoardQuestion question = new LeaderBoardQuestion(responder,skill);
            question.sendQuestion();
        }
    }
}
