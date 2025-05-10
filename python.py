import json
import psycopg2
from contextlib import contextmanager
from dotenv import load_dotenv
import os

load_dotenv()

# Database connection parameters
psql_params = {
    "DB_NAME": os.getenv("DATABASE_NAME"),
    "DB_USER": os.getenv("POSTGRES_USER"),
    "DB_PASSWORD": os.getenv("POSTGRES_PASSWORD"),
    "DB_HOST" : "localhost", 
    "DB_PORT" : "5432"  
}

@contextmanager
def connect_psql(db_params):
    conn = None
    try:
        conn = psycopg2.connect(dbname=db_params["DB_NAME"],
                                user=db_params["DB_USER"],
                                password=db_params["DB_PASSWORD"],
                                host=db_params["DB_HOST"],
                                port=db_params["DB_PORT"])
        cursor = conn.cursor()
        print("Connected to PostgreSQL")
        yield cursor
        conn.commit()

    except Exception as e:
        print(f"Database error: {e}")
        if conn:
            conn.rollback()
    finally:
        if conn:
            cursor.close()
            conn.close()
            print("Connection closed")
def cleanStr4SQL(s):
    if s is None:
        return ""
    return str(s).replace("'","`").replace("\n"," ")
def get_attributes(attributes):
    L = []
    if not attributes:
        return L
        
    for (attribute, value) in attributes.items():
        if isinstance(value, dict):
            # Recursively process nested attributes
            nested_attrs = get_attributes(value)
            for nested_attr in nested_attrs:
                # Create flattened key-value pairs
                L.append((f"{attribute}.{nested_attr[0]}", nested_attr[1]))
        else:
            # Add the attribute and its value to the list
            L.append((attribute, value))
    return L
def insert_business():
    with connect_psql(psql_params) as cursor:
        #reading the JSON file
        with open('.//yelp_business.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    cursor.execute("""INSERT INTO Business (business_id, business_name, star_rating, street_address, city, zipcode, state, num_tips, is_open, latitude, longitude)
                                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s); """, 
                                    (data['business_id'], cleanStr4SQL(data["name"]), data["stars"], cleanStr4SQL(data["address"]), data["city"], data["postal_code"], 
                                    data["state"], 0, data["is_open"], data["latitude"], data["longitude"]))
                    
                    # Use the improved get_attributes function to handle nested attributes
                    attributes = get_attributes(data.get("attributes", {}))
                    for attribute, value in attributes:
                        cursor.execute("""INSERT INTO BusinessAttributes(business_ID, attribute, attribute_value)
                                        VALUES (%s, %s, %s); """, 
                                        (data['business_id'], cleanStr4SQL(attribute), cleanStr4SQL(value)))
                    
                    # Process categories
                    categories = data["categories"].split(", ") if data.get("categories") else []
                    for category in categories:
                        cursor.execute("""INSERT INTO Categories(business_ID, category)
                                        VALUES (%s, %s); """, 
                                        (data['business_id'], cleanStr4SQL(category)))
                    
                    # Process hours
                    hours = data.get("hours", {})
                    for day, times in hours.items():
                        if times and "-" in times:
                            open_time, close_time = times.split("-")
                            cursor.execute("""INSERT INTO Hours(business_ID, day_name, close_time, open_time)
                                            VALUES (%s, %s, %s, %s); """, 
                                            (data['business_id'], cleanStr4SQL(day), cleanStr4SQL(close_time), cleanStr4SQL(open_time)))
                    
                except Exception as e:
                    print(f"Insert to business table failed for {data.get('business_id', 'unknown')}: {e}")
                    # Not using cursor.rollback() here as we're using a context manager
                
                line = f.readline()
                count_line += 1
        
        print(f"Processed {count_line} business")
        f.close()
def insert_checkins():
    with connect_psql(psql_params) as cursor:
        with open('.//yelp_checkin.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    for date in data["date"].split(","):
                        cursor.execute("""INSERT INTO Check_Ins (business_id, time_stamp)
                                        VALUES (%s, %s); """, 
                                        (data['business_id'], cleanStr4SQL(date.strip())))
                except Exception as e:
                    print(f"Insert to Checkins table failed for {data.get('business_id', 'unknown')}: {e}")
                
                line = f.readline()
                count_line += 1
        
        print(f"Processed {count_line} checkin")
        f.close()
def insert_users():
    with connect_psql(psql_params) as cursor:
        with open('.//yelp_user.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    cursor.execute("""INSERT INTO Users(user_id, name, average_stars, funny_score, useful_score, cool_score, fans, friends, tips, yelping_since)
                                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s); """, 
                                    (data['user_id'], cleanStr4SQL(data["name"]), data["average_stars"], data["funny"], 
                                     data["useful"], data["cool"], data["fans"], len(data["friends"]), 
                                     data["tipcount"], data["yelping_since"]))
                except Exception as e:
                    print(f"Insert to Users table failed for {data.get('user_id', 'unknown')}: {e}")
                
                line = f.readline()
                count_line += 1
        
        print(f"Processed {count_line} user")
        f.close()

def insert_friends():
    with connect_psql(psql_params) as cursor:
        with open('.//yelp_user.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    for friend in data.get("friends", []):
                        cursor.execute("""INSERT INTO Friend(user1_id, user2_id)
                                        VALUES (%s, %s); """, 
                                        (data['user_id'], friend))
                except Exception as e:
                    print(f"Insert to Friend table failed for {data.get('user_id', 'unknown')}: {e}")
                
                line = f.readline()
                count_line += 1
        
        print(f"Processed {count_line} friend")
        f.close()

def insert_tips():
    with connect_psql(psql_params) as cursor:
        with open('.//yelp_tip.JSON','r') as f:
            line = f.readline()
            count_line = 0
            while line:
                data = json.loads(line)
                try:
                    cursor.execute("""INSERT INTO Tip(business_id, tip_date, likes, text, user_id)
                                    VALUES (%s, %s, %s, %s, %s); """,
                                    (data["business_id"], data["date"], data["likes"], 
                                     cleanStr4SQL(data["text"]), data["user_id"]))
                except Exception as e:
                    print(f"Insert to Tip table failed: {e}")
                    print(data)
                
                line = f.readline()
                count_line += 1
        
        print(f"Processed {count_line} tip")
        f.close()

# Main execution
if __name__ == "__main__":
    insert_business()
    insert_checkins()
    insert_users()
    insert_friends()
    insert_tips()