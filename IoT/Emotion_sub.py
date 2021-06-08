import random
import time
import tensorflow as tf
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
# import RPi.GPIO as GPIO
import numpy as np
import json
import base64
import cv2
import dlib
import datetime
from konlpy.tag import Okt, Komoran
from gluonnlp.data import SentencepieceTokenizer
from transformers import TFGPT2LMHeadModel
import gluonnlp as nlp
import mysql.connector
from tensorflow.keras.models import load_model


class MyMqtt_Sub():
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

        self.senti_model = load_model('C:/Users/s_csmscox/jupyterSave/eye_blink/facial-emotion_model.h5')

        self.okt = Okt()
        self.komoran = Komoran()

        self.gpt2 = GPT2Model('C:/Users/s_csmscox/jupyterSave/KoGPT2_LM')

        # 토크나이저 로드
        TOKENIZER_PATH = '../AI/LM_GPT2/gpt_ckpt/gpt2_kor_tokenizer.spiece'
        self.tokenizer = SentencepieceTokenizer(TOKENIZER_PATH, num_best=0, alpha=0)
        self.vocab = nlp.vocab.BERTVocab.from_sentencepiece(TOKENIZER_PATH,
                                                            mask_token=None,
                                                            sep_token=None,
                                                            cls_token=None,
                                                            unknown_token='<unk>',
                                                            padding_token='<pad>',
                                                            bos_token='<s>',
                                                            eos_token='</s>')

        self.emo_pred_output = []
        self.emo_pred_time = None
        self.is_face_exist = False

        # top_k_rd = random.randint(4, 7)
        #
        # Text_final = (
        #     self.generate_sent(self.feeling(2), self.gpt2, greedy=False,
        #                        top_k=top_k_rd,
        #                        top_p=0.95))
        # mid = self.okt.normalize(str(Text_final))
        # mid_df = self.spacing_okt(mid)
        # sentence_result = '제가 ' + self.feeling(
        #     2) + '에 대한 문장을 만들어 볼께요.' + mid_df
        #
        # print(sentence_result)

        MQTT_MSG = json.dumps(
            {"type": 1, "message": "제가 침착에 대한 문장을 만들어 볼께요. 침착하게 듣고 침착하게 말 하는 사람은 그 마음에 안정감과 활력이 넘친다."})

        publish.single("android/him", MQTT_MSG, hostname=self.json_data["EC2"]["AI_IP"])

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
            faces = self.detector(myval)
            self.is_face_exist = False

            emo_pred = 4

            for face in faces:
                try:
                    cropped_img = myval[face.top():face.bottom(), face.left():face.right()]
                    cropped_img = cv2.resize(cropped_img, (48, 48))
                    cropped_img = cv2.cvtColor(cropped_img, cv2.COLOR_BGR2GRAY)

                    cropped_img = cropped_img.reshape((1, 48, 48, 1)).astype(np.float32)
                    cropped_img = cropped_img / 255

                    emo_pred = self.senti_model.call(tf.convert_to_tensor(cropped_img), training=False)
                    emo_pred = np.argmax(emo_pred)
                except:
                    pass

            print(emo_pred)

            # {0: 'Angry', 1: 'Fear', 2: 'Happy', 3: 'Sad', 4: 'Neutral'}
            self.emo_pred_output.append(emo_pred)

            if self.emo_pred_time is None:
                if len(self.emo_pred_output) == 100:
                    emo_result = self.find_most(self.emo_pred_output)
                    print(emo_result)
                    print(self.feeling(emo_result))
                    sentence_result = None

                    # 문장 생성 모델 predict
                    if emo_result == 4:
                        pass
                    else:
                        top_k_rd = random.randint(4, 7)

                        Text_final = (
                            self.generate_sent(self.feeling(emo_result), self.gpt2, greedy=False,
                                               top_k=top_k_rd,
                                               top_p=0.95))
                        mid = self.okt.normalize(str(Text_final))
                        mid_df = self.spacing_okt(mid)
                        sentence_result = '제가 ' + self.feeling(
                            emo_result) + '에 대한 문장을 만들어 볼께요.' + mid_df

                        # publish.single("android/him", sentence_result, hostname=self.json_data["EC2"]["AI_IP"])

                    time_now = datetime.datetime.now()

                    sql = "INSERT INTO analysisApp_emotion (user_id_id, emotion, time) VALUES (%s, %s, %s)"
                    val = ("him", emo_result, time_now)

                    self.cursor.execute(sql, val)

                    self.mydb.commit()

                    self.emo_pred_output.clear()
                    self.emo_pred_time = time_now

                    print(sentence_result)
                    print(self.emo_pred_time)
            else:
                now = datetime.datetime.now()
                if (now - self.emo_pred_time).seconds >= 60:
                    emo_result = self.find_most(self.emo_pred_output)
                    print(len(self.emo_pred_output))
                    sentence_result = None

                    # 문장 생성 모델 predict
                    if emo_result == 4:
                        pass
                    else:
                        top_k_rd = random.randint(4, 7)

                        Text_final = (
                            self.generate_sent(self.feeling(emo_result), self.gpt2, greedy=False,
                                               top_k=top_k_rd,
                                               top_p=0.95))
                        mid = self.okt.normalize(str(Text_final))
                        mid_df = self.spacing_okt(mid)
                        sentence_result = '제가 ' + self.feeling(
                            emo_result) + '에 대한 문장을 만들어 볼께요.' + mid_df

                        # publish.single("android/him", sentence_result, hostname=self.json_data["EC2"]["AI_IP"])

                    time_now = datetime.datetime.now()

                    sql = "INSERT INTO analysisApp_emotion (user_id_id, emotion, time) VALUES (%s, %s, %s)"
                    val = ("him", emo_result, time_now)

                    self.cursor.execute(sql, val)

                    self.mydb.commit()

                    self.emo_pred_output.clear()
                    self.emo_pred_time = time_now

                    print(sentence_result)
                    print(self.emo_pred_time)

    def find_most(self, emo_pred_output):
        maximum = 0
        emo = 0

        for i in range(0, 5):
            cnt = emo_pred_output.count(i)

            if cnt > maximum:
                maximum = cnt
                emo = i

        return emo

    def tf_top_k_top_p_filtering(self, logits, top_k=0, top_p=0.0, filter_value=-99999):
        _logits = logits.numpy()
        top_k = min(top_k, logits.shape[-1])
        if top_k > 0:
            indices_to_remove = logits < tf.math.top_k(logits, top_k)[0][..., -1, None]
            _logits[indices_to_remove] = filter_value

        if top_p > 0.0:
            sorted_logits = tf.sort(logits, direction='DESCENDING')
            sorted_indices = tf.argsort(logits, direction='DESCENDING')
            cumulative_probs = tf.math.cumsum(tf.nn.softmax(sorted_logits, axis=-1), axis=-1)

            sorted_indices_to_remove = cumulative_probs > top_p
            sorted_indices_to_remove = tf.concat([[False], sorted_indices_to_remove[..., :-1]], axis=0)
            indices_to_remove = sorted_indices[sorted_indices_to_remove].numpy().tolist()

            _logits[indices_to_remove] = filter_value
        return tf.constant([_logits])

    # 언어 생성 함수
    def generate_sent(self, seed_word, model, max_step=100, greedy=False, top_k=0, top_p=0.):
        sent = seed_word
        toked = self.tokenizer(sent)

        for _ in range(max_step):
            input_ids = tf.constant([self.vocab[self.vocab.bos_token], ] + self.vocab[toked])[None, :]
            outputs = model(input_ids)[:, -1, :]
            if greedy:
                gen = self.vocab.to_tokens(tf.argmax(outputs, axis=-1).numpy().tolist()[0])
            else:
                output_logit = self.tf_top_k_top_p_filtering(outputs[0], top_k=top_k, top_p=top_p)
                gen = self.vocab.to_tokens(tf.random.categorical(output_logit, 1).numpy().tolist()[0])[0]
            if gen == '</s>':
                break
            sent += gen.replace('▁', ' ')
            toked = self.tokenizer(sent)
        return sent

    # 언어생성 후 띄어쓰기 함수
    def spacing_okt(self, wrong_sentence):
        tagged = self.okt.pos(wrong_sentence)
        corrected = ""
        for i in tagged:
            if i[1] in ('Josa', 'PreEomi', 'Eomi', 'Suffix', 'Punctuation'):
                corrected += i[0]
            else:
                corrected += " " + i[0]
        if corrected[0] == " ":
            corrected = corrected[1:]
        return corrected

    def feeling(self, text):
        feeling_number = None
        if text == 0:
            feeling_number = '용서'
        elif text == 1:
            feeling_number = '용기'
        elif text == 2:
            feeling_number = '침착'
        elif text == 3:
            feeling_number = '사랑'
        elif text == 4:
            feeling_number = '중립'
        return feeling_number


class GPT2Model(tf.keras.Model):
    def __init__(self, dir_path):
        super(GPT2Model, self).__init__()
        self.gpt2 = TFGPT2LMHeadModel.from_pretrained(dir_path)

    def call(self, inputs):
        return self.gpt2(inputs)[0]


if __name__ == "__main__":
    try:
        mymqtt = MyMqtt_Sub()
    except KeyboardInterrupt:
        print("종료")
        # GPIO.cleanup()
