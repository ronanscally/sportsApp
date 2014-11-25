<?php

// createGroup.php
//
// Creates a group, and adds the group creator as administrator
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
    if ($decoded['userID'] && $decoded['groupName'] && $decoded['category']) {
        $userID     = $decoded['userID'];
        $groupName  = $decoded['groupName'];
        $category   = $decoded['category'];

        // mySQL add user to database
        $query = 'INSERT INTO groups(groupAdmin, name, category) VALUES ('.$userID.','.$groupName.', \''.$groupName.'\')';
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Friend successfully added. ",
            );

            // Fetch friends device ID and the user name of the person who added them
            $query  = 'SELECT userDevices.device, profiles.firstName, profiles.lastName ';
            $query .= 'FROM `userDevices` ';
            $query .= 'INNER JOIN profiles on profiles.userID='.$userID.' ';
            $query .= 'WHERE userDevices.userID='.$friendID.' ;';
            $result = mysqli_query($connection->myconn, $query);

            // Check if successful
            while ($row = mysqli_fetch_assoc($result)) {
                $firstName  = $row['firstName'];
                $lastName   = $row['lastName'];
                $deviceID   = $row['device'];

                $userName = $firstName.' '.$lastName;

                // Build notification
                $notification = array(
                  'message'   => $userName.' has added you! ',
                );

                // Send notification
                sendGoogleCloudMessage($notification, array($deviceID));
                echo var_dump($notification);
                echo var_dump($deviceID);
            }
        }
        // If unable to add friend...
        else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to add friend. ",
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
