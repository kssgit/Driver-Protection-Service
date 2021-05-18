import paho.mqtt.publish as publish
import cv2

img = cv2.imread('C:/Users/s_csmscox/jupyterSave/eye_blink/face4.jpg')
byteArr = bytearray(img)

publish.single("mydata/img", byteArr, hostname="3.35.174.45")
