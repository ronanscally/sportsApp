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
    if ($decoded['userID']) {
      $userID = $decoded['userID'];

      // mySQL get friends
      $query =  'SELECT friends.friend_2, profiles.firstName, profiles.lastName ';
      $query .= 'FROM `friends` ';
      $query .= 'INNER JOIN `profiles` on friends.friend_2=profiles.userID ';
      $query .= 'WHERE friends.friend_1='.$userID.' AND friends.status = 1 ';
      $query .= 'ORDER BY profiles.firstName; ';   // order firstname alphabetically
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Fetch each row
          while ($row = mysqli_fetch_assoc($result)) {
              // Get details
              $friendID  = $row['friend_2'];
              $firstName = $row['firstName'];
              $lastName  = $row['lastName'];

              // Create JSON object
              $response[] = array (
                  "success"   => "1",
                  "message"   => "Friend found. ",
                  "userID"    => $friendID,
                  "firstName" => $firstName,
                  "lastName"  => $lastName,
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
