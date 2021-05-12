from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import User,Co2,Eye,Emotion
import json

# 회원 운전 위험수치 가져오기
@api_view(['POST'])
def userData(request):
    param = json.loads(request.body)
    user_id = param['user_id']
    #사용자의 ID를 가지고 해당 사용자의 데이터 구하기

    # 구한 데이터 Serializer

    return Response("serializer된 데이터")



# 회원 가입
@api_view(['POST'])
def createUser(request):
    param = json.loads(request.body)
    pass


# 로그인 
@api_view(['POST'])
def login(request):
    param = json.loads(request.body)
    pass

