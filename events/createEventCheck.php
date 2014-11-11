<?php
    // Test script to verify createEvent.php
    //
    // Normally the app will send userID in a JSON object, straight to createEvent.php

    include '../postData.php';
    $destination = 'http://csiserver.ucd.ie/~09333541/sportsapp/events/createEvent.php';

    // Generate JSON object
    $data = array (
        "userID"    => "12345678",                      // User ID
        "title"     => "Soccer tonight in UCD",         // Users event title
        "sport"     => "soccer",                        // Choose from a list of sports (generated by getSportsNames.php)
        "dateTime"  => "2014-11-10 19:00",              // The supported range is '1000-01-01 00:00:00' to '9999-12-31 23:59:59' format=(YYYY-MM-DD HH:MM:SS)
        "numReqd"   => "10",                            // People required for this event
        "xlocation" => "-6.216833500000007",            // UCD coordinates
        "ylocation" => "53.3052276",                    // UCD coordinates
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
