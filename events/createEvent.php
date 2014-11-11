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
    if ($decoded['userID'] && $decoded['title'] && $decoded['sport'] && $decoded['dateTime'] && $decoded['numReqd']) {
        $eventHost  = $decoded['userID'];
        $eventTitle = $decoded['title'];
        $sport      = $decoded['sport'];
        $dateTime   = $decoded['dateTime'];
        $peopleReqd = $decoded['numReqd'];

        // mySQL add user to database
        $query = 'INSERT INTO events(hostID, title, sport, dateTime, peopleRequired) VALUES ('.$eventHost.',\''.$eventTitle.'\',\''.$sport.'\',\''.$dateTime.'\','.$peopleReqd.');';
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response = array (
                "success"   => "1",
                "message"   => "Event successfully created. ",
            );
        } else {
            $response = array (
                "success"   => "-1",
                "message"   => "Unable to create event. ",
            );
        }
    }
    // If not enough fields in JSON object
    else {
        // required field is missing
        $response = array (
            "success" => "0",
            "message" => "Required field(s) is missing",
        );
    }

    // Get last event ID
    $query = 'SELECT LAST_INSERT_ID();';
    $result = mysqli_query($connection->myconn, $query);

    // Fetch each row (should only be 1)
    if ($row = mysqli_fetch_assoc($result)) {
        $eventID     = $row['LAST_INSERT_ID()'];
        echo 'Event id = '.$eventID;
    }

    // Add location data
    if ($decoded['xlocation'] && $decoded['ylocation']) {
      $xLocation = $decoded['xlocation'];
      $yLocation = $decoded['ylocation'];

      // mySQL add user to database
      $query = 'INSERT INTO eventLocation(eventID, location) VALUES ('.$eventID.',GEOMFROMTEXT(\'POINT('.$xLocation.' '.$yLocation.')\', 0 ));';
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Create JSON object
          $response = array (
              "success"   => "1",
              "message"   => "Location data successfully added. ",
              "eventID"   => $eventID,
          );
      } else {
          $response = array (
              "success"   => "-1",
              "message"   => "Unable to add location data to event. ",
              "eventID"   => $eventID,
          );
      }
    }

    // echoing JSON response
    echo json_encode($response);

    // Close connection
    $connection->close();
?>
