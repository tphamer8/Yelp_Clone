[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/QAcfGHcq)
## Team name: Join Masters
## Team Members:
 * Samuel Goldsmith
 * Thomas Pham
 * Austin Peterson

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
### Instructions to run *phase2_python.py*
Create a file named .env in this directory, enter this into the file
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
python phase2_python.py
~~~
python3 on mac instead

## Alt method for mac
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python phase2_python.py

# ** * Seperate README in phase3 folder * **