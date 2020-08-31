package net.spirangle.awakening.items;

import com.wurmonline.server.Items;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemSettings;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import net.spirangle.awakening.AwakeningConstants;
import net.spirangle.awakening.Config;

import java.util.logging.Logger;

public class Vehicle {

    private static final Logger logger = Logger.getLogger(Vehicle.class.getName());

    public static void decayAbandonedLocks() {
        long lockDecayTime = System.currentTimeMillis()-Config.decayAbandonedLocksDays*AwakeningConstants.DAY_AS_MILLIS;
        for(Item item : Items.getAllItems()) {
            if(item.templateIsLockable() && item.getLockId()!=-10L && item.getOwnerId()==-10L) {
                if(Config.decayAbandonedLocksPVEOnly && item.isInPvPZone()) continue;
                PlayerInfo pi = null;
                if(item.getLastOwnerId()!=-10L) {
                    pi = PlayerInfoFactory.getPlayerInfoWithWurmId(item.getLastOwnerId());
                    if(pi!=null && (pi.lastLogout>=lockDecayTime || pi.getPower() >= MiscConstants.POWER_HERO)) continue;
                    Village village = Villages.getVillageForCreature(item.getLastOwnerId());
                    if(village!=null && village.containsItem(item)) continue;
                }
                final long lockId = item.getLockId();
                ItemSettings.remove(item.getWurmId());
                PermissionsHistories.addHistoryEntry(item.getWurmId(),System.currentTimeMillis(),-10L,"Auto","Lock decayed");
                item.setLockId(-10L);
                Items.destroyItem(lockId);
                if(Config.decayAbandonedLocksUnplant && item.isPlanted()) item.setIsPlanted(false);
                logger.info("Lock decayed for "+item.getName()+(pi!=null? " ("+pi.getName()+")" : "")+" ["+item.getTileX()+", "+item.getTileY()+"].");
            }
        }
    }

    @SuppressWarnings("unused")
    public static void occupySeat(final Seat seat,final com.wurmonline.server.behaviours.Vehicle vehicle,final Creature creature) {
    if(seat.occupant==-10L && creature!=null && creature.isPlayer() && seat.type==0 && !vehicle.isCreature()) {
            try {
                Item item = Items.getItem(vehicle.wurmid);
                if((item.isCart() || item.isBoat()) && item.getLockId()==-10L) {
                    if(item.setNewOwner(creature.getWurmId())) {
                        creature.getCommunicator().sendSafeServerMessage("You claim the "+item.getName()+" as the new owner.");
                        logger.info(creature.getName()+" claim the "+item.getName()+" as the new owner.");
                    }
                }
            } catch(NoSuchItemException e) {}
        }
    }
}
