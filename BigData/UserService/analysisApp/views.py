from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import User,Co2,Eye,Emotion
from .UserSerializer import UserSerializer
from .UserDataSerializer import Co2Serializer,EmotionSerializer,EyeSerializer
import json
# from django.core.context_processors import csrf

# 회원 운전 위험수치 가져오기
@api_view(['GET'])
def userData(request,userid):
    user_id = userid
    #사용자의 ID를 가지고 해당 사용자의 데이터 구하기
    co2 = Co2.objects.filter(user_id=user_id)
    eye =Eye.objects.filter(user_id=user_id)
    emotion = Emotion.objects.filter(user_id=user_id)
    # 구한 데이터 Serializer
    co2Serializer =  Co2Serializer(co2,many=True)
    eyeSerializer = EyeSerializer(eye , many=True)
    emotionSerializer = EmotionSerializer(emotion,many=True)
    
    json_list = {
        'Co2':co2Serializer.data,
        'Eye':eyeSerializer.data,
        'Emotion':emotionSerializer.data
    }

    return Response(json_list)



# 회원 가입
@api_view(['POST'])
def createUser(request):
    # 역 직렬화
    serializer = UserSerializer(data= request.data)
    # 사용자 비밀번호 암호화를 해야 하나??
    if(serializer.is_valid()):
        serializer.save()
    else:
        return Response({'success':False})
    return Response({'success':True})


#사용자 아이디 중복 체크
@api_view(['GET'])
def userIdcheck(request,userid):
    user_id =userid
    try:
        userIdcheck = User.objects.get(user_id=user_id)
        return Response({'result':'1'})
    except Exception:
        return Response({'result':'0'})


# 로그인 
@api_view(['POST'])
def login(request):
    param = json.loads(request.body)
    user_id = param['user_id']
    user_pwd = param['user_pwd']
    try:
        idcheck = User.objects.get(user_id = user_id)
        # 아이디 존재
        if user_pwd == idcheck['user_pwd']:
            return Response({'user_id':user_id,'success':True})
        else:
            return Response( {'message':'비밀번호가 일치하지 않습니다.','success':False})
    except Exception:
        return Response({'message':'존재하지 않는 아이디','success':False})

