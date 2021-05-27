import paho.mqtt.client as mqtt
import PIL.Image as pilimg
import json


def on_connect(client, userdata, flags, rc):
    print("connect.." + str(rc));
    if rc == 0:
        client.subscribe("mydata/CO2");
    else:
        print("connect fail");


def on_message(client, userdata, msg):
    try:
        f = open('output.jpg', "wb")
        f.write(msg.payload)
        print("Image Received")
        f.close()

        im = pilimg.open('output.jpg');
        im.show();
    except Exception as e:
        print("error ", e);
        print("error!..");


with open('../key.json', 'r') as f:
    json_data = json.load(f)

mqttClient = mqtt.Client()
mqttClient.on_connect = on_connect;
mqttClient.on_message = on_message;
mqttClient.connect(json_data["EC2"]["AI_IP"], json_data["MQTT"]["PORT"], 60);
mqttClient.loop_forever();