<?php
 
// editProfile.php
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

    // Check for required fields
    if ($decoded['userID']) {
        $userID = $decoded['userID'];
        $firstName = $decoded['firstName'];
        $lastName = $decoded['lastName'];
        $dob = $decoded['dob'];
     
        // Connect to database
        $connection = new createCon();
        $connection->connect();
     
        // mySQL check for user in database
        $query = 'UPDATE `profiles` SET firstName = \''.$firstName.'\', lastName = \''.$lastName.'\', dob = \''.$dob.'\' WHERE userID = '.$userID.';';
        $result = mysqli_query($connection->myconn, $query);
        
        // Check if row exists or not
        // if ($row = mysqli_fetch_assoc($result)) {
        if (mysqli_affected_rows($connection->myconn)) {
            // Create JSON object
            $response = array (
                "success"   => "1",
                "message"   => "Profile updated successfully. ",
                "userID"    => $userID,
                "firstName" => $firstName,
                "lastName"  => $lastName,
                "dob"       => $dob,
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