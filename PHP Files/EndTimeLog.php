<?php

	$con = mysqli_connect("mysql9.000webhost.com", "a8191505_aalee", "10letterspw", "a8191505_ihdb");

	$id = $_POST["ID"];

	$sql = "UPDATE route
	SET end = NOW( ) WHERE ID = ". $id;

	if (mysqli_query($con, $sql)) {
		echo "Update Successful";
	} else {
		echo "Error updating record: " . mysqli_error($con);
	}

?>
