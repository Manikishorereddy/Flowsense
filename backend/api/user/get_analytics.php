<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$path = isset($_GET['path']) ? $_GET['path'] : '';
if (empty($path)) {
    http_response_code(400);
    echo json_encode(["message" => "No path provided."]);
    exit();
}

$jsonFile = '../../' . str_replace('.mp4', '.json', $path);

if (file_exists($jsonFile)) {
    echo file_get_contents($jsonFile);
} else {
    http_response_code(404);
    echo json_encode(["message" => "Analytics not found."]);
}
?>
