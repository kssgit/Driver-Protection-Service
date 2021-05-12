from django.db import models
from rest_framework import serializers
from .models import User,Co2,Eye,Emotion

class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        # models = 
        # fields = []
        pass