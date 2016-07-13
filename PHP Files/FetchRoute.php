<?php

    $con = mysqli_connect("mysql9.000webhost.com", "a8191505_aalee", "10letterspw", "a8191505_ihdb");

    $ID = $_POST["ID"];


    $statement = mysqli_prepare($con, "SELECT ID, userID, agencyID, donorID FROM route WHERE ID = ?");
    mysqli_stmt_bind_param($statement, "i", $ID);
    mysqli_stmt_execute($statement);

    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $ID, $userID, $agencyID, $donorID);

    $response = array();
    $response["success"] = false;


    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["ID"] = $ID;
        $response["userID"] = $userID;
        $response["agencyID"] = $agencyID;
        $response["donorID"] = $donorID;
    }

    $sql = "SELECT donorAddr FROM donor WHERE ID = ".$response["donorID"];
    $result = mysqli_query($con, $sql);

    if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_assoc($result)) {
            $response["donorAddr"] = $row["donorAddr"];
        }
    } else {
        echo "0 results";
    }

    $sql = "SELECT agencyAddr FROM agency WHERE ID = ".$response["agencyID"];
    $result = mysqli_query($con, $sql);

    if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_assoc($result)) {;
            $response["agencyAddr"] = $row["agencyAddr"];
        }
    } else {
        echo "0 results";
    }

    echo json_encode($response);

   
?>

