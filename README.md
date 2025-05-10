<h1>Yelp Application Clonet</h1>
<!-- ### [YouTube Demonstration](https://youtu.be/7eJexJVCqJo) -->
<h2 style="color: darkblue;">Description</h2>
This application queries the data on <a href="https://business.yelp.com/data/resources/open-dataset/">Yelp's Open Dataset</a> through a user-friendly interface designed on JavaFX. The app can search business by location, categories, and attributes, as well as query similar businesses to a selected company.
<br />

<h2 style="color: darkblue;">Languages and Utilities Used</h2>

- <b>PostgreSQL</b>
- <b>Python</b>
- <b>JavaFX</b>
- <b>Datagrib</b>

<h2>Design Process</h2>
1. Design an Entity-Relation (ER) diagram based on the Yelp Dataset <br/>
2. Created the Database and implemented relations of the ER Diagram into create tables <br/>
3. Parsed the JSON data files into insert statements using Python scripts and loaded the database <br/>
4. Programmed triggers, functions, and test queries, and ran them on the populated database <br/>
5. Designed the application using JavaFX, Scene Builder, and CSS styling <br/>
6. Used the implementations from step 4 to query and display the data <br/>

<h2>Program walk-through:</h2>






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
