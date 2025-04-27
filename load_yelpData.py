 
import json
import psycopg2
from contextlib import contextmanager
from dotenv import load_dotenv
import os

load_dotenv()

# Database connection parameters
psql_params = {
    "DB_NAME" : "yelp",  # get from environment variables
    "DB_USER" : "samuel",  # get from environment variables
    "DB_PASSWORD" : "milo", # get from environment variables
    "DB_HOST" : "localhost",  # or the IP address of your database server
    "DB_PORT" : "5432"  # Default PostgreSQL port
}
@contextmanager
def connect_psql(db_params):
    conn = None
    #connect to tempdb database on postgres server using psycopg2
    try:
        conn = psycopg2.connect(dbname=db_params["DB_NAME"],
                                user=db_params["DB_USER"],
                                password=db_params["DB_PASSWORD"],
                                host=db_params["DB_HOST"],
                                port=db_params["DB_PORT"])
        # Create a cursor object to execute SQL queries
        cursor = conn.cursor()
        print("Connected to PostgreSQL")

        # Yield cursor (execution pauses here, allowing the caller to use it)
        yield cursor

        # Commit transactions (if needed)
        conn.commit()

    except Exception as e:
        print(f"Database error: {e}")
        if conn:
            conn.rollback()  # Rollback in case of an error
    finally:
        if conn:
            cursor.close()
            conn.close()
            print("Connection closed")

"""cleanStr4SQL function removes the "single quote" or "back quote" characters from strings. """
def cleanStr4SQL(s):
    return s.replace("'","`").replace("\n"," ")

# Insert business data
def insert_business():
    with connect_psql(psql_params) as cursor:
        #reading the JSON file
        with open('.//yelp_business.JSON','r') as f:    #TODO: update path for the input file
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                # Generate the INSERT statement for the current business
                # TODO: The below INSERT statement is based on a simple (and incomplete) businesstable schema. Update the statement based on your own table schema and
                # include values for all businessTable attributes
                try:
                    cursor.execute("""INSERT INTO Business (business_id, business_name, star_rating, street_address, city, zipcode, state, num_tips, is_open, latitude, longitude)
                                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s); """, 
                                    (data['business_id'],cleanStr4SQL(data["name"]), data["stars"], cleanStr4SQL(data["address"]), data["city"], data["postal_code"], 
                                    data["state"], 0, data["is_open"], data["latitude"], data["longitude"] ) )  
                    
                    attributes = data.get("attributes", {})
                    for key, value in attributes.items():
                        cursor.execute("""INSERT INTO BusinessAttributes(business_ID, attribute, attribute_value)
                                        VALUES (%s, %s, %s); """, 
                                        (data['business_id'],cleanStr4SQL(key), cleanStr4SQL(str(value))) )
                    categories = data["categories"].split(", ")
                    for category in categories:
                        cursor.execute("""INSERT INTO Categories(business_ID, category)
                                        VALUES (%s, %s); """, 
                                        (data['business_id'],cleanStr4SQL(category)) )
                    hours = data.get("hours", {})
                    for day, times in hours.items():
                            close_time, open_time = times.split("-")
                            cursor.execute("""INSERT INTO Hours(business_ID, day_name, close_time, open_time)
                                            VALUES (%s, %s, %s, %s); """, 
                                            (data['business_id'],cleanStr4SQL(day), cleanStr4SQL(open_time), cleanStr4SQL(close_time)))
                    
                except Exception as e:
                    print("Insert to business table failed!",e)
                    cursor.rollback()  # Rollback in case of an error
                line = f.readline()
                count_line +=1
        print(count_line)
        f.close()

def insert_checkins():
    with connect_psql(psql_params) as cursor:
        #reading the JSON file
        with open('.//yelp_checkin.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    for date in data["date"].split(","):
                        cursor.execute("""INSERT INTO Check_Ins (business_id, time_stamp)
                                        VALUES (%s, %s); """, 
                                        (data['business_id'],cleanStr4SQL(date)) )              
                except Exception as e:
                    print("Insert to Checkins table failed!",e)
                    break
                line = f.readline()
                count_line +=1
        print(count_line)
        f.close()
def insert_users():
    with connect_psql(psql_params) as cursor:
        #reading the JSON file
        with open('.//yelp_user.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    cursor.execute("""INSERT INTO Users(user_id, name, average_stars, funny_score, useful_score, cool_score, fans, friends, tips, yelping_since)
                                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s); """, 
                                    (data['user_id'],cleanStr4SQL(data["name"]), data["average_stars"], data["funny"], data["useful"], data["cool"], len(data["friends"]),data["fans"] , data["tipcount"], data["yelping_since"] ) )          
                except Exception as e:
                    print("Insert to Users table failed!",e)
                    break
                line = f.readline()
                count_line +=1
        print(count_line)
        f.close()
def insert_friends():
    with connect_psql(psql_params) as cursor:
        #reading the JSON file
        with open('.//yelp_user.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    for friend in data["friends"]:
                        cursor.execute("""INSERT INTO Friend(user1_id, user2_id)
                                        VALUES (%s, %s); """, 
                                        (data['user_id'],friend) )
                except Exception as e:
                    print("Insert to Users table failed!",e)
                    break
                line = f.readline()
                count_line +=1
        print(count_line)
        f.close()
def insert_tips():
    with connect_psql(psql_params) as cursor:
        #reading the JSON file
        with open('.//yelp_tip.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    cursor.execute("""INSERT INTO Tip(business_id, tip_date, likes, text, user_id)
                                    VALUES (%s, %s, %s, %s, %s)
                                    ON CONFLICT (user_id, tip_date, business_id) DO NOTHING; """, #TODO review data, without ON CONFLICT, it will insert duplicate data
                                    (data["business_id"], data["date"], data["likes"], data["text"], data["user_id"]) )          
                except Exception as e:
                    print("Insert to Tips table failed!",e)
                    print(data)
                    break
                line = f.readline()
                count_line +=1
        print(count_line)
        f.close()
insert_business()
insert_checkins()
insert_users()
insert_friends()
insert_tips()