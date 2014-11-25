<?php

// attendEvent.php
//
// Called when attending or declining an event invitation
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
    if ($decoded['userID'] && $decoded['eventID'] && $decoded['attendingStatus']) {
        // Required fields
        $userID           = $decoded['userID'];
        $eventID          = $decoded['eventID'];
        $attendingStatus  = $decoded['attendingStatus'];

        // mySQL add user to database
        $query = 'UPDATE `eventParticipants` SET `attendingStatus` =  '.$attendingStatus.' WHERE  `eventParticipants`.`userID`='.$userID.' AND  `eventParticipants`.`eventID`='.$eventID;
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Event status updated. ",
            );
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to update event status. ",
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
