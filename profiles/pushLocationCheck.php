<?php
    // Test script to verify createEvent.php
    //
    // Normally the app will send userID in a JSON object, straight to createEvent.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/profiles/pushLocation.php';

    // Generate JSON object
    $data = array (
        "userID"    => "121234561234",                      // User ID
        "lng"       => "-6.216833500000007",            // UCD coordinates
        "lat"       => "53.3052276",                    // UCD coordinates
    );

    // Encode JSON data
    $jsonObject = json_encode($data);

    // Send JSON via POST
    $result = postJSON($destination, $jsonObject);

    // Check POST return
    if($result == FALSE) {
        echo 'Error encountered. <br />';
    } else  {
        echo 'Successful POST. <br />';
    }

    // Print response
    echo "<pre>$result</pre>";
?>
