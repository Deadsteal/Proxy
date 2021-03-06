package com.shareyourproxy;


/**
 * Constant values for activity and layouts.fragment arguments, and shared preference keys.
 */
public class Constants {

    //Key used to retrieve a saved user from shared preferences.
    public static final String KEY_LOGGED_IN_USER = "com.shareyourproxy.key_logged_in_user";
    //Bundled extra key for value containing the user profile to be opened with {@link
    //IntentLauncher#launchUserProfileActivity(Activity, User, String)}.
    public static final String ARG_USER_SELECTED_PROFILE = "com.proxy.user_selected_profile";
    //Bundled extra key used to distinguish a selected group.
    public static final String ARG_SELECTED_GROUP = "com.proxy.selected_group";
    public static final String ARG_EDIT_GROUP_TYPE = "com.proxy.edit_group_type";
    public static final String ARG_LOGGEDIN_USER_ID = "com.shareyourproxy.arg_loggedin_user_id";
    public static final String ARG_SHOW_TOOLBAR = "com.shareyourproxy.arg_show_profile_toolbar";
    public static final String ARG_MAINFRAGMENT_SELECTED_TAB = "com.shareyourproxy" +
        ".arg_mainfragment_selected_tab";
    public static final String ARG_MAINGROUPFRAGMENT_WAS_GROUP_DELETED = "com.shareyourproxy" +
        ".arg_was_group_deleted";
    public static final String ARG_MAINGROUPFRAGMENT_DELETED_GROUP = "com.shareyourproxy" +
        ".arg_deleted_group";
    //Query param used in FirebaseAuthenticator and FirebaseInterceptor
    public static final String QUERY_AUTH = "auth";
    public static final String KEY_GOOGLE_PLUS_AUTH = "com.shareyourproxy.key_google_plus_auth";
    public static final String PROVIDER_GOOGLE = "google";
    public static final String MASTER_KEY = "com.shareyourproxy.application.shared_preferences_key";
    public static final String KEY_PLAY_INTRODUCTION = "com.shareyourproxy.play_introduction";
    public static final String KEY_DISMISSED_WHOOPS = "com.shareyourproxy.dismissed_whoops";
    public static final String KEY_DISMISSED_SAFE_INFO = "com.shareyourproxy.dismissed_safe_info";
    public static final String KEY_DISMISSED_SHARE_PROFILE =
        "com.shareyourproxy.dismissed_share_profile";
    public static final String KEY_DISMISSED_CUSTOM_URL =
        "com.shareyourproxy.dismissed_custom_url";
    public static final String KEY_DISMISSED_INVITE_FRIENDS =
        "com.shareyourproxy.dismissed_invite_friends";
    public static final String KEY_DISMISSED_PUBLIC_GROUP =
        "com.shareyourproxy.dismissed_public_group";
    public static final String KEY_DISMISSED_MAIN_GROUP =
        "com.shareyourproxy.dismissed_main_group";

    /**
     * Private constructor.
     */
    private Constants() {

    }
}
