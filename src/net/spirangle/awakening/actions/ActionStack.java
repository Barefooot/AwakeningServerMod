package net.spirangle.awakening.actions;

import com.wurmonline.server.Constants;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.RobotQuestion;
import com.wurmonline.server.skills.Skill;
import net.spirangle.awakening.Config;
import net.spirangle.awakening.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class ActionStack {

    private static final Logger logger = Logger.getLogger(ActionStack.class.getName());

    private static final Short[] CHECKED_ACTIONS = {
        Actions.CUT,Actions.TAME,Actions.CRUSH,Actions.PICKSEED,Actions.COMBINE,Actions.CHOP,Actions.CHOP_UP,Actions.LOCKPICK,
        Actions.TRACK,Actions.BURN,Actions.USE,Actions.PRAY,Actions.SACRIFICE,Actions.DESECRATE,Actions.DIG,Actions.MINE,
        Actions.MINEUPWARDS,Actions.MINEDOWNWARDS,Actions.CREATE,Actions.PROSPECT,Actions.REPAIR,Actions.CONTINUE_BUILDING,
        Actions.CONTINUE_BUILDING_FENCE,Actions.IMPROVE,Actions.TUNNEL,Actions.LOAD,Actions.FIRE,Actions.WINCH,Actions.WINCH5,Actions.WINCH10,
        Actions.SPELL_BLESS,Actions.SPELL_VESSEL,Actions.SPELL_DOMINATE,Actions.MIX,Actions.MIX_INFO,Actions.DREDGE,Actions.TRAP,Actions.DISARM,
        Actions.MEDITATE,Actions.PUPPETEER,Actions.SPELL_LIGHTTOKEN,Actions.SMELT,Actions.USE_PENDULUM,Actions.DIG_TO_PILE,Actions.SPELL_DISPEL,
    };
    private static final Set<Short> checkedActions = new HashSet<>(Arrays.asList(CHECKED_ACTIONS));

    private static final SimpleDateFormat fileDate = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static class Slot {
        short action;
        long time;
        long diff;

        Slot(long time) {
            this.action = -10;
            this.time = time;
            this.diff = 0L;
        }

        void update(short action,Slot slot) {
            this.action = action;
            this.time = System.currentTimeMillis();
            this.diff = this.time-slot.time;
        }

        boolean match(Slot slot) {
            long d = diff-slot.diff;
            return action!=-10 && action==slot.action && d>=-1000L && d<=1000L;
        }
    }

    public static class Pattern {
        long wurmId;
        Slot[] slots;
        int actionsCounter;
        long actionsCounterTime;
        int patternActions;
        int patternReports;
        boolean patternFound;
        boolean robotTest;
        long robotTestTime;
        long robotTestMinTime;
        int robotTestActions;
        int randomActionsCounter;
        long logActionsTime;
        PrintWriter logActionsWriter;

        Pattern(long wurmId) {
            long now = System.currentTimeMillis();
            this.wurmId = wurmId;
            this.slots = new Slot[20];
            for(int i=0; i<slots.length; ++i) slots[i] = new Slot(now-3600000L);
            actionsCounter = 0;
            actionsCounterTime = (now/1000l)*1000L;
            robotTestTime = now;
            patternFound = false;
            logActionsTime = 0L;
            logActionsWriter = null;
            clear();
        }

        void addAction(Action action) {
            long now = System.currentTimeMillis();
            ++actionsCounter;
            if(now-actionsCounterTime>=1000L) {
                actionsCounter = 0;
                actionsCounterTime = (now/1000l)*1000L;
            } else if(actionsCounter>=20) {
                String verb = action.getActionEntry().getVerbString();
                String message = action.getPerformer().getName()+" was kicked for flooding server with "+actionsCounter+" "+verb+" actions.";
                logger.log(Level.WARNING,message);
                kickPlayer((Player)action.getPerformer(),"Flooding server with actions.");
                actionsCounter = 0;
                return;
            }
            Slot s1 = slots[19],s2 = slots[0];
            s1.update(action.getNumber(),s2);
            if(s1.diff<Config.antiMacroPatternedActionTime || !checkedActions.contains(action.getNumber())) {
                s2.time = s1.time;
                return;
            }
            if(robotTest) {
                if(now-robotTestTime>=Config.antiMacroRobotPunishTime*60000L) {
                    sendClearedMessage(action.getPerformer(),"did not answer the question in time");
                    clear();
                } else if(--robotTestActions<0) {
                    clear();
                    punish(action);
                }
            }
            int c = 0,n = 0;
            final Slot s = s1;
            for(int i=1; true; ++i) {
                slots[i-1] = s1;
                if(s.match(s2)) ++n;
                if(i==slots.length) break;
                s1 = s2;
                s2 = slots[i];
            }
            if(n>=slots.length-5) {
                ++patternActions;
                c = 1;
            } else if(n>=1) {
                for(int i=2,x=slots.length/2; i<=x; ++i) {
                    if(s.match(slots[i])) {
                        n = i;
                        for(int j=i+1; j<slots.length; ++j)
                            if(slots[j].match(slots[j-i])) ++n;
                        if(n>=slots.length-5) {
                            ++patternActions;
                            c = i;
                            break;
                        }
                    }
                }
            }
            if(!robotTest) {
                if(patternActions >= Config.antiMacroPatternedActions) {
                    patternActions = 0;
                    ++patternReports;
                } else if(patternReports>0) {
                    testRobot(action,true);
                } else if(--randomActionsCounter<0) {
                    testRobot(action,false);
                }
            }

            if(logActionsWriter!=null) {
                if(logActionsTime>now) {
                    String verb = action.getActionEntry().getVerbString();
                    short number = action.getNumber();
                    logActionsWriter.println(logDate.format(new Date(now))+" - "+verb+" id="+number+", timestamp="+now+", diff="+s.diff);
                } else {
                    logActionsWriter.close();
                    logActionsWriter = null;
                }
            }
        }

        public void clear() {
            patternActions = 0;
            patternReports = 0;
            robotTestMinTime = Config.antiMacroRobotTestTimeMin*60000L+
                               Server.rand.nextInt((int)(Config.antiMacroRobotTestTimeMax-Config.antiMacroRobotTestTimeMin)*60000);
            randomActionsCounter = Config.antiMacroRandomActionsMin+
                                   Server.rand.nextInt(Config.antiMacroRandomActionsMax-Config.antiMacroRandomActionsMin);
            patternFound = false;
            robotTest = false;
        }

        public void sendClearedMessage(Creature performer,String message) {
            if(!robotTest) {
                performer.getCommunicator().sendSafeServerMessage("You failed the robot test.");
                return;
            }
            message = performer.getName()+" was cleared: "+message;
            logger.log(Level.WARNING,message);
            performer.getCommunicator().sendSafeServerMessage("You are a Wurm player, and not a robot.");
        }

        public void setLogActionsTime(Player performer,Player player,long time) {
            logActionsTime = System.currentTimeMillis()+time*60000L;
            long now = System.currentTimeMillis();
            if(logActionsWriter==null) {
                try {
                    File file = new File(Constants.dbHost+"/Logs/monitor-"+player.getName()+"-"+fileDate.format(new Date(now))+".log");
                    FileWriter fw = new FileWriter(file,true);
                    logActionsWriter = new PrintWriter(fw);
                } catch(IOException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }
            }
            if(logActionsWriter!=null) {
                logActionsWriter.println(logDate.format(new Date(now))+" "+performer.getName()+" monitors "+player.getName()+" for "+time+" minutes:");
                logActionsWriter.println("id is action id; timestamp is in milliseconds; diff is milliseconds and difference between previous action");
                logActionsWriter.println("----------------------------------------------");
                logActionsWriter.flush();
            }
        }

        private void testRobot(Action action,boolean found) {
            long now = System.currentTimeMillis();
            if(robotTest || now-robotTestTime<robotTestMinTime) return;
            Player player = (Player)action.getPerformer();
            patternFound = found;
            robotTest = true;
            robotTestActions = Config.antiMacroRobotActionsCounter;
            String name = player.getName();
            int total = patternReports*Config.antiMacroPatternedActions+patternActions;
            String verb = action.getActionEntry().getVerbString();
            short number = action.getNumber();
            String message = "Robot question sent to "+player.getName()+",  after "+total+" patterned "+verb+" ["+number+"] actions.";
            logger.log(Level.WARNING,message);
            player.getCommunicator().sendAlertServerMessage("To prevent macro cheating, you've been sent a question. "+
                                                            "Please reply before doing anything else, or do nothing and wait for "+
                                                            Config.antiMacroRobotPunishTime+" minutes, before continuing.");
            RobotQuestion question = new RobotQuestion(player,this,patternFound);
            question.sendQuestion();
            robotTestTime = now;
        }

        private void punish(Action action) {
            Player player = (Player)action.getPerformer();
            Skill[] skills = player.getSkills().getSkills();
            double skillLoss = 10.0+Server.rand.nextDouble()*20.0;
            Skill skill = null;
            List<Skill> upperSkills = new ArrayList<>(100);
            for(int i = 0; i<skills.length; ++i) {
                Skill s = skills[i];
                if(s.getType()!=4 && s.getType()!=2) continue;
                if(s.getKnowledge(0.0)>50.0) upperSkills.add(s);
                if(skill==null || s.getKnowledge()>skill.getKnowledge()) skill = s;
            }
            if(!upperSkills.isEmpty())
                skill = upperSkills.get(Server.rand.nextInt(upperSkills.size()));
            if(skillLoss+1.0>skill.getKnowledge()) skillLoss = skill.getKnowledge()-1.0;
            if(Config.antiMacroPunishing) {
                skill.setKnowledge(skill.getKnowledge()-skillLoss,false,true);
            }
            String message = " punished for cheating and lost "+StringUtils.bigdecimalFormat.format(skillLoss)+" in "+skill.getName()+".";
            player.getCommunicator().sendAlertServerMessage("You were"+message);
            logger.log(Level.WARNING,player.getName()+" was"+message);
        }
    }

    public static void kickPlayer(Player player,String reason) {
        if(player.hasLink()) {
            player.getCommunicator().sendShutDown(reason,true);
            player.setSecondsToLogout(5);
        } else {
            Players.getInstance().logoutPlayer(player);
        }
    }

    public static void addAction(Action action) {
        Creature performer = action.getPerformer();
        if(!performer.isPlayer()) return;
        if(!Config.antiMacroHandling) return;
        short nr = action.getNumber();
        if(nr==Actions.ATTACK || nr==Actions.PUSH || nr==Actions.PUSH_GENTLY || nr==Actions.PULL || nr==Actions.PULL_GENTLY || nr==Actions.TURN_ITEM || nr==Actions.TURN_ITEM_BACK) return;
        getInstance().addActionToPattern(action);
    }

    private static ActionStack instance = null;

    public static ActionStack getInstance() {
        if(instance==null) instance = new ActionStack();
        return instance;
    }

    private Map<Long,Pattern> patterns;

    private ActionStack() {
        patterns = new HashMap<>();
    }

    public Pattern getPattern(Player player) {
        if(player==null) return null;
        return patterns.get(player.getWurmId());
    }

    private void addActionToPattern(Action action) {
        long wurmId = action.getPerformer().getWurmId();
        Pattern pattern = patterns.get(wurmId);
        if(pattern==null) {
            pattern = new Pattern(wurmId);
            patterns.put(wurmId,pattern);
        }
        pattern.addAction(action);
    }
}
