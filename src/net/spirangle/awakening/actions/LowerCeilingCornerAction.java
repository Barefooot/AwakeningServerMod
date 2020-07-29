package net.spirangle.awakening.actions;

import com.wurmonline.mesh.CaveTile;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.Tiles.TileBorderDirection;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.zones.Zones;

import java.util.List;
import java.util.logging.Logger;

public class LowerCeilingCornerAction extends AwakeningAction {

    private static final Logger logger = Logger.getLogger(LowerCeilingCornerAction.class.getName());

    public LowerCeilingCornerAction() {
        // 4=ACTION_TYPE_FATIGUE, 5=ACTION_TYPE_POLICED
        super("Lower ceiling corner","lowering ceiling",new int[]{ 4,5 },4);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item object) {
        if(performer instanceof Player && object!=null && object.getTemplateId()==ItemList.concrete) {
            if(!performer.isOnSurface()) return getActionEntryList();
        }
        return null;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item subject,Item object) {
        return getBehavioursFor(performer,object);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item object,int tilex,int tiley,boolean onSurface,int tile) {
        if(!onSurface && performer instanceof Player && object!=null && object.getTemplateId()==ItemList.concrete) {
            return getActionEntryList();
        }
        return null;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item object,int tilex,int tiley,boolean onSurface,int tile,int dir) {
        return this.getBehavioursFor(performer,object,tilex,tiley,onSurface,tile);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item object,int tilex,int tiley,boolean onSurface,TileBorderDirection dir,boolean border,int heightOffset) {
        if(!onSurface && !border) {
            return this.getBehavioursFor(performer,object,tilex,tiley,onSurface,0);
        }
        return null;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item object,int tilex,int tiley,boolean onSurface,boolean corner,int tile,int heightOffset) {
        return this.getBehavioursFor(performer,object,tilex,tiley,onSurface,tile);
    }

    @Override
    public boolean action(Action action,Creature performer,Item target,short num,float counter) {
        return lowerCeilingCorner(action,performer,target,false,num,counter);
    }

    @Override
    public boolean action(Action action,Creature performer,Item source,int tilex,int tiley,boolean onSurface,int heightOffset,int tile,short num,float counter) {
        return lowerCeilingCorner(action,performer,source,onSurface,num,counter);
    }

    @Override
    public boolean action(Action action,Creature performer,Item source,int tilex,int tiley,boolean onSurface,int heightOffset,int tile,int dir,short num,float counter) {
        return lowerCeilingCorner(action,performer,source,onSurface,num,counter);
    }

    @Override
    public boolean action(Action action,Creature performer,Item source,int tilex,int tiley,boolean onSurface,boolean corner,int tile,int heightOffset,short num,float counter) {
        return lowerCeilingCorner(action,performer,source,onSurface,num,counter);
    }

    @Override
    public boolean action(Action action,Creature performer,Item source,int tilex,int tiley,boolean onSurface,int heightOffset,TileBorderDirection dir,long borderId,short num,float counter) {
        return lowerCeilingCorner(action,performer,source,onSurface,num,counter);
    }

    private boolean lowerCeilingCorner(Action action,Creature performer,Item source,boolean onSurface,short num,float counter) {
        if(!onSurface && performer instanceof Player && source!=null && source.getTemplateId()==ItemList.concrete) {
            if(!performer.isOnSurface()) return lowerCeilingCorner(action,performer,source,num,counter);
        }
        return true;
    }

    private boolean lowerCeilingCorner(Action action,Creature performer,Item source,short num,float counter) {
        final int tilex = (int)performer.getStatus().getPositionX()+2 >> 2;
        final int tiley = (int)performer.getStatus().getPositionY()+2 >> 2;
        if(!GeneralUtilities.isValidTileLocation(tilex,tiley) || performer.isOnSurface() || performer.getLayer() >= 0) {
            performer.getCommunicator().sendNormalServerMessage("The ceiling can not be lowered here.");
            return true;
        }
        int tile = Server.caveMesh.getTile(tilex,tiley);
        final short h = Tiles.decodeHeight(tile);
        final int h2 = CaveTile.decodeCeilingHeight(tile);
        if(h2<=20 || (h<0 && h2+h<=20)) {
            performer.getCommunicator().sendNormalServerMessage("The ceiling is too low.");
            return true;
        }
        if(counter==1.0f || counter==0.0f || action.justTickedSecond()) {
            if(performer.getCurrentTile().getStructure()!=null) {
                performer.getCommunicator().sendNormalServerMessage("This cannot be done in buildings.");
                return true;
            }
            if(Zones.protectedTiles[tilex][tiley]) {
                performer.getCommunicator().sendNormalServerMessage("For some strange reason you can't bring yourself to change this place.");
                return true;
            }
            if(performer.getFloorLevel()>0) {
                performer.getCommunicator().sendNormalServerMessage("You must be standing on the ground in order to do this!");
                return true;
            }
            if(Zones.isTileCornerProtected(tilex,tiley)) {
                performer.getCommunicator().sendNormalServerMessage("This tile is protected by the gods. You can not lower the ceiling here.");
                return true;
            }
            if(source.getWeightGrams()<source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("The "+source.getName()+" contains too little material to be usable.");
                return true;
            }
        }
        Skill masonry = performer.getSkills().getSkillOrLearn(SkillList.MASONRY);
        int time = 0;
        if(counter==1.0f) {
            time = (int)Math.max(30.0,100.0-masonry.getKnowledge(source,0.0));
            action.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to spread out the "+source.getName()+".");
            Server.getInstance().broadCastAction(performer.getName()+" starts spreading the "+source.getName()+".",performer,5);
            performer.sendActionControl(getActionEntry().getVerbString(),true,time);
        } else {
            time = action.getTimeLeft();
        }
        if(counter*10.0f>time) {
            performer.getStatus().modifyStamina(-3000.0f);
            source.setWeight(source.getWeightGrams()-source.getTemplate().getWeightGrams(),true);
            masonry.skillCheck(1.0,source,0.0,false,counter);
            source.setDamage(source.getDamage()+0.0005f*source.getDamageModifier());
            if(performer.getLayer()<0) {
                Server.caveMesh.setTile(tilex,tiley,Tiles.encode((short)(h),Tiles.decodeType(tile),(byte)(h2-1)));
            }
            if(source.getWeightGrams()<source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("The "+source.getName()+" contains too little material to be usable.");
            } else {
                Players.getInstance().sendChangedTile(tilex,tiley,false,false);
                performer.getCommunicator().sendNormalServerMessage("You lower the ceiling a bit.");
                Server.getInstance().broadCastAction(performer.getName()+" lowers the ceiling a bit.",performer,5);
            }
            return true;
        }
        return false;
    }
}
