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
      $userDistance_km = 5;        // This is the distance from event constraint

      // mySQL get records
      // To return date as dd/mm/yyyy
      $query  = 'SELECT events.eventID, events.sport, events.title, sports.sportID, eventCoords.lat,  eventCoords.lng,  eventCoords.name as placeName, eventCoords.address, userLocation.lat, userLocation.lng, ';
      $query .= "(DATE_FORMAT(events.startTime, '%d/%m/%Y')) AS startDate, (DATE_FORMAT(events.startTime, '%H:%i')) AS startTime, ";
      $query .= "(DATE_FORMAT(events.startTime, '%W')) AS startDay, ";
      $query .= "(DATE_FORMAT(events.endTime, '%d/%m/%Y')) AS endDate, (DATE_FORMAT(events.endTime, '%H:%i')) AS endTime, ";
      $query .= "(DATE_FORMAT(events.endTime, '%W')) AS endDay, ";
      $query .= '(ROUND( 6371 * acos( cos( radians(userLocation.lat) ) * cos( radians(eventCoords.lat ) ) * cos( radians( eventCoords.lng ) - radians(userLocation.lng) ) + sin( radians(userLocation.lat) ) * sin( radians(eventCoords.lat ) ) ) ,1) ) as distance_km ';
      $query .= 'FROM  `publicEvents` ';
      $query .= 'INNER JOIN  `events`        ON publicEvents.eventID = events.eventID ';
      $query .= 'INNER JOIN  `eventCoords`   ON publicEvents.eventID = eventCoords.eventID ';
      $query .= 'INNER JOIN  `sports` ON events.sport = sports.sport ';
      $query .= 'INNER JOIN  `userLocation` ON userLocation.userID = '.$userID.' ';
      $query .= 'WHERE events.startTime > CURRENT_TIMESTAMP() ';
      $query .= 'HAVING distance_km < '.$userDistance_km.' ';
      $query .= 'ORDER BY (events.startTime) ASC ';

      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Fetch each row
          while ($row = mysqli_fetch_assoc($result)) {
              // Get details
              $eventID  = $row['eventID'];
              $sport    = $row['sport'];
              $sportID  = $row['sportID'];
              $title    = $row['title'];
              $lat      = $row['lat'];
              $lng      = $row['lng'];
              $placeName = $row['placeName'];
              $placeAddr = $row['address'];

              // Start time
              $startDate = $row['startDate'];
              $startTime = $row['startTime'];
              $startDay  = $row['startDay'];

              // End time
              $endDate = $row['endDate'];
              $endTime = $row['endTime'];
              $endDay  = $row['endDay'];

              // Distance
              $distance  = $row['distance_km'];

              $startDate = str_replace('\\', '', $startDate);
              $endDate   = str_replace('\\', '', $endDate);

              // Create JSON object
              $response[] = array (
                  "success"   => "1",
                  "message"   => "Event found. ",
                  "eventID"   => $eventID,
                  "sport"     => $sport,
                  "sportID"   => $sportID,
                  "eventName" => $title,
                  "lat"       => $lat,
                  "lng"       => $lng,
                  "distance"  => $distance." km",
                  "placeName" => $placeName,
                  "placeAddr" => $placeAddr,

                  "startDate" => $startDate,
                  "startTime" => $startTime,
                  "startDay"  => $startDay,
                  "endDate"   => $endDate,
                  "endTime"   => $endTime,
                  "endDay"    => $endDay,
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
