<?php
    // Test script to verify createEvent.php
    //
    // Normally the app will send userID in a JSON object, straight to createEvent.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/notifications/scanEvents.php';

    // Send JSON via POST
    $result = postJSON($destination, NULL);

    // Check POST return
    if($result == FALSE) {
        echo 'Error encountered. <br />';
    } else  {
        echo 'Successful POST. <br />';
    }

    // Print response.
    echo "<pre>$result</pre>";
?>
