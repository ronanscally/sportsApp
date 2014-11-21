<?php

    // Taken from http://php.net/manual/en/function.curl-exec.php

    /**
    * Send a POST requst using cURL (JSON object)
    * @param string $url to request
    * @param array $jsonObject containing JSOB object to send
    * @param array $options for cURL
    * @return string
    */ 

    function postJSON($url, $jsonObject, array $options = array()) {
        // Default parameters
        $defaults = array(
            CURLOPT_POST => 1,
            CURLOPT_HEADER => 0,
            CURLOPT_URL => $url,
            CURLOPT_FRESH_CONNECT => 1,
            CURLOPT_RETURNTRANSFER => 1,
            CURLOPT_FORBID_REUSE => 1,
            CURLOPT_TIMEOUT => 4,
            CURLOPT_POSTFIELDS => $jsonObject,
            CURLOPT_HTTPHEADER => array('Content-Type:application/json'),
        );

        // Set up destination
        $ch = curl_init($url);

        // Setup request to send JSON via POST
        curl_setopt_array($ch, ($options + $defaults));

        // Send request, error check
        if(!$result = curl_exec($ch)) {
            trigger_error(curl_error($ch));
        }

        // Close
        curl_close($ch);

        // Return result
        return $result;
    };

    /**
    * Send a GET requst using cURL
    * @param string $url to request
    * @param array $get values to send
    * @param array $options for cURL
    * @return string
    */
    // Not sure where to use this yet...
    function getJSON($url, array $get = NULL, array $options = array()) {
        // Default parameters
        $defaults = array(
            CURLOPT_URL => $url. (strpos($url, '?') === FALSE ? '?' : ''). http_build_query($get),
            CURLOPT_HEADER => 0,
            CURLOPT_RETURNTRANSFER => TRUE,
            CURLOPT_TIMEOUT => 4
        );

        // Initialise, set up options
        $ch = curl_init();
        curl_setopt_array($ch, ($options + $defaults));

        // Send request, error check
        if( ! $result = curl_exec($ch)) {
            trigger_error(curl_error($ch));
        }

        // Close
        curl_close($ch);

        return $result;
    }

?>
