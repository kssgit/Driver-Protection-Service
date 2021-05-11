import time
import threading
import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO



# 이산화탄소 subscribe
class MyMqtt_Sub():
    def __init__(self):

        client = mqtt.Client()
        client.on_connect = self.on_connect
        client.on_message = self.on_message
        client.connect("3.35.174.45", 1883, 60)  # EC2 mqttbroker 주소 
        ##############################
        #GPIO 설정
        GPIO.setmode(GPIO.BCM)
     
        ############################
        #AI 설정

        ###########################
        client.loop_forever()

    def on_connect(self, client, userdata, flags, rc):
        print("connect.." + str(rc))
        if rc == 0:
            client.subscribe("mydata/sensor")
        else:
            print("연결실패")

    def on_message(self, client, userdata, msg):

        myval = msg.payload.decode("utf-8")

        print(myval)
        print(msg.topic + "----" + str(myval))

        # 이미지 판별 
        if myval == '' :
            pass




        

if __name__ == "__main__":
    try:
        mymqtt = MyMqtt_Sub()
    except KeyboardInterrupt:
        print("종료")
        GPIO.cleanup()