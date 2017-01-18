package mx.com.broadcastv.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ServicesContract {

    public static final String CONTENT_AUTHORITY = "mx.com.broadcastv";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LOGIN = "login";

    public static final String PATH_USERS = "users";

    public static final String PATH_CHANNELS = "channels";

    public static final String PATH_CHANNEL_USER = "user_channel";


    public static final class LoginEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOGIN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOGIN;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOGIN;

        public static final String TABLE_NAME = "Login";

        public static final String ID = "_id";

        public static final String COL_TOKEN = "token";

        public static final String COL_ID_USER = "id_user";

        public static final String COL_CREATED_AT = "created_at";


        public static Uri buildLoginUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTokenUriQuery(String token) {
            return CONTENT_URI.buildUpon().appendPath(token).build();
        }

        public static Uri buildTokenUserUriQuery(String token, String id_user) {
            return CONTENT_URI.buildUpon().appendPath(token).appendPath(id_user).build();
        }

        public static String getTokenFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        public static String getIdUserFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }


    }

    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String TABLE_NAME = "Users";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String ID = "_id";

        public static final String COL_LANG_ID = "langid";

        public static final String COL_USER_ID = "userid";

        public static final String COL_USERNAME = "username";

        public static final String COL_USER_LOGON = "user_logon";

        public static Uri buildUsersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUserIdUriQuery(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }

        public static Uri buildUsernameUriQuery(String userId, String username) {
            return CONTENT_URI.buildUpon().appendPath(userId).appendPath(username).build();
        }


        public static String getUserIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getUsernameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static final class ChannelEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHANNELS).build();

        public static final String TABLE_NAME = "Channels";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHANNELS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHANNELS;

        public static final String ID = "_id";

        public static final String COL_COUNTRY = "country";

        public static final String COL_DESCRIPTION = "description";

        public static final String COL_CHANNEL_ID = "channel_id";

        public static final String COL_NAME = "name";

        public static final String COL_LANGUAGE = "language";

        public static final String COL_LOGO = "logo";

        public static final String COL_URL = "url";

        public static final String COL_GROUP_ID = "group_id";

        public static final String COL_GROUP_NAME = "group_name";

        public static final String REMOVE_SELF = "remove_self";

        public static final String COL_ID_USER = "id_user";

        public static final String COL_IS_FAVORITE = "is_favorite";

        public static Uri buildChannelUri(String id) {
            return CONTENT_URI;
        }

        public static Uri buildChannelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildChannelIdUriQuery(String channelId) {
            return CONTENT_URI.buildUpon().appendPath(channelId).build();
        }

        public static Uri buildChannelNameUriQuery(String channelId, String channelName) {
            return CONTENT_URI.buildUpon().appendPath(channelId).appendPath(channelName).build();
        }

        public static Uri buildChannelWithGroupId(
                String channelId, int groupId) {
            return CONTENT_URI.buildUpon().appendPath(channelId)
                    .appendQueryParameter(COL_GROUP_ID, Integer.toString(groupId)).build();
        }

        public static Uri buildFavoriteChannels(
                boolean isFavorite, String userId) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COL_IS_FAVORITE, Boolean.toString(isFavorite))
                    .appendQueryParameter(COL_ID_USER, userId).build();
        }

        public static Uri buildChannelWithGroupIdAndRemoveSelf(
                int channelId, int groupId, boolean removeSelf) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(channelId))
                    .appendQueryParameter(COL_GROUP_ID, Integer.toString(groupId))
                    .appendQueryParameter(REMOVE_SELF, Boolean.toString(removeSelf)).build();
        }

        public static String getChannelIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getChannelNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static int getGroupIdFromUri(Uri uri) {
            String groupId = uri.getQueryParameter(COL_GROUP_ID);
            if (null != groupId && groupId.length() > 0)
                return Integer.parseInt(groupId);
            else
                return 0;
        }

        public static int getRemoveSelfFromUri(Uri uri) {
            String removeSelf = uri.getQueryParameter(REMOVE_SELF);
            if (null != removeSelf) {
                return 1;
            } else {
                return 0;
            }
        }

        public static int getIsFavoriteFromUri(Uri uri) {
            String favorite = uri.getQueryParameter(COL_IS_FAVORITE);
            if (null != favorite) {
                return 1;
            } else {
                return 0;
            }
        }

        public static String getUserIdFromUri(Uri uri) {
            String userId = uri.getQueryParameter(COL_ID_USER);
            if (null != userId)
                return userId;
            else
                return null;
        }
    }

    public static final class ChannelUserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHANNEL_USER).build();

        public static final String TABLE_NAME = "Channel_User";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHANNEL_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHANNEL_USER;

        public static final String ID = "_id";

        public static final String COL_ID_USER = "id_user";

        public static final String COL_ID_CHANNEL = "id_channel";


        public static Uri buildChannelUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUserIdUriQuery(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }

        public static Uri buildUserChannelIdUriQuery(String userId, String channelId) {
            return CONTENT_URI.buildUpon().appendPath(userId).appendPath(channelId).build();
        }


        public static String getUserIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getChannelIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

}
