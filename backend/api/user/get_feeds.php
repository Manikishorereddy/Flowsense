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

try {
    $org_id = isset($_GET['org_id']) ? intval($_GET['org_id']) : 0;
    if ($org_id > 0) {
        $stmt = $pdo->prepare("SELECT v.*, o.org_name FROM videos v JOIN organizations o ON v.org_id = o.id WHERE v.org_id = ? ORDER BY v.created_at DESC");
        $stmt->execute([$org_id]);
    } else {
        $stmt = $pdo->query("SELECT v.*, o.org_name FROM videos v JOIN organizations o ON v.org_id = o.id ORDER BY v.created_at DESC");
    }
    $videos = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        "message" => "Feeds retrieved",
        "feeds" => $videos
    ]);
} catch(PDOException $e) {
    http_response_code(500);
    echo json_encode(["message" => "Database error: " . $e->getMessage()]);
}
?>
