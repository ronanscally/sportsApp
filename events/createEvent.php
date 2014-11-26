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
    if ($decoded['userID'] && $decoded['title'] && $decoded['sport'] && $decoded['startTime'] && $decoded['endTime'] && $decoded['numReqd']) {
        $eventHost  = $decoded['userID'];
        $eventTitle = $decoded['title'];
        $sport      = $decoded['sport'];
        $startTime  = $decoded['startTime'];
        $endTime    = $decoded['endTime'];
        $peopleReqd = $decoded['numReqd'];

        // mySQL add user to database
        $query = 'INSERT INTO events(hostID, title, sport, startTime, endTime, peopleRequired) VALUES ('.$eventHost.',\''.$eventTitle.'\',\''.$sport.'\',FROM_UNIXTIME('.$startTime.')/1000,FROM_UNIXTIME('.$endTime.')/1000,'.$peopleReqd.');';
        $result = mysqli_query($connection->myconn, $query);

        // Check if successful
        if ($result) {
            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Event successfully created. ",
            );
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "Unable to create event. ",
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

    // Get last event ID
    $query = 'SELECT LAST_INSERT_ID();';
    $result = mysqli_query($connection->myconn, $query);

    // Fetch each row (should only be 1)
    if ($row = mysqli_fetch_assoc($result)) {
        $eventID = $row['LAST_INSERT_ID()'];
    }

    // Add location data
    if ($decoded['lat'] && $decoded['lng'] && $eventID) {
      $lat = $decoded['lat'];
      $lng = $decoded['lng'];

      // mySQL add user to database
      $query = 'INSERT INTO eventLocation(eventID, location) VALUES ('.$eventID.',GEOMFROMTEXT(\'POINT('.$lng.' '.$lat.')\', 0 ));';
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Create JSON object
          $response[] = array (
              "success"   => "1",
              "message"   => "Location data successfully added. ",
              "eventID"   => $eventID,
          );
      } else {
          $response[] = array (
              "success"   => "-1",
              "message"   => "Unable to add location data to event. ",
              "eventID"   => $eventID,
          );
      }

      // mySQL add user to database
      $query = 'INSERT INTO eventCoords(eventID, lat, lng) VALUES ('.$eventID.','.$lat.','.$lng.')';
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Create JSON object
          $response[] = array (
              "success"   => "1",
              "message"   => "Location data successfully added. ",
              "eventID"   => $eventID,
          );
      } else {
          $response[] = array (
              "success"   => "-1",
              "message"   => "Unable to add location data to event. ",
              "eventID"   => $eventID,
          );
      }

      // mySQL add user to database
      $query = 'INSERT INTO eventParticipants(eventID, userID, attendingStatus) VALUES ('.$eventID.','.$eventHost.', 2)';
      $result = mysqli_query($connection->myconn, $query);
    }

    // echoing JSON response
    echo json_encode($response);

    // Close connection
    $connection->close();
?>
