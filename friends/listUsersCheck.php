<?php
    // Test script to verify editProfile.php
    //
    // Normally the app will send userID in a JSON object, straight to editProfile.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/friends/listUsers.php';

    // Generate JSON object
    // Look up details in DB for this user ID
    $data = array (
      "userID"    => "12345678",                      // User ID
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
