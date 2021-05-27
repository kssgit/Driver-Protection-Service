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
