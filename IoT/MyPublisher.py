import paho.mqtt.publish as publish
import cv2
import time
import json
import base64
import PIL.Image as pilimg

with open('../key.json', 'r') as f:
    json_data = json.load(f)

cap = cv2.VideoCapture('C:/Users/s_csmscox/jupyterSave/eye_blink/face.mp4')
total_start = time.time()
frame = 0

while True:
    ret, img = cap.read()
    start = time.time()
    if not ret:
        break

    img = cv2.resize(img, dsize=(0, 0), fx=0.5, fy=0.5)
    img = pilimg.fromarray(img)

    if frame % 2 == 0:
        byteArr = base64.b64encode(img)
        MQTT_MSG = json.dumps({"byteArr": byteArr.decode('utf-8'), "user_id":0})
        publish.single("Sleep/img", MQTT_MSG, hostname=json_data["EC2"]["AI_IP"])
        print("", frame)
        print(time.time() - start)
        time.sleep(0.1)

    frame += 1

print("total_time :", time.time() - total_start)
