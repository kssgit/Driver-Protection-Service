import paho.mqtt.publish as publish
import cv2

img = cv2.imread('이미지 경로')

publish.single("mydata/img", img, hostname="3.35.174.45")
