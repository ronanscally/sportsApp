<?php
 
// deleteProfile.php
//
// Deletes the users details from the database
//
// Returns a JSON object

    // Include database connection class 
    include 'dbConnect.php';
    include 'postData.php';

    // Get JSON POST and decode
    $received = file_get_contents("php://input");
    $decoded = json_decode($received, true);

    // Check for required fields
    if ($decoded['userID']) {
        $userID = $decoded['userID'];
     
        // Connect to database
        $connection = new createCon();
        $connection->connect();
     
        // Mysql delete row with matched id
        $query = 'DELETE FROM `profiles` WHERE userID = '.$userID.';';
        $result = mysqli_query($connection->myconn, $query);
        
        // Check if row exists or not
        if (mysqli_affected_rows($connection->myconn)) {
            // Create JSON object
            $response = array (
                "success"   => "1",
                "message"   => "Profile successfully deleted. ",
            );
        // If row does not exist, return -1
        } else {
            $response = array (
                "success"   => "-1",
                "message"   => "No database entry for this user. ",
            );
        }
    } 
    // If no userID field in JSON object 
    else {
        // required field is missing
        $response = array (
            "success" => "0",
            "message" => "Required field(s) is missing",
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