<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

require_once '../../config/db.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Receive JSON payload
$data = json_decode(file_get_contents("php://input"));
$video_id = isset($data->video_id) ? intval($data->video_id) : 0;
$org_id = isset($data->org_id) ? intval($data->org_id) : 0;

if ($video_id <= 0 || $org_id <= 0) {
    http_response_code(400);
    echo json_encode(["message" => "Invalid parameters."]);
    exit();
}

try {
    // Verify ownership and get path
    $stmt = $pdo->prepare("SELECT file_path FROM videos WHERE id = ? AND org_id = ?");
    $stmt->execute([$video_id, $org_id]);
    $video = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($video) {
        $filePath = '../../' . $video['file_path'];
        
        // Delete from database
        $delStmt = $pdo->prepare("DELETE FROM videos WHERE id = ?");
        $delStmt->execute([$video_id]);

        // Delete actual file
        if (file_exists($filePath)) {
            unlink($filePath);
        }

        echo json_encode(["message" => "Video deleted successfully."]);
    } else {
        http_response_code(404);
        echo json_encode(["message" => "Video not found or access denied."]);
    }
} catch(PDOException $e) {
    http_response_code(500);
    echo json_encode(["message" => "Database error: " . $e->getMessage()]);
}
?>
