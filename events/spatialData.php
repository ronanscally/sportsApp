<?php

// listEvents.php
//
// Retrieves events from DB
//
// Returns array of JSON objects

// Decodes a POINT string
function decodePoint($string) {
    $explodeString = explode(" ",$string);

    echo var_dump($string);

    // $coordinates[] = $explodeString[0];  // x-coordinate
    // $coordinates[] = $explodeString[1];  // y-coordinate

    $explodeString[0] = preg_replace('/[^\.\-0-9,]|,[0-9]*$/','', $explodeString[0]);
    $explodeString[1] = preg_replace('/[^\.\-0-9,]|,[0-9]*$/','', $explodeString[1]);

    // Return result
    return $coordinates;
};

    // Include database connection class
    include '../dbConnect.php';
    include '../postData.php';

    // Get JSON POST and decode
    // Look up details in DB for this user ID
    $data = array (
      "eventID"    => "1234622",                      // User ID
    );

    // Encode JSON data
    $jsonObject = json_encode($data);

    $received = $jsonObject;
    $decoded = json_decode($received, true);

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // Check for required fields
    if ($decoded['eventID']) {
      $eventID = $decoded['eventID'];

      // mySQL get records
      $query =  'SELECT eventID, AsWKT(location) FROM eventLocation where eventID='.$eventID;

      // Send
      $result = mysqli_query($connection->myconn, $query);

      // Fetch row
      if ($row = mysqli_fetch_assoc($result)) {
        var_dump($row);
        $test = $row['AsWKT(location)'];
        $test = explode(" ",$test);

        $xCoord = $test[0];
        $yCoord = $test[1];

        $xCoord = preg_replace('/[^\.\-0-9,]|,[0-9]*$/','',$xCoord);
        $yCoord = preg_replace('/[^\.\-0-9,]|,[0-9]*$/','',$yCoord);

          // Get details
          // Create JSON object
          $response[] = array (
              "success"   => "1",
              "message"   => "Event found. ",
              "xCoord"  => $xCoord,
              "yCoord"  => $yCoord,
          );
      } else {
          $response[] = array (
              "success"   => "-1",
              "message"   => "Unable to retrieve records. ",
          );
          echo 'failed to find record';
      }
    }
    // If no userID field in JSON object
    else {
        // required field is missing
        $response[] = array (
            "success" => "0",
            "message" => "Required field(s) is missing",
        );
        echo 'no match';
    }
    // echo 'done';

    var_dump(json_encode($response));

    // Close connection
    $connection->close();
?>
