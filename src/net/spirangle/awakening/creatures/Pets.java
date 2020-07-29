package net.spirangle.awakening.creatures;

import com.wurmonline.server.creatures.Creature;

import java.util.List;


@SuppressWarnings("unused")
public class Pets {

    public static byte getPetAttitude(final Creature dominator,final Creature target,final int decisionState) {
        if(decisionState==1) {
            return target.getAttitude(dominator);
        }
        return 1;
    }

    public static byte getTargetAttitude(final Creature dominator,final Creature target,final int decisionState) {
        if(decisionState==1) {
            return target.getAttitude(dominator);
        }
        return 1;
    }

    public static void getStateString(final Creature creature,final List<String> strings,int petOrderState) {
        if(creature.hasPet()) {
            if(petOrderState==0) strings.add("Pet:passive");
            else if(petOrderState==1) strings.add("Pet:fight");
        }
    }
}
