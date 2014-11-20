<?php

// addDevice.php
//
// Adds a device in the database for a user
//
// Returns a JSON object

    // Include database connection class
    include '/home/2009/09333541/public_html/sportsapp/dbConnect.php';
    include '/home/2009/09333541/public_html/sportsapp/postData.php';
    include '/home/2009/09333541/public_html/sportsapp/notifications/sendGCM.php';

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // Fetch each user ID and their corresponding coordinates and device IDs
    $query  = 'SELECT profiles.userID, userDevices.device, userLocation.lng, userLocation.lat ';
    $query .= 'FROM `profiles` ';
    $query .= 'INNER JOIN `userDevices` on profiles.userID=userDevices.userID ';
    $query .= 'INNER JOIN `userLocation` on profiles.userID=userLocation.userID ';
    $query .= 'WHERE 1;';

    // Store all data from the query
    $result = mysqli_query($connection->myconn, $query);
    while ($row = mysqli_fetch_assoc($result)) {
        $user[]   = $row['userID'];
        $device[] = $row['device'];
        $lng[]    = $row['lng'];
        $lat[]    = $row['lat'];
    }

    // Fetch each event ID occurring in the next hour
    $query = 'SELECT * FROM `events` WHERE dateTime > CURRENT_TIMESTAMP() AND dateTime <= TIMESTAMPADD(HOUR,1,CURRENT_TIMESTAMP());';
    $result = mysqli_query($connection->myconn, $query);
    while ($row = mysqli_fetch_assoc($result)) {
        $event[] = $row['eventID'];
    }

    echo 'test';
    $i = 0;
    // For each user, scan through all relevant events sorted by distance occuring in the next hour
    foreach ($user as $thisUser)  {
        $query  = 'SELECT events.title, events.dateTime, events.sport, ';
        $query .= '( 3959 * acos( cos( radians('.$lat[$i].') ) * cos( radians(eventCoords.lat ) ) * cos( radians( eventCoords.lng ) - radians('.$lng[$i].') ) + sin( radians('.$lat[$i].') ) * sin( radians(eventCoords.lat ) ) ) ) ';
        $query .= 'AS distance ';
        $query .= 'FROM `events` ';
        $query .= 'INNER JOIN `eventCoords` on events.eventID=eventCoords.eventID ';
        $query .= 'WHERE events.dateTime > CURRENT_TIMESTAMP() AND events.dateTime <= TIMESTAMPADD(HOUR,1,CURRENT_TIMESTAMP()) ';
        // $query .= 'HAVING distance < 50 ';     // add distance constraint
        $query .= 'ORDER BY distance;';

        // Execute query and send notification if required
        $result = mysqli_query($connection->myconn, $query);
        while ($row = mysqli_fetch_assoc($result)) {
            $notification = array(
              'title'     => $row['title'],
              'distance'  => $row['distance']." km",
              'dateTime'  => $row['dateTime'],
              'sport'     => $row['sport'],
            );
            sendGoogleCloudMessage($notification, array($device[$i]));
            echo var_dump($notification);
            echo var_dump($device[$i]);
            echo 'i = '.$i;
        }
        $i++;
    }

    // Close connection
    $connection->close();
?>
