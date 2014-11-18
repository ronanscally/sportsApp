<?php

// listEvents.php
//
// Retrieves events from DB
//
// Returns array of JSON objects

    // Include database connection class
    include '../dbConnect.php';
    include '../postData.php';
    include '../spatialData.php';

    // Get JSON POST and decode
    $received = file_get_contents("php://input");
    $decoded = json_decode($received, true);

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // Check for required fields
    if ($decoded['eventID']) {
      $eventID = $decoded['eventID'];

      // mySQL get records
      $query =  'SELECT events.dateTime, events.peopleRequired, events.peopleAttending, events.hostID, events.title, events.sport, AsWKT(eventLocation.location) ';
      $query .= 'FROM  `events` ';
      $query .= 'INNER JOIN  `eventLocation` ON eventLocation.eventID = events.eventID ';
      $query .= 'WHERE events.eventID='.$eventID.' ';
      $query .= 'ORDER BY (events.dateTime) ASC ';

      // Send
      $result = mysqli_query($connection->myconn, $query);

      // Fetch row
      if ($row = mysqli_fetch_assoc($result)) {
          // Get details
          $hostID   = $row['hostID'];
          $numReqd  = $row['peopleRequired'];
          $numAttn  = $row['peopleAttending'];
          $date     = $row['dateTime'];
          $title    = $row['title'];
          $location = $row['AsWKT(eventLocation.location)'];
          // $attending = $row['attendingStatus'];
          $sport    = $row['sport'];

          $coords = decodePoint($location);

          // Create JSON object
          $response[] = array (
              "success"   => "1",
              "message"   => "Event found. ",
              "eventID"   => $eventID,
              "hostID"    => $hostID,
              "numReqd"   => $numReqd,
              "numAttn"   => $numAttn,
              "eventName" => $title,
              "date"      => $date,
              "xCoord"    => $coords[0],
              "yCoord"    => $coords[1],
              "attnStatus"=> $attending,
              "sport"     => $sport,
          );
      } else {
          $response[] = array (
              "success"   => "-1",
              "message"   => "Unable to retrieve records. ",
          );
      }
    }
    // If no userID field in JSON object
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
