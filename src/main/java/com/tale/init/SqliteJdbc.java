package com.tale.init;

import com.blade.Blade;
import com.blade.kit.IOKit;
import com.blade.kit.base.Config;
import com.tale.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Sqlite 数据库操作
 * <p>
 * Created by biezhi on 2017/3/4.
 */
public final class SqliteJdbc {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteJdbc.class);

    private SqliteJdbc() {
    }

    public static final String DB_NAME = "tale.db";
//    public static String DB_PATH = SqliteJdbc.class.getClassLoader().getResource("").getPath() + DB_NAME;
    public static String DB_PATH = "/home/sun_zhang_cn/blog/data/"+DB_NAME;
    public static String DB_SRC = "jdbc:sqlite://" + DB_PATH;


    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            LOGGER.error("加载sqlite驱动失败",e);
        }
    }

    /*public static String getDBPath(){
        if (null == DB_PATH){
            DB_PATH = TaleUtils.getDBPath()+ "/" +TaleUtils.getDBName();
        }
        return DB_PATH;
    }

    public static String getDBSrc(){
        if (null == DB_SRC){
            DB_SRC = "jdbc:sqlite://" + getDBPath();
        }
        return DB_SRC;
    }*/

    /**
     * 测试连接并导入数据库
     */
    public static void importSql() {
        try {
            if(Blade.$().isDev()){
                if (DB_PATH == null){
                    DB_PATH = System.getProperty("user.dir") + "/" + DB_NAME;
                }
                DB_SRC = "jdbc:sqlite://" + DB_PATH;
            }
            Connection con = DriverManager.getConnection(DB_SRC);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='t_options'");
            int count = rs.getInt(1);
            if (count == 0) {
                String cp = SqliteJdbc.class.getClassLoader().getResource("").getPath();
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cp + "schema.sql"), "UTF-8");
                String sql = IOKit.toString(isr);
                statement.executeUpdate(sql);
                LOGGER.info("initialize import database.");
            }
            rs.close();
            statement.close();
            con.close();
            LOGGER.info("database path is: {}", DB_PATH);
        } catch (Exception e) {
            LOGGER.error("initialize database fail", e);
        }
    }

}
