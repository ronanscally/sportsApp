<?php

// scanUpcomingEvents.php
//
// Scans events occurring in the next 24 hours
//
// Run this script in the crontab, occurring as often as possible (daemon)

    // Include database connection class
    include '/home/2009/09333541/public_html/sportsapp/dbConnect.php';

    // Connect to database
    $connection = new createCon();
    $connection->connect();

    // Scan for events occurring in the next 24 hours
    // Insert into public events table if they are found
    $query  = 'INSERT INTO publicEvents ';
    $query .= 'SELECT events.eventID ';
    $query .= 'FROM `events` ';
    $query .= 'NATURAL LEFT JOIN publicEvents ';
    $query .= 'WHERE events.startTime > CURRENT_TIMESTAMP() AND events.startTime <= TIMESTAMPADD(HOUR,24,CURRENT_TIMESTAMP()) AND publicEvents.eventID IS NULL ';

    // Execute the query
    $result = mysqli_query($connection->myconn, $query);

    // Close connection
    $connection->close();
?>
