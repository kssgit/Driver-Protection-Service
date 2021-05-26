from picamera import PiCamera
import time
import paho.mqtt.publish as publish

camera = PiCamera(); #파이카메라 객체 생성
camera.start_preview(); #미리보기 화면시작
time.sleep(5);

for i in range(1,4):
    camera.capture('/home/pi/iot/Finalproj/image%s.jpg'%i);
    print(i);
    f = open("image%s.jpg1" % i, "rb");
    file = f.read();
    sfile = bytearray(file);
    publish.single("mydata/CO2", sfile, hostname="172.30.1.56");
    time.sleep(1);


print("publish completed");
camera.stop_preview();








