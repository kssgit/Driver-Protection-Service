import time
import paho.mqtt.client as mqtt
from tensorflow.keras.models import load_model
import tensorflow as tf
import cv2
import dlib
import numpy as np
from imutils import face_utils
import json
import base64
import PIL.Image as pilimg
import paho.mqtt.publish as publish
import datetime
import mysql.connector


# 이산화탄소 subscribe
class MyMqtt_Sub:
    def __init__(self):
        with open('../key.json', 'r') as f:
            self.json_data = json.load(f)

        client = mqtt.Client()
        client.on_connect = self.on_connect
        client.on_message = self.on_message
        client.connect(self.json_data["EC2"]["AI_IP"], self.json_data["MQTT"]["PORT"], 60)  # EC2 mqttbroker 주소
        ##############################
        # DB 설정
        try:
            db = self.json_data["DB_Server"]
            ip = self.json_data["EC2"]
            self.mydb = mysql.connector.connect(
                host=ip["IP"],
                user=db["USER"],
                password=db["PASSWORD"],
                port=db["PORT"],
                database=db["NAME"]
            )
            self.cursor = self.mydb.cursor()
        except Exception as e:
            print(f"Error connecting to MariaDB Platform: {e}")
            self.cursor = None
            self.mydb = None

        ############################
        # AI 설정
        self.detector = dlib.get_frontal_face_detector()
        self.predictor = dlib.shape_predictor(
            './shape_predictor_68_face_landmarks.dat')

        gpus = tf.config.experimental.list_physical_devices('GPU')
        if gpus:
            try:
                # Currently, memory growth needs to be the same across GPUs
                for gpu in gpus:
                    tf.config.experimental.set_memory_growth(gpu, True)
                logical_gpus = tf.config.experimental.list_logical_devices('GPU')
                print(len(gpus), "Physical GPUs,", len(logical_gpus), "Logical GPUs")
            except RuntimeError as e:
                # Memory growth must be set before GPUs have been initialized
                print(e)

        self.img_size = (32, 32)

        with tf.device('/cpu:0'):
            self.model = load_model('C:/Users/dltmd/jupyterSave/model/eye_blink_CNN_ImgGen1_FT.h5')

        self.origin_img = None

        self.eye_blink = 0
        self.co2 = 0
        self.nose = 0
        self.mouse = 0

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

        self.user_id = None
        self.result_cnt = 0
        self.co2_cnt = 0
        self.ppm = 0
        self.is_face_exist = False
        self.pred_time = datetime.datetime.now()
        self.co2_pred_time = self.pred_time
        ###########################
        client.loop_forever()

    def on_connect(self, client, userdata, flags, rc):
        print("connect.." + str(rc))
        if rc == 0:
            client.subscribe("Sleep/img")
            client.subscribe("Sleep/Co2")
            client.subscribe("Android/user_id")
        else:
            print("연결실패")

    def on_message(self, client, userdata, msg):

        # start = time.time()
        # self.frame += 1
        time_now = datetime.datetime.now()

        # 졸음 상태 => boolean 줘서 다른 간섭 안하도록?
        if msg.topic == "Sleep/img":

            payload = None

            try:
                f = open('output1.jpg', "wb")
                payload = json.loads(msg.payload)

                f.write(base64.b64decode(payload['byteArr']))
                f.close()

            except Exception as e:
                print("error ", e)

            myval = cv2.imread('output1.jpg')

            # json_data = json.loads(msg.payload)
            # myval = np.frombuffer(base64.b64decode(json_data['byteArr']), np.uint8)
            # # myval = np.frombuffer(json_data['byteArr'], np.uint8)
            # myval = myval.reshape(426, 240, 3)

            if self.origin_img is None:
                self.origin_img = myval
            else:
                faces = self.detector(myval)
                self.is_face_exist = False

                for face in faces:
                    self.is_face_exist = True
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

                    # eye_alert_count가 10 초과하면 eye_blink = 1
                    if self.eye_alert_count >= 20:
                        print("Wake up!(eye_blink)")
                        self.eye_blink = 1

                    # 코 끝 특징점이 기존에 비해 내려가면 고개 내려갔다고 판정
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
                        self.nose = 0

                    # nose_alert_cnt가 10 초과하면 nose = 1
                    if self.nose_alert_cnt >= 20:
                        print("Wake up!(movement)")
                        self.nose = 1

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
                        self.mouse = 0

                    # mouse_alert_cnt가 10 초과하면 mouse = 1
                    if self.mouse_alert_cnt >= 20:
                        print("Wake up!(yawning)")
                        self.mouse = 1

            print(self.eye_blink, self.nose, self.mouse, self.co2)

        if msg.topic == 'Sleep/Co2':

            payload = json.loads(msg.payload)
            myco2 = int(payload['content'])
            self.ppm = myco2

            if myco2 >= 2500:
                self.co2_cnt += 1
            else:
                self.co2 = 0
                self.co2_cnt = 0

            if self.co2_cnt > 20:
                print("Wake Up!(Co2)")
                self.co2 = 1

            print(self.ppm)

        if (time_now - self.co2_pred_time).seconds >= 5:
            MQTT_MSG = json.dumps({"type": 3, "co2": self.ppm})

            # publish.single("android/him", MQTT_MSG, hostname=self.json_data["EC2"]["AI_IP"])

            sql = "INSERT INTO analysisApp_co2 (user_id_id, amount, time) VALUES (%s, %s, %s)"
            val = ("him", self.ppm, time_now)

            self.cursor.execute(sql, val)
            self.mydb.commit()

            self.co2_pred_time = datetime.datetime.now()

        if self.is_face_exist and (time_now - self.pred_time).seconds >= 5:
            result = self.sleep_gate(self.eye_blink, self.nose, self.mouse, self.co2)

            # 경고 및 위험 알람을 울릴 때 DB에 졸음 상태 저장
            if result == 1:
                # 관짝 소년단
                MQTT_MSG = json.dumps({"type": 2})

                # publish.single("android/him", MQTT_MSG, hostname=self.json_data["EC2"]["AI_IP"])

                print("위험")
            elif result == 2:
                # 경고 알림
                if self.co2 == 1:
                    MQTT_MSG = json.dumps({"type": 1, "message": "창문을 열어주세요"})

                    # publish.single("android/him", MQTT_MSG, hostname=self.json_data["EC2"]["AI_IP"])
                else:
                    MQTT_MSG = json.dumps({"type": 1, "message": "전방을 주시하세요"})

                    # publish.single("android/him", MQTT_MSG, hostname=self.json_data["EC2"]["AI_IP"])

                print("경고")
            else:
                print("안졸음")

            sql = "INSERT INTO analysisApp_eye (user_id_id, is_sleep, time) VALUES (%s, %s, %s)"
            val = ("him", result, time_now)

            self.cursor.execute(sql, val)
            self.mydb.commit()

            self.pred_time = datetime.datetime.now()

            # sql = "INSERT INTO analysisApp_co2 (user_id_id, amount, time) VALUES (%s, %s, %s)"
            # val = ("him", result, time_now)
            #
            # self.cursor.execute(sql, val)

            # if result > 0 and (self.result_cnt % 5 == 0) and (self.result_cnt != 0):
            #
            #     if self.result_cnt == 15:
            #         time_now = datetime.datetime.now()
            #
            #         sql = "INSERT INTO analysisApp_eye (user_id_id, is_sleep, time) VALUES (%s, %s, %s)"
            #         val = ("him", result, time_now)
            #
            #         self.cursor.execute(sql, val)
            #
            #         self.mydb.commit()
            #         # publish.single("android/him", "눈을 뜨세요", hostname=self.json_data["EC2"]["AI_IP"])
            #         print(time_now)
            #         print("눈을 뜨세요")
            #     elif self.result_cnt == 20:
            #         time_now = datetime.datetime.now()
            #
            #         sql = "INSERT INTO analysisApp_eye (user_id_id, is_sleep, time) VALUES (%s, %s, %s)"
            #         val = ("him", result, time_now)
            #
            #         self.cursor.execute(sql, val)
            #
            #         self.mydb.commit()
            #         # publish.single("android/him", "졸면 안돼요", hostname=self.json_data["EC2"]["AI_IP"])
            #         print(time_now)
            #         print("졸면 안돼요")
            #
            #     # if result == 1:
            #     #     if self.result_cnt == 10:
            #     #         # publish.single("android/him", "눈을 뜨세요", hostname=self.json_data["EC2"]["AI_IP"])
            #     #         print("눈을 뜨세요")
            #     #     elif self.result_cnt % 20 == 0:
            #     #         # publish.single("android/him", "졸면 안돼요", hostname=self.json_data["EC2"]["AI_IP"])
            #     #         print("졸면 안돼요")
            #
            #     self.result_cnt += 1
            #
            # elif result > 0:
            #     self.result_cnt += 1
            # else:
            #     self.result_cnt = 0

        # print("time :", time.time() - start)
        # print("", self.frame)

    def sleep_gate(self, eye_blink, mouse, nose, co2):
        if eye_blink == 1:
            return 1    # 졸음

        cnt = 0

        if nose == 1:
            cnt += 1
        if mouse == 1:
            cnt += 1
        if co2 == 1:
            cnt += 1

        if cnt > 1:     # 눈 깜박임을 제외한 조건 2가지 이상 만족 => 졸음
            return 1    # 졸음
        elif cnt == 1:  # 눈 깜박임을 제외한 조건 1가지 만족 => 경계
            return 2    # 경계
        else:
            return 0    # 안졸음

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
