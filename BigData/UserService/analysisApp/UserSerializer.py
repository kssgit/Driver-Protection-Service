from rest_framework import serializers
from .models import User,Co2,Eye,Emotion

class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = [
            'user_id',
            'user_pwd',
            'name',
            'birth',
            'phone_number',
            'email',
            'gender',
            'serial_no1',
            ]
        
class UserLoginSerializer(serializers.HyperlinkedModelSerializer):
    class Meta : 
        model = User
        fields = [
            'user_id',
            'user_pwd'
        ]


    # user_id = param['user_id']
    # user_pwd = param['user_pwd']
    # name = param['name']
    # birth = param['birth']
    # phone_number = param['phone_number']
    # email = param['email']
    # gender = param['gender']
    # serial_no1 = param['serial_no1']