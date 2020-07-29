package net.spirangle.awakening.creatures;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.items.Item;


@SuppressWarnings("unused")
public class Traits {

    @SuppressWarnings("unused")
    public static void milk(Item milk,Creature performer,Creature target) {
        if(!target.hasTraits()) return;
        float weightMod = 1.0f;
        float qlMod = 1.0f;
        if(target.hasTrait(0)) weightMod *= 0.9f;    /* It will fight fiercely. */
        if(target.hasTrait(2)) weightMod *= 0.95f;   /* It is a tough bugger. */
        if(target.hasTrait(3)) weightMod *= 1.3f;    /* It has a strong body. */
        if(target.hasTrait(5)) weightMod *= 1.4f;    /* It can carry more than average. */
        if(target.hasTrait(6)) weightMod *= 1.25f;   /* It has very strong leg muscles. */
        if(target.hasTrait(8)) weightMod *= 0.8f;    /* It has malformed hindlegs. */
        if(target.hasTrait(9)) weightMod *= 0.8f;    /* The legs are of different length. */
        if(target.hasTrait(10)) weightMod *= 0.95f;  /* It seems overly aggressive. */
        if(target.hasTrait(11)) weightMod *= 1.2f;   /* It looks very unmotivated. */
        if(target.hasTrait(12)) qlMod *= 1.25f;      /* It is unusually strong willed. */
        if(target.hasTrait(13)) qlMod *= 0.8f;       /* It has some illness. */
        if(target.hasTrait(14)) weightMod *= 1.5f;   /* It looks constantly hungry. */
        if(target.hasTrait(19)) qlMod *= 0.95f;      /* It looks feeble and unhealthy. */
        if(target.hasTrait(20)) qlMod *= 1.4f;       /* It looks unusually strong and healthy. */
        if(target.hasTrait(21)) qlMod *= 1.3f;       /* It has a certain spark in its eyes. */
        if(weightMod!=1.0f) milk.setWeight((int)(weightMod*(float)milk.getWeightGrams()),true);
        if(qlMod!=1.0f) milk.setQualityLevel(Math.max(1.0f,Math.min(100.0f,qlMod*milk.getQualityLevel())));
    }

    @SuppressWarnings("unused")
    public static void shear(Item wool,Creature performer,Creature target) {
        if(!target.hasTraits()) return;
        float weightMod = 1.0f;
        float qlMod = 1.0f;
        if(target.hasTrait(0)) weightMod *= 1.2f;   /* It will fight fiercely. */
        if(target.hasTrait(1)) weightMod *= 0.85f;  /* It has fleeter movement than normal. */
        if(target.hasTrait(2)) weightMod *= 1.25f;  /* It is a tough bugger. */
        if(target.hasTrait(3)) qlMod *= 1.2f;       /* It has a strong body. */
        if(target.hasTrait(4)) weightMod *= 0.75f;  /* It has lightning movement. */
        if(target.hasTrait(7)) weightMod *= 0.75f;  /* It has keen senses. */
        if(target.hasTrait(10)) weightMod *= 1.3f;  /* It seems overly aggressive. */
        if(target.hasTrait(11)) weightMod *= 1.5f;  /* It looks very unmotivated. */
        if(target.hasTrait(12)) weightMod *= 1.4f;  /* It is unusually strong willed. */
        if(target.hasTrait(13)) qlMod *= 0.9f;      /* It has some illness. */
        if(target.hasTrait(19)) qlMod *= 0.85f;     /* It looks feeble and unhealthy. */
        if(target.hasTrait(20)) qlMod *= 1.5f;      /* It looks unusually strong and healthy. */
        if(target.hasTrait(21)) qlMod *= 1.3f;      /* It has a certain spark in its eyes. */
        if(weightMod!=1.0f) wool.setWeight((int)(weightMod*(float)wool.getWeightGrams()),true);
        if(qlMod!=1.0f) wool.setQualityLevel(Math.max(1.0f,Math.min(100.0f,qlMod*wool.getQualityLevel())));
    }

    @SuppressWarnings("unused")
    public static float getSizeMod(Creature creature) {
        int templId = creature.getTemplate().getTemplateId();
        float sizeMod = 1.0f;
        byte modtype = creature.getStatus().getModType();
        if(!creature.hasTraits() || creature.getStatus().getModType()!=0 || creature.isVehicle() ||
           templId==CreatureTemplateIds.FOAL_CID || templId==CreatureTemplateIds.HELL_FOAL_CID || templId==CreatureTemplateIds.UNICORN_FOAL_CID)
            return sizeMod;
        if(creature.hasTrait(3)) sizeMod *= 1.06f;   /* It has a strong body. */
        if(creature.hasTrait(5)) sizeMod *= 1.06f;   /* It can carry more than average. */
        if(creature.hasTrait(6)) sizeMod *= 1.06f;   /* It has very strong leg muscles. */
        if(creature.hasTrait(8)) sizeMod *= 0.95f;   /* It has malformed hindlegs. */
        if(creature.hasTrait(10)) sizeMod *= 0.85f;  /* It seems overly aggressive. */
        if(creature.hasTrait(12)) sizeMod *= 0.95f;  /* It is unusually strong willed. */
        if(creature.hasTrait(13)) sizeMod *= 0.90f;  /* It has some illness. */
        if(creature.hasTrait(14)) sizeMod *= 1.11f;  /* It looks constantly hungry. */
        if(creature.hasTrait(19)) sizeMod *= 0.95f;  /* It looks feeble and unhealthy. */
        if(creature.hasTrait(20)) sizeMod *= 1.08f;  /* It looks unusually strong and healthy. */
        if(sizeMod!=1.0f) {
            if(templId==CreatureTemplateIds.BISON_CID) {
                sizeMod = 1.0f+(sizeMod-1.0f)*0.35f;
            }
        }
        return sizeMod;
    }
}

