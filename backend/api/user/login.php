<?php
require_once '../../config/db.php';
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method not allowed"]);
    exit();
}

$data = json_decode(file_get_contents("php://input"));

if (!isset($data->email) || !isset($data->password)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Email and password are required"]);
    exit();
}

$email = $data->email;
$password = $data->password;

try {
    $stmt = $pdo->prepare("SELECT id, full_name, location, email, password_hash FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user && password_verify($password, $user['password_hash'])) {
        http_response_code(200);
        echo json_encode([
            "status" => "success", 
            "message" => "Login successful", 
            "id" => $user['id'],
            "full_name" => $user['full_name'],
            "location" => $user['location'],
            "email" => $user['email']
        ]);
    } else {
        http_response_code(401);
        echo json_encode(["status" => "error", "message" => "Invalid email or password"]);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Login failed"]);
}
?>
