# Setup Instructions

## Database

### Create Database
Run in any terminal
~~~
psql -U postgres
CREATE DATABASE yelp;
GRANT ALL ON DATABASE yelp TO user;
\c yelp postgres
GRANT ALL ON SCHEMA PUBLIC TO user;
~~~
Then run the SQL code in db_setup.sql on the database to setup tables and functions.

### Populate Database

Create a file named .env in this directory, enter this into the file
>POSTGRES_USER=username  
POSTGRES_PASSWORD=password  
DATABASE_NAME=yelp
### Prepare environment
run in terminal
~~~
pip install -r requirements.txt
~~~
### Run code
~~~
python db_populate.py
~~~
## Java Application

*Go into /YelpApp directory*

### Connect Database

Create a file named .env in this directory, enter this into the file

>JDBC_URL=jdbc:postgresql://localhost:5432/yelp
>
>JDBC_USER=username
>
>JDBC_PASS=password

### IntelliJ setup

1. Open YelpApp directory as an IntelliJ project
2. Build the application, and check that the java environment is correct on your device
3. Run using the default configuration for main in src/main/java/dev/cs3431/yelpapp/YelpApp.java 