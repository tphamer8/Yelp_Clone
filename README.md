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

<h2>Program walk-through:</h2>
1. When a user joins the Discord server, they are prompted to verify their account by first typing "!verify" in the #verification channel
<img src="https://imgur.com/Pt9Pe21.png" height="100%" width="100%" alt="Welcome/Verifcation Message"/>
<br/>
2. The bot retrieves the corresponding data from the Google Sheet, verifying the order number and email address.
<img src="https://github.com/tphamer8/SLP_Discord_Bot/blob/main/SLP%20Bot%20Message%20Photo.jpg" height="100%" width="100%" alt="Welcome/Verifcation Message"/>
<br/>
3. If a match is found, the bot assigns the appropriate role to the user, granting access to the correct session channels. <br/>
<br/>
4. If there is no match, the bot informs the user of the issue, and the user doesn't obtain access to the channels.
<br/>
<br/>
AWS Instance Hosting: To ensure the bot runs continuously, it is hosted on an AWS EC2 instance. This provides a scalable, reliable, and secure environment, allowing the bot to run 24/7 and handle multiple concurrent verification requests efficiently.
<br/>
<br/>

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
