<?php

class createCon  {
    var $host   = 'localhost';      // host address
    var $user   = 'root';           // mySQL username
    var $passwd = '';               // mySQL password
    var $db     = 'sportsApp';      // mySQL database
    var $myconn;                    // holds connection

    // Connect
    function connect() {
        $con = mysqli_connect($this->host, $this->user, $this->passwd, $this->db);
        if (!$con) {
            die('Could not connect to database!');
        } else {
            $this->myconn = $con;
            echo 'Connection established!';}
        return $this->myconn;
    }

    // Close connection
    function close() {
        mysqli_close($myconn);
        echo 'Connection closed!';
    }
}

?>