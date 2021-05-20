import mysql.connector
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

serial_no1 = "2313wqe" 
select_where_query = "SELECT user_id from user WHERE serial_no1=?" 

mycursor.execute(select_where_query,(serial_no1,))
result = mycursor.fetchall()