import mysql.connector
import json

def mariadb_co2_insert(user_id,amount):
    try:
        with open('../key.json')as json_file:
            json_data = json.load(json_file)
        db = json_data["DB_Server"]
        ip = json_data["EC2"]
        mydb = mysql.connector.connect(
            host = ip["IP"],
            user = db["USER"], 
            password = db["PASSWORD"],
            port=db["PORT"],
            database = db["NAME"]
        )
    except Exception as e:
        print(f"Error connecting to MariaDB Platform: {e}")

    mycursor = mydb.cursor()

    sql = "INSERT INTO co2 (user_id, amount) VALUES (%s, %s)"
    val = (user_id, amount)

    mycursor.execute(sql, val)

    mydb.commit()

    print(mycursor.rowcount, "record inserted")