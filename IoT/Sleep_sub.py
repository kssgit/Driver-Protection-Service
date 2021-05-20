import time
import threading
import paho.mqtt.client as mqtt
# import RPi.GPIO as GPIO
from tensorflow.keras.models import load_model
import tensorflow as tf
import cv2
import dlib
import numpy as np
from imutils import face_utils
from matplotlib import pyplot as plt
from PIL import Image
import json


# 이산화탄소 subscribe
class MyMqtt_Sub:
    def __init__(self):
        with open('../key.json', 'r') as f:
            json_data = json.load(f)

        client = mqtt.Client()
        client.on_connect = self.on_connect
        client.on_message = self.on_message
        client.connect(json_data["EC2"]["IP"], json_data["MQTT"]["PORT"], 60)  # EC2 mqttbroker 주소
        ##############################
        #GPIO 설정
        # GPIO.setmode(GPIO.BCM)
     
        ############################
        ############################
        #AI 설정
        self.detector = dlib.get_frontal_face_detector()
        self.predictor = dlib.shape_predictor(
            './shape_predictor_68_face_landmarks.dat')
        self.img_size = (32, 32)

        with tf.device('/cpu:0'):
            self.model = load_model('C:/Users/s_csmscox/jupyterSave/eye_blink/eye_blink_CNN_ImgGen1_FT.h5')

        self.origin_img = None

        self.eye_blink = None
        self.co2 = None
        self.motion = None

        self.frame = 0
        self.eye_alert_count = 0

        self.nose_sum = 0
        self.nose_cnt = 0
        self.nose_mean = 0
        self.nose_alert_cnt = 0

        self.mouse_sum = 0
        self.mouse_cnt = 0
        self.mouse_mean = 0
        self.mouse_alert_cnt = 0

        ###########################
        client.loop_forever()

    def on_connect(self, client, userdata, flags, rc):
        print("connect.." + str(rc))
        if rc == 0:
            img_data = client.subscribe("IoT/img")
            Co2_data = client.subscribe("IoT/Co2")
            user_id = client.subscribe("Android/user_id")
        else:
            print("연결실패")

    def on_message(self, client, userdata, msg):

        # start = time.time()
        self.frame += 1

        if msg.topic == "mydata/img":
            json_data = json.loads(msg.payload)
            myval = np.frombuffer(json_data['byteArr'], np.uint8)
            myval = myval.reshape(852, 480, 3)

            print(json_data['user_id'])

            if self.origin_img is None:
                self.origin_img = myval
            else:
                faces = self.detector(myval)

                for face in faces:
                    shapes = self.predictor(myval, face)
                    shapes = face_utils.shape_to_np(shapes)

                    eye_img_l, eye_rect_l = self.crop_eye(myval, eye_points=shapes[36:42])
                    eye_img_r, eye_rect_r = self.crop_eye(myval, eye_points=shapes[42:48])

                    eye_img_l = cv2.resize(eye_img_l, dsize=self.img_size)
                    eye_img_r = cv2.resize(eye_img_r, dsize=self.img_size)

                    # 왼쪽 눈
                    eye_input_l = eye_img_l.copy().reshape((1, self.img_size[1], self.img_size[0], 3)).astype(
                        np.float32)
                    eye_input_l = eye_input_l / 255

                    with tf.device('/cpu:0'):
                        pred_l = self.model.call(tf.convert_to_tensor(eye_input_l), training=False)
                    pred_l = np.argmax(pred_l)

                    # 오른쪽 눈
                    eye_input_r = eye_img_r.copy().reshape((1, self.img_size[1], self.img_size[0], 3)).astype(
                        np.float32)
                    eye_input_r = eye_input_r / 255

                    with tf.device('/cpu:0'):
                        pred_r = self.model.call(tf.convert_to_tensor(eye_input_r), training=False)
                    pred_r = np.argmax(pred_r)

                    # 두 눈 다 감은 경우 졸음으로 예측
                    if pred_l == 0 and pred_r == 0:
                        self.eye_alert_count += 1
                    else:
                        self.eye_alert_count = 0
                        self.eye_blink = 0

                    # eye_alert_count가 10 초과하면 경고 메세지
                    if self.eye_alert_count > 10:
                        print("Wake up!(eye_blink)")
                        self.eye_blink = 1

                    # 코 끝 특징점이 기존에 비해 내려가면 고개 내려갔다고 판정?
                    if self.nose_cnt == 0:
                        self.nose_sum += shapes[33][1]
                        self.nose_cnt += 1
                        self.nose_mean = self.nose_sum / self.nose_cnt
                    elif shapes[33][1] - self.nose_mean > 10:
                        self.nose_alert_cnt += 1
                    else:
                        self.nose_sum += shapes[33][1]
                        self.nose_cnt += 1
                        self.nose_mean = self.nose_sum / self.nose_cnt
                        self.nose_alert_cnt = 0

                    if self.nose_alert_cnt > 10:
                        print("Wake up!(movement)")

                    # 입 특징점 기반으로 하품 감지
                    mouse_rate = (shapes[57][1] - shapes[51][1]) / (shapes[54][0] - shapes[48][0])

                    if self.mouse_cnt == 0:
                        self.mouse_sum += mouse_rate
                        self.mouse_cnt += 1
                        self.mouse_mean = self.mouse_sum / self.mouse_cnt
                    elif mouse_rate - self.mouse_mean > 0.2:
                        self.mouse_alert_cnt += 1
                    else:
                        self.mouse_sum += mouse_rate
                        self.mouse_cnt += 1
                        self.mouse_mean = self.mouse_sum / self.mouse_cnt
                        self.mouse_alert_cnt = 0

                    if self.mouse_alert_cnt > 10:
                        print("Wake up!(yawning)")

        elif msg.topic == 'Co2_data':

            if msg.payload >= 1500:
                print("Wake Up!(Co2)")
                self.co2 = 1
            elif msg.payload >= 2000:
                # 창문 열린다든지?
                self.co2 = 2
            else:
                self.co2 = 0

        # self.sleep_gate(self, self.eye_blink, self.motion, self.co2)
        # print("time :", time.time() - start)
        print("", self.frame)

    @staticmethod
    def sleep_gate(self, eye_blink, motion, co2):
        if eye_blink == 1 and motion == 1 and co2 == 1:
            print("졸았다!!!!!")
        else:
            print("안 졸았다!!")

    @staticmethod
    def mse(self, img, compare_img):
        err = np.sum((img.astype("float") - compare_img.astype("float")) ** 2)
        err /= float(img.shape[0] * compare_img.shape[1])
        return err

    def crop_eye(self, img, eye_points):
        x1, y1 = np.amin(eye_points, axis=0)
        x2, y2 = np.amax(eye_points, axis=0)
        cx, cy = (x1 + x2) / 2, (y1 + y2) / 2

        w = (x2 - x1) * 1.2
        h = w * self.img_size[1] / self.img_size[0]

        margin_x, margin_y = w / 2, h / 2

        min_x, min_y = int(cx - margin_x), int(cy - margin_y)
        max_x, max_y = int(cx + margin_x), int(cy + margin_y)

        eye_rect = np.rint([min_x, min_y, max_x, max_y]).astype(np.int)

        eye_img = img[eye_rect[1]:eye_rect[3], eye_rect[0]:eye_rect[2]]

        return eye_img, eye_rect


if __name__ == "__main__":
    try:
        mymqtt = MyMqtt_Sub()
    except KeyboardInterrupt:
        print("종료")
        # GPIO.cleanup()
