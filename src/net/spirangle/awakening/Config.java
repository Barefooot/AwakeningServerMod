package net.spirangle.awakening;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


public class Config implements Configurable {

    private static final Logger logger = Logger.getLogger(Config.class.getName());

    public static boolean useCommandHandler = false;
    public static boolean handleCommandConfig = false;
    public static boolean handleCommandRestore = false;
    public static boolean handleCommandAlts = false;
    public static boolean handleCommandMonitor = false;
    public static boolean handleCommandPvP = false;
    public static boolean handleCommandKarma = false;
    public static boolean handleCommandRotate = false;
    public static boolean handleCommandInventory = false;
    public static boolean handleCommandTransfer = false;
    public static boolean handleCommandSQL = false;
    public static boolean handleCommandSchedule = false;
    public static boolean handleCommandTile = false;
    public static boolean handleCommandSetResource = false;
    public static boolean handleCommandFlowersFix = false;
    public static boolean handleCommandLag = false;
    public static boolean handleCommandBank = false;
    public static boolean handleCommandPvPList = false;

    public static boolean useLeaderBoard = false;
    public static boolean useLowerCaveCeiling = false;
    public static boolean useScheduler = false;
    public static boolean useItemCreationEntries = false;
    public static boolean usePlayerSettings = false;

    public static boolean useHandleServerLag = false;
    public static boolean useAcceptLoginDifferentIPs = false;
    public static boolean useMayorsCanTurnUnlawful = false;
    public static boolean useOffspringNames = false;
    public static boolean useCustomPetHandling = false;
    public static boolean usePlayerHoverText = false;
    public static boolean useKingdomsMayAlly = false;
    public static boolean useInitializeFarmToMidnightGMT = false;
    public static boolean useFarmGrowthWhenTended = false;
    public static boolean useAcceptingDeityNames = false;
    public static boolean useIsTurnableFix = false;
    public static boolean useIsMoveableFix = false;
    public static boolean useCorpseColourCreatureFix = false;
    public static boolean useCorpseSizeCreatureFix = false;
    public static boolean useAdjustSizeByTraits = false;
    public static boolean useAdjustMilkByTraits = false;
    public static boolean useAdjustWoolByTraits = false;
    public static boolean useBrandNullPointerFix = false;
    public static boolean useMayDropDirt = false;
    public static boolean useCheckForTreeSprout = false;
    public static boolean useContinueInHouseWithoutManage = false;
    public static boolean useCanAllowEveryone = false;
    public static boolean useVillagePermissionsPvEzone = false;
    public static boolean useCanPlantMarkerBlessed = false;
    public static boolean useOldSkillMeditation = false;

    public static int serverLagReportTime = 30;

    public static boolean antiMacroHandling = false;
    public static boolean antiMacroPunishing = false;
    public static long antiMacroPatternedActionTime = 30;
    public static int antiMacroPatternedActions = 100;
    public static int antiMacroRobotActionsCounter = 10;
    public static long antiMacroRobotTestTimeMin = 120;
    public static long antiMacroRobotTestTimeMax = 240;
    public static long antiMacroRobotPunishTime = 10;
    public static int antiMacroRandomActionsMin = 1000;
    public static int antiMacroRandomActionsMax = 2000;

    public static boolean useDecayAbandonedLocks = false;
    public static long decayAbandonedLocksDays = 90L;
    public static boolean decayAbandonedLocksPVEOnly = false;
    public static boolean decayAbandonedLocksUnplant = false;
    public static boolean useClaimByEmbarkUnlockedVehicle = false;

    public static boolean usePlagues = false;
    public static int plagueRadius = 50;
    public static int plagueMinPopulation = 500;
    public static int plagueMaxCreaturesMargin = 1000;

    public static boolean useInventorySupplier = false;
    public static long[] kingdomTraders = null;

    private static Config instance = null;

    public static Config getInstance() {
        if(instance==null) instance = new Config();
        return instance;
    }

    private Config() {
    }

    @Override
    public void configure(Properties properties) {

        useCommandHandler = Boolean.parseBoolean(properties.getProperty("useCommandHandler","false"));
        handleCommandConfig = Boolean.parseBoolean(properties.getProperty("handleCommandConfig","false"));
        handleCommandRestore = Boolean.parseBoolean(properties.getProperty("handleCommandRestore","false"));
        handleCommandAlts = Boolean.parseBoolean(properties.getProperty("handleCommandAlts","false"));
        handleCommandMonitor = Boolean.parseBoolean(properties.getProperty("handleCommandMonitor","false"));
        handleCommandPvP = Boolean.parseBoolean(properties.getProperty("handleCommandPvP","false"));
        handleCommandKarma = Boolean.parseBoolean(properties.getProperty("handleCommandKarma","false"));
        handleCommandRotate = Boolean.parseBoolean(properties.getProperty("handleCommandRotate","false"));
        handleCommandInventory = Boolean.parseBoolean(properties.getProperty("handleCommandInventory","false"));
        handleCommandTransfer = Boolean.parseBoolean(properties.getProperty("handleCommandTransfer","false"));
        handleCommandSQL = Boolean.parseBoolean(properties.getProperty("handleCommandSQL","false"));
        handleCommandSchedule = Boolean.parseBoolean(properties.getProperty("handleCommandSchedule","false"));
        handleCommandTile = Boolean.parseBoolean(properties.getProperty("handleCommandTile","false"));
        handleCommandSetResource = Boolean.parseBoolean(properties.getProperty("handleCommandSetResource","false"));
        handleCommandFlowersFix = Boolean.parseBoolean(properties.getProperty("handleCommandFlowersFix","false"));
        handleCommandLag = Boolean.parseBoolean(properties.getProperty("handleCommandLag","false"));
        handleCommandBank = Boolean.parseBoolean(properties.getProperty("handleCommandBank","false"));
        handleCommandPvPList = Boolean.parseBoolean(properties.getProperty("handleCommandPvPList","false"));

        useLeaderBoard = Boolean.parseBoolean(properties.getProperty("useLeaderBoard","false"));
        useLowerCaveCeiling = Boolean.parseBoolean(properties.getProperty("useLowerCaveCeiling","false"));
        useScheduler = Boolean.parseBoolean(properties.getProperty("useScheduler","false"));
        useItemCreationEntries = Boolean.parseBoolean(properties.getProperty("useItemCreationEntries","false"));
        usePlayerSettings = Boolean.parseBoolean(properties.getProperty("usePlayerSettings","false"));

        useAcceptLoginDifferentIPs = Boolean.parseBoolean(properties.getProperty("useAcceptLoginDifferentIPs","false"));
        useMayorsCanTurnUnlawful = Boolean.parseBoolean(properties.getProperty("useMayorsCanTurnUnlawful","false"));
        useOffspringNames = Boolean.parseBoolean(properties.getProperty("useOffspringNames","false"));
        useCustomPetHandling = Boolean.parseBoolean(properties.getProperty("useCustomPetHandling","false"));
        usePlayerHoverText = Boolean.parseBoolean(properties.getProperty("usePlayerHoverText","false"));
        useKingdomsMayAlly = Boolean.parseBoolean(properties.getProperty("useKingdomsMayAlly","false"));
        useInitializeFarmToMidnightGMT = Boolean.parseBoolean(properties.getProperty("useInitializeFarmToMidnightGMT","false"));
        useFarmGrowthWhenTended = Boolean.parseBoolean(properties.getProperty("useFarmGrowthWhenTended","false"));
        useAcceptingDeityNames = Boolean.parseBoolean(properties.getProperty("useAcceptingDeityNames","false"));
        useIsTurnableFix = Boolean.parseBoolean(properties.getProperty("useIsTurnableFix","false"));
        useIsMoveableFix = Boolean.parseBoolean(properties.getProperty("useIsMoveableFix","false"));
        useCorpseColourCreatureFix = Boolean.parseBoolean(properties.getProperty("useCorpseColourCreatureFix","false"));
        useCorpseSizeCreatureFix = Boolean.parseBoolean(properties.getProperty("useCorpseSizeCreatureFix","false"));
        useAdjustSizeByTraits = Boolean.parseBoolean(properties.getProperty("useAdjustSizeByTraits","false"));
        useAdjustMilkByTraits = Boolean.parseBoolean(properties.getProperty("useAdjustMilkByTraits","false"));
        useAdjustWoolByTraits = Boolean.parseBoolean(properties.getProperty("useAdjustWoolByTraits","false"));
        useBrandNullPointerFix = Boolean.parseBoolean(properties.getProperty("useBrandNullPointerFix","false"));
        useMayDropDirt = Boolean.parseBoolean(properties.getProperty("useMayDropDirt","false"));
        useCheckForTreeSprout = Boolean.parseBoolean(properties.getProperty("useCheckForTreeSprout","false"));
        useContinueInHouseWithoutManage = Boolean.parseBoolean(properties.getProperty("useContinueInHouseWithoutManage","false"));
        useCanAllowEveryone = Boolean.parseBoolean(properties.getProperty("useCanAllowEveryone","false"));
        useVillagePermissionsPvEzone = Boolean.parseBoolean(properties.getProperty("useVillagePermissionsPvEzone","false"));
        useCanPlantMarkerBlessed = Boolean.parseBoolean(properties.getProperty("useCanPlantMarkerBlessed","false"));
        useOldSkillMeditation = Boolean.parseBoolean(properties.getProperty("useOldSkillMeditation","false"));

        useHandleServerLag = Boolean.parseBoolean(properties.getProperty("useHandleServerLag","false"));
        serverLagReportTime = Integer.parseInt(properties.getProperty("serverLagReportTime","30"));

        antiMacroHandling = Boolean.parseBoolean(properties.getProperty("antiMacroHandling","false"));
        antiMacroPunishing = Boolean.parseBoolean(properties.getProperty("antiMacroPunishing","false"));
        antiMacroPatternedActionTime = Long.parseLong(properties.getProperty("antiMacroPatternedActionTime","300"));
        antiMacroPatternedActions = Integer.parseInt(properties.getProperty("antiMacroPatternedActions","100"));
        antiMacroRobotActionsCounter = Integer.parseInt(properties.getProperty("antiMacroRobotActionsCounter","10"));
        antiMacroRobotTestTimeMin = Long.parseLong(properties.getProperty("antiMacroRobotTestTimeMin","120"));
        antiMacroRobotTestTimeMax = Long.parseLong(properties.getProperty("antiMacroRobotTestTimeMax","240"));
        antiMacroRobotPunishTime = Long.parseLong(properties.getProperty("antiMacroRobotPunishTime","10"));
        antiMacroRandomActionsMin = Integer.parseInt(properties.getProperty("antiMacroRandomActionsMin","1000"));
        antiMacroRandomActionsMax = Integer.parseInt(properties.getProperty("antiMacroRandomActionsMax","2000"));

        useDecayAbandonedLocks = Boolean.parseBoolean(properties.getProperty("useDecayAbandonedLocks","false"));
        decayAbandonedLocksDays = Long.parseLong(properties.getProperty("decayAbandonedLocksDays","90"));
        decayAbandonedLocksPVEOnly = Boolean.parseBoolean(properties.getProperty("decayAbandonedLocksPVEOnly","false"));
        decayAbandonedLocksUnplant = Boolean.parseBoolean(properties.getProperty("decayAbandonedLocksUnplant","false"));
        useClaimByEmbarkUnlockedVehicle = Boolean.parseBoolean(properties.getProperty("useClaimByEmbarkUnlockedVehicle","false"));

        usePlagues = Boolean.parseBoolean(properties.getProperty("usePlagues","false"));
        plagueRadius = Integer.parseInt(properties.getProperty("plagueRadius","50"));
        plagueMinPopulation = Integer.parseInt(properties.getProperty("plagueMinPopulation","500"));
        plagueMaxCreaturesMargin = Integer.parseInt(properties.getProperty("plagueMaxCreaturesMargin","1000"));

        useInventorySupplier = Boolean.parseBoolean(properties.getProperty("useInventorySupplier","false"));
        List<Long> kingdomTradersList = new ArrayList<>();
        for(int i = 0; true; ++i) {
            String s = properties.getProperty("kingdomTrader_"+i);
            if(s==null) break;
            kingdomTradersList.add(Long.parseLong(s));
        }
        if(!kingdomTradersList.isEmpty()) {
            kingdomTraders = new long[kingdomTradersList.size()];
            for(int i=0; i<kingdomTraders.length; ++i)
                kingdomTraders[i] = kingdomTradersList.get(i);
        }
    }
}
