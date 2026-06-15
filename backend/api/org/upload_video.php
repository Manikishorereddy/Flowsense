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

$org_id = isset($_POST['org_id']) ? intval($_POST['org_id']) : 0;
$title = isset($_POST['title']) ? $_POST['title'] : 'Untitled Video';

if ($org_id <= 0) {
    http_response_code(400);
    echo json_encode(["message" => "Invalid Organization ID."]);
    exit();
}

if (!isset($_FILES['video_file'])) {
    http_response_code(400);
    echo json_encode(["message" => "No video file provided."]);
    exit();
}

$uploadDir = '../../uploads/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$file = $_FILES['video_file'];
$fileName = time() . '_' . basename($file['name']);
$targetPath = $uploadDir . $fileName;

if (move_uploaded_file($file['tmp_name'], $targetPath)) {
    // Save relative path for easy web access
    $dbPath = 'uploads/' . $fileName;

    try {
        $stmt = $pdo->prepare("INSERT INTO videos (org_id, title, file_path) VALUES (?, ?, ?)");
        $stmt->execute([$org_id, $title, $dbPath]);
        
        $videoId = $pdo->lastInsertId();

        // Run python script in background (Windows style)
        $pythonScript = realpath(__DIR__ . '/analyze_video.py');
        $videoFile = realpath($targetPath);
        if ($pythonScript && $videoFile) {
            pclose(popen("start /B python \"$pythonScript\" \"$videoFile\" > NUL 2>&1", "r"));
        }
        
        http_response_code(201);
        echo json_encode([
            "message" => "Video uploaded successfully",
            "video" => [
                "id" => $videoId,
                "title" => $title,
                "file_path" => $dbPath
            ]
        ]);
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(["message" => "Database error: " . $e->getMessage()]);
    }
} else {
    http_response_code(500);
    echo json_encode(["message" => "Failed to save file."]);
}
?>
