import datetime
import random
import mysql.connector
import json

try:
    with open('../key.json')as json_file:
        json_data = json.load(json_file)
        db = json_data["DB_Server"]
        ip = json_data["EC2"]
        mydb = mysql.connector.connect(
            host=ip["IP"],
            user=db["USER"],
            password=db["PASSWORD"],
            port=db["PORT"],
            database=db["NAME"]
        )
except Exception as e:
    print(f"Error connecting to MariaDB Platform: {e}")

mycursor = mydb.cursor()

for i in range(0, 120):

    if i % 60 < 10:
        time_str = "2021-05-28 " + str(13 + int(i / 60)) + ":0" + str(i % 60)
    else:
        time_str = "2021-05-28 " + str(13 + int(i / 60)) + ":" + str(i % 60)

    dt = datetime.datetime.strptime(time_str, "%Y-%m-%d %H:%M")

    one = random.randint(0, 120)
    two = random.randint(0, 30)
    is_sleep = 0

    if one == 0:
        is_sleep = 1
    elif two == 0:
        is_sleep = 2

    if is_sleep > 0:
        sql = "INSERT INTO analysisApp_eye (user_id_id, is_sleep, time) VALUES (%s, %s, %s)"
        val = ("kim", is_sleep, dt)

        mycursor.execute(sql, val)

mydb.commit()

# def mariadb_eye_insert(user_id, is_sleep):
#     try:
#         with open('../key.json')as json_file:
#             json_data = json.load(json_file)
#             db = json_data["DB_Server"]
#             ip = json_data["EC2"]
#             mydb = mysql.connector.connect(
#                 host=ip["IP"],
#                 user=db["USER"],
#                 password=db["PASSWORD"],
#                 port=db["PORT"],
#                 database=db["NAME"]
#             )
#     except Exception as e:
#         print(f"Error connecting to MariaDB Platform: {e}")
#
#     mycursor = mydb.cursor()
#
#     time_now = datetime.datetime.now()
#
#     sql = "INSERT INTO analysisApp_eye (user_id_id, is_sleep, time) VALUES (%s, %s, %s)"
#     val = (user_id, is_sleep, time_now)
#
#     mycursor.execute(sql, val)
#
#     mydb.commit()
#
#     print(mycursor.rowcount, "record inserted")