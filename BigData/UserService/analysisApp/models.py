from django.db import models

class User(models.Model) :
    user_id = models.CharField(max_length=50, primary_key=True)
    user_pwd = models.CharField(max_length=250)
    name = models.CharField(max_length=50)
    birth = models.CharField(max_length=50)
    phone_number = models.CharField(max_length=50)
    email = models.CharField(max_length=50)
    gender = models.CharField(max_length=50)
    create_time = models.DateField(auto_now_add=True)
    serial_no1 = models.CharField(max_length=50)

class Co2(models.Model) :
    time = models.DateTimeField(auto_now_add=True)
    amount = models.IntegerField()
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)

class Eye(models.Model) :
    time = models.DateTimeField(auto_now_add=True)
    is_sleep = models.IntegerField()
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)

class Emotion(models.Model) :
    time = models.DateTimeField(auto_now_add=True)
    emotion = models.IntegerField() # 7가지
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)
# Create your models here.
class DangerTime(models.Model) :
    hour0 = models.IntegerField()
    hour1 = models.IntegerField()
    hour2 = models.IntegerField()
    hour3 = models.IntegerField()
    hour4 = models.IntegerField()
    hour5 = models.IntegerField()
    hour6 = models.IntegerField()
    hour7 = models.IntegerField()
    hour8 = models.IntegerField()
    hour9 = models.IntegerField()
    hour10 = models.IntegerField()
    hour11 = models.IntegerField()
    hour12 = models.IntegerField()
    hour13 = models.IntegerField()
    hour14 = models.IntegerField()
    hour15 = models.IntegerField()
    hour16 = models.IntegerField()
    hour17 = models.IntegerField()
    hour18 = models.IntegerField()
    hour19 = models.IntegerField()
    hour20 = models.IntegerField()
    hour21 = models.IntegerField()
    hour22 = models.IntegerField()
    hour23 = models.IntegerField()
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)