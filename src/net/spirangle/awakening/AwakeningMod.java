package net.spirangle.awakening;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import net.spirangle.awakening.actions.LeaderBoardAction;
import net.spirangle.awakening.actions.LowerCeilingCornerAction;
import net.spirangle.awakening.actions.SettingsAction;
import net.spirangle.awakening.items.InventorySupplier;
import net.spirangle.awakening.items.ItemTemplateCreatorAwakening;
import net.spirangle.awakening.items.Vehicle;
import net.spirangle.awakening.players.PlayersData;
import net.spirangle.awakening.time.Scheduler;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AwakeningMod implements WurmServerMod, Configurable, PreInitable, Initable,
                                     ItemTemplatesCreatedListener, ServerStartedListener, ServerShutdownListener,
                                     PlayerLoginListener, PlayerMessageListener {

    private static final Logger logger = Logger.getLogger(AwakeningMod.class.getName());

    @Override
    public void configure(Properties properties) {
        Config.getInstance().configure(properties);
    }

    @Override
    public void preInit() {
        CodeInjections.preInit();
        ModActions.init();
    }

    @Override
    public void init() {
    }

    @Override
    public void onItemTemplatesCreated() {
        if(Config.useItemCreationEntries) {
            ItemTemplateCreatorAwakening.initItemTemplates();
            ItemTemplateCreatorAwakening.modifyItems();
        }
    }

    @Override
    public void onServerStarted() {
        AwakeningDb.init();
        if(Config.useScheduler) {
            Scheduler.getInstance().start();
        }
        if(Config.useInventorySupplier) {
            InventorySupplier.getInstance().init();
        }
        if(Config.useItemCreationEntries) {
            ItemTemplateCreatorAwakening.initCreationEntries();
        }
        if(Config.useLowerCaveCeiling) {
            ModActions.registerAction(new LowerCeilingCornerAction());
        }
        if(Config.useLeaderBoard) {
            ModActions.registerAction(new LeaderBoardAction());
        }
        if(Config.usePlayerSettings) {
            ModActions.registerAction(new SettingsAction());
            PlayersData.getInstance().loadPlayersData();
        }
        if(Config.useDecayAbandonedLocks) {
            Vehicle.decayAbandonedLocks();
        }
    }

    @Override
    public void onServerShutdown() {
        if(Config.usePlayerSettings) {
            try(Connection con = ModSupportDb.getModSupportDb()) {
                while(!PlayersData.getInstance().savePlayersData(con)) ;
            } catch(SQLException e) {
                logger.log(Level.SEVERE,"Failed to save data: "+e.getMessage(),e);
            }
        }
    }

    @Override
    public void onPlayerLogin(Player player) {
        if(Config.usePlayerSettings) {
            PlayersData.getInstance().onPlayerLogin(player);
        }
    }

    @Override
    public MessagePolicy onPlayerMessage(Communicator communicator,String message,String title) {
        if(!Config.useCommandHandler) return MessagePolicy.PASS;
        char c = message.charAt(0);
        MessagePolicy m = MessagePolicy.PASS;
        if(c=='#') {
            m = CommandHandler.getInstance().handleGmCommand(communicator,message,title);

        } else if(c=='/') {
            m = CommandHandler.getInstance().handleCommand(communicator,message,title);
        }
        return m;
    }

    @Deprecated
    @Override
    public boolean onPlayerMessage(Communicator communicator,String msg) {
        return false;
    }
}
