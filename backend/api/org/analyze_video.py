import sys
import cv2
import json
import os
from ultralytics import YOLO

def analyze_video(video_path):
    if not os.path.exists(video_path):
        print(f"Error: File not found {video_path}")
        sys.exit(1)

    print(f"Analyzing {video_path}...")
    
    # Load YOLOv8 nano model (lightweight and fast)
    model = YOLO('yolov8n.pt')
    
    cap = cv2.VideoCapture(video_path)
    
    if not cap.isOpened():
        print(f"Error: Could not open video {video_path}")
        sys.exit(1)

    fps = cap.get(cv2.CAP_PROP_FPS)
    if fps <= 0:
        fps = 30 # fallback
        
    frame_interval = int(fps) # Sample 1 frame per second
    
    analytics_data = []
    
    frame_count = 0
    current_sec = 0
    
    while True:
        ret, frame = cap.read()
        if not ret:
            break
            
        if frame_count % frame_interval == 0:
            # Run inference on the frame
            # classes=0 limits detection to 'person' only
            results = model(frame, classes=0, verbose=False)
            
            # Count number of bounding boxes
            boxes = results[0].boxes
            person_count = len(boxes)
            
            frame_h, frame_w = frame.shape[:2]
            box_data = []
            
            if person_count > 0:
                for box in boxes:
                    # Normalized coordinates [x_center, y_center, width, height]
                    x_c, y_c, w, h = box.xywhn[0].tolist()
                    box_data.append({
                        "x": x_c - (w / 2),
                        "y": y_c - (h / 2),
                        "w": w,
                        "h": h
                    })
            
            analytics_data.append({
                "time_sec": current_sec,
                "count": person_count,
                "boxes": box_data
            })
            
            print(f"Sec {current_sec}: {person_count} persons detected.")
            current_sec += 1
            
        frame_count += 1
        
    cap.release()
    
    # Save the output to a JSON file alongside the video
    base_path = os.path.splitext(video_path)[0]
    json_path = f"{base_path}.json"
    
    with open(json_path, 'w') as f:
        json.dump(analytics_data, f, indent=4)
        
    print(f"Analytics saved to {json_path}")

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python analyze_video.py <path_to_video>")
        sys.exit(1)
        
    video_file = sys.argv[1]
    analyze_video(video_file)
