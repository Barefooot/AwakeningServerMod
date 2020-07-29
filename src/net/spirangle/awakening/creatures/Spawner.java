package net.spirangle.awakening.creatures;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.*;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.creatures.*;
import com.wurmonline.server.items.*;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.spirangle.awakening.time.Scheduler.TimePeriod;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Spawner {

    private static final Logger logger = Logger.getLogger(Spawner.class.getName());

    private static final String[] maleNobleRanks = {
        "Sir","Lord","Baronet","Baron","Viscount","Earl","Count","Duke"
    };

    private static final String[] femaleNobleRanks = {
        "Lady","Dame","Baronetess","Baroness","Viscountess","Countess","Marquis","Duchess"
    };

    private static final String[] militaryRanks = {
        "Colonel","Captain","General","Admiral"
    };

    private static final String[] nobleSirNames = {
        "Alastor","Jourdain","Jewell","Gaufridus","Hugues","Ansgot","Ferrand","Arnaud","Gales","Renfred","Bartel","Fouquaut",
        "Arthur","Serell","Dreu","Harman","Thybaut","Reignald","Warner","Lambin","Randal","Ligart","Nicolas","Bertaut","Moss",
        "Wymund","Druettus","Isaac","Fouchier","Merhild","Milisendis","Gawne","Berengaria","Sarra","Dimia","Sari","Afra",
        "Mallot","Richill","Harsent","Aliss"
    };

    private static final String[] maleFirstNames = {
        "Rodriq","Merle","Felaron","Irepan","Kosmon","Dermox","Yartol","Urkol","Prazel","Gonfer","Lankal","Bertie","Collett",
        "Renaut","Ottie","Daniel","Wilkie","Joachim","Jakke","Gervas","Tuyon","Perez","Dickon","Herment","Goce","Nigs",
        "Tedric","Charles","Tobey","Will","Ernold","Norman","Hugin","Rand","Dain","Stefanus","Simon","Barat","Francis","Hubert",
        "James","Gregory","Andrew","Hartley","Sebastian","Sinclair","Alistair","Baldwin","Rupert","Porter"
    };

    private static final String[] femaleFirstNames = {
        "Yazna","Swilrin","Felian","Olias","Vilia","Konsipa","Xelien","Panilla","Herime","Nevtin","Tevis",
        "Nina","Melisentia","Giselle","Jeene","Elinor","Brigitta","Thea","Ymenia","Jocey","Crystina","Merione","Elysant",
        "Aline","Jaquelot","Albree","Muriele","Ismanna","Evelot"," Jean","Alexia","Jismond","Isota","Lylie","Auphrey",
        "Tetty","Ossenna","Rosalind","Eva","Dora","Miranda","Angela","Viola","Lucille","Clara","Maisy","Camille"
    };

    private static final String[] lastNames = {
        "Gifardus","Owen","Thiebaut","Houdart","Oudin","Raimond","Azer","Herriot","Tancard","Merek","Eudon","Janequin","Hammond",
        "Rotbertus","Adelard","Hardouin","Drugo","Anchier","Botolph","Jeffery","Ansiau","Tyon","Nigs","Mihel","Dodd","Bob",
        "Tillman","Carver","Hayward","Seales","Scully","Cantrell","Fowler","Cater","Challender","Todd","Tranter","Lush","Walker",
        "Cotter","Dexter","Spencer","Faulkner","Salter","Lorimer","Brewster","Leadbetter","Paige","Tyler","Flax","Collier","Palmer",
        "Carter","Thatcher","Heard","Hinman"
    };

    private static final String[] titles = {
        "Brave","Coward","Fat","Wicked","Dirty","Honourable","Fanatic","Virtuous","Noble","Unholy","Great","Dangerous",
        "Beautiful","Silly","Ugly","Unscrupulous","Trustworthy","Pathetic","Glorious","Humble","Pious","Doubtful",
        "Sinful"
    };

    public static class SpawnSkill {
        int skillId;
        float knowledge;

        public SpawnSkill(int skillId,float knowledge) {
            this.skillId = skillId;
            this.knowledge = knowledge;
        }
    }

    public static class SpawnDrop {
        float chance;
        int templateId;
        int recipeId;
        TimePeriod timePeriod;
        float minQl;
        float maxQl;

        public SpawnDrop(float chance,int templateId,int recipeId,TimePeriod timePeriod,float minQl,float maxQl) {
            this.chance = chance;
            this.templateId = templateId;
            this.recipeId = recipeId;
            this.timePeriod = timePeriod;
            this.minQl = minQl;
            this.maxQl = maxQl;
        }
    }


    public static class SpawnCreature {
        int templateId, num;
        String name;
        float chance;
        SpawnSkill[] skills;
        SpawnDrop[] drops;

        public SpawnCreature(int templateId,String name,float chance,int num,SpawnSkill[] skills,SpawnDrop[] drops) {
            this.templateId = templateId;
            this.name = name;
            this.chance = chance;
            this.num = num;
            this.skills = skills;
            this.drops = drops;
        }
    }


    public static class SpawnArea {
        int spawnId, cx, cy, sx, sy, ex, ey, hourOn, hourOff, max;
        boolean surface;
        SpawnCreature[] creatures;
        String onMessage, offMessage, onSound, offSound;

        public SpawnArea(int spawnId,int cx,int cy,int sx,int sy,int ex,int ey,boolean surface,int hourOn,int hourOff,
                         SpawnCreature[] creatures,int max,String onMessage,String offMessage,String onSound,String offSound) {
            this.spawnId = spawnId;
            this.cx = cx;
            this.cy = cy;
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
            this.surface = surface;
            this.hourOn = hourOn;
            this.hourOff = hourOff;
            this.creatures = creatures;
            this.max = max;
            this.onMessage = onMessage;
            this.offMessage = offMessage;
            this.onSound = onSound;
            this.offSound = offSound;
        }
    }


    private static Spawner instance = null;

    public static Spawner getInstance() {
        if(instance==null) instance = new Spawner();
        return instance;
    }

    private Spawner() {
    }

    public int spawn(SpawnArea area) {
        if(area==null || area.creatures==null || area.max==0) return 0;
        int ret = 0;
        int hour = WurmCalendar.getHour();
        if(area.hourOn==area.hourOff || area.hourOn<0 || (area.hourOn<area.hourOff? (hour >= area.hourOn && hour<area.hourOff) : (hour >= area.hourOn || hour<area.hourOff))) {
            if(hour==area.hourOn) {
                if(area.onMessage!=null)
                    Server.getInstance().broadCastMessage(area.onMessage,area.cx,area.cy,area.surface,20);
                if(area.onSound!=null) SoundPlayer.playSound(area.onSound,area.cx,area.cy,area.surface,4.0f);
                ;
            }
            try(Connection con = ModSupportDb.getModSupportDb()) {
                List<Creature> creatures = getSpawnedCreatures(con,area.spawnId);
                float f, n = Server.rand.nextFloat();
                int i, s, num, spawned = 0;
                SpawnCreature sc = null;
                CreatureTemplate ct;
                CreatureStatus cs;
                for(i = 0,f = area.creatures[i].chance; i<area.creatures.length; ++i,f += area.creatures[i].chance)
                    if(n<=f) {
                        sc = area.creatures[i];
                        break;
                    }
                if(sc==null) return 0;
                if(creatures.size()>0)
                    for(Creature c : creatures)
                        if(c.isAlive()) {
                            ++spawned;
                            ct = c.getTemplate();
                            cs = c.getStatus();
                            if(cs.age+1 >= (c.isReborn()? 14 : ct.getMaxAge())) {
                                ((DbCreatureStatus)cs).updateAge(1);
                            }
                        }
                if(spawned<area.max) {
                    try(PreparedStatement ps = con.prepareStatement("INSERT INTO AWA_CREATURES (WURMID,SPAWNID,POSX,POSY,ROTATION,CREATED) VALUES(?,?,?,?,?,?)")) {
                        CreatureTemplate template;
                        try {
                            template = CreatureTemplateFactory.getInstance().getTemplate(sc.templateId);
                            num = sc.num;
                            if(spawned+num>area.max) num = area.max-spawned;
                            for(i = 0; i<num; ++i) {
                                byte gender = (byte)(Server.rand.nextBoolean()? MiscConstants.SEX_FEMALE : MiscConstants.SEX_MALE);
                                String name = null;
                                if(sc.name!=null) {
                                    if(" @undead ".indexOf(sc.name)!=-1) {
                                        name = gender==MiscConstants.SEX_FEMALE? getFemaleName() : getMaleName();
                                    } else {
                                        name = sc.name;
                                    }
                                }
                                Creature creature = spawnCreature(ps,template,area,sc,name,gender,(byte)-1,area.spawnId);
                                if(creature!=null) {
                                    if(sc.skills!=null) {
                                        for(s = 0; s<sc.skills.length; ++s)
                                            creature.setSkill(sc.skills[s].skillId,sc.skills[s].knowledge);
                                    }
                                    if(sc.drops!=null) {
                                        Item dropItem = null;
                                        float rnd = Server.rand.nextFloat();
                                        for(SpawnDrop drop : sc.drops)
                                            if(rnd>drop.chance) rnd -= drop.chance;
                                            else if(drop.timePeriod==null || drop.timePeriod.isNow()) {
                                                float ql = drop.minQl+Server.rand.nextFloat()*(drop.maxQl-drop.minQl);
                                                if(drop.templateId >= 0)
                                                    dropItem = ItemFactory.createItem(drop.templateId,ql,null);
                                                else if(drop.recipeId >= 0) dropItem = getRecipe(drop.recipeId,ql,name);
                                                break;
                                            }
                                        if(dropItem!=null) creature.getInventory().insertItem(dropItem,true);
                                    }
                                    ++ret;
                                }
                            }
                            ps.executeBatch();
                        } catch(NoSuchCreatureTemplateException e) {}
                    }
                }
            } catch(Exception e) {
                logger.log(Level.SEVERE,"Failed to spawn area:"+e.getMessage(),e);
            }
        } else if(hour==area.hourOff && area.hourOn!=area.hourOff && area.hourOn >= 0) {
            destroySpawnedCreatures(area.spawnId,true);
            if(area.offMessage!=null)
                Server.getInstance().broadCastMessage(area.offMessage,area.cx,area.cy,area.surface,20);
            if(area.offSound!=null) SoundPlayer.playSound(area.offSound,area.cx,area.cy,area.surface,4.0f);
            ;
        }
        return ret;
    }

    public Creature spawnCreature(PreparedStatement ps,CreatureTemplate template,int cx,int cy,int sx,int sy,int ex,int ey,String name,byte gender,byte age,int spawnId) {
        int x = sx+(ex>sx? Server.rand.nextInt(ex-sx) : 0);
        int y = sy+(ey>sy? Server.rand.nextInt(ey-sy) : 0);
        VolaTile tile = Zones.getOrCreateTile(x,y,true);
        if((tile!=null && tile.getStructure()!=null && tile.getStructure().isFinished()) || Terraforming.isTileUnderWater(1,x,y,true)) {
            x = cx;
            y = cy;
        }
        return spawnCreature(ps,template,(x<<2)+2.0f,(y<<2)+2.0f,-1.0f,0,name,gender,age,spawnId);
    }

    public Creature spawnCreature(PreparedStatement ps,CreatureTemplate template,SpawnArea area,SpawnCreature sc,String name,byte gender,byte age,int spawnId) {
        int x = area.sx+(area.ex>area.sx? Server.rand.nextInt(area.ex-area.sx) : 0);
        int y = area.sy+(area.ey>area.sy? Server.rand.nextInt(area.ey-area.sy) : 0);
        if(area.surface) {
            VolaTile tile = Zones.getOrCreateTile(x,y,area.surface);
            if((tile!=null && tile.getStructure()!=null && tile.getStructure().isFinished()) || Terraforming.isTileUnderWater(1,x,y,area.surface)) {
                x = area.cx;
                y = area.cy;
            }
        } else {
            if(Tiles.isSolidCave((byte)Tiles.decodeType((int)Server.caveMesh.data[x|y<<Constants.meshSize])) || Terraforming.isTileUnderWater(1,x,y,area.surface)) {
                x = area.cx;
                y = area.cy;
            }
        }
        return spawnCreature(ps,template,(x<<2)+2.0f,(y<<2)+2.0f,-1.0f,area.surface? 0 : -1,name,gender,age,spawnId);
    }

    public Creature spawnCreature(Connection con,int templateId,float posx,float posy,float rot,int layer,String name,byte gender,byte age,int spawnId) throws SQLException {
        try(PreparedStatement ps = con.prepareStatement("INSERT INTO AWA_CREATURES (WURMID,SPAWNID,POSX,POSY,ROTATION,CREATED) VALUES(?,?,?,?,?,?)")) {
            Creature creature = spawnCreature(ps,templateId,posx,posy,rot,layer,name,gender,age,spawnId);
            if(creature!=null) {
                ps.executeBatch();
                return creature;
            }
        }
        return null;
    }

    public Creature spawnCreature(PreparedStatement ps,int templateId,float posx,float posy,float rot,int layer,String name,byte gender,byte age,int spawnId) {
        try {
            CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(templateId);
            return spawnCreature(ps,template,posx,posy,rot,layer,name,gender,age,spawnId);
        } catch(NoSuchCreatureTemplateException e) {
            logger.log(Level.SEVERE,"Failed to get creature template "+templateId+".",e);
        }
        return null;
    }

    public Creature spawnCreature(PreparedStatement ps,CreatureTemplate template,float posx,float posy,float rot,int layer,String name,byte gender,byte age,int spawnId) {
        if(name==null) name = template.getName();
        if(gender<0) gender = Server.rand.nextBoolean()? MiscConstants.SEX_FEMALE : MiscConstants.SEX_MALE;
        boolean reborn = false;
        switch(template.getTemplateId()) {
            case CreatureTemplateIds.SKELETON_CID:
            case CreatureTemplateIds.ZOMBIE_CID:
            case CreatureTemplateIds.GUARD_SPIRIT_GOOD_LENIENT:
            case CreatureTemplateIds.GUARD_SPIRIT_EVIL_LENIENT:
            case CreatureTemplateIds.GUARD_SPIRIT_GOOD_ABLE:
            case CreatureTemplateIds.GUARD_SPIRIT_EVIL_ABLE:
            case CreatureTemplateIds.GUARD_SPIRIT_GOOD_DANGEROUS:
            case CreatureTemplateIds.GUARD_SPIRIT_EVIL_DANGEROUS:
            case CreatureTemplateIds.WRAITH_CID:
                age = 0;
                reborn = true;
                break;
            case CreatureTemplateIds.RAT_LARGE_CID:
                age = (byte)(1+Server.rand.nextInt(20));
                break;
            default:
                if(age<0) age = 0;
        }
        if(rot<0.0f) rot = Server.rand.nextFloat()*360f;
        try {
            Creature creature = Creature.doNew(template.getTemplateId(),true,posx,posy,rot,layer,name,gender,(byte)0,(byte)0,reborn,age);
            ps.setLong(1,creature.getWurmId());
            ps.setInt(2,spawnId);
            ps.setFloat(3,posx);
            ps.setFloat(4,posy);
            ps.setFloat(5,rot);
            ps.setLong(6,System.currentTimeMillis());
            ps.addBatch();
            return creature;
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Failed to spawn creature: "+e.getMessage(),e);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public int clearArea(int sx,int sy,int ex,int ey,boolean surfaced,boolean noPlayers,boolean keepHumans) {
        int n = 0;
        List<Creature> creatures = new ArrayList<>();
        for(int x = sx; x<=ex; ++x)
            for(int y = sy; y<=ey; ++y) {
                VolaTile tile = Zones.getOrCreateTile(x,y,surfaced);
                Creature[] carr = tile.getCreatures();
                for(Creature c : carr) {
                    if(!c.isPlayer()) {
                        if(!c.isHuman() || !keepHumans) creatures.add(c);
                    } else if(noPlayers) return 0;
                }
            }
        if(creatures.size()>0) {
            for(Creature c : creatures) {
                c.destroy();
                ++n;
            }
        }
        for(int x = sx; x<=ex; ++x)
            for(int y = sy; y<=ey; ++y) {
                VolaTile tile = Zones.getOrCreateTile(x,y,surfaced);
                Item[] iarr = tile.getItems();
                for(Item i : iarr) {
                    if(i.getTemplate().isButcheredItem()) {
                        Items.destroyItem(i.getWurmId());
                    }
                }
            }
        return n;
    }

    public int destroySpawnedCreatures(int spawnId,boolean all) {
        try(Connection con = ModSupportDb.getModSupportDb()) {
            return destroySpawnedCreatures(con,spawnId,all);
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to destroy spawned creatures.",e);
        }
        return 0;
    }

    public int destroySpawnedCreatures(Connection con,int spawnId,boolean all) throws SQLException {
        int n = 0;
        try(PreparedStatement ps = con.prepareStatement("SELECT WURMID FROM AWA_CREATURES WHERE SPAWNID=?")) {
            ps.setInt(1,spawnId);
            try(ResultSet rs = ps.executeQuery()) {
                long wurmId;
                Creatures creatures = Creatures.getInstance();
                Creature creature;
                while(rs.next()) {
                    wurmId = rs.getLong(1);
                    try {
                        creature = creatures.getCreature(wurmId);
                        if(creature!=null && (creature.isAlive() || all)) {
                            creature.destroy();
                            ++n;
                        }
                    } catch(NoSuchCreatureException e) {}
                }
            }
            try(PreparedStatement ps2 = con.prepareStatement("DELETE FROM AWA_CREATURES WHERE SPAWNID=?")) {
                ps2.setInt(1,spawnId);
                ps2.execute();
            }
        }
        return n;
    }

    @SuppressWarnings("unused")
    public List<Creature> getSpawnedCreatures(int spawnId) {
        try(Connection con = ModSupportDb.getModSupportDb()) {
            return getSpawnedCreatures(con,spawnId);
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to load spawned creatures.",e);
        }
        return null;
    }

    public List<Creature> getSpawnedCreatures(Connection con,int spawnId) throws SQLException {
        try(PreparedStatement ps = con.prepareStatement("SELECT WURMID FROM AWA_CREATURES WHERE SPAWNID=?")) {
            ps.setInt(1,spawnId);
            try(ResultSet rs = ps.executeQuery()) {
                List<Creature> list = new ArrayList<>();
                long wurmId;
                Creatures creatures = Creatures.getInstance();
                Creature creature;
                while(rs.next()) {
                    wurmId = rs.getLong(1);
                    try {
                        creature = creatures.getCreature(wurmId);
                        if(creature!=null) {
                            list.add(creature);
                        }
                    } catch(NoSuchCreatureException e) {}
                }
                return list;
            }
        }
    }

    @SuppressWarnings("unused")
    public Creature getSpawnedCreature(int spawnId,boolean clearDead) {
        try(Connection con = ModSupportDb.getModSupportDb()) {
            return getSpawnedCreature(con,spawnId,clearDead);
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to load spawned creature.",e);
        }
        return null;
    }

    public Creature getSpawnedCreature(Connection con,int spawnId,boolean clearDead) throws SQLException {
        try(PreparedStatement ps = con.prepareStatement("SELECT WURMID FROM AWA_CREATURES WHERE SPAWNID=?")) {
            ps.setInt(1,spawnId);
            try(ResultSet rs = ps.executeQuery()) {
                long wurmId;
                Creature creature = null;
                while(rs.next()) {
                    wurmId = rs.getLong(1);
                    try {
                        creature = Creatures.getInstance().getCreature(wurmId);
                    } catch(NoSuchCreatureException e) {}
                    if(creature!=null) return creature;
                    if(clearDead) {
                        try(PreparedStatement ps2 = con.prepareStatement("DELETE FROM AWA_CREATURES WHERE WURMID=?")) {
                            ps2.setLong(1,wurmId);
                            ps2.execute();
                        }
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static boolean isIndoors(int x,int y) {
        VolaTile tile = Zones.getOrCreateTile(x,y,true);
        return tile!=null && tile.getStructure()!=null && tile.getStructure().isFinished();
    }

    public static String getMaleName() {
        return getName(maleNobleRanks,militaryRanks,nobleSirNames,maleFirstNames,lastNames,titles);
    }

    public static String getFemaleName() {
        return getName(femaleNobleRanks,militaryRanks,nobleSirNames,femaleFirstNames,lastNames,titles);
    }

    public static String getName(String[] nobleRanks,String[] military,String[] noble,String[] first,String[] last,String[] titles) {
        int r = Server.rand.nextInt(0x7fffffff);
        String n1, n2 = null, n3 = null;
        if(((r >> 16)%4)==0) { // Noble
            n1 = nobleRanks[r%nobleRanks.length];
            if(((r >> 8)%5)==0) n2 = first[r%first.length];
            else n2 = noble[r%noble.length];
        } else if(((r >> 20)%5)==0) { // Military
            n1 = military[(r >> 8)%military.length];
            if(((r >> 24)%5)==0) n2 = noble[r%noble.length];
            else n2 = last[r%last.length];
        } else if(((r >> 24)%4)>0) { // Citizen
            n1 = first[r%first.length];
            if(((r >> 16)%6)==0) n3 = titles[(r >> 8)%titles.length];
            else n2 = last[(r >> 8)%last.length];
        } else { // Peasant
            n1 = first[r%first.length];
        }
        if(n2==null && n3!=null) {
            n2 = n1;
            n1 = nobleRanks[0];
        }
        if(n1!=null && n2!=null && n3!=null) return n1+" "+n2+" the "+n3;
        else if(n1!=null && n2!=null) return n1+" "+n2;
        else if(n1!=null) return n1;
        return "";
    }

    public static Item getRecipe(int recipeId,float ql,String signature) {
        Recipe recipe = Recipes.getRecipeById((short)recipeId);
        if(recipe==null) return null;
        int pp = Server.rand.nextBoolean()? 1272 : 748;
        try {
            Item item = ItemFactory.createItem(pp,ql,(byte)0,recipe.getLootableRarity(),null);
            item.setInscription(recipe,signature,1550103);
            return item;
        } catch(FailedException|NoSuchTemplateException e) {
            logger.log(Level.WARNING,"Failed to create recipe.",e);
        }
        return null;
    }
}

