<?php

// listUsers.php
//
// List all users in database to add as a friend
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

      // mySQL list all users
      $query  = 'SELECT userID, firstName, lastName, MAX(status) AS friendStatus ';
      $query .= 'FROM ';
      $query .= '(SELECT profiles.userID, 0 AS status, profiles.firstName, profiles.lastName ';
      $query .= 'FROM profiles ';
      $query .= 'UNION ALL ';
      $query .= 'SELECT friends.friend_2, friends.status, profiles.firstName, profiles.lastName ';
      $query .= 'FROM friends ';
      $query .= 'INNER JOIN `profiles` on friends.friend_2=profiles.userID ';
      $query .= 'WHERE friend_1='.$userID.' AND friends.status=1) ';
      $query .= 'T1 ';
      $query .= 'WHERE userID!='.$userID.' ';
      $query .= 'GROUP BY userID  ';
      $query .= 'HAVING friendStatus=0 ';
      $query .= 'ORDER BY firstName';
      
      $result = mysqli_query($connection->myconn, $query);

      // Check if successful
      if ($result) {
          // Fetch each row
          while ($row = mysqli_fetch_assoc($result)) {
              // Get details
              $userID       = $row['userID'];
              $firstName    = $row['firstName'];
              $lastName     = $row['lastName'];
              $friendStatus = $row['friendStatus'];

              // Create JSON object
              $response[] = array (
                  "success"       => "1",
                  "message"       => "User found. ",
                  "userID"        => $userID,
                  "firstName"     => $firstName,
                  "lastName"      => $lastName,
                  "friendStatus"  => $friendStatus,
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
