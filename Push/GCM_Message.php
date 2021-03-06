<?php

//------------------------------
// Payload data you want to send
// to Android device (will be
// accessible via intent extras)
//------------------------------

$data = array( 'message' => 'Hello World!' );

//------------------------------
// The recipient registration IDs
// that will receive the push
// (Should be stored in your DB)
//
// Read about it here:
// http://developer.android.com/google/gcm/
//------------------------------

$ids = array('APA91bHYrC6A04lCW04z_1TaoWxVJ-yWs21Cc9P5XLMsywsYTatQYQUyS7sWOAZFVDESL-MRnKwt8RF4uCEXVN3MuuTE6ojprmcui7ahLvH1Ac8Ubf5DEl2qCeU0hyJbS5pcchKDyTTnSYk7QrA1vSzH-q8NXc3bgw');


//------------------------------
// Call our custom GCM function
//------------------------------

sendGoogleCloudMessage(  $data, $ids );

//------------------------------
// Define custom GCM function
//------------------------------

function sendGoogleCloudMessage( $data, $ids )
{
    //------------------------------
    // Replace with real GCM API
    // key from Google APIs Console
    //
    // https://code.google.com/apis/console/
    //------------------------------

    $apiKey = 'AIzaSyCg0TnBOzhJqT2SwiwwtF-w134uhSepdDA';

    //------------------------------
    // Define URL to GCM endpoint
    //------------------------------

    $url = 'https://android.googleapis.com/gcm/send';

    //------------------------------
    // Set GCM post variables
    // (Device IDs and push payload)
    //------------------------------

    $post = array(
                    'registration_ids'  => $ids,
                    'data'              => $data,
                    );

    //------------------------------
    // Set CURL request headers
    // (Authentication and type)
    //------------------------------

    $headers = array(
                        'Authorization: key=' . $apiKey,
                        'Content-Type: application/json'
                    );

    //------------------------------
    // Initialize curl handle
    //------------------------------

    $ch = curl_init();

    //------------------------------
    // Set URL to GCM endpoint
    //------------------------------

    curl_setopt( $ch, CURLOPT_URL, $url );

    //------------------------------
    // Set request method to POST
    //------------------------------

    curl_setopt( $ch, CURLOPT_POST, true );

    //------------------------------
    // Set our custom headers
    //------------------------------

    curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );

    //------------------------------
    // Get the response back as
    // string instead of printing it
    //------------------------------

    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

    //------------------------------
    // Set post data as JSON
    //------------------------------

    curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $post ) );

    //------------------------------
    // Actually send the push!
    //------------------------------

    $result = curl_exec( $ch );

    //------------------------------
    // Error? Display it!
    //------------------------------

    if ( curl_errno( $ch ) )
    {
        echo 'GCM error: ' . curl_error( $ch );
    }

    //------------------------------
    // Close curl handle
    //------------------------------

    curl_close( $ch );

    //------------------------------
    // Debug GCM response
    //------------------------------

    echo $result;
}
?>
