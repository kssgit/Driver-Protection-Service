from django.shortcuts import render
from django.http import JsonResponse
from django.conf import settings
from .apps import AppConfig
from rest_framework.decorators import api_view
import os
import cv2
import numpy as np
import pandas as pd
import time
import glob
import requests
from scipy import ndimage
from scipy.ndimage import zoom


@api_view(["POST"])

def eye_blink(request):
    url = request.POST.get('url')

    pass

