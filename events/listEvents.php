<?php

// listEvents.php
//
// Retrieves events from DB
//
// Returns array of JSON objects

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
    if ($decoded['userID']) {
      $userID = $decoded['userID'];

      // mySQL get records
      // To return date as dd/mm/yyyy
      // $query =  'SELECT eventParticipants.eventID, eventParticipants.attendingStatus, (DATE_FORMAT(events.startTime, '%d/%m/%Y')) AS eventDate, events.sport, events.title, eventLocation.location ';
      $query  = 'SELECT eventParticipants.eventID, eventParticipants.attendingStatus, events.sport, events.title, eventLocation.location, sports.sportID, eventCoords.name, eventCoords.address, ';
      $query .= "(DATE_FORMAT(events.startTime, '%d/%m/%Y')) AS date, (DATE_FORMAT(events.startTime, '%H:%i')) AS time, (DATE_FORMAT(events.startTime, '%W')) AS day ";
      $query .= 'FROM  `eventParticipants` ';
      $query .= 'INNER JOIN  `events`        ON eventParticipants.eventID = events.eventID ';
      $query .= 'INNER JOIN  `eventLocation` ON eventParticipants.eventID = eventLocation.eventID ';
      $query .= 'INNER JOIN  `eventCoords`   ON eventParticipants.eventID = eventCoords.eventID ';
      $query .= 'INNER JOIN  `sports` ON events.sport = sports.sport ';
      $query .= 'WHERE eventParticipants.userID='.$userID.' AND events.startTime > CURRENT_TIMESTAMP() ';
      $query .= 'ORDER BY (events.startTime) ASC ';
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Fetch each row
          while ($row = mysqli_fetch_assoc($result)) {
              // Get details
              $eventID  = $row['eventID'];
              $date     = $row['date'];
              $time     = $row['time'];
              $day      = $row['day'];
              $title    = $row['title'];
              $placeName = $row['name'];
              $placeAddr = $row['address'];
              $sport    = $row['sport'];
              $sportID  = $row['sportID'];
              $status   = $row['attendingStatus'];

              $date = str_replace('\\', '', $date);

              // Create JSON object
              $response[] = array (
                  "success"   => "1",
                  "message"   => "Event found. ",
                  "eventID"   => $eventID,
                  "eventName" => $title,
                  "startDate" => $date,
                  "startTime" => $time,
                  "startDay"  => $day,
                  "placeName" => $placeName,
                  "placeAddr" => $placeAddr,
                  "sport"     => $sport,
                  "sportID"   => $sportID,
              );
          }
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
