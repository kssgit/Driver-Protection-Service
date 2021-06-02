from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import User,Co2,Eye,Emotion
from .UserSerializer import UserSerializer,UserLoginSerializer
from .UserDataSerializer import Co2Serializer,EmotionSerializer,EyeSerializer
import json
import hashlib
import pandas as pd
from datetime import datetime, timedelta


# from django.core.context_processors import csrf

# 회원 운전 위험수치 가져오기
@api_view(['GET'])
def userData(request,userid):
    user_id = userid
    print(user_id)

    #사용자의 ID를 가지고 해당 사용자의 데이터 구하기
    co2 = Co2.objects.filter(user_id=user_id)
    eye = Eye.objects.filter(user_id=user_id)
    emotion = Emotion.objects.filter(user_id=user_id)

    # 시간 전처리


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


# 사용자 하루 운전 수치 분석
@api_view(["GET"]) 
def one_day_data(request):
    pass


# 사용자 정보 수정 (시리얼 번호 변경)
@api_view(["PUT"])
def user_data_update(request):
    user_id = request.data['user_id']
    serial_no1 = request.data['serial_no1']
    user = User.objects.get(user_id = user_id)
    user.serial_no1 =serial_no1
    user.save()
    return Response({"result":True})


# 회원 가입
@api_view(['POST'])
def createUser(request):
    user_pwd = request.data['user_pwd'].encode()
    encode_pwd = hashlib.sha256(user_pwd).hexdigest()

    json_data = request.data
    print(json_data)
    json_data['user_pwd']=encode_pwd
    print(json_data)
    # 역 직렬화
    serializer = UserSerializer(data = json_data)
   

    if(serializer.is_valid()):
        print("성공")
        serializer.save()
        return Response({'success':True})
    else:
        print("실패")
        return Response({'success':False})
   


#사용자 아이디 중복 체크
@api_view(['GET'])
def userIdcheck(request,userid):
    user_id =userid
    try:
        userIdcheck = User.objects.get(user_id=user_id)
        return Response({'result':False})
    except Exception:
        return Response({'result':True})

# 시리얼 번호 중복 체크
@api_view(['GET'])
def serial_no_check(request,serial_no1):
    serial_no1=serial_no1
    try:
        serialcheck = User.objects.get(serial_no1=serial_no1)
        return Response({'result':False})
    except Exception:
        return Response({'result':True})



# 로그인 
@api_view(['POST'])
def login(request):
    # loginSerial = UserLoginSerializer(data = request.data)
    json_param = json.loads(request.body)
    # print(json_param)
    user_id = json_param['user_id']
    print("user_id :",user_id)
    userpwd = json_param['user_pwd']
    # print("user_pwd :",userpwd)

    user_pwd = userpwd.encode()
    encode_pwd = hashlib.sha256(user_pwd).hexdigest()
    # print(encode_pwd)

    try:
        idcheck = User.objects.get(user_id = user_id)
        # 아이디 존재
        print(idcheck.user_pwd)
        if encode_pwd == idcheck.user_pwd:
            print("로그인 성공")
            return Response({'message':'성공','user_id':user_id,'success':True})
        else:
            # print("로그인 실패1")
            return Response( {'message':'비밀번호','success':False})
    except Exception:
        print("로그인 실패2")
        return Response({'message':'아이디','success':False})


# 회원 운전 위험수치 가져오기
@api_view(['GET'])
def today_state(request, userid):
    user_id = userid
    print(user_id)

    # 사용자의 ID를 가지고 해당 사용자의 데이터 구하기
    # 졸음운전 데이터 + 감정 데이터
    emotion = Emotion.objects.filter(user_id=user_id)

    # 1. co2 전처리
    emotion_time = []
    emotion_emotion = []
    for a in emotion:
        emotion_time.append(a.time)
        emotion_emotion.append(a.emotion)
    df_Emotion = pd.DataFrame({'time':emotion_time, 'emotion':emotion_emotion})
    print(df_Emotion)

    # 1-1. 시간 전처리
    # 1-1-1. 시간 단위 쪼개기
    print(df_Emotion.info())
    df_Emotion['year'] = df_Emotion['time'].dt.year
    df_Emotion['month'] = df_Emotion['time'].dt.month
    df_Emotion['day'] = df_Emotion['time'].dt.day
    df_Emotion['hour'] = df_Emotion['time'].dt.hour
    df_Emotion['minute'] = df_Emotion['time'].dt.minute
    df_Emotion['second'] = df_Emotion['time'].dt.second
    print(df_Emotion)
    today = datetime.today()

    # 1-1-2.
    df_Emotion = df_Emotion.loc[df_Emotion.day==today.dt.day]

    # 구한 데이터 Serializer
    #co2Serializer = Co2Serializer(co2, many=True)
    #eyeSerializer = EyeSerializer(eye, many=True)
    #emotionSerializer = EmotionSerializer(emotion, many=True)

    json_list = {
        #'Co2': co2Serializer.data,
        #'Eye': eyeSerializer.data,
        #'Emotion': emotionSerializer.data,
        'Test' : df_Emotion,
    }

    return Response(json_list)

# 회원 운전 위험수치 가져오기
@api_view(['GET'])
def test(request, userid):
    user_id = userid
    print(user_id)

    # 사용자의 ID를 가지고 해당 사용자의 데이터 구하기
    co2 = Co2.objects.filter(user_id=user_id)
    eye = Eye.objects.filter(user_id=user_id)
    emotion = Emotion.objects.filter(user_id=user_id)

    # 1. co2 전처리
    co2_time = []
    co2_amount = []
    for a in co2:
        co2_time.append(a.time)
        co2_amount.append(a.amount)
    df_Co2 = pd.DataFrame({'time':co2_time, 'amount':co2_amount})
    print(df_Co2)

    # 1-1. 시간 전처리
    # 1-1-1. 시간 단위 쪼개기
    print(df_Co2.info())
    df_Co2['year'] = df_Co2['time'].dt.year
    df_Co2['month'] = df_Co2['time'].dt.month
    df_Co2['day'] = df_Co2['time'].dt.day
    df_Co2['hour'] = df_Co2['time'].dt.hour
    df_Co2['minute'] = df_Co2['time'].dt.minute
    df_Co2['second'] = df_Co2['time'].dt.second
    print(df_Co2)
    print(datetime.today().day)

    # 1-1-2. 오늘 날짜의 데이터만 추출
    today = datetime.today()
    df_Co2 = df_Co2.loc[df_Co2.year == today.year]
    df_Co2 = df_Co2.loc[df_Co2.month==today.month]
    df_Co2 = df_Co2.loc[df_Co2.day==today.day]
    print(df_Co2)

    # 1-1-2. 어제의 날짜 데이터만 추출
    yesterday = datetime.today() - timedelta(1)
    print(yesterday)

    # 구한 데이터 Serializer
    co2Serializer = Co2Serializer(co2, many=True)
    eyeSerializer = EyeSerializer(eye, many=True)
    emotionSerializer = EmotionSerializer(emotion, many=True)

    json_list = {
        'Co2': co2Serializer.data,
        'Eye': eyeSerializer.data,
        'Emotion': emotionSerializer.data,
        'Test' : df_Co2,
    }

    return Response(json_list)