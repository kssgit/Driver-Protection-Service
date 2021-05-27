from django.urls import path
from . import views
urlpatterns = [
    path('userdata/<str:userid>',views.userData),
    path('createuser/',views.createUser),
    path('login/',views.login),
    path('userIDcheck/<str:userid>',views.userIdcheck),
    path('userSerialcheck/<str:serial_no1>',views.serial_no_check),
    path('test/<str:userid>',views.test),
]