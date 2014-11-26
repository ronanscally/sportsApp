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
    if ($decoded['eventID'] && $decoded['userID']) {
      $eventID = $decoded['eventID'];
      $userID  = $decoded['userID'];

      $query  = 'SELECT events.startTime, events.endTime, events.hostID, events.title, events.sport, AsWKT(eventLocation.location), sports.sportID, events.peopleRequired, COUNT(eventParticipants.userID) as numAttending, COUNT(eventParticipants.userID) = events.peopleRequired as full ';
      $query .= 'FROM  `events`  ';
      $query .= 'INNER JOIN  `eventLocation` ON eventLocation.eventID = events.eventID  ';
      $query .= 'INNER JOIN  `eventParticipants` ON eventParticipants.eventID = events.eventID  ';
      $query .= 'INNER JOIN  `sports` ON events.sport = sports.sport  ';
      $query .= 'WHERE events.eventID='.$eventID.' AND eventParticipants.attendingStatus=2 ';
      $query .= 'ORDER BY (events.startTime) ASC ';

      // Send
      $result = mysqli_query($connection->myconn, $query);

      // Fetch row
      if ($row = mysqli_fetch_assoc($result)) {
          // Get details
          $hostID   = $row['hostID'];
          $numReqd  = $row['peopleRequired'];
          $numAttn  = $row['numAttending'];
          $eventFull = $row['full'];
          $startTime= $row['startTime'];
          $endTime  = $row['endTime'];
          $title    = $row['title'];
          $location = $row['AsWKT(eventLocation.location)'];
          // $attending = $row['attendingStatus'];
          $sport    = $row['sport'];
          $sportID  = $row['sportID'];

          $coords = decodePoint($location);

          $query = 'SELECT eventParticipants.attendingStatus from eventParticipants WHERE eventParticipants.userID='.$userID.' AND eventParticipants.eventID='.$eventID;
          $result = mysqli_query($connection->myconn, $query);
          if ($row = mysqli_fetch_assoc($result)) { $attending = $row['attendingStatus']; }
          
          // Create JSON object
          $response[] = array (
              "success"   => "1",
              "message"   => "Event found. ",
              "eventID"   => $eventID,
              "hostID"    => $hostID,
              "numReqd"   => $numReqd,
              "numAttn"   => $numAttn,
              "eventFull" => $eventFull,
              "eventName" => $title,
              "startTime" => $startTime,
              "endTime"   => $endTime,
              "lng"       => $coords[0],
              "lat"       => $coords[1],
              "attnStatus"=> $attending,
              "sport"     => $sport,
              "sportID"   => $sportID,
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
