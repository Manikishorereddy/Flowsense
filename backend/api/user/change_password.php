<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once '../../config/database.php';

$data = json_decode(file_get_contents("php://input"));

if (!isset($data->email) || !isset($data->new_password)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Email and new password are required"]);
    exit();
}

$email = filter_var($data->email, FILTER_SANITIZE_EMAIL);
$new_password = $data->new_password;

$database = new Database();
$pdo = $database->getConnection();

try {
    // Check if user exists
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    if ($stmt->rowCount() == 0) {
        http_response_code(404);
        echo json_encode(["status" => "error", "message" => "User not found"]);
        exit();
    }

    // Hash the new password
    $password_hash = password_hash($new_password, PASSWORD_DEFAULT);

    // Update password
    $update_stmt = $pdo->prepare("UPDATE users SET password_hash = ? WHERE email = ?");
    
    if ($update_stmt->execute([$password_hash, $email])) {
        http_response_code(200);
        echo json_encode(["status" => "success", "message" => "Password updated successfully"]);
    } else {
        http_response_code(503);
        echo json_encode(["status" => "error", "message" => "Unable to update password"]);
    }
} catch(PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>
