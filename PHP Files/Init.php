<?php

$host = "mysql9.000webhost.com";
$user = "a8191505_aalee";
$password = "10letterspw";
$db = "a8191505_ihdb";

$con = mysqli_connect($host, $user, $password, $db);

if (!$con) {
	die("Error in connection " . mysqli_connect_error());
} else {
}

?>