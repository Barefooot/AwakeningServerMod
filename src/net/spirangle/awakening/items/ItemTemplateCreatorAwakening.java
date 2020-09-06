package net.spirangle.awakening.items;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.BehaviourList;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.shared.constants.IconConstants;
import com.wurmonline.shared.constants.ItemMaterials;
import net.spirangle.awakening.Config;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ItemTemplateCreatorAwakening {

    private static final Logger logger = Logger.getLogger(ItemTemplateCreatorAwakening.class.getName());

    public static final int bulkChest = 93501;
    public static final int diamondLens = 3504;
    public static final int brassTube = 3505;

    public static final int coffeeBean = 93601;
    public static final int coffeeGround = 93602;
    public static final int coffee = 93603;
    public static final int espresso = 93604;
    public static final int peanut = 93621;
    public static final int peanutButter = 93622;
    public static final int tabasco = 93631;
    public static final int toffeeApple = 93641;
    public static final int cocktail = 93651;

    public static final int clayGardenGnome = 3801;
    public static final int clayGardenGnomeGreen = 3802;
    public static final int clayMaskIsles = 3803;
    public static final int clayMidsummerMask = 3804;
    public static final int clayPaleMask = 3805;

    public static void initItemTemplates() {
        try {

            /* Bulk Chest: */
            if(Config.useBulkChest) {
                final ItemTemplate bc = ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.bulkChest,3,
                    "bulk chest","bulk chests","almost full","somewhat occupied","half-full","emptyish",
                    "A sturdy chest made from planks and strengthened with iron ribbons.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_OWNER_DESTROYABLE,
                        ItemTypes.ITEM_TYPE_NOTAKE,
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_LOCKABLE,
                        ItemTypes.ITEM_TYPE_HOLLOW,
                        ItemTypes.ITEM_TYPE_BULKCONTAINER,
                        ItemTypes.ITEM_TYPE_TRANSPORTABLE,
                        ItemTypes.ITEM_TYPE_HASDATA,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_USES_SPECIFIED_CONTAINER_VOLUME
                    },
                    (short)IconConstants.ICON_CONTAINER_CHEST_LARGE,
                    BehaviourList.itemBehaviour,0,9072000L,80,70,120,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.furniture.wooden.coffer.",40.0f,100000,ItemMaterials.MATERIAL_WOOD_BIRCH,10000,true,-1);
                bc.setContainerSize(30,50,200);
                logger.info("Created template for: bulk chest ["+ItemTemplateCreatorAwakening.bulkChest+"]");
            }

            if(Config.useRecipeItems) {
                /* Coffee Items: */
                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.coffeeBean,
                    "coffee bean","coffee beans","excellent","good","ok","poor",
                    "A coffee seed, commonly called coffee bean, is a seed of the coffee plant, and is the source for coffee.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_BULK,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION,
                        ItemTypes.ITEM_TYPE_FOOD,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_FOOD_COCOA_BEAN,
                    BehaviourList.itemBehaviour,0,28800L,2,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.resource.cocoabean.",8.0f,200,ItemMaterials.MATERIAL_VEGETARIAN,100,false);
                logger.info("Created template for: coffee bean ["+ItemTemplateCreatorAwakening.coffeeBean+"]");


                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.coffeeGround,
                    "ground coffee","ground coffee","excellent","good","ok","poor",
                    "Ground and roasted coffee bean.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_BULK,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION,
                        ItemTypes.ITEM_TYPE_FOOD,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_LIQUID_TANNIN,
                    BehaviourList.itemBehaviour,0,172800L,2,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.food.cocoa.",15.0f,200,ItemMaterials.MATERIAL_VEGETARIAN,100,false);
                logger.info("Created template for: ground coffee ["+ItemTemplateCreatorAwakening.coffeeGround+"]");

                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.coffee,
                    "coffee","coffee","excellent","good","ok","poor",
                    "Coffee is a way of stealing time that should by rights belong to your older self.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_LIQUID,
                        ItemTypes.ITEM_TYPE_LIQUID_COOKING,
                        ItemTypes.ITEM_TYPE_LIQUID_DRINKABLE,
                        ItemTypes.ITEM_TYPE_DECAYDESTROYS,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION
                    },
                    (short)IconConstants.ICON_LIQUID_TANNIN,
                    BehaviourList.itemBehaviour,0,172800L,2,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.liquid.tannin.",15.0f,200,ItemMaterials.MATERIAL_VEGETARIAN,100,false);
                logger.info("Created template for: coffee ["+ItemTemplateCreatorAwakening.coffee+"]");

                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.espresso,
                    "espresso","espresso","excellent","good","ok","poor",
                    "Black as the devil, hot as hell, pure as an angel, sweet as love.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_LIQUID,
                        ItemTypes.ITEM_TYPE_LIQUID_COOKING,
                        ItemTypes.ITEM_TYPE_LIQUID_DRINKABLE,
                        ItemTypes.ITEM_TYPE_DECAYDESTROYS,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION
                    },
                    (short)IconConstants.ICON_LIQUID_TANNIN,
                    BehaviourList.itemBehaviour,0,172800L,2,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.liquid.tannin.",35.0f,1000,ItemMaterials.MATERIAL_VEGETARIAN,100,false);
                logger.info("Created template for: espresso ["+ItemTemplateCreatorAwakening.espresso+"]");

                /* Peanut: */
                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.peanut,
                    "peanut","peanuts","excellent","good","ok","poor",
                    "The peanut, also known as the groundnut and the goober, is a legume crop grown mainly for its edible seeds.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_SEED,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION,
                        ItemTypes.ITEM_TYPE_BULK,
                        ItemTypes.ITEM_TYPE_FOOD,
                        ItemTypes.ITEM_TYPE_FRUIT,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_FOOD_PINENUT,
                    BehaviourList.itemBehaviour,0,86400L,3,4,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.food.pinenuts.",15.0f,200,ItemMaterials.MATERIAL_VEGETARIAN,100,false)
                                   .setNutritionValues(6730,131,684,137);
                logger.info("Created template for: peanut ["+ItemTemplateCreatorAwakening.peanut+"]");

                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.peanutButter,
                    "peanut butter","peanut butter","excellent","good","ok","poor",
                    "Peanut butter is a food paste or spread made from ground dry roasted peanuts.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_DISH,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION,
                        ItemTypes.ITEM_TYPE_BULK,
                        ItemTypes.ITEM_TYPE_FOOD,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_FOOD_BUTTER,
                    BehaviourList.itemBehaviour,0,86400L,2,3,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.food.butter.",15.0f,200,ItemMaterials.MATERIAL_VEGETARIAN,100,false);
                logger.info("Created template for: peanut butter ["+ItemTemplateCreatorAwakening.peanutButter+"]");

                /* Tabasco: */
                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.tabasco,
                    "tabasco","tabasco","excellent","good","ok","poor",
                    "Tabasco sauce is a brand of hot sauce made exclusively from tabasco peppers.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_LIQUID,
                        ItemTypes.ITEM_TYPE_LIQUID_COOKING,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION,
                        ItemTypes.ITEM_TYPE_FERMENTED,
                        ItemTypes.ITEM_TYPE_DECAYDESTROYS,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_FOOD_KETCHUP,
                    BehaviourList.itemBehaviour,0,172800L,2,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.resource.passata.",15.0f,600,ItemMaterials.MATERIAL_VEGETARIAN,100,false);
                logger.info("Created template for: tabasco ["+ItemTemplateCreatorAwakening.tabasco+"]");

                /* Toffee Apple: */
                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.toffeeApple,
                    "toffee apple","toffee apples","delicious","nice","old","rotten",
                    "Sweet and crunchy apple covered in toffee.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_FOOD,
                        ItemTypes.ITEM_TYPE_LOWNUTRITION,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_FOOD_APPLE_GREEN,
                    BehaviourList.itemBehaviour,0,604800L,5,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.food.apple.green.",100.0f,300,ItemMaterials.MATERIAL_VEGETARIAN,10,true);
                logger.info("Created template for: toffee apple ["+ItemTemplateCreatorAwakening.toffeeApple+"]");

                /* Cocktail: */
                ItemTemplateCreator.createItemTemplate(
                    ItemTemplateCreatorAwakening.cocktail,
                    "cocktail","cocktails","excellent","good","ok","poor",
                    "You can't buy happiness, but you can prepare a cocktail and that's kind of the same thing.",
                    new short[]{
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_LIQUID,
                        ItemTypes.ITEM_TYPE_LIQUID_COOKING,
                        ItemTypes.ITEM_TYPE_LIQUID_DRINKABLE,
                        ItemTypes.ITEM_TYPE_USES_FOOD_STATE
                    },
                    (short)IconConstants.ICON_LIQUID_FRUITJUICE,
                    BehaviourList.itemBehaviour,0,604800L,2,5,5,-10,MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                    "model.liquid.juice.",30.0f,1000,ItemMaterials.MATERIAL_WATER,100,false);
                logger.info("Created template for: cocktail ["+ItemTemplateCreatorAwakening.cocktail+"]");
            }

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
        /* Bulk Chest: */
        final AdvancedCreationEntry bc = CreationEntryCreator.createAdvancedEntry(
            SkillList.CARPENTRY_FINE,
            ItemList.ironBand,ItemList.plank,ItemTemplateCreatorAwakening.bulkChest,
            false,false,0.0f,true,true,CreationCategories.STORAGE);
        bc.addRequirement(new CreationRequirement(1,ItemList.plank,10,true));
        bc.addRequirement(new CreationRequirement(2,ItemList.ironBand,3,true));
        bc.addRequirement(new CreationRequirement(3,ItemList.nailsIronSmall,4,true));

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

    @SuppressWarnings("unused")
    public static boolean isBulkContainer(Item item) {
        return item.getTemplateId()==ItemTemplateCreatorAwakening.bulkChest;
    }

    static {
        TempStates.addState(new TempState(clayGardenGnome,ItemList.gardenGnome,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayGardenGnomeGreen,ItemList.gardenGnomeGreen,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayMaskIsles,ItemList.maskIsles,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayMidsummerMask,ItemList.midsummerMask,(short)10000,true,false,false));
        TempStates.addState(new TempState(clayPaleMask,ItemList.maskPale,(short)10000,true,false,false));
    }
}


