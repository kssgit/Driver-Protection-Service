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
    yesterday = datetime.today() - timedelta(1)
    df_Co2 = df_Co2.loc[df_Co2.year == yesterday.year]
    df_Co2 = df_Co2.loc[df_Co2.month==yesterday.month]
    df_Co2 = df_Co2.loc[df_Co2.day==yesterday.day]
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

# 회원 운전 위험수치 가져오기
@api_view(['GET'])
def yesterdayData(request, userid):
    user_id = userid
    print(user_id)

    # 사용자의 ID를 가지고 해당 사용자의 데이터 구하기
    eye = Eye.objects.filter(user_id=user_id)
    emotion = Emotion.objects.filter(user_id=user_id)

    # 1. eye 전처리
    eye_time = []
    eye_issleep = []
    for a in eye:
        eye_time.append(a.time)
        eye_issleep.append(a.is_sleep)
    df_eye = pd.DataFrame({'time': eye_time, 'is_sleep': eye_issleep})
    print(df_eye)

    # 1-2. 시간 단위 쪼개기
    print(df_eye.info())
    df_eye['year'] = df_eye['time'].dt.year
    df_eye['month'] = df_eye['time'].dt.month
    df_eye['day'] = df_eye['time'].dt.day
    df_eye['hour'] = df_eye['time'].dt.hour
    df_eye['minute'] = df_eye['time'].dt.minute
    df_eye['second'] = df_eye['time'].dt.second
    print(df_eye)

    # 1-3. 어제의 날짜 데이터만 추출
    yesterday = datetime.today() - timedelta(1)
    print(yesterday)
    df_eye = df_eye.loc[df_eye.year == yesterday.year]
    df_eye = df_eye.loc[df_eye.month == yesterday.month]
    df_eye = df_eye.loc[df_eye.day == yesterday.day]
    print(df_eye)
    print(df_eye.info())
    # 1-4.
    print(df_eye.groupby('is_sleep').size())
    df_sleep = pd.DataFrame(df_eye.groupby('is_sleep').size())
    #eye0 = df_eye.groupby('is_sleep').size()[0]
    #eye1 = df_eye.groupby('is_sleep').size()[1]
    #eye2 = df_eye.groupby('is_sleep').size()[2]

    #df_sleep = pd.DataFrame({'good': eye0, 'sleep': eye1, 'warning': eye2})
    print(df_sleep)
    print(df_sleep.info())

    # 2. emotion 전처리
    emotion_time = []
    emotion_emotion = []
    for a in emotion:
        emotion_time.append(a.time)
        emotion_emotion.append(a.emotion)
    df_emotion = pd.DataFrame({'time': emotion_time, 'emotion': emotion_emotion})
    # print(df_emotion)

    # 2-1. 시간 단위 쪼개기
    # print(df_emotion.info())
    df_emotion['year'] = df_emotion['time'].dt.year
    df_emotion['month'] = df_emotion['time'].dt.month
    df_emotion['day'] = df_emotion['time'].dt.day
    df_emotion['hour'] = df_emotion['time'].dt.hour
    df_emotion['minute'] = df_emotion['time'].dt.minute
    df_emotion['second'] = df_emotion['time'].dt.second
    # print(df_emotion)

    # 2-2. 어제의 날짜 데이터만 추출
    yesterday = datetime.today() - timedelta(1)
    # print(yesterday)
    df_emotion = df_emotion.loc[df_emotion.year == yesterday.year]
    df_emotion = df_emotion.loc[df_emotion.month == yesterday.month]
    df_emotion = df_emotion.loc[df_emotion.day == yesterday.day]
    # print(df_emotion)


    json_list = {
        'Sleep' : df_sleep,
        'Emotion' : df_emotion,
    }

    return Response(json_list)