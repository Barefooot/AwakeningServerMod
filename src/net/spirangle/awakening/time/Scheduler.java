package net.spirangle.awakening.time;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.players.Player;
import net.spirangle.awakening.Config;
import net.spirangle.awakening.creatures.Spawner;
import net.spirangle.awakening.creatures.Spawner.SpawnArea;
import net.spirangle.awakening.items.InventorySupplier;
import net.spirangle.awakening.players.PlayersData;
import net.spirangle.awakening.util.Cache;
import net.spirangle.awakening.util.StringUtils;
import net.spirangle.awakening.zones.Plague;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import java.sql.*;
import java.time.Year;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Scheduler {

    private static final Logger logger = Logger.getLogger(Scheduler.class.getName());

    public static final int REAL_MINUTE_TASK = 0x1000;
    public static final int WURM_HOUR_TASK = 0x2000;
    public static final int EVERY_TICK_TASK = 0x4000;

    public static final int BROAD_CAST_TASK = 1|REAL_MINUTE_TASK;
    public static final int DESTROY_CREATURE_TASK = 3|WURM_HOUR_TASK|EVERY_TICK_TASK;

    public static final String broadCastKey = "broadcast";
    public static final String destroyCreatureKey = "destroycreature";

    private static final Set<Integer> gmTasks = new HashSet<>(Arrays.asList(DESTROY_CREATURE_TASK));

    private static int serverLag = 0;
    private static int serverLagReported = 0;
    private static long serverLagReportTime = 0L;

    @SuppressWarnings("unused")
    public static void handleServerLag(int lag) {
        if(lag-serverLag>3 && Server.getSecondsUptime()>=600) {
            long now = System.currentTimeMillis();
            if(serverLagReportTime<=now-(Config.serverLagReportTime*60000L)) {
                Server.getInstance().broadCastAlert("Server is currently experiencing some lag. Since last lag report "+(lag-serverLagReported)+" seconds.",false,(byte)0);
                serverLagReported = lag;
                serverLagReportTime = now;
            }
        }
        serverLag = lag;
    }

    private static Scheduler instance = null;

    public static Scheduler getInstance() {
        if(instance==null) instance = new Scheduler();
        return instance;
    }

    public static class TimePeriod {
        public final int shour;
        public final int sminute;
        public final int sday;
        public final int smonth;
        public final int syear;
        public final int ehour;
        public final int eminute;
        public final int eday;
        public final int emonth;
        public final int eyear;
        private long lastCheck;
        private boolean isNow;

        public TimePeriod(final int shour,final int sminute,final int sday,final int smonth,final int syear,final int ehour,final int eminute,final int eday,final int emonth,final int eyear) {
            this.shour = shour;
            this.sminute = sminute;
            this.sday = sday;
            this.smonth = smonth;
            this.syear = syear;
            this.ehour = ehour;
            this.eminute = eminute;
            this.eday = eday;
            this.emonth = emonth;
            this.eyear = eyear;
            this.lastCheck = 0L;
            this.isNow = false;
        }

        public boolean isNow() {
            if(lastCheck<=System.currentTimeMillis()-60000L) {
                int sy = syear, ey = eyear;
                if(sy==-1 || ey==-1) {
                    sy = ey = Year.now().getValue();
                    if(emonth<smonth || (emonth==smonth && (eday<sday || (eday==sday && (ehour<shour || (ehour==shour && eminute<sminute))))))
                        ++ey;
                }
                isNow = WurmCalendar.nowIsBetween(shour,sminute,sday,smonth,sy,ehour,eminute,eday,emonth,ey);
                lastCheck = System.currentTimeMillis();
            }
            return isNow;
        }
    }

    private interface ScheduleEvent {
        boolean action(ScheduleTask task);
    }

    private class ScheduleTask {
        int id;
        int type;
        String name;
        long wurmId;
        int start;
        int delay;
        String message;
        int color;
        int min;
        int max;
        long created;
        int counter;
        long initialTime;
        ScheduleEvent stopEvent;

        public ScheduleTask(int type,String name,long wurmId,int start,int delay,String message) {
            this(type,name,wurmId,start,delay,message,0,0,0,System.currentTimeMillis(),null);
        }

        public ScheduleTask(int type,String name,long wurmId,int start,int delay,String message,ScheduleEvent stopEvent) {
            this(type,name,wurmId,start,delay,message,0,0,0,System.currentTimeMillis(),stopEvent);
        }

        public ScheduleTask(int type,String name,long wurmId,int start,int delay,String message,int color,int min,int max,ScheduleEvent stopEvent) {
            this(type,name,wurmId,start,delay,message,color,min,max,System.currentTimeMillis(),stopEvent);
        }

        public ScheduleTask(int type,String name,long wurmId,int start,int delay,String message,int color,int min,int max,long created,ScheduleEvent stopEvent) {
            this.id = -1;
            this.type = type;
            this.name = name;
            this.wurmId = wurmId;
            this.start = start;
            this.delay = delay;
            this.message = message;
            this.color = color;
            this.min = min;
            this.max = max;
            this.created = created;
            this.counter = 0;
            this.initialTime = getInitialTime(this.start);
            this.stopEvent = stopEvent;
        }

        public ScheduleTask(ResultSet rs,ScheduleEvent stopEvent) throws SQLException {
            this.id = rs.getInt(1);
            this.type = rs.getInt(2);
            this.name = rs.getString(3);
            this.wurmId = -10L;
            this.start = rs.getInt(4);
            this.delay = rs.getInt(5);
            this.message = rs.getString(6);
            this.color = rs.getInt(7);
            this.min = rs.getInt(8);
            this.max = rs.getInt(9);
            this.created = rs.getLong(10);
            this.counter = 0;
            this.initialTime = getInitialTime(this.start);
            this.stopEvent = stopEvent;
        }

        public boolean save() {
            try(Connection con = ModSupportDb.getModSupportDb();
                PreparedStatement ps = con.prepareStatement("INSERT INTO AWA_SCHEDULE (ID,TYPE,NAME,START,DELAY,MESSAGE,COLOR,MIN,MAX,CREATED) VALUES(NULL,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1,this.type);
                ps.setString(2,this.name);
                ps.setInt(3,this.start);
                ps.setInt(4,this.delay);
                ps.setString(5,this.message);
                ps.setInt(6,this.color);
                ps.setInt(7,this.min);
                ps.setInt(8,this.max);
                ps.setLong(9,this.created);
                int rows = ps.executeUpdate();
                if(rows==0) throw new SQLException("Creating task failed, no rows affected.");
                try(ResultSet rs = ps.getGeneratedKeys()) {
                    if(rs.next()) this.id = rs.getInt(1);
                    else throw new SQLException("Creating task failed, no ID obtained.");
                }
            } catch(SQLException e) {
                logger.log(Level.SEVERE,"Failed to insert schedule tasks.",e);
                return false;
            }
            return true;
        }

        public boolean stop() {
            if(stopEvent!=null) {
                return stopEvent.action(this);
            }
            return true;
        }

        public boolean delete() {
            try(Connection con = ModSupportDb.getModSupportDb();
                PreparedStatement ps = con.prepareStatement("DELETE FROM AWA_SCHEDULE WHERE ID=?")) {
                ps.setInt(1,this.id);
                int rows = ps.executeUpdate();
                if(rows==0) throw new SQLException("Deleting task failed, no rows affected.");
            } catch(SQLException e) {
                logger.log(Level.SEVERE,"Failed to delete schedule tasks.",e);
                return false;
            }
            return true;
        }

        private long getInitialTime(int start) {
            long time = System.currentTimeMillis()/60000L;
            int minutes = (int)(time%60L);
            return time+(minutes<start? start-minutes : 60+start-minutes);
        }

        public boolean isRealMinuteTask() {
            return (type&REAL_MINUTE_TASK)!=0;
        }

        public boolean isWurmHourTask() {
            return (type&WURM_HOUR_TASK)!=0;
        }

        public boolean isEveryTickTask() {
            return (type&EVERY_TICK_TASK)!=0;
        }
    }


    private class RealMinuteHandler implements Runnable {
        private Map<String,ScheduleTask> tasks;
        private long start;
        private long time;
        private long wurmTime;
        private int schedulerLag;

        public RealMinuteHandler(Scheduler scheduler) {
            this.tasks = scheduler.tasks;
            this.start = 0L;
            this.time = 0L;
            this.wurmTime = 0L;
            this.schedulerLag = 0;
        }

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            if(time==0L) start = now/60000L;

            long t = start+time;
            if((t%60L)==0L) tickRealHour(start+time);

            if((WurmCalendar.currentTime/3600L)>(wurmTime/3600L)) tickWurmHour();

            Iterator<ScheduleTask> iter = tasks.values().iterator();
            while(iter.hasNext()) {
                ScheduleTask task = iter.next();
                if(task.isRealMinuteTask()) {
                    if(!task.isEveryTickTask() && (t<task.initialTime || ((t-task.initialTime)%task.delay)!=0)) continue;
                    logger.info("Schedule task <"+task.name+"> time: "+time+", start: "+task.start+", delay: "+task.delay);
                    switch(task.type) {
                        case BROAD_CAST_TASK:
                            Server.getInstance().broadCastSafe(task.message,false,(byte)0);
                            break;
                    }
                    ++task.counter;
                }
            }
            ++time;
            wurmTime = WurmCalendar.currentTime;

            long runLoopTime = System.currentTimeMillis()-now;
            if(runLoopTime>1000L) {
                schedulerLag += (int)(runLoopTime/1000L);
                logger.info("Elapsed time ("+runLoopTime+"ms) for this loop was more than 1 second so adding it to the lag count, which is now: "+schedulerLag);
            }
        }
    }


    private ScheduledExecutorService scheduler;
    @SuppressWarnings("unused")
    private ScheduledFuture<?> realMinuteHandle;
    private Map<String,ScheduleTask> tasks;

    private SpawnArea[] areas;

    private Scheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        realMinuteHandle = null;
        tasks = new HashMap<>();
        areas = new SpawnArea[0];
    }

    public void start() {
        try(Connection con = ModSupportDb.getModSupportDb();
            PreparedStatement ps = con.prepareStatement("SELECT ID,TYPE,NAME,START,DELAY,MESSAGE,COLOR,MIN,MAX,CREATED FROM AWA_SCHEDULE");
            ResultSet rs = ps.executeQuery()) {
            ScheduleTask task;
            while(rs.next()) {
                task = new ScheduleTask(rs,t -> t.delete());
                tasks.put(task.name,task);
                logger.info("Loading schedule task ["+task.id+"] <"+task.name+">");
            }
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to load schedule tasks.",e);
        }
        long start, delay;

        start = 60000L-(System.currentTimeMillis()%60000L);
        delay = 60000L;
        logger.info("Starting real minute scheduler (start: "+start+", current: "+System.currentTimeMillis()+", server: "+Server.getStartTime()+")...");
        realMinuteHandle = scheduler.scheduleAtFixedRate(new RealMinuteHandler(this),start,delay,TimeUnit.MILLISECONDS);
    }

    public void tickRealHour(long time) {
        long t1 = System.currentTimeMillis(),t2 = t1,t3 = t1,t4;
        try {

            Cache.getInstance().gc();
            t2 = System.currentTimeMillis();
            if(Config.usePlayerSettings) {
                try(Connection con = ModSupportDb.getModSupportDb()) {
                    PlayersData.getInstance().savePlayersData(con);
                    t3 = System.currentTimeMillis();
                } catch(SQLException e) {
                    logger.log(Level.SEVERE,"Failed to save data: "+e.getMessage(),e);
                }
            }

        } catch(Exception e) {
            logger.log(Level.WARNING,"Failed tickRealHour: "+e.getMessage(),e);
        }
        t4 = System.currentTimeMillis();
        logger.info("Tick real hour poll. ["+(t4-t1)+" milliseconds, 1:"+(t2-t1)+", 2:"+(t3-t2)+"]");
    }

    /**
     * Called from within the tickSecond method of WurmCalendar (overhead is smaller than running an external thread).
     */
    @SuppressWarnings("unused")
    public void tickWurmHour() {
        long t1 = System.currentTimeMillis(),t2 = t1,t3 = t1,t4 = t1,t5;
        long t = WurmCalendar.getCurrentTime();
        try {
            Spawner spawner = Spawner.getInstance();
            if(areas!=null)
                for(int i = 0; i<areas.length; ++i)
                    spawner.spawn(areas[i]);
            t2 = System.currentTimeMillis();

            int hour = WurmCalendar.getHour();
            if(Config.useInventorySupplier && hour==6) {
                InventorySupplier.getInstance().update();
            }
            if(Config.usePlagues && (hour%4)==3) {
                if(Plague.shouldSpreadPlague()) {
                    logger.info("Plague check - max creatures: "+Servers.localServer.maxCreatures+", total creatures: "+Creatures.getInstance().getNumberOfCreatures());
                    Plague.getInstance().spreadPlague(Config.plagueRadius);
                }
            }
            t3 = System.currentTimeMillis();

            Iterator<ScheduleTask> iter = tasks.values().iterator();
            while(iter.hasNext()) {
                ScheduleTask task = iter.next();
                if(task.isWurmHourTask()) {
                    switch(task.type) {
                        case DESTROY_CREATURE_TASK:
                            if(--task.delay<=0) {
                                task.stop();
                                iter.remove();
                            }
                            break;
                    }
                    ++task.counter;
                }
            }
            t4 = System.currentTimeMillis();

        } catch(Exception e) {
            logger.log(Level.WARNING,"Failed tickWurmHour: "+e.getMessage(),e);
        }
        t5 = System.currentTimeMillis();
        logger.info("Tick wurm hour poll. ["+(t5-t1)+" milliseconds, 1:"+(t2-t1)+", 2:"+(t3-t2)+", 3:"+(t4-t3)+"]");
    }

    public void sendList(Communicator communicator) {
        Player player = communicator.getPlayer();
        boolean gm = player.getPower()>=MiscConstants.POWER_DEMIGOD;
        int n = 0;
        for(ScheduleTask task : tasks.values()) {
            if(!gm && gmTasks.contains(task.type)) continue;
            String type = "---";
            String text = "";
            switch(task.type) {
                case BROAD_CAST_TASK:
                    type = "Broadcast";
                    text = StringUtils.format("#%06X",task.color)+" \""+task.message+"\"";
                    break;
                case DESTROY_CREATURE_TASK:
                    type = "Destroy creature";
                    break;
            }
            if(n==0) communicator.sendSafeServerMessage("Scheduled tasks:");
            communicator.sendSafeServerMessage(type+" <"+task.name+"> [start: "+task.start+", delay: "+task.delay+"] "+text);
            ++n;
        }
        if(n==0) communicator.sendSafeServerMessage("No tasks are running.");
    }

    public void stop(Communicator communicator,String name) {
        Player player = communicator.getPlayer();
        boolean gm = player.getPower()>=MiscConstants.POWER_DEMIGOD;
        int type = -1,i, n = 0;
        String key = null;
        if(name!=null) {
            if(name.equals(broadCastKey)) {
                type = BROAD_CAST_TASK;
                name = null;
                key = "broadcast";
            } else if(name.equals(destroyCreatureKey)) {
                type = DESTROY_CREATURE_TASK;
                name = null;
                key = "destroy creature";
            }
        }
        Iterator<ScheduleTask> iter = tasks.values().iterator();
        while(iter.hasNext()) {
            ScheduleTask task = iter.next();
            if((type==-1 || type==task.type) && (name==null || name.equals(task.name))) {
                if(!gm && gmTasks.contains(task.type)) continue;
                if(task.stop()) {
                    iter.remove();
                    communicator.sendSafeServerMessage((key!=null? "The "+key : "A")+" task <"+task.name+"> has stopped.");
                    ++n;
                }
            }
        }
        if(n==0) communicator.sendSafeServerMessage("No matching task was stopped.");
    }

    public void startBroadCast(Communicator communicator,String name,int start,int delay,int color,String text) {
        ScheduleTask task = new ScheduleTask(BROAD_CAST_TASK,name,-10L,start,delay,text,color,0,0,t -> t.delete());
        if(task.save()) {
            tasks.put(task.name,task);
            communicator.sendSafeServerMessage("Starting a scheduled broadcast message every "+task.delay+" minutes: "+task.message);
        }
    }

    public void startDestroyCreature(Creature creature,int hours,String text) {
        if(creature==null || creature.isDead()) return;
        ScheduleTask task = new ScheduleTask(DESTROY_CREATURE_TASK,Long.toString(creature.getWurmId()),creature.getWurmId(),0,hours,text,t -> {
            try {
                Creature c = Creatures.getInstance().getCreature(t.wurmId);
                if(c!=null && !c.isDead()) {
                    if(t.message!=null)
                        Server.getInstance().broadCastMessage(t.message,c.getTileX(),c.getTileY(),c.isOnSurface(),10);
                    c.destroy();
                }
            } catch(NoSuchCreatureException e) {}
            return true;
        });
        tasks.put(task.name,task);
    }
}

