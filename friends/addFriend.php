<?php

// addFriend.php
//
// Adds a user to the friends table
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
    if ($decoded['userID'] && $decoded['friendID']) {
        $userID   = $decoded['userID'];
        $friendID = $decoded['friendID'];

        // mySQL add user to database
        $query = 'INSERT INTO friends(friend_1, friend_2, status) VALUES ('.$userID.','.$friendID.', 1)';
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Friend successfully added. ",
            );
        } else {
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
