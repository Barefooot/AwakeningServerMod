package net.spirangle.awakening.actions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.LeaderBoardQuestion;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LeaderBoardAction extends AwakeningAction {

    private static final Logger logger = Logger.getLogger(LeaderBoardAction.class.getName());

    public LeaderBoardAction() {
        super("Leaderboard","showing leaderboard",new int[]{ 0 });
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item source,Item object) {
        return this.getBehavioursFor(performer,object);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item object) {
        if(performer instanceof Player && object!=null && (object.getTemplateId()==ItemList.bodyBody || object.getTemplateId()==ItemList.bodyHand)) {
            return getActionEntryList();
        }
        return null;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item subject,Skill skill) {
        return this.getBehavioursFor(performer,skill);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Skill skill) {
        if(performer instanceof Player && skill!=null &&
           (skill.getNumber() >= SkillList.BODY && skill.getNumber()<=SkillList.RESTORATION)) {
            return getActionEntryList();
        }
        return null;
    }

    @Override
    public boolean action(Action act,Creature performer,Item target,short action,float counter) {
        showLeaderBoard(performer,null);
        return true;
    }

    @Override
    public boolean action(Action act,Creature performer,Item source,Item target,short action,float counter) {
        showLeaderBoard(performer,null);
        return true;
    }

    @Override
    public boolean action(Action act,Creature performer,Skill skill,short action,float counter) {
        showLeaderBoard(performer,skill);
        return true;
    }

    @Override
    public boolean action(Action act,Creature performer,Item source,Skill skill,short action,float counter) {
        showLeaderBoard(performer,skill);
        return true;
    }

    private void showLeaderBoard(Creature performer,Skill skill) {
        if(performer.isPlayer()) {
            try {
                LeaderBoardQuestion question = new LeaderBoardQuestion(performer,skill);
                question.sendQuestion();
            } catch(Throwable e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                performer.getCommunicator().sendAlertServerMessage("Something went wrong, please report to a GM.");
            }
        }
    }
}
