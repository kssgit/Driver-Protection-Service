import mysql.connector
import json
import datetime
import random
import pandas as pd

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

df = pd.read_csv('C:/Users/s_csmscox/Downloads/drowzy.csv')

for u, t, s in zip(df['user_id_id'], df['time'], df['is_sleep']):

    co2 = 1800

    if s == 1:
        co2 = random.randint(2000, 2500)
    else:
        co2 = random.randint(1800, 1999)

    sql = "INSERT INTO analysisApp_co2 (user_id_id, amount, time) VALUES (%s, %s, %s)"
    val = (u, co2, t)

    mycursor.execute(sql, val)

mydb.commit()

# def mariadb_co2_insert(user_id,amount):
#     try:
#         with open('../key.json')as json_file:
#             json_data = json.load(json_file)
#         db = json_data["DB_Server"]
#         ip = json_data["EC2"]
#         mydb = mysql.connector.connect(
#             host = ip["IP"],
#             user = db["USER"],
#             password = db["PASSWORD"],
#             port=db["PORT"],
#             database = db["NAME"]
#         )
#     except Exception as e:
#         print(f"Error connecting to MariaDB Platform: {e}")
#
#     mycursor = mydb.cursor()
#
#     sql = "INSERT INTO co2 (user_id, amount) VALUES (%s, %s)"
#     val = (user_id, amount)
#
#     mycursor.execute(sql, val)
#
#     mydb.commit()
#
#     print(mycursor.rowcount, "record inserted")