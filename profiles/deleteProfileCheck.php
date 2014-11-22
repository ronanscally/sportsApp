<?php
    // Test script to verify deleteProfile.php
    //
    // Normally the app will send userID in a JSON object, straight to deleteProfile.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/profiles/deleteProfile.php';

    // Generate JSON object
    // Look up details in DB for this user ID
    $data = array (
        "userID"    => "10152504871783499",
        "firstName" => "Mark",
        "lastName"  => "Purcell",
        "dob"       => "1992-11-03",
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
    echo "====RESPONSE====";
    echo "<pre>$result</pre>";
    echo "====END==== <br />";
?>
