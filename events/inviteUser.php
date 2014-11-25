<?php

// inviteUsers.php
//
// Invites users to events
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
    if ($decoded['eventID'] && $decoded['userID']) {
        $userID   = $decoded['userID'];
        $eventID  = $decoded['eventID'];

        // mySQL add user to event participants table
        $query = 'INSERT INTO eventParticipants(userID, eventID, attendingStatus) VALUES ('.$userID.','.$eventID.', 0)';
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            $response[] = array (
                "success"   => "1",
                "message"   => "Participant invited. ",
            );

            // Send notification to invited users device
            $query  = 'SELECT userDevices.device, events.title ';
            $query .= 'FROM `userDevices` ';
            $query .= 'INNER JOIN events on events.eventID='.$eventID.' ';
            $query .= 'WHERE userDevices.userID='.$userID.' ;';
            $result = mysqli_query($connection->myconn, $query);

            // Check if successful
            while ($row = mysqli_fetch_assoc($result)) {
                $eventName  = $row['title'];
                $deviceID   = $row['device'];

                $message = 'You have been invited to '.$eventName.'! ';

                // Build notification
                $notification = array(
                  'eventInvite' => $userName.' has added you! ',
                );

                // Send notification
                sendGoogleCloudMessage($notification, array($deviceID));
            }




        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to invite user to event. ",
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
