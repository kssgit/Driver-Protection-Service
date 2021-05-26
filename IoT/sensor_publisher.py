import paho.mqtt.client as mqtt
import json
import serial
from paho.mqtt import publish

port = '/dev/ttyACM0'
brate = 9600
seri = serial.Serial(port,baudrate=brate,timeout=None)
print(seri.name)

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("connected OK")
    else:
        print("Bad connection", rc)


def on_disconnect(client, userdata, flags, rc=0):
    print(str(rc))


def on_publish(client, userdata, mid):
    print("In on_pub callback mid= ", mid)

def runSensor():
    while True:
        if seri.in_waiting != 0:
            content = seri.readline()
            print(content[:-2].decode())
            return content

# 새로운 클라이언트 생성
client = mqtt.Client()
# 콜백 함수 설정 on_connect(브로커에 접속), on_disconnect(브로커에 접속중료), on_publish(메세지 발행)
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_publish = on_publish
# address : localhost, port: 1883 에 연결
client.connect("192.168.0.82",1883,60)
client.loop_start()
# common topic 으로 메세지 발행
client.publish('mydata/CO2', runSensor(), 1)
client.loop_forever()
# 연결 종료
#client.disconnect()