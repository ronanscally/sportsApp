<?php

// createEvent.php
//
// Creates an entry in the events table in the database
// Also creates an entry in the eventLocation table
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
    if ($decoded['userID'] && $decoded['lat'] && $decoded['lng']) {
        $userID  = $decoded['userID'];
        $lat = $decoded['lat'];
        $lng = $decoded['lng'];

        // mySQL add user to database
        $query = 'UPDATE userLocation SET `lng`='.$lng.', `lat`='.$lat.' WHERE userID='.$userID;
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Location data successfully added to users profile. ",
                "userID"    => $userID,
            );
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to add location data to users profile. ",
                "userID"    => $userID,
            );
        }
        $query = 'UPDATE userLocationSpatial SET `location`=GEOMFROMTEXT(\'POINT('.$lng.' '.$lat.')\', 0 ) WHERE userID='.$userID.';';
        $result = mysqli_query($connection->myconn, $query);
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
