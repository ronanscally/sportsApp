<?php

// inviteUsers.php
//
// Invites users to events
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
    foreach ($decoded as $thisDecoded)  {
      if ($thisDecoded['eventID'] && $thisDecoded['userID']) {
          $eventID  = $thisDecoded['eventID'];
          $userID   = $thisDecoded['userID'];

          // mySQL add user to database
          $query = 'INSERT INTO eventParticipants(userID, eventID, attendingStatus) VALUES ('.$userID.','.$eventID.',NULL);';
          $result = mysqli_query($connection->myconn, $query);

          // Check if successful
          if ($result) {
              // Create JSON object
              $response[] = array (
                  "success"   => "1",
                  "message"   => "Participant invited. ",
              );
          } else {
              $response[] = array (
                  "success"   => "-1",
                  "message"   => "Unable to invite user to event. ",
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
