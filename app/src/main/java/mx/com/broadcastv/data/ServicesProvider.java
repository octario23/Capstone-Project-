package mx.com.broadcastv.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import mx.com.broadcastv.db.BroadcastvSQLHelper;

public class ServicesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private BroadcastvSQLHelper mOpenHelper;


    static final int LOGIN = 100;
    static final int LOGIN_TOKEN = 101;
    static final int LOGIN_ID_USER = 102;
    static final int USER = 200;
    static final int USER_ID = 201;
    static final int USER_NAME = 202;
    static final int CHANNEL = 300;
    static final int CHANNEL_ID = 301;
    static final int CHANNEL_NAME = 302;
    static final int CHANNEL_USER = 400;
    static final int CHANNEL_USER_ID = 401;
    static final int CHANNEL_USER_CHANNEL_ID = 402;

    private static final SQLiteQueryBuilder sLoginQueryBuilder;
    private static final SQLiteQueryBuilder sUserQueryBuilder;
    private static final SQLiteQueryBuilder sChannelQueryBuilder;
    private static final SQLiteQueryBuilder sChannelUserQueryBuilder;

    static {
        sLoginQueryBuilder = new SQLiteQueryBuilder();
        sLoginQueryBuilder.setTables(ServicesContract.LoginEntry.TABLE_NAME);
        sUserQueryBuilder = new SQLiteQueryBuilder();
        sUserQueryBuilder.setTables(ServicesContract.UserEntry.TABLE_NAME);
        sChannelQueryBuilder = new SQLiteQueryBuilder();
        sChannelQueryBuilder.setTables(ServicesContract.ChannelEntry.TABLE_NAME);
        sChannelUserQueryBuilder = new SQLiteQueryBuilder();
        sChannelUserQueryBuilder.setTables(
                ServicesContract.ChannelUserEntry.TABLE_NAME + " INNER JOIN " +
                        ServicesContract.ChannelEntry.TABLE_NAME +
                        " ON " + ServicesContract.ChannelUserEntry.TABLE_NAME +
                        "." + ServicesContract.ChannelUserEntry.COL_ID_CHANNEL +
                        " = " + ServicesContract.ChannelEntry.TABLE_NAME +
                        "." + ServicesContract.ChannelEntry.COL_CHANNEL_ID);

    }

    private static final String sLoginTokenSelection =
            ServicesContract.LoginEntry.TABLE_NAME +
                    "." + ServicesContract.LoginEntry.COL_TOKEN + " = ? ";

    private static final String sLoginUserSelection =
            ServicesContract.LoginEntry.TABLE_NAME +
                    "." + ServicesContract.LoginEntry.COL_ID_USER + " = ? ";

    private static final String sUserIdSelection =
            ServicesContract.UserEntry.TABLE_NAME +
                    "." + ServicesContract.UserEntry.COL_USER_ID + " = ? ";

    private static final String sUserNameSelection =
            ServicesContract.UserEntry.TABLE_NAME +
                    "." + ServicesContract.UserEntry.COL_USERNAME + " = ? ";

    private static final String sChannelIdSelection =
            ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_CHANNEL_ID + " = ? ";

    private static final String sChannelNameSelection =
            ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_NAME + " LIKE ? ";

    private static final String sChannelGroupIdSelection =
            ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_GROUP_ID + " = ? ";

    private static final String sGroupIdWithRemoveSelection =
            ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_GROUP_ID + " = ?  AND " +
                    ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_CHANNEL_ID + " <> ? ";

    private static final String sChannelIsFavoriteSelection =
            ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_IS_FAVORITE + " = ? AND " +
                    ServicesContract.ChannelEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelEntry.COL_ID_USER + " = ? ";

    private static final String sChannelUserIdSelection =
            ServicesContract.ChannelUserEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelUserEntry.COL_ID_USER + " = ? ";

    private static final String sChannelChannelIdSelection =
            ServicesContract.ChannelUserEntry.TABLE_NAME +
                    "." + ServicesContract.ChannelUserEntry.COL_ID_CHANNEL + " = ? ";


    private Cursor getLoginByToken(Uri uri, String[] projection, String sortOrder) {
        String token = ServicesContract.LoginEntry.getTokenFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sLoginTokenSelection;
        selectionArgs = new String[]{token};


        return sLoginQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getLoginByUserId(Uri uri, String[] projection, String sortOrder) {
        String userId = ServicesContract.LoginEntry.getIdUserFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sLoginUserSelection;
        selectionArgs = new String[]{userId};


        return sLoginQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getUserById(Uri uri, String[] projection, String sortOrder) {
        String id = ServicesContract.UserEntry.getUserIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sUserIdSelection;
        selectionArgs = new String[]{id};


        return sUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getUserByName(Uri uri, String[] projection, String sortOrder) {
        String username = ServicesContract.UserEntry.getUsernameFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sUserNameSelection;
        selectionArgs = new String[]{username};


        return sUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getChannelById(Uri uri, String[] projection, String sortOrder) {
        String id = ServicesContract.ChannelEntry.getChannelIdFromUri(uri);
        int groupId = ServicesContract.ChannelEntry.getGroupIdFromUri(uri);
        int removeSelf = ServicesContract.ChannelEntry.getRemoveSelfFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (groupId == 0 && removeSelf == 0) {
            selection = sChannelIdSelection;
            selectionArgs = new String[]{id};
        } else if (groupId != 0 && removeSelf == 0) {
            selectionArgs = new String[]{Integer.toString(groupId)};
            selection = sChannelGroupIdSelection;
        } else {
            selectionArgs = new String[]{Integer.toString(groupId), id};
            selection = sGroupIdWithRemoveSelection;
        }


        return sChannelQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getChannelByName(Uri uri, String[] projection, String sortOrder) {
        String name = ServicesContract.ChannelEntry.getChannelNameFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sChannelNameSelection;
        selectionArgs = new String[]{"%" + name + "%"};


        return sChannelQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteChannels(Uri uri, String[] projection, String sortOrder) {
        int isFavorite = ServicesContract.ChannelEntry.getIsFavoriteFromUri(uri);
        String userId = ServicesContract.ChannelEntry.getUserIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (userId != null) {
            selectionArgs = new String[]{Integer.toString(isFavorite), userId};
            selection = sChannelIsFavoriteSelection;


            return sChannelQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    private Cursor getChannelUserById(Uri uri, String[] projection, String sortOrder) {
        String id = ServicesContract.ChannelUserEntry.getUserIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sChannelUserIdSelection;
        selectionArgs = new String[]{id};


        return sChannelUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getChannelUserByChannel(Uri uri, String[] projection, String sortOrder) {
        String id = ServicesContract.ChannelUserEntry.getChannelIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sChannelChannelIdSelection;
        selectionArgs = new String[]{id};


        return sChannelUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ServicesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ServicesContract.PATH_LOGIN, LOGIN);
        matcher.addURI(authority, ServicesContract.PATH_LOGIN + "/*", LOGIN_TOKEN);
        matcher.addURI(authority, ServicesContract.PATH_LOGIN + "/*/*", LOGIN_ID_USER);
        matcher.addURI(authority, ServicesContract.PATH_USERS, USER);
        matcher.addURI(authority, ServicesContract.PATH_USERS + "/*", USER_ID);
        matcher.addURI(authority, ServicesContract.PATH_USERS + "/*/*", USER_NAME);
        matcher.addURI(authority, ServicesContract.PATH_CHANNELS, CHANNEL);
        matcher.addURI(authority, ServicesContract.PATH_CHANNELS + "/*", CHANNEL_ID);
        matcher.addURI(authority, ServicesContract.PATH_CHANNELS + "/*/*", CHANNEL_NAME);
        matcher.addURI(authority, ServicesContract.PATH_CHANNEL_USER, CHANNEL_USER);
        matcher.addURI(authority, ServicesContract.PATH_CHANNEL_USER + "/*", CHANNEL_USER_ID);
        matcher.addURI(authority, ServicesContract.PATH_CHANNEL_USER + "/*/*", CHANNEL_USER_CHANNEL_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BroadcastvSQLHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LOGIN:
                return ServicesContract.LoginEntry.CONTENT_TYPE;
            case LOGIN_TOKEN:
                return ServicesContract.LoginEntry.CONTENT_TYPE;
            case LOGIN_ID_USER:
                return ServicesContract.LoginEntry.CONTENT_TYPE;
            case USER:
                return ServicesContract.UserEntry.CONTENT_TYPE;
            case USER_ID:
                return ServicesContract.UserEntry.CONTENT_TYPE;
            case USER_NAME:
                return ServicesContract.UserEntry.CONTENT_ITEM_TYPE;
            case CHANNEL:
                return ServicesContract.ChannelEntry.CONTENT_TYPE;
            case CHANNEL_ID:
                return ServicesContract.ChannelEntry.CONTENT_TYPE;
            case CHANNEL_NAME:
                return ServicesContract.ChannelEntry.CONTENT_TYPE;
            case CHANNEL_USER:
                return ServicesContract.ChannelUserEntry.CONTENT_TYPE;
            case CHANNEL_USER_ID:
                return ServicesContract.ChannelUserEntry.CONTENT_TYPE;
            case CHANNEL_USER_CHANNEL_ID:
                return ServicesContract.ChannelUserEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            case LOGIN: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ServicesContract.LoginEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOGIN_TOKEN: {
                retCursor = getLoginByToken(uri, projection, sortOrder);
                break;
            }
            case LOGIN_ID_USER: {
                retCursor = getLoginByUserId(uri, projection, sortOrder);
                break;
            }
            case USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ServicesContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case USER_ID: {
                retCursor = getUserById(uri, projection, sortOrder);
                break;
            }
            case USER_NAME: {
                retCursor = getUserByName(uri, projection, sortOrder);
                break;
            }
            case CHANNEL: {
                int isFavorite = ServicesContract.ChannelEntry.getIsFavoriteFromUri(uri);
                if (isFavorite == 0) {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ServicesContract.ChannelEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                } else {
                    retCursor = getFavoriteChannels(uri, projection, sortOrder);
                }
                break;
            }
            case CHANNEL_ID: {
                retCursor = getChannelById(uri, projection, sortOrder);
                break;
            }
            case CHANNEL_NAME: {
                retCursor = getChannelByName(uri, projection, sortOrder);
                break;
            }
            case CHANNEL_USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ServicesContract.ChannelUserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CHANNEL_USER_ID: {
                retCursor = getChannelUserById(uri, projection, sortOrder);
                break;
            }
            case CHANNEL_USER_CHANNEL_ID: {
                retCursor = getChannelUserByChannel(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case LOGIN: {
                long _id = db.insert(ServicesContract.LoginEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ServicesContract.LoginEntry.buildLoginUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER: {
                long _id = db.insert(ServicesContract.UserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ServicesContract.UserEntry.buildUsersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CHANNEL: {
                long _id = db.insert(ServicesContract.ChannelEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ServicesContract.ChannelEntry.buildChannelUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CHANNEL_USER: {
                long _id = db.insert(ServicesContract.ChannelUserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ServicesContract.ChannelUserEntry.buildChannelUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
//        String app,tag;
//        if(null == selection) selection = "1";
//        switch (match){
//            case HUELLA:
//                rowsDeleted = db.delete(
//                        ServicesContract.HuellaDigitalEntry.TABLE_NAME, selection, selectionArgs
//                );
//                break;
//            case HUELLA_APP:
//                app = ServicesContract.HuellaDigitalEntry.getPackageAppFromUri(uri);
//                selection = ServicesContract.HuellaDigitalEntry.PAQUETE_APP + "= ?";
//                selectionArgs = new String[]{app};
//                rowsDeleted = db.delete(
//                        ServicesContract.HuellaDigitalEntry.TABLE_NAME,
//                        selection, selectionArgs);
//                break;
//            case HUELLA_APP_TAG:
//                app = ServicesContract.HuellaDigitalEntry.getPackageAppFromUri(uri);
//                tag = ServicesContract.HuellaDigitalEntry.getTagFromUri(uri);
//                selection = ServicesContract.HuellaDigitalEntry.PAQUETE_APP + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.TAG + "= ?";
//                selectionArgs = new String[]{app, tag};
//                rowsDeleted = db.delete(
//                        ServicesContract.HuellaDigitalEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case TRABAJADOR_WITH_ID_APP:
//                String id = ServicesContract.HuellaDigitalEntry.getIdTrabajadorFromUri(uri);
//                app = ServicesContract.HuellaDigitalEntry.getPackageAppFromUri(uri);
//                tag = ServicesContract.HuellaDigitalEntry.getTagFromUri(uri);
//                selection = ServicesContract.HuellaDigitalEntry.ID_TRABAJADOR + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.PAQUETE_APP + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.TAG + "= ?" ;
//                selectionArgs = new String[]{id, app, tag};
//                rowsDeleted = db.delete(ServicesContract.HuellaDigitalEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case TRABAJADOR_EMPLEADO_APP:
//                String idTrabajador = ServicesContract.HuellaDigitalEntry.getIdTrabajadorFromUri(uri);
//                String idEmpleado = ServicesContract.HuellaDigitalEntry.getIdEmpleadoFromUri(uri);
//                app = ServicesContract.HuellaDigitalEntry.getPackageAppFromUri(uri);
//                tag = ServicesContract.HuellaDigitalEntry.getTagFromUri(uri);
//                selection = ServicesContract.HuellaDigitalEntry.ID_TRABAJADOR + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.ID_EMPLEADO + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.PAQUETE_APP + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.TAG + "= ? ";
//                selectionArgs = new String[]{idTrabajador, idEmpleado, app, tag};
//                rowsDeleted = db.delete(ServicesContract.HuellaDigitalEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case TRABAJADOR_EMPLEADO_HUELLA_APP:
//                String idTrab = ServicesContract.HuellaDigitalEntry.getIdTrabajadorFromUri(uri);
//                String idEmpl = ServicesContract.HuellaDigitalEntry.getIdEmpleadoFromUri(uri);
//                String posicion = ServicesContract.HuellaDigitalEntry.getPosicionDedoFromUri(uri);
//                app = ServicesContract.HuellaDigitalEntry.getPackageAppFromUri(uri);
//                tag = ServicesContract.HuellaDigitalEntry.getTagFromUri(uri);
//                selection = ServicesContract.HuellaDigitalEntry.ID_TRABAJADOR + "= ? " +
//                        " AND " + ServicesContract.HuellaDigitalEntry.ID_EMPLEADO + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.POSICION_DEDO + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.PAQUETE_APP + "= ?" +
//                        " AND " + ServicesContract.HuellaDigitalEntry.TAG + "= ?";
//                selectionArgs = new String[]{idTrab, idEmpl, posicion, app, tag};
//                rowsDeleted = db.delete(ServicesContract.HuellaDigitalEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case MOTIVOS:
//                rowsDeleted = db.delete(
//                        ServicesContract.MotivosEntry.TABLE_NAME, selection, selectionArgs
//                );
//                break;
//            case MOTIVOS_ID:
//                String idMotivo = ServicesContract.MotivosEntry.getMotivoIdFromUri(uri);
//                selection = ServicesContract.MotivosEntry.ID + "= ?";
//                selectionArgs = new String [] {idMotivo};
//                rowsDeleted = db.delete(ServicesContract.MotivosEntry.TABLE_NAME,selection,
//                        selectionArgs);
//                break;
//            case EXCEPCIONES_ID:
//                String idExcepcion = ServicesContract.ExcepcionHuellaEntry.getExcepcionIdFromUri(uri);
//                selection = ServicesContract.ExcepcionHuellaEntry.ID + "= ?";
//                selectionArgs = new String [] {idExcepcion};
//                rowsDeleted = db.delete(ServicesContract.ExcepcionHuellaEntry.TABLE_NAME,selection,
//                        selectionArgs);
//                break;
//            default:
//                throw  new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        if (rowsDeleted !=0){
//            getContext().getContentResolver().notifyChange(uri,null);
//        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (sUriMatcher.match(uri)) {
            case CHANNEL: {
                rowsUpdated = db.update(
                        ServicesContract.ChannelEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LOGIN:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ServicesContract.LoginEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case USER:
                db.beginTransaction();
                int returnCnt = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ServicesContract.UserEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCnt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCnt;
            case CHANNEL:
                db.beginTransaction();
                int returnCntExc = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ServicesContract.ChannelEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCntExc++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCntExc;
            case CHANNEL_USER:
                db.beginTransaction();
                int returnCntCh = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ServicesContract.ChannelUserEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCntCh++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCntCh;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
