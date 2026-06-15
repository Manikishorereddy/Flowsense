<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// A simple endpoint to be called when the app starts.
// Could be used to check if the API is up, get config, or force updates.
$response = array(
    "status" => "success",
    "message" => "FlowSense AI Backend is running.",
    "version" => "1.0.0",
    "timestamp" => time()
);

echo json_encode($response);
?>
