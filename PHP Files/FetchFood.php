<?php

    $con = mysqli_connect("mysql9.000webhost.com", "a8191505_aalee", "10letterspw", "a8191505_ihdb");

    $ID = $_POST["ID"];

    $statement = mysqli_prepare($con, "SELECT * FROM food WHERE ID = ?");
    mysqli_stmt_bind_param($statement, "i", $ID);
    mysqli_stmt_execute($statement);

    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $ID, $foodDescrip, $foodType, $foodAmount);

    $response = array();
    $response["success"] = false;

    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["foodDescrip"] = $foodDescrip;
        $response["foodType"] = $foodType;
        $response["foodAmount"] = $foodAmount;
    }

    echo json_encode($response);
?>