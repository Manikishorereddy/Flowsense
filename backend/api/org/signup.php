<?php
require_once '../../config/db.php';
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method not allowed"]);
    exit();
}

$data = json_decode(file_get_contents("php://input"));

if (!isset($data->org_name) || !isset($data->email) || !isset($data->password)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Organization name, email, and password are required"]);
    exit();
}

$org_name = htmlspecialchars(strip_tags($data->org_name));
$email = filter_var($data->email, FILTER_SANITIZE_EMAIL);
$password = $data->password;

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Invalid email format"]);
    exit();
}

$password_hash = password_hash($password, PASSWORD_DEFAULT);

try {
    $stmt = $pdo->prepare("INSERT INTO organizations (org_name, email, password_hash) VALUES (?, ?, ?)");
    $stmt->execute([$org_name, $email, $password_hash]);
    
    http_response_code(201);
    echo json_encode(["status" => "success", "message" => "Organization registered successfully", "id" => $pdo->lastInsertId()]);
} catch (PDOException $e) {
    if ($e->getCode() == 23000) {
        http_response_code(409);
        echo json_encode(["status" => "error", "message" => "Email already exists"]);
    } else {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Registration failed"]);
    }
}
?>
