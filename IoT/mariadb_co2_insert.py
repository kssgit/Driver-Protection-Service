import mysql.connector

def mariadb_co2_insert():
    mydb = mysql.connector.connect(
        host = "localhost",
        user = "username", 
        password = "password",
        port="포트번호",
        database = "mydatabase"
    )

    mycursor = mydb.cursor()

    sql = "INSERT INTO co2 (user_id, amount) VALUES (%s, %s)"
    val = ("user_id", "amount")

    mycursor.execute(sql, val)

    mydb.commit()

    print(mycursor.rowcount, "record inserted")