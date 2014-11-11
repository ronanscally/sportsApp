<?php
    // Test script to verify deleteEvent.php
    //
    // Normally the app will send eventID in a JSON object, straight to deleteEvent.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/events/deleteEvent.php';

    // Generate JSON object
    // Look up details in DB for this user ID
    $data = array (
        "eventID"    => "1234630",
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
