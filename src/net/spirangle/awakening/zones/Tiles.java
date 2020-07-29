package net.spirangle.awakening.zones;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.GrassData.FlowerType;
import com.wurmonline.mesh.GrassData.GrowthStage;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.mesh.TreeData.TreeType;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.spirangle.awakening.players.PlayerData;
import net.spirangle.awakening.players.PlayersData;
import net.spirangle.awakening.util.Cache;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tiles {

    private static final Logger logger = Logger.getLogger(Tiles.class.getName());

    private static final String rockKey = "map.mesh.rock";
    private static final String biomesKey = "map.mesh.biomes";
    private static final String caveKey = "map.mesh.cave";

    private static final FlowerType[] flowerTypes = {
        FlowerType.FLOWER_1,FlowerType.FLOWER_2,FlowerType.FLOWER_3,
        FlowerType.FLOWER_4,FlowerType.FLOWER_5,FlowerType.FLOWER_6,
        FlowerType.FLOWER_7
    };

    @SuppressWarnings("unused")
    public static long getLastPolledTiles() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }

    public static boolean checkForFarmGrowth(final VolaTile tile,final boolean farmed) {
        if(tile.getVillage()!=null) {
            PlayerData pd = PlayersData.getInstance().get(tile.getVillage().mayorName);
            if(pd==null || pd.isDeedFarming()) return farmed;
        }
        return true;
    }

    public static boolean checkForTreeSprout(final int tilex,final int tiley,final int origtype,final byte origdata) {
        final Tile tile = com.wurmonline.mesh.Tiles.getTile(origtype);
        if(tile.isTree()) {
            TreeType type = tile.getTreeType((byte)(origdata&0xF));
            if(type==TreeType.OAK || type==TreeType.WILLOW) {
                logger.info("Tiles: Prevent oak and willow from spreading by sprout.");
                return false;
            }
        }
        return true;
    }

    public static MeshIO loadMesh(String key,String path) {
        MeshIO mesh = (MeshIO)Cache.getInstance().get(key);
        if(mesh==null) {
            if(rockKey.equals(key)) path += "/rock_layer.map";
            else if(biomesKey.equals(key)) path += "/top_layer.map";
            else if(caveKey.equals(key)) path += "/map_cave.map";
            else {
                logger.log(Level.WARNING,"Not a valid key.");
                return null;
            }
            try {
                mesh = MeshIO.open(path);
            } catch(IOException e) {
                logger.log(Level.WARNING,"Could not load map data: "+e.getMessage(),e);
                return null;
            }
            logger.info("Loaded map data "+path+".");
            Cache.getInstance().put(key,mesh,1200L);
        }
        return mesh;
    }

    public static int restoreBiomes(int sx,int sy,int ex,int ey) {
        MeshIO mesh = (MeshIO)Cache.getInstance().get(biomesKey);
        if(mesh==null) {
            logger.info("Map data is not loaded for biomes.");
            return -1;
        }
        int n = 0;
        int data, origData;
        Tile tile, origTile;
        byte tileType, origTileData;
        short height;
        GrowthStage growthStage;
        FlowerType flower;
        for(int y = sy; y<=ey; ++y) {
            for(int x = sx; x<=ex; ++x) {
                data = Server.surfaceMesh.getTile(x,y);
                tileType = com.wurmonline.mesh.Tiles.decodeType(data);
                tile = com.wurmonline.mesh.Tiles.getTile(tileType);
                if(tile.isTree() || tile.isBush() || tile.isGrass() || tile.isMycelium()) {
                    origTile = getSurfaceTile(mesh,x,y);
                    if(origTile.isTree() || origTile.isBush() || origTile.isGrass() || origTile.isMycelium()) {
                        height = com.wurmonline.mesh.Tiles.decodeHeight(data);
                        origData = mesh.getTile(x,y);
                        origTileData = com.wurmonline.mesh.Tiles.decodeData(origData);
                        if(origTile.isGrass()) {
                            flower = FlowerType.decodeTileData((int)origTileData);
                            if(flower!=FlowerType.NONE && flower.getType()>FlowerType.FLOWER_7.getType()) {
                                growthStage = GrowthStage.decodeTileData((int)origTileData);
                                origTileData = GrassData.encodeGrassTileData(growthStage,flower);
                            }
                        }
                        Server.setSurfaceTile(x,y,height,origTile.getId(),origTileData);
                        ++n;
                    }
                }
            }
        }
        return n;
    }

    public static int restoreMycelium(int sx,int sy,int ex,int ey) {
        MeshIO mesh = (MeshIO)Cache.getInstance().get(biomesKey);
        if(mesh==null) {
            logger.info("Map data is not loaded for biomes.");
            return -1;
        }
        int n = 0;
        int data;
        Tile tile, origTile;
        byte tileType, tileData;
        short height;
        GrowthStage growthStage;
        for(int y = sy; y<=ey; ++y) {
            for(int x = sx; x<=ex; ++x) {
                data = Server.surfaceMesh.getTile(x,y);
                tileType = com.wurmonline.mesh.Tiles.decodeType(data);
                tile = com.wurmonline.mesh.Tiles.getTile(tileType);
                if((tile.isTree() || tile.isBush() || tile.isGrass()) && !tile.isMycelium()) {
                    origTile = getSurfaceTile(mesh,x,y);
                    if(origTile.isMycelium()) {
                        height = com.wurmonline.mesh.Tiles.decodeHeight(data);
                        tileData = com.wurmonline.mesh.Tiles.decodeData(data);
                        if(tile.isTree()) {
                            tileType = tile.getTreeType(tileData).asMyceliumTree();
                        } else if(tile.isBush()) {
                            tileType = tile.getBushType(tileData).asMyceliumBush();
                        } else {
                            tileType = Tile.TILE_MYCELIUM.id;
                            if(tile.isGrass()) {
                                growthStage = GrowthStage.decodeTileData((int)tileData);
                                tileData = GrassData.encodeGrassTileData(growthStage,FlowerType.NONE);
                            }
                        }
                        Server.setSurfaceTile(x,y,height,tileType,tileData);
                        ++n;
                    }
                }
            }
        }
        return n;
    }

    private static Tile getSurfaceTile(MeshIO mesh,int x,int y) {
        return com.wurmonline.mesh.Tiles.getTile(com.wurmonline.mesh.Tiles.decodeType(mesh.getTile(x,y)));
    }

    @SuppressWarnings("unused")
    private static short getSurfaceHeight(MeshIO mesh,int x,int y) {
        return com.wurmonline.mesh.Tiles.decodeHeight(mesh.getTile(x,y));
    }

    @SuppressWarnings("unused")
    private static byte getSurfaceData(MeshIO mesh,int x,int y) {
        return com.wurmonline.mesh.Tiles.decodeData(mesh.getTile(x,y));
    }

    public static int fixFlowers(int sx,int sy,int ex,int ey) {
        int n = 0;
        try {
            int w = Zones.worldTileSizeX;
            int h = Zones.worldTileSizeY;
            for(int x = sx; x<=ex; ++x)
                for(int y = sy; y<=ey; ++y)
                    if(fixFlower(x,y,Server.surfaceMesh.getTile(x,y))) ++n;
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Flowers fix command error: "+e.getMessage(),e);
            return -1;
        }
        return n;
    }

    private static boolean fixFlower(int x,int y,int tile) {
        byte tileType = com.wurmonline.mesh.Tiles.decodeType(tile);
        byte tileData = com.wurmonline.mesh.Tiles.decodeData(tile);
        FlowerType flower = FlowerType.decodeTileData((int)tileData);
        if(flower!=FlowerType.NONE && tileType==Tile.TILE_GRASS.id) {
            if(flower.getType()>FlowerType.FLOWER_7.getType()) {
                GrowthStage growthStage = GrowthStage.decodeTileData((int)tileData);
                flower = flowerTypes[Server.rand.nextInt(7)];
                Server.setSurfaceTile(x,y,com.wurmonline.mesh.Tiles.decodeHeight(tile),Tile.TILE_GRASS.id,
                                      GrassData.encodeGrassTileData(growthStage,flower));
                //				  Players.getInstance().sendChangedTile(x,y,true,false);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean canPlantMarker(@Nullable final Creature performer,final Item marker) {
        if(performer!=null) {
            if(marker.isRoadMarker() && marker.getBless()==null) {
                performer.getCommunicator().sendNormalServerMessage("Can only plant if the "+marker.getName()+" has been blessed.");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public static boolean mayDropDirt(final Creature performer) {
        if(!performer.isPlayer() || !performer.isOnSurface()) return true;
        int dropTileX = (int)performer.getStatus().getPositionX()+2 >> 2;
        int dropTileY = (int)performer.getStatus().getPositionY()+2 >> 2;
        Village village = Villages.getVillage(dropTileX,dropTileY,true);
        if(village==null) return true;
        VolaTile tile = Zones.getOrCreateTile(dropTileX,dropTileY,true);
        byte tk = tile.getKingdom();
        byte pk = performer.getKingdomId();
        if(tk<=0 || tk==pk) return true;
        Kingdom kingdom = Kingdoms.getKingdomOrNull(tk);
        if(kingdom==null || kingdom.isAllied(pk)) return true;
        performer.getCommunicator().sendAlertServerMessage("Terraforming here will attract too much enemy attention.");
        return false;
    }

    public static int getDistance(int x1,int y1,int x2,int y2) {
        int dx = Math.abs(x1-x2);
        int dy = Math.abs(y1-y2);
        return (int)Math.sqrt(dx*dx+dy*dy);
    }
}
