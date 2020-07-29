package net.spirangle.awakening;

import com.wurmonline.server.Constants;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AwakeningDb {

    private static final Logger logger = Logger.getLogger(AwakeningDb.class.getName());

    private static final String SQLITE_DB_DRIVER = "org.sqlite.JDBC";

    public static final String CREATE_SCHEDULE = "CREATE TABLE AWA_SCHEDULE ("+
                                                 " ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                 " TYPE INTEGER NOT NULL DEFAULT 0,"+
                                                 " NAME TEXT NOT NULL DEFAULT '',"+
                                                 " START INTEGER NOT NULL DEFAULT 0,"+
                                                 " DELAY INTEGER NOT NULL DEFAULT 1,"+
                                                 " MESSAGE TEXT NOT NULL DEFAULT '',"+
                                                 " COLOR INTEGER NOT NULL DEFAULT 0,"+
                                                 " MIN INTEGER NOT NULL DEFAULT 0,"+
                                                 " MAX INTEGER NOT NULL DEFAULT 0,"+
                                                 " CREATED INTEGER NOT NULL DEFAULT 0"+
                                                 ")";

    public static final String CREATE_PLAYERSDATA = "CREATE TABLE AWA_PLAYERSDATA ("+
                                                    " WURMID INTEGER NOT NULL PRIMARY KEY,"+
                                                    " NAME TEXT NOT NULL DEFAULT '',"+
                                                    " KINGDOM INTEGER NOT NULL DEFAULT 0,"+
                                                    " FLAGS INTEGER NOT NULL DEFAULT 0,"+
                                                    " DATA TEXT NOT NULL DEFAULT '',"+
                                                    " CREATED INTEGER NOT NULL DEFAULT 0,"+
                                                    " CHANGED INTEGER NOT NULL DEFAULT 0"+
                                                    ")";

    public static final String CREATE_CREATURES = "CREATE TABLE AWA_CREATURES ("+
                                                  " WURMID INTEGER NOT NULL PRIMARY KEY,"+
                                                  " SPAWNID INTEGER NOT NULL DEFAULT 0,"+
                                                  " POSX REAL NOT NULL DEFAULT 0,"+
                                                  " POSY REAL NOT NULL DEFAULT 0,"+
                                                  " ROTATION REAL NOT NULL DEFAULT 0,"+
                                                  " CREATED INTEGER NOT NULL DEFAULT 0"+
                                                  ")";
    public static final String[] CREATE_CREATURES_INDEX = {
        "CREATE INDEX AWA_CREATURES_spawnId ON AWA_CREATURES(SPAWNID)"
    };

    public static void init() {
        try(Connection con = ModSupportDb.getModSupportDb();
            Statement st = con.createStatement()) {
            createTable(con,st,"AWA_SCHEDULE",CREATE_SCHEDULE,null);
            createTable(con,st,"AWA_PLAYERSDATA",CREATE_PLAYERSDATA,null);
            createTable(con,st,"AWA_CREATURES",CREATE_CREATURES,CREATE_CREATURES_INDEX);
        } catch(SQLException e) {
            logger.log(Level.SEVERE,"Failed to create database table.",e);
        }
    }

    public static void createTable(Connection con,Statement st,String tableName,String table,String[] indexes) throws SQLException {
        if(!ModSupportDb.hasTable(con,tableName)) {
            st.execute(table);
            logger.info("Created database table '"+tableName+"'.");
            if(indexes!=null) {
                for(int i = 0; i<indexes.length; ++i)
                    st.execute(indexes[i]);
                logger.info("Created indexes for '"+tableName+"'.");
            }
        }
    }

    public static Connection getConnection(String db) {
        String dbConnection = getDbConnectionString(Constants.dbHost,db);
        try {
            Class.forName(SQLITE_DB_DRIVER);
            return DriverManager.getConnection(dbConnection);
        } catch(SQLException|ClassNotFoundException e) {
            logger.log(Level.WARNING,"Failed to initialize db connection: "+e.getMessage(),e);
        }
        return null;
    }

    public static String getDbConnectionString(String host,String db) {
        return "jdbc:sqlite:"+host+"/"+db+".db";
    }
}
