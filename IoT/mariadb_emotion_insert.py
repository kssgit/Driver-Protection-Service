import mysql.connector

def mariadb_emotion_insert():
    try:
        mydb = mysql.connector.connect(
            host = "localhost",
            user = "username", 
            password = "password",
            port="포트번호",
            database = "mydatabase"
        )

        
    except Except as e :
        print(f"Error connecting to MariaDB Platform: {e}")

    mycursor = mydb.cursor()

    sql = "INSERT INTO co2 (user_id, emotion) VALUES (%s, %s)"
    val = ("user_id", "emotion")

    mycursor.execute(sql, val)

    mydb.commit()

    print(mycursor.rowcount, "record inserted")