import mysql.connector
import json

def maraidb_userid_select(serial_no1):
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

    serial_no1 = "2313wqe" 
    select_where_query = "SELECT user_id from user WHERE serial_no1=?" 

    mycursor.execute(select_where_query,(serial_no1,))
    result = mycursor.fetchall()
    return result