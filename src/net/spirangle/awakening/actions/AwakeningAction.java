package net.spirangle.awakening.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AwakeningAction implements ModAction, BehaviourProvider, ActionPerformer {

    private static final Logger logger = Logger.getLogger(AwakeningAction.class.getName());

    private final short actionId;
    private final ActionEntry actionEntry;
    private final List<ActionEntry> actionEntryList;

    public AwakeningAction(String actionString,String verb,int[] types) {
        this(actionString,verb,types,2);
    }

    public AwakeningAction(String actionString,String verb,int[] types,int maxRange) {
        actionId = (short)ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionId,actionString,verb,types);
        if(maxRange!=2) {
            try {
                ReflectionUtil.setPrivateField(actionEntry,ReflectionUtil.getField(actionEntry.getClass(),"maxRange"),4);
            } catch(NoSuchFieldException|IllegalAccessException e) {
                logger.log(Level.SEVERE,"Edit action entry "+actionString+": "+e.getMessage(),e);
            }
        }
        actionEntryList = Arrays.asList(actionEntry);
        ModActions.registerAction(actionEntry);
        logger.info(this.getClass().getName()+": "+actionId);
    }

    @Override
    public short getActionId() { return actionId; }

    @Override
    public BehaviourProvider getBehaviourProvider() { return this; }

    @Override
    public ActionPerformer getActionPerformer() { return this; }

    protected ActionEntry getActionEntry() {
        return actionEntry;
    }

    protected List<ActionEntry> getActionEntryList() {
        return actionEntryList;
    }
}
