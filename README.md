## Setup help
### Create Database
Run in any terminal
~~~
psql -U postgres
CREATE DATABASE name;
GRANT ALL ON DATABASE name TO user;
\c name postgres
GRANT ALL ON SCHEMA PUBLIC TO user;
~~~
### Instructions to run *python.py*
Create a file named .env in this directory, and enter this into the file
>POSTGRES_USER=username  
POSTGRES_PASSWORD=password  
DATABASE_NAME=database
### Prepare environment
run in terminal
~~~
pip install -r requirements.txt
~~~
### Run code
~~~
python python.py
~~~
python3 on mac instead of python

## Alternative method for mac
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python python.py
