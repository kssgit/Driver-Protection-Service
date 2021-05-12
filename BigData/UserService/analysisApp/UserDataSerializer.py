from django.db import models
from rest_framework import serializers
from .models import User,Co2,Eye,Emotion


class Co2Serializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Co2
        fields = [
            'amount',
            'time'
        ]

class EyeSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Eye
        fields = [
            'is_sleep',
            'time'
        ]

class EmotionSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Emotion
        fields = [
            'emotion',
            'time'
        ]