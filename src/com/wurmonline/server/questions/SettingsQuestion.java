package com.wurmonline.server.questions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import net.spirangle.awakening.Config;
import net.spirangle.awakening.players.LeaderBoard;
import net.spirangle.awakening.players.PlayerData;
import net.spirangle.awakening.players.PlayersData;
import net.spirangle.awakening.util.StringUtils;

import java.util.Properties;

import static net.spirangle.awakening.AwakeningConstants.SETTINGS_QUESTION_TYPE;


public class SettingsQuestion extends Question {
    private Player target;

    public SettingsQuestion(Player responder) {
        this(responder,null);
    }

    public SettingsQuestion(Player responder,Player target) {
        super(responder,"Settings","",SETTINGS_QUESTION_TYPE,responder.getWurmId());
        this.target = target;
    }

    @Override
    public void sendQuestion() {
        Player responder = (Player)getResponder();
        Player player = target!=null && responder.getPower() >= MiscConstants.POWER_DEMIGOD? target : responder;
        StringBuilder bml = new StringBuilder();
        int w = 450;
        int h = 350;
        String name = StringUtils.bmlString(player.getName());
        PlayerData pd = PlayersData.getInstance().get(player);
        LeaderBoard lb = pd.getLeaderBoard();
        bml.append("border{\n")
           .append(" varray{rescale='true';\n")
           .append("  text{type='bold';text=\"Player settings for ").append(name).append(":\"}\n")
           .append(" };\n")
           .append(" null;\n")
           .append(" scroll{horizontal='false';vertical='true';\n")
           .append("  varray{rescale='true';\n")
           .append("   passthrough{id='id';text='").append(getId()).append("'};\n")
           .append("   text{text=''}\n");
        if(Servers.isThisAPvpServer()) {
            if(pd.isPvP())
                bml.append("   text{type='bold';text=\"You have the PvP settings checked\";hover=\"This means you are playing with PvP rules all the time. Only a GM can remove this setting.\"}\n");
            else
                bml.append("   checkbox{id='pvp';text=\"Play with PvP settings (cannot uncheck once set)\";hover=\"This setting will say to other players that you play by PvP rules all the time.\"}\n");
        }
        if(Config.useLeaderBoard) {
            bml.append("   checkbox{id='lbhide';").append(lb.isHidden()? "selected='true'" : "").append("text=\"Hide my name on leaderboards\";hover=\"Individual settings on each leaderboard overrides this setting.\"}\n");
        }
        Village village = Villages.getVillageForCreature(player);
        if(village!=null && village.isMayor(player)) {
            bml.append("   text{text=''}\n")
               .append("   text{type='bold';text='Being mayor of a village, you have extra settings:'}\n");
            if(Config.useFarmGrowthWhenTended) {
                bml.append("   checkbox{id='deedfarm';").append(pd.isDeedFarming()? "selected='true'" : "").append("text=\"Farm growth only when tended\";hover=\"Farm tiles on deed will only grow when they are tended.\"}\n");
            }
            bml.append("   harray{\n")
               .append("    text{size='120,17';text='Deed visibility:'};\n")
               .append("    dropdown{id='deedvis';size='250,15';default='").append(pd.getDeedVisibility()).append("';options=\"show deed on map viewer,hide name,hide name and border,hide terraforming to perimiter\";hover=\"How to display your deed on the map viewer, on the web page.\"}\n")
               .append("   }");
        }
        if(responder.getPower() >= MiscConstants.POWER_DEMIGOD && player.getWurmId()!=responder.getWurmId()) {
            bml.append("   text{text=''}\n")
               .append("   text{text=\"GM Settings:\"}\n")
               .append("   checkbox{id='pa';").append(player.isPlayerAssistant()? "selected='true'" : "").append("text=\"Player Assistant\"}\n");
        }
        bml.append("  }\n")
           .append(" };\n")
           .append(" null;\n")
           .append(" right{\n")
           .append("  harray{\n")
           .append("   button{id='cancel';size='80,20';text='Cancel'}\n")
           .append("   button{id='submit';size='80,20';text='Save'}\n")
           .append("  }\n")
           .append(" }\n")
           .append("}\n");

        responder.getCommunicator().sendBml(w,h,false,true,bml.toString(),200,200,200,this.title);
    }

    @Override
    public void answer(Properties properties) {
        Player responder = (Player)getResponder();

        if("true".equals(properties.getProperty("cancel"))) return;

        Player player = target!=null && responder.getPower() >= MiscConstants.POWER_DEMIGOD? target : responder;
        boolean changed = false;
        PlayerData pd = PlayersData.getInstance().get(player);
        if(Servers.isThisAPvpServer()) {
            if("true".equals(properties.getProperty("pvp","false"))) changed = pd.setPvP(true) || changed;
        }
        if(Config.useLeaderBoard) {
            LeaderBoard lb = pd.getLeaderBoard();
            changed = lb.setHidden("true".equals(properties.getProperty("lbhide","false"))) || changed;
        }
        if(Config.useFarmGrowthWhenTended) {
            changed = pd.setNoDeedFarming(!"true".equals(properties.getProperty("deedfarm","true"))) || changed;
        }
        if(properties.containsKey("deedvis")) {
            changed = pd.setDeedVisibility(Integer.parseInt(properties.getProperty("deedvis"))) || changed;
        }
        if(changed) {
            responder.getCommunicator().sendNormalServerMessage((player==responder? "Your settings" : "The settings of "+target.getName())+" have been updated.");
        }
        if(responder.getPower() >= MiscConstants.POWER_DEMIGOD && player.getWurmId()!=responder.getWurmId()) {
            boolean pa = "true".equals(properties.getProperty("pa","false"));
            if(pa!=player.isPlayerAssistant()) {
                player.setPlayerAssistant(pa);
                if(pa) {
                    player.getCommunicator().sendSafeServerMessage("You are now a Community Assistant. New players may ask you questions.");
                    player.getCommunicator().sendSafeServerMessage("The suggested way to approach new players is not to approach them directly but instead let them ask questions.");
                } else {
                    player.getCommunicator().sendSafeServerMessage("You are no longer a Community Assistant.");
                }
            }
        }
    }
}
