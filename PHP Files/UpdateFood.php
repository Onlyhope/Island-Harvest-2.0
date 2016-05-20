<?php

	$con = mysqli_connect("mysql9.000webhost.com", "a8191505_aalee", "10letterspw", "a8191505_ihdb");

	$id = $_POST["ID"];
	$foodDescrip = $_POST["foodDescrip"];
	$foodType = $_POST["foodType"];
	$foodAmount = $_POST["foodAmount"];

	$sql = "UPDATE food
		SET foodDescrip = '". $foodDescrip ."', foodType = '". $foodType ."', foodAmount = ". $foodAmount ."
		WHERE ID =". $id;

	if (mysqli_query($con, $sql)) {
		echo "Update Successful";
	} else {
		echo "Error updating record: " . mysqli_error($con);
	}

?>
