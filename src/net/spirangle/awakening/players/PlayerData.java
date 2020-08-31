package net.spirangle.awakening.players;


import com.google.common.base.Strings;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.support.JSONException;
import com.wurmonline.server.support.JSONObject;
import com.wurmonline.server.support.JSONString;
import com.wurmonline.server.support.JSONTokener;

import java.util.logging.Level;
import java.util.logging.Logger;


public class PlayerData implements JSONString {

    private static final Logger logger = Logger.getLogger(PlayerData.class.getName());

    private static final long FLAG_PVP                   = 0x0000000000000002L;
    private static final long FLAG_NO_DEED_FARMING       = 0x0000000000001000L;
    private static final long FLAG_KINGDOM_CHANGE_LOGIN  = 0x0010000000000000L;

    @SuppressWarnings("unused")
    public static String getHoverText(final Player player,final String hoverText) {
        PlayerData pd = PlayersData.getInstance().get(player);
        if(pd.isPvP()) return hoverText.length()==0? "[PvP]" : "[PvP] "+hoverText;
        return hoverText;
    }

    public final long wurmId;
    public final String name;
    private byte kingdom;
    private long flags;
    private LeaderBoard leaderBoard;
    private int deedVisibility;
    boolean changed;

    PlayerData(long wurmId,String name,byte kingdom,long flags,String data) {
        this.wurmId = wurmId;
        this.name = name;
        this.kingdom = kingdom;
        this.flags = flags;
        this.leaderBoard = null;
        this.deedVisibility = 0;
        try {
            JSONTokener jt = new JSONTokener(data);
            JSONObject jo = new JSONObject(jt);
            JSONObject jo2;
            if((jo2 = jo.optJSONObject("leaderboard"))!=null) {
                this.leaderBoard = new LeaderBoard(this,jo2);
            }
            this.deedVisibility = jo.optInt("deedVisibility",0);
        } catch(JSONException e) {
            logger.log(Level.WARNING,"Failed to parse player data for "+wurmId,e);
        }
        this.changed = false;
    }

    PlayerData(Player player) {
        this.wurmId = player.getWurmId();
        this.name = player.getName();
        this.kingdom = player.getKingdomId();
        this.flags = 0L;
        this.leaderBoard = null;
        this.deedVisibility = 0;
        this.changed = false;
    }

    @Override
    public String toJSONString() {
        JSONString[] elements = {
            this.leaderBoard
        };
        StringBuilder data = new StringBuilder("{");
        int n = 0;
        for(JSONString e : elements) {
            if(e==null) continue;
            String json = e.toJSONString();
            if(Strings.isNullOrEmpty(json)) continue;
            if(n>0) data.append(',');
            data.append(json);
            ++n;
        }
        if(this.deedVisibility!=0) {
            if(n>0) data.append(',');
            data.append("deedVisibility:"+this.deedVisibility);
        }
        data.append("}");
        return data.toString();
    }

    public LeaderBoard getLeaderBoard() {
        if(leaderBoard==null) leaderBoard = new LeaderBoard(this);
        return leaderBoard;
    }

    public byte getKingdom() {
        return kingdom;
    }

    public boolean setKingdom(byte kingdom) {
        if(kingdom==this.kingdom) return false;
        this.kingdom = kingdom;
        return changed = true;
    }

    public long getFlags() {
        return flags;
    }

    public int getDeedVisibility() {
        return deedVisibility;
    }

    public boolean setDeedVisibility(int deedVisibility) {
        if(this.deedVisibility==deedVisibility) return false;
        this.deedVisibility = deedVisibility;
        return changed = true;
    }

    public boolean setPvP(boolean pvp) {
        if(pvp==isPvP()) return false;
        flags ^= FLAG_PVP;
        return changed = true;
    }

    public boolean isPvP() {
        return (flags&FLAG_PVP)!=0;
    }

    public boolean setNoDeedFarming(boolean noDeedFarming) {
        if(noDeedFarming==isNoDeedFarming()) return false;
        flags ^= FLAG_NO_DEED_FARMING;
        return changed = true;
    }

    public boolean isDeedFarming() {
        return (flags&FLAG_NO_DEED_FARMING)==0;
    }

    public boolean isNoDeedFarming() {
        return (flags&FLAG_NO_DEED_FARMING)!=0;
    }

    public boolean setKingdomChangeLogin(boolean login) {
        if(login==isKingdomChangeLoginDone()) return false;
        flags ^= FLAG_KINGDOM_CHANGE_LOGIN;
        return changed = true;
    }

    public boolean isKingdomChangeLoginDone() {
        return (flags&FLAG_KINGDOM_CHANGE_LOGIN)!=0;
    }
}
