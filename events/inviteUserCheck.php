<?php
    // Test script to verify createEvent.php
    //
    // Normally the app will send userID in a JSON object, straight to createEvent.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/events/inviteUser.php';

    // Generate JSON object
    $data = array (
        "userID"    => "10153331077144992",              // User ID
        "eventID"   => "1234643",               // Event ID
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

    // Print response.
    echo "<pre>$result</pre>";
?>
