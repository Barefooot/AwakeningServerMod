package com.wurmonline.server.questions;

import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;
import net.spirangle.awakening.Config;
import net.spirangle.awakening.actions.ActionStack.Pattern;

import java.util.Properties;
import java.util.logging.Logger;

import static net.spirangle.awakening.AwakeningConstants.ROBOT_QUESTION_TYPE;

public class RobotQuestion extends Question {

    private static final Logger logger = Logger.getLogger(RobotQuestion.class.getName());

    private Pattern pattern;
    private boolean patternFound;

    public RobotQuestion(Player responder,Pattern pattern,boolean patternFound) {
        super(responder,"Robot check","",ROBOT_QUESTION_TYPE,responder.getWurmId());
        this.pattern = pattern;
        this.patternFound = patternFound;
    }

    @Override
    public void sendQuestion() {
        if(this.pattern==null) return;
        Player responder = (Player)getResponder();
        StringBuilder bml = new StringBuilder();
        int w = 400;
        int h = 300;
        float x = 0.25f+Server.rand.nextFloat()*0.5f;
        float y = 0.25f+Server.rand.nextFloat()*0.5f;
        bml.append("border{\n")
           .append(" null;\n")
           .append(" null;\n")
           .append(" varray{rescale='true';\n")
           .append("  passthrough{id='id';text='").append(getId()).append("'};\n")
           .append("  text{text=\"Are you a robot?\"}\n")
           .append("  text{text=''}\n")
           .append("  radio{group='robot';id='yes';text='Yes';selected='true'}\n")
           .append("  radio{group='robot';id='no';text='No'}\n");
        bml.append("  text{text=''}")
           .append("  text{color='255,127,127';text=\"To prevent macro cheating, you are being sent this question. You are not suspected of cheating, "+
                   "and this is only a routine check sent randomly to all players. Please make sure to answer 'no' to this question "+
                   "before doing anything else. You can also wait "+Config.antiMacroRobotPunishTime+" minutes without doing anything, and "+
                   "then you can continue as usual.\"}")
           .append("  text{text=''}");
        bml.append(" };\n")
           .append(" null;\n")
           .append(" right{\n")
           .append("  harray{\n")
           .append("   button{id='submit';size='80,20';text='Ok'}\n")
           .append("  }\n")
           .append(" }\n")
           .append("}\n");
        responder.getCommunicator().sendBml(w,h,x,y,false,true,bml.toString(),200,200,200,this.title);
    }

    @Override
    public void answer(Properties properties) {
        if(pattern==null) return;
        Player responder = (Player)getResponder();
        String robot = properties.getProperty("robot");
        if("no".equals(robot)) {
            pattern.sendClearedMessage(responder,"answered the question in time");
            pattern.clear();
        } else if("yes".equals(robot)) {
            responder.getCommunicator().sendAlertServerMessage("You responded \"yes\", which means you failed the robot test. "+
                                                               "The only way to safely continue now is to wait "+Config.antiMacroRobotPunishTime+
                                                               " minutes, after which you can continue.");
        }
    }
}
