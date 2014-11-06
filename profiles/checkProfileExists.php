<?php
 
// checkProfileExists.php
//
// Checks if there is an entry in the database for the input user ID, which is in a JSON object, 
// sent via a HTTP POST request
//
// Returns a JSON object

    // Include database connection class 
    include '../dbConnect.php';
    include '../postData.php';

    // Get JSON POST and decode
    $received = file_get_contents("php://input");
    $decoded = json_decode($received, true);

    // Check for required fields
    if ($decoded['userID']) {
        $userID = $decoded['userID'];
     
        // Connect to database
        $connection = new createCon();
        $connection->connect();
     
        // mySQL check for user in database
        $query = 'SELECT * FROM `profiles` WHERE userID='.$userID;
        $result = mysqli_query($connection->myconn, $query);
        
        // Fetch each row (should only be 1)
        if ($row = mysqli_fetch_assoc($result)) {
            // Get details
            $firstName  = $row['firstName'];
            $lastName   = $row['lastName'];
            $dob        = $row['dob'];
            $userID     = $row['userID'];
            
            // Create JSON object
            $response = array (
                "success"   => "1",
                "message"   => "Users profile exists. ",
                "userID"    => $userID,
                "firstName" => $firstName,
                "lastName"  => $lastName,
                "dob"       => $dob,
            );            
        }
        // If row does not exist, return -1 to tell app to prompt user for details
        else {
            $response = array (
                "success"   => "-1",
                "message"   => "No database entry for this user, prompt for profile creation",
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