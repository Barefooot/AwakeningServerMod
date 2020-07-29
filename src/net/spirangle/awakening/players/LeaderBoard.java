package net.spirangle.awakening.players;

import com.wurmonline.server.support.JSONArray;
import com.wurmonline.server.support.JSONObject;
import com.wurmonline.server.support.JSONString;

import java.util.HashSet;


public class LeaderBoard implements JSONString {

    private PlayerData playerData;
    private boolean hide;
    private HashSet<Long> boards;

    public LeaderBoard(PlayerData playerData) {
        this.playerData = playerData;
        this.hide = false;
        this.boards = new HashSet<>();
    }

    public LeaderBoard(PlayerData playerData,JSONObject jo) {
        this.playerData = playerData;
        this.hide = jo.optBoolean("hide",false);
        this.boards = new HashSet<Long>();
        JSONArray ja = jo.optJSONArray("boards");
        if(ja!=null) {
            for(int i = 0, n = ja.length(); i<n; ++i)
                this.boards.add(ja.optLong(i));
        }
    }

    public boolean setHidden(boolean hide) {
        if(this.hide!=hide) {
            this.hide = hide;
            boards.clear();
            playerData.changed = true;
            return true;
        }
        return false;
    }

    public boolean setHidden(long board,boolean hide) {
        boolean h = boards.contains(board);
        if(this.hide) h = !h;
        if(h!=hide) {
            if(this.hide? !hide : hide) boards.add(board);
            else boards.remove(board);
            playerData.changed = true;
            return true;
        }
        return false;
    }

    public boolean isHidden() {
        return this.hide;
    }

    public boolean isHidden(long board) {
        boolean hide = boards.contains(board);
        return this.hide? !hide : hide;
    }

    @Override
    public String toJSONString() {
        StringBuilder data = new StringBuilder();
        data.append("leaderboard:{hide:").append(hide).append(",boards:[");
        int n = 0;
        for(long b : boards) {
            if(n>0) data.append(',');
            data.append(b);
            ++n;
        }
        data.append("]}");
        return data.toString();
    }
}
