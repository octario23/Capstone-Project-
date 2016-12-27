package mx.com.broadcastv.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BroadcastvSQLHelper extends SQLiteOpenHelper {

    //    TODO if any change in the DB is made, update the database_version number
    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "services.db";

    String sqlCreateLogin = "CREATE TABLE Login" +
            "(_id INTEGER PRIMARY KEY, " +
            " token TEXT, " +
            " id_user INTEGER, " +
            " created_at TEXT ) ";


    String sqlCreateUsers = "CREATE TABLE Users " +
            "(_id INTEGER PRIMARY KEY, " +
            " langId TEXT, " +
            " userId TEXT, " +
            " username TEXT, " +
            " user_logon TEXT )";

    String sqlCreateChannels = "CREATE TABLE Channels " +
            "(_id INTEGER PRIMARY KEY, " +
            " country TEXT, " +
            " description TEXT, " +
            " channel_id TEXT, " +
            " name TEXT, " +
            " language TEXT, " +
            " logo TEXT, " +
            " url TEXT, " +
            " group_id TEXT, " +
            " group_name TEXT,  " +
            " id_user INTEGER, " +
            " is_favorite INTEGER )" ;

    String sqlCreateChannelUser = "CREATE TABLE Channel_User " +
            "(_id INTEGER PRIMARY KEY, " +
            " id_user INTEGER, " +
            " id_channel INTEGER )";

// TODO remove insert sql statement when connected to webservices
//    String sqlInsertChannels =
//            "INSERT INTO Channels (country,channel_id,description,name,language,url,group_id,group_name) " +
//                    "VALUES ('MX',4,'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit ','WarCraft','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',54,'Peliculas'), " +
//                    "('MX',5,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.','Los Indestructibles','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',54,'Peliculas')," +
//                    "('MX',6,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea ','Civil War','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',56,'Peliculas')," +
//                    "('MX',7,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea ','Alicia En El Pais De Las Maravillas','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',56,'Peliculas')," +
//                    "('MX',8,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea ','Rambo III','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',54,'Peliculas')," +
//                    "('MX',9,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea ','Suicide Squad','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',56,'Peliculas')," +
//                    "('MX',10,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea ','Soy Legenda','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',54,'Peliculas')," +
//                    "('MX',11,'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea ','Linterna Verde','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',54,'Peliculas')," +
//                    "('MX',12,'Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.','Batman','SP', 'http://www.spruto.tv/get_file/8/0ebf37ecdcc7efe9ad25eb1a330b7814/215000/215324/215324.mp4',56,'Peliculas')";
//
//    String sqlInsertUser =
//            "INSERT INTO Users (userId,username) " +
//                    "VALUES (1,'octario')";
    public BroadcastvSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateLogin);
        db.execSQL(sqlCreateUsers);
        db.execSQL(sqlCreateChannels);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS Login");
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Channels");
        db.execSQL("DROP TABLE IF EXISTS Channel_User");
    }
}
