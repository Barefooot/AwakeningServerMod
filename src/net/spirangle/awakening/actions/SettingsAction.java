package net.spirangle.awakening.actions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.SettingsQuestion;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SettingsAction extends AwakeningAction {

    private static final Logger logger = Logger.getLogger(SettingsAction.class.getName());

    public SettingsAction() {
        super("Settings","updating extended game settings",new int[]{ 0 });
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
    public List<ActionEntry> getBehavioursFor(Creature performer,Creature target) {
        return this.getBehavioursFor(performer,null,target);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item subject,Creature target) {
        if(performer instanceof Player && target!=null && target instanceof Player && performer.getPower() >= MiscConstants.POWER_DEMIGOD) {
            return getActionEntryList();
        }
        return null;
    }

    @Override
    public boolean action(Action act,Creature performer,Item target,short action,float counter) {
        return this.action(act,performer,null,target,action,counter);
    }

    @Override
    public boolean action(Action act,Creature performer,Item source,Item target,short action,float counter) {
        if(performer.isPlayer()) {
            SettingsQuestion question = new SettingsQuestion((Player)performer);
            question.sendQuestion();
        }
        return true;
    }

    @Override
    public boolean action(Action action,Creature performer,Creature target,short num,float counter) {
        return this.action(action,performer,null,target,num,counter);
    }

    @Override
    public boolean action(Action action,Creature performer,Item source,Creature target,short num,float counter) {
        if(performer.isPlayer() && performer.getPower() >= MiscConstants.POWER_DEMIGOD) {
            try {
                SettingsQuestion question = new SettingsQuestion((Player)performer,(Player)target);
                question.sendQuestion();
            } catch(Throwable e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                performer.getCommunicator().sendAlertServerMessage("Something went wrong, please report to a GM.");
            }
        }
        return true;
    }
}
