<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

require_once '../../config/db.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$org_id = isset($_GET['org_id']) ? intval($_GET['org_id']) : 0;

if ($org_id <= 0) {
    http_response_code(400);
    echo json_encode(["message" => "Invalid Organization ID."]);
    exit();
}

try {
    $stmt = $pdo->prepare("SELECT id, title, file_path, created_at FROM videos WHERE org_id = ? ORDER BY created_at DESC");
    $stmt->execute([$org_id]);
    $videos = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        "message" => "Videos retrieved",
        "videos" => $videos
    ]);
} catch(PDOException $e) {
    http_response_code(500);
    echo json_encode(["message" => "Database error: " . $e->getMessage()]);
}
?>
