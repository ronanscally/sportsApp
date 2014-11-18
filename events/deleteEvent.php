<?php

// deleteProfile.php
//
// Deletes the users details from the database
//
// Returns a JSON object

    // Include database connection class
    include '../dbConnect.php';
    include '../postData.php';

    // Get JSON POST and decode
    $received = file_get_contents("php://input");
    $decoded = json_decode($received, true);

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // Check for required fields
    if ($decoded['eventID']) {
        $eventID = $decoded['eventID'];

        // Mysql delete row with matched id
        // $query =  'DELETE FROM `events` WHERE eventID = '.$eventID.';';
        $query  = 'DELETE `events`, `eventLocation` ';
        $query .= 'FROM `events` ';
        $query .= 'INNER JOIN `eventLocation` on events.eventID=eventLocation.eventID ';
        $query .= 'WHERE events.eventID='.$eventID.' ';

        $result = mysqli_query($connection->myconn, $query);

        // Check if row exists or not
        if (mysqli_affected_rows($connection->myconn)) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Event successfully deleted. ",
            );
        // If row does not exist, return -1
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "No database entry for this event. ",
            );
        }
    }
    // If no event field in JSON object
    else {
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
