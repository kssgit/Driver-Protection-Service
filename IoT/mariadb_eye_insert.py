import mysql.connector

mydb = mysql.connector.connect(
    host = "localhost",
    user = "username", 
    password = "password",
    port="포트번호",
    database = "mydatabase"
)

mycursor = mydb.cursor()

sql = "INSERT INTO eye (user_id, is_sleep) VALUES (%s, %s)"
val = ("user_id", "is_sleep")

mycursor.execute(sql, val)

mydb.commit()

print(mycursor.rowcount, "record inserted")