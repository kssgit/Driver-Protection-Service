import time
import threading
import paho.mqtt.client as mqtt
# import RPi.GPIO as GPIO
import numpy as np
import json
import base64


class MyMqtt_Sub():
    def __init__(self):

        with open('../key.json', 'r') as f:
            json_data = json.load(f)

        client = mqtt.Client()
        client.on_connect = self.on_connect
        client.on_message = self.on_message
        client.connect(json_data["EC2"]["IP"], json_data["MQTT"]["PORT"], 60)  # EC2 mqttbroker 주소

        ##############################
        #GPIO 설정
        #GPIO.setmode(GPIO.BCM)
     
        ############################
        #AI 설정

        ###########################
        client.loop_forever()

    def on_connect(self, client, userdata, flags, rc):
        print("connect.." + str(rc))
        if rc == 0:
            client.subscribe("Emotion/img")
            client.subscribe("Android/user_id")
        else:
            print("연결실패")

    def on_message(self, client, userdata, msg):

        if msg.topic == "Sleep/img":
            json_data = json.loads(msg.payload)
            myval = np.frombuffer(base64.b64decode(json_data['byteArr']), np.uint8)
            myval = myval.reshape(426, 240, 3)

            # 감정 분류 모델 predict => 문장 생성 모델 predict => 안드로이드로 문장 전송


if __name__ == "__main__":
    try:
        mymqtt = MyMqtt_Sub()
    except KeyboardInterrupt:
        print("종료")
        # GPIO.cleanup()
