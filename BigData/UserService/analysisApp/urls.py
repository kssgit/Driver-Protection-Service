from django.urls import path
from . import views
urlpatterns = [
    path('userdata/<str:userid>',views.userData),
    path('createuser/',views.createUser),#회원 가입
    path('login/',views.login),#로그인
    path('userSerialChange/',views.user_data_update),#시리얼 번호 변경
    path('userIDcheck/<str:userid>',views.userIdcheck),#아이디 중복 check
    path('userSerialcheck/<str:serial_no1>',views.serial_no_check),#시리얼 번호 중복 Check
    path('test/<str:userid>',views.test),
]