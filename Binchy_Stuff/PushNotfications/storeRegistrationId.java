/**
 * Stores the registration ID and app versionCode in the application's
 * {@code SharedPreferences}.
 *
 * @param context application's context.
 * @param regId registration ID
 */
private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getGCMPreferences(context);
    int appVersion = getAppVersion(context);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PROPERTY_REG_ID, regId);
    editor.putInt(PROPERTY_APP_VERSION, appVersion);
    editor.commit();
}