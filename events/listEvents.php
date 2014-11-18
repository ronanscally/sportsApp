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
      $query =  'SELECT eventParticipants.eventID, eventParticipants.attendingStatus, events.dateTime, events.sport, events.title, eventLocation.location ';
      $query .= 'FROM  `eventParticipants` ';
      $query .= 'INNER JOIN  `events`        ON eventParticipants.eventID = events.eventID ';
      $query .= 'INNER JOIN  `eventLocation` ON eventParticipants.eventID = eventLocation.eventID ';
      $query .= 'WHERE eventParticipants.userID='.$userID.' ';
      $query .= 'ORDER BY (events.dateTime) ASC ';

      // $query .=
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Fetch each row
          while ($row = mysqli_fetch_assoc($result)) {
              // Get details
              $eventID  = $row['eventID'];
              $date     = $row['dateTime'];
              $title    = $row['title'];
              $location = $row['location'];
              $sport    = $row['sport'];
              $status   = $row['attendingStatus'];

              // Create JSON object
              $response[] = array (
                  "success"   => "1",
                  "message"   => "Event found. ",
                  "eventID"   => $eventID,
                  "eventName" => $title,
                  "date"      => $date,
                  "location"  => $location,
                  "sport"  => $sport,
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
