<?php

class createCon  {
    var $host   = 'csserver.ucd.ie';      // host address
    var $user   = '09333541';           // mySQL username
    var $passwd = 'msrqtgj1';               // mySQL password
    var $db     = '09333541';      // mySQL database
    var $myconn;                    // holds connection

    // Connect
    function connect() {
        $con = mysqli_connect($this->host, $this->user, $this->passwd, $this->db);
        if (!$con) {
            die('Could not connect to database!');
        } else {
            $this->myconn = $con;
            // echo 'Connection established!';
        }
        return $this->myconn;
    }

    // Close connection
    function close() {
        mysqli_close($myconn);
        // echo 'Connection closed!';
    }
}

?>
