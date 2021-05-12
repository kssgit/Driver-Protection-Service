from django.urls import path
from . import views
urlpatterns = [
    path('userdata/<str:userid>',views.userData),
    path('createuser/',views.createUser),
    path('login/',views.login),
    path('userIDcheck/<str:userid>',views.userIdcheck),

]