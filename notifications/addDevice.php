<?php

// addDevice.php
//
// Adds a device in the database for a user
//
// Returns a JSON object

    // Include database connection class
    include '../dbConnect.php';
    include '../postData.php';

    // Get JSON POST and decode
    $received   = file_get_contents("php://input");
    $decoded    = json_decode($received, true);

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // Check for required fields
    if ($decoded['userID'] && $decoded['deviceID']) {
        $userID   = $decoded['userID'];
        $deviceID = $decoded['deviceID'];

        // mySQL add user to database
        $query = 'INSERT INTO userDevices(userID, device) VALUES ('.$userID.',\''.$deviceID.'\')';
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Device successfully added. ",
            );
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to add device. ",
            );
        }
    }
    // If not enough fields in JSON object
    else {
        // required field is missing
        $response[] = array (
            "success" => "0",
            "message" => "Required field(s) is missing",
        );
    }

    // echoing JSON response
    echo json_encode($response);

    // Close connection
    $connection->close();
?>
