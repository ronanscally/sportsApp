<?php

// listEvents.php
//
// Retrieves events from DB
//
// Returns array of JSON objects

// Decodes a POINT string
function decodePoint($string) {
    $coordinates = explode(" ",$string);

    $coordinates[0] = preg_replace('/[^\.\-0-9,]|,[0-9]*$/','', $coordinates[0]);
    $coordinates[1] = preg_replace('/[^\.\-0-9,]|,[0-9]*$/','', $coordinates[1]);

    // Return result
    return $coordinates;
};

?>
