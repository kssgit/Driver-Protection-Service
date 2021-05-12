from rest_framework import serializers
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import User,Co2,Eye,Emotion
from .UserSerializer import UserSerializer
import json

# 회원 운전 위험수치 가져오기
@api_view(['GET'])
def userData(request,userid):
    user_id = userid
    #사용자의 ID를 가지고 해당 사용자의 데이터 구하기
    

    # 구한 데이터 Serializer

    return Response("serializer된 데이터")



# 회원 가입
@api_view(['POST'])
def createUser(request):
    # 역 직렬화
    serializer = UserSerializer(data= request.data)
    if(serializer.is_valid()):
        serializer.save()
    return Response('Success')


#사용자 아이디 중복 체크
@api_view(['GET'])
def userIdcheck(request,userid):
    user_id =userid
    userIdcheck = User.objects.get(user_id=user_id)
    if userIdcheck :
        return Response('1')
    else:
        return Response('0')


# 로그인 
@api_view(['POST'])
def login(request):
    param = json.loads(request.body)
    user_id = param['user_id']
    user_pwd = param['user_pwd']
    idcheck = User.objects.get(user_id = user_id)
    if idcheck:
        # 아이디 존재
        if user_pwd == idcheck['user_pwd']:
            return Response({'user_id':user_id,'result':True})
        else:
            return Response('비밀번호가 일치하지 않습니다.')
    else:
        return Response('존재하지 않는 아이디')

