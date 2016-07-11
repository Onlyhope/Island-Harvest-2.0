<?php

	$con = mysqli_connect("mysql9.000webhost.com", "a8191505_aalee", "10letterspw", "a8191505_ihdb");

	$id = $_POST["ID"];
	$tripTime = $_POST["tripTime"];



	$sql = "UPDATE route
	SET tripTime = ?, end = NOW( ) WHERE ID = ?;";

	$statement = mysqli_prepare($con, $sql);
	mysqli_stmt_bind_param($statement, "si", $tripTime, $id);
	mysqli_stmt_execute($statement);

	/*if (mysqli_query($con, $sql)) {
		echo "Update Successful";
	} else {
		echo "Error updating record: " . mysqli_error($con);
	}*/

?>

