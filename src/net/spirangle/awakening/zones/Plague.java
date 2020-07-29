package net.spirangle.awakening.zones;

import com.wurmonline.math.TilePos;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.zones.FaithZone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.shared.constants.CreatureTypes;
import net.spirangle.awakening.Config;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Plague {

    private static final Logger logger = Logger.getLogger(Plague.class.getName());

    public static boolean shouldSpreadPlague() {
        return Creatures.getInstance().getNumberOfCreatures()>Servers.localServer.maxCreatures+Config.plagueMaxCreaturesMargin;
    }

    private static Plague instance = null;

    public static Plague getInstance() {
        if(instance==null) instance = new Plague();
        return instance;
    }

    private Plague() {

    }

    private TilePos getRandomPlaguePoint(int radius) {
        List<Village> villages = Arrays.asList(Villages.getVillages());
        for(int i=0; i<50; ++i) {
            int x = Server.rand.nextInt(Zones.worldTileSizeX);
            int y = Server.rand.nextInt(Zones.worldTileSizeY);
            final int t = Server.surfaceMesh.getTile(x,y);
            final int h = com.wurmonline.mesh.Tiles.decodeHeight(t);
            if(h<0 || h>3000) continue;
            if(villages.stream().anyMatch(v -> {
                if(v.isPermanent && Tiles.getDistance(v.getTokenX(),v.getTokenY(),x,y)<=300) return true;
                int sx = v.startx-v.getPerimeterSize()-radius;
                int sy = v.starty-v.getPerimeterSize()-radius;
                int ex = v.endx+v.getPerimeterSize()+radius;
                int ey = v.endy+v.getPerimeterSize()+radius;
                if(x>=sx && y>=sy && x<=ex && y<=ey) return true;
                return false;
            })) continue;
            try {
                FaithZone fz = Zones.getFaithZone(x,y,true);
                if(fz!=null && fz.getCurrentRuler()!=null) continue;
            } catch(NoSuchZoneException e) {}
            TilePos pos = new TilePos();
            pos.set(x,y);
            return pos;
        }
        return null;
    }

    public int spreadPlague(int radius) {
        TilePos pos = getRandomPlaguePoint(radius);
        if(pos==null) return 0;
        int n = 0;
        Server.getInstance().broadCastMessage("The curse has hit this area with the plague.",pos.x,pos.y,true,radius+5);
        for(int y=pos.y-radius; y<pos.y+radius; ++y) {
            if(y<0) y = 0;
            else if(y>=Zones.worldTileSizeY) break;
            for(int x=pos.x-radius; x<pos.x+radius; ++x) {
                if(x<0) x = 0;
                else if(x>=Zones.worldTileSizeX) break;
                if(Tiles.getDistance(pos.x,pos.y,x,y)>radius) continue;
                VolaTile tile = Zones.getOrCreateTile(x,y,true);
                for(Creature c : tile.getCreatures()) {
                    if(c.isDead() || c.isPlayer() || c.isHuman() || c.isSpiritGuard() || c.leader!=null || c.numattackers>0 ||
                       c.isCaredFor() || c.isBranded() || c.isBred() || c.isDominated() || c.isHitched() || c.isPregnant() ||
                       c.getStatus().getModType()!=CreatureTypes.C_MOD_NONE || c.isUndead() || c.isGhost() || c.isUnique() ||
                       Creatures.getInstance().getCreatureByType(c.getTemplate().getTemplateId())<Config.plagueMinPopulation) continue;
                    c.die(true,"Plague");
                    Creatures.getInstance().getNumberOfCreatures();
                    ++n;
                }
            }
        }
        if(n>0) {
            logger.info("The plague hit "+pos.x+", "+pos.y+" and killed "+n+" creatures.");
        }
        return n;
    }
}
