private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
...
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);
    mDisplay = (TextView) findViewById(R.id.display);

    context = getApplicationContext();

    // Check device for Play Services APK.
    if (checkPlayServices()) {
        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        ...
    }
}

// You need to do the Play Services APK check here too.
@Override
protected void onResume() {
    super.onResume();
    checkPlayServices();
}

/**
 * Check the device to make sure it has the Google Play Services APK. If
 * it doesn't, display a dialog that allows users to download the APK from
 * the Google Play Store or enable it in the device's system settings.
 */
private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
        } else {
            Log.i(TAG, "This device is not supported.");
            finish();
        }
        return false;
    }
    return true;
}