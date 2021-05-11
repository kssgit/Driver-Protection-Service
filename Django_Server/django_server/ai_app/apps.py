import os
import tensorflow as tf
from django.conf import settings
from django.apps import AppConfig
from tensorflow.keras.models import load_model


class AiAppConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'ai_app'

    # gpu 메모리 최대로 잡는 것을 방지
    gpus = tf.config.experimental.list_physical_devices('GPU')
    if gpus:
        try:
            # Currently, memory growth needs to be the same across GPUs
            for gpu in gpus:
                tf.config.experimental.set_memory_growth(gpu, True)
            logical_gpus = tf.config.experimental.list_logical_devices('GPU')
            print(len(gpus), "Physical GPUs,", len(logical_gpus), "Logical GPUs")
        except RuntimeError as e:
            # Memory growth must be set before GPUs have been initialized
            print(e)

    # path = os.path.join(settings.MODELS, "모델명")
    model = load_model('C:/Users/dltmd/jupyterSave/Multicampus/eye_blink_CNN.h5')
