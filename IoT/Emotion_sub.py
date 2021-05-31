import random
import time
import threading
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
# import RPi.GPIO as GPIO
import numpy as np
import json
import base64
import cv2
import datetime


class MyMqtt_Sub():
    def __init__(self):

        with open('../key.json', 'r') as f:
            self.json_data = json.load(f)

        client = mqtt.Client()
        client.on_connect = self.on_connect
        client.on_message = self.on_message
        client.connect(self.json_data["EC2"]["AI_IP"], self.json_data["MQTT"]["PORT"], 60)  # EC2 mqttbroker 주소

        ##############################
        #GPIO 설정
        #GPIO.setmode(GPIO.BCM)
     
        ############################
        #AI 설정
        self.emo_pred_output = []
        self.emo_pred_time = None

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

        if msg.topic == "Emotion/img":

            payload = None

            try:
                f = open('output2.jpg', "wb")
                payload = json.loads(msg.payload)

                f.write(base64.b64decode(payload['byteArr']))
                f.close()

            except Exception as e:
                print("error ", e)

            myval = cv2.imread('output2.jpg')

            # 감정 분류 모델 predict
            # 이미지 ??장 들어 왔을 때 감정 판단 or 분 단위로 판단??
            emo_pred = random.randint(0, 5)
            self.emo_pred_output.append(emo_pred)

            if self.emo_pred_time is None:
                if len(self.emo_pred_output) == 100:
                    emo_result = self.find_most(self.emo_pred_output)

                    # 문장 생성 모델 predict
                    sentence_result = "첫번째 예시입니다."

                    publish.single("android/him", sentence_result, hostname=self.json_data["EC2"]["AI_IP"])

                    self.emo_pred_output.clear()
                    self.emo_pred_time = datetime.datetime.now()
                    print(self.emo_pred_time)
            else:
                now = datetime.datetime.now()
                if (now - self.emo_pred_time).seconds >= 60:
                    emo_result = self.find_most(self.emo_pred_output)

                    # 문장 생성 모델 predict
                    sentence_result = "두번째 예시입니다."
                    publish.single("android/him", sentence_result, hostname=self.json_data["EC2"]["AI_IP"])

                    self.emo_pred_output.clear()
                    self.emo_pred_time = now
                    print(self.emo_pred_time)

    def find_most(self, emo_pred_output):
        maximum = 0

        for i in range(0, 5):
            cnt = emo_pred_output.count(i)

            if cnt > maximum:
                maximum = cnt

        return maximum

if __name__ == "__main__":
    try:
        mymqtt = MyMqtt_Sub()
    except KeyboardInterrupt:
        print("종료")
        # GPIO.cleanup()
