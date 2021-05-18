FROM ubuntu

RUN apt-get update
RUN apt-get install -y nginx
RUN apt-get install -y vim
RUN apt-get install -y git
RUN apt-get install -y python3
RUN pip install django djangorestframework django-sslserver mysqlclient gunicorn


#EXPOSE 1833

