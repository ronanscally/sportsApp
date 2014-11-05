<?php
 
// createProfile.php
//
// Creates an entry in the profiles table in the database   
//
// Returns a JSON object

    // Include database connection class 
    include 'dbConnect.php';
    include 'postData.php';

    // Get JSON POST and decode
    $received   = file_get_contents("php://input");
    $decoded    = json_decode($received, true);

    // Check for required fields
    if ($decoded['userID'] && $decoded['firstName'] && $decoded['lastName'] && $decoded['dob']) {
        $userID     = $decoded['userID'];
        $firstName  = $decoded['firstName'];
        $lastName   = $decoded['lastName'];
        $dob        = $decoded['dob'];
     
        // Connect to database
        $connection = new createCon();
        $connection->connect();
     
        // mySQL add user to database
        $query = 'INSERT INTO profiles(userID, firstName, lastName, dob) VALUES ('.$userID.',\''.$firstName.'\',\''.$lastName.'\',\''.$dob.'\');';
        $result = mysqli_query($connection->myconn, $query);
        
        // Check if successful
        if ($result) {
            // Create JSON object
            $response = array (
                "success"   => "1",
                "message"   => "Profile successfully created. ",
            );
        } else {
            $response = array (
                "success"   => "-1",
                "message"   => "Unable to create profile for this user. ",
            );
        }
    } 
    // If not enough fields in JSON object 
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