import serial
import paho.mqtt.publish as publish

port = '/dev/ttyACM0'
brate = 9600

seri = serial.Serial(port,baudrate=brate,timeout=None)
print(seri.name)

a=1;

while a:
    if seri.in_waiting !=0:
        content = seri.readline()
        print(content[:-2].decode())
        publish.single("mydata/Co2",content, hostname="192.168.0.82")