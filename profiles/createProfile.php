<?php

// createProfile.php
//
// Creates an entry in the profiles table in the database
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
    if ($decoded['userID'] && $decoded['firstName'] && $decoded['lastName'] && $decoded['dob']) {
        $userID     = $decoded['userID'];
        $firstName  = $decoded['firstName'];
        $lastName   = $decoded['lastName'];
        $dob        = $decoded['dob'];

        // mySQL add user to database
        $query  = 'INSERT INTO profiles(userID, firstName, lastName, dob) VALUES ('.$userID.',\''.$firstName.'\',\''.$lastName.'\',\''.$dob.'\');';

        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Profile successfully created. ",
            );
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to create profile for this user. ",
            );
        }

        $query = 'INSERT INTO friends(friend_1, friend_2, status) VALUES ('.$userID.', '.$userID.', 0);';
        $result = mysqli_query($connection->myconn, $query);
        $query = 'INSERT INTO userLocation(userID, lat, lng) VALUES ('.$userID.', NULL, NULL);';
        $result = mysqli_query($connection->myconn, $query);
        $query = 'INSERT INTO userLocationSpatial(userID, location)  VALUES ('.$userID.',GEOMFROMTEXT(\'POINT(0 0)\', 0 ));';
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
