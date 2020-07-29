package net.spirangle.awakening.items;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.BehaviourList;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.shared.constants.IconConstants;
import com.wurmonline.shared.constants.ItemMaterials;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ItemTemplateCreatorAwakening {

    private static final Logger logger = Logger.getLogger(ItemTemplateCreatorAwakening.class.getName());

    public static final int diamondLens = 3504;
    public static final int brassTube = 3505;

    public static final int clayGardenGnome = 3801;
    public static final int clayGardenGnomeGreen = 3802;
    public static final int clayMaskIsles = 3803;
    public static final int clayMidsummerMask = 3804;
    public static final int clayPaleMask = 3805;

    public static void initItemTemplates() {
        try {

            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.clayGardenGnome,
                "clay red garden gnome","clay gnomes","almost full","somewhat occupied","half-full","emptyish",
                "A small serious gnome stands here. It could be hardened by fire.",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED,
                    ItemTypes.ITEM_TYPE_TURNABLE,
                    ItemTypes.ITEM_TYPE_DECORATION,
                    ItemTypes.ITEM_TYPE_UNFIRED,
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_HOLLOW,
                    ItemTypes.ITEM_TYPE_NOPUT,
                    ItemTypes.ITEM_TYPE_CONTAINER_LIQUID
                },
                (short)IconConstants.ICON_ICON_UNFINISHED_ITEM,
                BehaviourList.itemBehaviour,0,172800L,10,10,40,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statue.garden.",75.0f,20000,ItemMaterials.MATERIAL_CLAY);
            logger.info("Created template for: clay red garden gnome ["+ItemTemplateCreatorAwakening.clayGardenGnome+"]");

            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.clayGardenGnomeGreen,
                "clay green garden gnome", "clay gnomes", "almost full", "somewhat occupied", "half-full", "emptyish",
                "A small serious gnome stands here. It could be hardened by fire.",
                new short[] {
                    ItemTypes.ITEM_TYPE_NAMED,
                    ItemTypes.ITEM_TYPE_TURNABLE,
                    ItemTypes.ITEM_TYPE_DECORATION,
                    ItemTypes.ITEM_TYPE_UNFIRED,
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_HOLLOW,
                    ItemTypes.ITEM_TYPE_NOPUT,
                    ItemTypes.ITEM_TYPE_CONTAINER_LIQUID
                },
                (short)IconConstants.ICON_ICON_UNFINISHED_ITEM,
                BehaviourList.itemBehaviour,0,172800L,10,10,40,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statue.garden.green.",90.0f,20000,ItemMaterials.MATERIAL_CLAY);
            logger.info("Created template for: clay green garden gnome ["+ItemTemplateCreatorAwakening.clayGardenGnomeGreen+"]");

            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.clayMaskIsles,
                "clay mask of the isles","clay masks","excellent", "good", "ok", "poor",
                "A clay mask.",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED,
                    ItemTypes.ITEM_TYPE_TURNABLE,
                    ItemTypes.ITEM_TYPE_DECORATION,
                    ItemTypes.ITEM_TYPE_UNFIRED,
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_NOPUT
                },
                (short)IconConstants.ICON_ICON_UNFINISHED_ITEM,
                BehaviourList.itemBehaviour,0,172800L,1,10,20,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.armour.head.mask.isles.",50.0f,200,ItemMaterials.MATERIAL_CLAY);
            logger.info("Created template for: clay mask of the isles ["+ItemTemplateCreatorAwakening.clayMaskIsles+"]");

            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.clayMidsummerMask,
                "clay mask of rebirth","clay masks","excellent", "good", "ok", "poor",
                "A clay mask.",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED,
                    ItemTypes.ITEM_TYPE_TURNABLE,
                    ItemTypes.ITEM_TYPE_DECORATION,
                    ItemTypes.ITEM_TYPE_UNFIRED,
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_NOPUT
                },
                (short)IconConstants.ICON_ICON_UNFINISHED_ITEM,
                BehaviourList.itemBehaviour,0,172800L,1,10,20,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.armour.head.mask.midsummer.",60.0f,200,ItemMaterials.MATERIAL_CLAY);
            logger.info("Created template for: clay mask of rebirth ["+ItemTemplateCreatorAwakening.clayMidsummerMask+"]");

            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.clayPaleMask,
                "clay pale mask","clay masks","excellent", "good", "ok", "poor",
                "A clay mask.",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED,
                    ItemTypes.ITEM_TYPE_TURNABLE,
                    ItemTypes.ITEM_TYPE_DECORATION,
                    ItemTypes.ITEM_TYPE_UNFIRED,
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_NOPUT
                },
                (short)IconConstants.ICON_ICON_UNFINISHED_ITEM,
                BehaviourList.itemBehaviour,0,172800L,1,10,20,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.armour.head.mask.pale.",90.0f,200,ItemMaterials.MATERIAL_CLAY);
            logger.info("Created template for: clay pale mask ["+ItemTemplateCreatorAwakening.clayPaleMask+"]");

            /* Spyglass components: */
            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.diamondLens,
                "lens","lenses","excellent","good","ok","poor",
                "A lens made from a polished diamond.",
                new short[]{
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_NO_IMPROVE,
                },
                (short)IconConstants.ICON_DECO_GEM_DIAMOND,
                BehaviourList.itemBehaviour,0,Long.MAX_VALUE,1,1,1,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.gem.diamond.",70.0f,100,ItemMaterials.MATERIAL_DIAMOND,100000,false);
            logger.info("Created template for: diamond lens ["+ItemTemplateCreatorAwakening.diamondLens+"]");

            ItemTemplateCreator.createItemTemplate(
                ItemTemplateCreatorAwakening.brassTube,
                "tube","tubes","excellent","good","ok","poor",
                "A tube made from brass.",
                new short[]{
                    ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_METAL,
                },
                (short)IconConstants.ICON_TOOL_SPYGLASS,
                BehaviourList.itemBehaviour,0,2419200L,3,5,40,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statuette.",50.0f,2000,ItemMaterials.MATERIAL_BRASS,500,false);
            logger.info("Created template for: brass tube ["+ItemTemplateCreatorAwakening.brassTube+"]");

        } catch(IOException e) {
            logger.log(Level.SEVERE,"Initialize item templates error.",e);
        }
    }

    public static void modifyItems() {
        setCraftable(ItemList.shoulderPads7,40.0f,true);
        setCraftable(ItemList.shoulderPads19,40.0f,true);
        setCraftable(ItemList.shoulderPads9,60.0f,true);
        setCraftable(ItemList.shoulderPads21,60.0f,true);

        setCraftable(ItemList.maskEnlightended,50.0f,true);
        setCraftable(ItemList.maskShadow,40.0f,true);
        setCraftable(ItemList.maskOfTheReturner,70.0f,true);
        setCraftable(ItemList.maskTrollHalloween,80.0f,true);
        setCraftable(ItemList.maskSkullHalloween,90.0f,false);
        setCraftable(ItemList.maskChallenge,70.0f,true);
        setCraftable(ItemList.maskRavager,90.0f,true);

        setCraftable(ItemList.summerHat,60.0f,false);
        setCraftable(ItemList.strawBed,30.0f,false);
        setCraftable(ItemList.xmasLunchbox,50.0f,false);

        setCraftable(ItemList.yuleGoat,35.0f,false);
        setCraftable(ItemList.santaHat,35.0f,true);

        setCraftable(ItemList.spyglass,70.0f,true);

        try {
            ItemTemplate summerHat = ItemTemplateFactory.getInstance().getTemplate(ItemList.summerHat);
            ReflectionUtil.setPrivateField(summerHat,ReflectionUtil.getField(summerHat.getClass(),"material"),ItemMaterials.MATERIAL_STRAW);
            ItemTemplate xmasLunchbox = ItemTemplateFactory.getInstance().getTemplate(ItemList.xmasLunchbox);
            ReflectionUtil.setPrivateField(xmasLunchbox,ReflectionUtil.getField(xmasLunchbox.getClass(),"itemDescriptionLong"),"A wicker basket with containers for storing a bit of food and drink for travelling.");

            ItemTemplate gardenGnome = ItemTemplateFactory.getInstance().getTemplate(ItemList.gardenGnome);
            ReflectionUtil.setPrivateField(gardenGnome,ReflectionUtil.getField(gardenGnome.getClass(),"itemDescriptionLong"),"A small serious green gnome stands here.");
            ItemTemplate gardenGnomeGreen = ItemTemplateFactory.getInstance().getTemplate(ItemList.gardenGnomeGreen);
            ReflectionUtil.setPrivateField(gardenGnomeGreen,ReflectionUtil.getField(gardenGnomeGreen.getClass(),"itemDescriptionLong"),"A small serious green gnome stands here.");
            ItemTemplate yuleGoat = ItemTemplateFactory.getInstance().getTemplate(ItemList.yuleGoat);
            ReflectionUtil.setPrivateField(yuleGoat,ReflectionUtil.getField(yuleGoat.getClass(),"itemDescriptionLong"),"A popular decoration for the holiday of awakening is a goat made from straw.");
            ItemTemplate christmasTree = ItemTemplateFactory.getInstance().getTemplate(ItemList.christmasTree);
            ReflectionUtil.setPrivateField(christmasTree,ReflectionUtil.getField(christmasTree.getClass(),"name"),"yule tree");
            ReflectionUtil.setPrivateField(christmasTree,ReflectionUtil.getField(christmasTree.getClass(),"plural"),"yule trees");
            ReflectionUtil.setPrivateField(christmasTree,ReflectionUtil.getField(christmasTree.getClass(),"itemDescriptionLong"),"A beautiful yule tree, with colorful decoration.");

            ItemTemplate spyglass = ItemTemplateFactory.getInstance().getTemplate(ItemList.spyglass);
            ReflectionUtil.setPrivateField(spyglass,ReflectionUtil.getField(spyglass.getClass(),"nodrop"),false);

            ItemTemplate rectMarbleTable = ItemTemplateFactory.getInstance().getTemplate(ItemList.rectMarbleTable);
            ReflectionUtil.setPrivateField(rectMarbleTable,ReflectionUtil.getField(rectMarbleTable.getClass(),"colorable"),true);
            ItemTemplate roundMarbleTable = ItemTemplateFactory.getInstance().getTemplate(ItemList.roundMarbleTable);
            ReflectionUtil.setPrivateField(roundMarbleTable,ReflectionUtil.getField(roundMarbleTable.getClass(),"colorable"),true);
            ItemTemplate marblePlanter = ItemTemplateFactory.getInstance().getTemplate(ItemList.marblePlanter);
            ReflectionUtil.setPrivateField(marblePlanter,ReflectionUtil.getField(marblePlanter.getClass(),"colorable"),true);
            ItemTemplate pillar = ItemTemplateFactory.getInstance().getTemplate(ItemList.pillarDecoration);
            ReflectionUtil.setPrivateField(pillar,ReflectionUtil.getField(pillar.getClass(),"colorable"),true);

        } catch(NoSuchTemplateException|NoSuchFieldException|IllegalAccessException e) {
            logger.log(Level.SEVERE,"Edit template private field error: "+e.getMessage(),e);
        }
    }

    public static void setCraftable(int id,float difficulty,boolean improve) {
        try {
            ItemTemplate it = ItemTemplateFactory.getInstance().getTemplate(id);
            ReflectionUtil.setPrivateField(it,ReflectionUtil.getField(it.getClass(),"difficulty"),difficulty);
            ReflectionUtil.setPrivateField(it,ReflectionUtil.getField(it.getClass(),"repairable"),true);
            ReflectionUtil.setPrivateField(it,ReflectionUtil.getField(it.getClass(),"noImprove"),!improve);
        } catch(NoSuchTemplateException|NoSuchFieldException|IllegalAccessException e) {
            logger.log(Level.SEVERE,"Edit template private field error, for template ID "+id+" (craftable): "+e.getMessage(),e);
        }
    }

    public static void initCreationEntries() {
        CreationEntryCreator.createMetallicEntries(
            SkillList.SMITHING_ARMOUR_PLATE,
            ItemList.anvilLarge,ItemList.ironBar,ItemList.shoulderPads7,
            false,true,10.0f,false,false,0,35.0,CreationCategories.ARMOUR);
        CreationEntryCreator.createMetallicEntries(
            SkillList.SMITHING_ARMOUR_PLATE,
            ItemList.anvilLarge,ItemList.ironBar,ItemList.shoulderPads19,
            false,true,10.0f,false,false,0,35.0,CreationCategories.ARMOUR);

        CreationEntryCreator.createMetallicEntries(
            SkillList.SMITHING_GOLDSMITHING,
            ItemList.anvilSmall,ItemList.ironBar,ItemList.shoulderPads9,
            false,true,10.0f,false,false,0,55.0,CreationCategories.ARMOUR);
        CreationEntryCreator.createMetallicEntries(
            SkillList.SMITHING_GOLDSMITHING,
            ItemList.anvilSmall,ItemList.ironBar,ItemList.shoulderPads21,
            false,true,10.0f,false,false,0,55.0,CreationCategories.ARMOUR);

        CreationEntryCreator.createSimpleEntry(
            SkillList.LEATHERWORKING,
            ItemList.scissors,ItemList.leather,ItemList.maskEnlightended,
            false,true,50.0f,false,false,0,40.0,CreationCategories.CLOTHES);
        final AdvancedCreationEntry maskShadow = CreationEntryCreator.createAdvancedEntry(
            SkillList.LEATHERWORKING,
            ItemList.scissors,ItemList.leather,ItemList.maskShadow,
            false,true,10.0f,false,false,0,30.0,CreationCategories.CLOTHES);
        maskShadow.setUseTemplateWeight(true);
        maskShadow.setDepleteFromSource(0);
        maskShadow.setDepleteFromTarget(200);
        maskShadow.addRequirement(new CreationRequirement(1,ItemList.tooth,7,true));
        maskShadow.addRequirement(new CreationRequirement(2,ItemList.tannin,1,true));
        CreationEntryCreator.createSimpleEntry(
            SkillList.POTTERY,
            ItemList.bodyHand,ItemList.clay,ItemTemplateCreatorAwakening.clayMaskIsles,
            false,true,10.0f,false,false,0,40.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.POTTERY,
            ItemList.bodyHand,ItemList.clay,ItemTemplateCreatorAwakening.clayMidsummerMask,
            false,true,10.0f,false,false,0,50.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.LEATHERWORKING,
            ItemList.scissors,ItemList.leather,ItemList.maskOfTheReturner,
            false,true,50.0f,false,false,0,60.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.LEATHERWORKING,
            ItemList.scissors,ItemList.leather,ItemList.maskTrollHalloween,
            false,true,100.0f,false,false,0,70.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.CARPENTRY_FINE,
            ItemList.knifeCarving,ItemList.skullGoblin,ItemList.maskSkullHalloween,
            false,true,300.0f,false,false,0,80.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.SMITHING_GOLDSMITHING,
            ItemList.anvilSmall,ItemList.silverBar,ItemList.maskChallenge,
            false,true,10.0f,false,false,0,60.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.LEATHERWORKING,
            ItemList.scissors,ItemList.leather,ItemList.maskRavager,
            false,true,100.0f,false,false,0,80.0,CreationCategories.CLOTHES);
        CreationEntryCreator.createSimpleEntry(
            SkillList.POTTERY,
            ItemList.bodyHand,ItemList.clay,ItemTemplateCreatorAwakening.clayPaleMask,
            false,true,10.0f,false,false,0,80.0,CreationCategories.CLOTHES);

        final AdvancedCreationEntry summerHat = CreationEntryCreator.createAdvancedEntry(
            SkillList.THATCHING,
            ItemList.bodyHand,ItemList.thatch,ItemList.summerHat,
            false,true,10.0f,false,false,0,50.0,CreationCategories.CLOTHES);
        summerHat.setUseTemplateWeight(true);
        summerHat.setDepleteFromSource(0);
        summerHat.setDepleteFromTarget(200);
        summerHat.addRequirement(new CreationRequirement(1,ItemList.clothYard,1,false));
        summerHat.addRequirement(new CreationRequirement(2,ItemList.clothString,1,true));
        summerHat.addRequirement(new CreationRequirement(3,ItemList.flower3,1,true));
        ArmourTemplate.armourTemplates.remove(ItemList.summerHat);

        final AdvancedCreationEntry strawBed = CreationEntryCreator.createAdvancedEntry(
            SkillList.THATCHING,
            ItemList.bodyHand,ItemList.thatch,ItemList.strawBed,
            false,true,0.0f,false,true,CreationCategories.DECORATION);
        strawBed.setUseTemplateWeight(false);
        strawBed.setDepleteFromSource(0);
        strawBed.setDepleteFromTarget(2500);
        strawBed.addRequirement(new CreationRequirement(1,ItemList.thatch,15,false));

        final AdvancedCreationEntry picnicBasket = CreationEntryCreator.createAdvancedEntry(
            SkillList.THATCHING,
            ItemList.bodyHand,ItemList.thatch,ItemList.xmasLunchbox,
            false,true,0.0f,false,false,0,40.0,CreationCategories.CONTAINER);
        strawBed.setUseTemplateWeight(false);
        strawBed.setDepleteFromSource(0);
        strawBed.setDepleteFromTarget(50);
        picnicBasket.addRequirement(new CreationRequirement(1,ItemList.jarPottery,1,true));
        picnicBasket.addRequirement(new CreationRequirement(2,ItemList.thatch,6,true));
        picnicBasket.addRequirement(new CreationRequirement(3,ItemList.shaft,6,true));
        picnicBasket.addRequirement(new CreationRequirement(4,ItemList.leatherStrip,2,true));
        picnicBasket.addRequirement(new CreationRequirement(5,ItemList.sheetLead,1,true));
        picnicBasket.addRequirement(new CreationRequirement(6,ItemList.clothYard,1,true));

        final AdvancedCreationEntry clayGardenGnome = CreationEntryCreator.createAdvancedEntry(
            SkillList.POTTERY,
            ItemList.bodyHand,ItemList.clay,ItemTemplateCreatorAwakening.clayGardenGnome,
            false,true,0.0f,false,false,CreationCategories.POTTERY);
        clayGardenGnome.addRequirement(new CreationRequirement(1,ItemList.sourceSalt,5,true));

        final AdvancedCreationEntry clayGardenGnomeGreen = CreationEntryCreator.createAdvancedEntry(
            SkillList.POTTERY,
            ItemList.bodyHand,ItemList.clay,ItemTemplateCreatorAwakening.clayGardenGnomeGreen,
            false,true,0.0f,false,false,CreationCategories.POTTERY);
        clayGardenGnomeGreen.addRequirement(new CreationRequirement(1,ItemList.sourceSalt,5,true));

        final AdvancedCreationEntry yuleGoat = CreationEntryCreator.createAdvancedEntry(
            SkillList.THATCHING,
            ItemList.bodyHand,ItemList.thatch,ItemList.yuleGoat,
            false,true,0.0f,false,false,CreationCategories.DECORATION);
        yuleGoat.setUseTemplateWeight(true);
        yuleGoat.setDepleteFromSource(0);
        yuleGoat.setDepleteFromTarget(480);
        yuleGoat.addRequirement(new CreationRequirement(1,ItemList.clothYard,1,true));
        yuleGoat.addRequirement(new CreationRequirement(2,ItemList.sourceSalt,5,true));

        final AdvancedCreationEntry santaHat = CreationEntryCreator.createAdvancedEntry(
            SkillList.CLOTHTAILORING,
            ItemList.needleIron,ItemList.clothYard,ItemList.santaHat,
            false,true,5.0f,false,false,0,25.0,CreationCategories.CLOTHES);
        santaHat.setUseTemplateWeight(true);
        santaHat.setDepleteFromSource(0);
        santaHat.setDepleteFromTarget(30);
        santaHat.addRequirement(new CreationRequirement(1,ItemList.cochineal,1,true));

        final AdvancedCreationEntry santaHat2 = CreationEntryCreator.createAdvancedEntry(
            SkillList.CLOTHTAILORING,
            ItemList.needleCopper,ItemList.clothYard,ItemList.santaHat,
            false,true,5.0f,false,false,0,25.0,CreationCategories.CLOTHES);
        santaHat2.setUseTemplateWeight(true);
        santaHat2.setDepleteFromSource(0);
        santaHat2.setDepleteFromTarget(30);
        santaHat2.addRequirement(new CreationRequirement(1,ItemList.cochineal,1,true));

        CreationEntryCreator.createSimpleEntry(
            SkillList.STONECUTTING,
            ItemList.whetStone,ItemList.diamond,ItemTemplateCreatorAwakening.diamondLens,
            false,true,10.0f,false,false,0,60.0,CreationCategories.TOOL_PARTS);
        CreationEntryCreator.createSimpleEntry(
            SkillList.SMITHING_GOLDSMITHING,
            ItemList.anvilSmall,ItemList.brassBar,ItemTemplateCreatorAwakening.brassTube,
            false,true,10.0f,false,false,CreationCategories.TOOL_PARTS);
        final AdvancedCreationEntry spyglass = CreationEntryCreator.createAdvancedEntry(
            SkillList.SMITHING_GOLDSMITHING,
            ItemTemplateCreatorAwakening.brassTube,ItemList.brassBand,ItemList.spyglass,
            false,false,0.0f,true,false,CreationCategories.TOOLS);
        spyglass.addRequirement(new CreationRequirement(1,ItemList.brassBand,1,true));
        spyglass.addRequirement(new CreationRequirement(2,ItemList.leather,1,true));
        spyglass.addRequirement(new CreationRequirement(3,ItemTemplateCreatorAwakening.diamondLens,2,true));

    }

    static {
        TempStates.addState(new TempState(clayGardenGnome,ItemList.gardenGnome,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayGardenGnomeGreen,ItemList.gardenGnomeGreen,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayMaskIsles,ItemList.maskIsles,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayMidsummerMask,ItemList.midsummerMask,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayPaleMask,ItemList.maskPale,(short)10000,true,false,false));
    }
}


