<?php

// getSportNames.php
//
// Retrieves sport names from DB
//
// Returns array of JSON objects

    // Include database connection class
    include '../dbConnect.php';
    include '../postData.php';

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // mySQL add user to database
    $query = 'SELECT sport FROM `sports` SORT ORDER BY sport';
    $result = mysqli_query($connection->myconn, $query);

    // Check if successful
    if ($result) {
        // Fetch each row (should only be 1)
        while ($row = mysqli_fetch_assoc($result)) {
            // Get details
            $sportName  = $row['sport'];

            // Create JSON object
            $response[] = array (
                "success"   => "1",
                "message"   => "Sports found. ",
                "sport"     => $sportName,
            );
        }
    } else {
        $response = array (
            "success"   => "-1",
            "message"   => "Unable to retrieve records. ",
        );
    }
    // echoing JSON response
    echo json_encode($response);

    // $encodedResp = json_encode($response);

    // Check JSON object
    // var_dump(json_decode($encodedResp, true));

    // Send JSON object
    // echo $encodedResp;
?>
