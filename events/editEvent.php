<?php

// editEvent.php
//
// Changes the users details in the database
//
// Returns a JSON object

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
    if ($decoded['eventID']) {
        $eventID  = $decoded['eventID'];
        $title    = $decoded['title'];
        $sport    = $decoded['sport'];
        $startTime = $decoded['startTime'];
        $endTime = $decoded['endTime'];
        $numReqd  = $decoded['numReqd'];

        // mySQL check for user in database
        $query = 'UPDATE `events` SET title=\''.$title.'\', sport=\''.$sport.'\', startTime=\''.$startTime.'\', endTime=\''.$endTime.'\', peopleRequired = \''.$numReqd.'\' WHERE eventID = '.$eventID.';';
        $result = mysqli_query($connection->myconn, $query);

        // Check if row exists or not
        if (mysqli_affected_rows($connection->myconn)) {
            // Create JSON object
            $response[] = array (
                "success"  => "1",
                "message"  => "Event updated successfully. ",
                "eventID"  => $eventID,
                "title"    => $title,
                "sport"    => $sport,
                "startTime"=> $startTime,
                "endTime"  => $endTime,
                "numReqd"  => $numReqd,
            );
        // If row does not exist, return -1
        } else {
            $response[] = array (
                "success"   => "-1",
                "message"   => "No database entry for this event. ",
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

    // Add location data
    if ($decoded['lng'] && $decoded['lat']) {
      $lng = $decoded['lng'];
      $lat = $decoded['lat'];

      // mySQL add user to database
      $query = 'UPDATE eventLocation SET `location`=GEOMFROMTEXT(\'POINT('.$lng.' '.$lat.')\', 0 ) WHERE eventID='.$eventID.';';
      $result = mysqli_query($connection->myconn, $query);

      $query = 'UPDATE eventCoords SET `lng`='.$lng.', `lat`='.$lat.' WHERE eventID='.$eventID.';';
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Create JSON object
          $response[] = array (
              "success"   => "1",
              "message"   => "Location data successfully edited. ",
              "eventID"   => $eventID,
          );
      } else {
          $response[] = array (
              "success"   => "-1",
              "message"   => "Unable to edit location data to event. ",
              "eventID"   => $eventID,
          );
      }
    }

    // echoing JSON response
    echo json_encode($response);

    // Close connection
    $connection->close();
?>
