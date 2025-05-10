-- JoinMasters: Samuel Goldsmith, Thomas Pham, Austin Peterson
DROP TABLE IF EXISTS Tip;
DROP TABLE IF EXISTS Check_Ins;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS BusinessAttributes;
DROP TABLE IF EXISTS Friend;
DROP TABLE IF EXISTS Users;
drop table if exists hours;
DROP TABLE IF EXISTS Business CASCADE ;


CREATE TABLE Business (
    business_id CHAR(22),
    business_name VARCHAR(100),
    star_rating INTEGER,
    street_address VARCHAR(100),
    city VARCHAR(20),
    zipcode VARCHAR(5),
    state CHAR(2),
    num_tips INTEGER,
    is_open INTEGER,
    latitude FLOAT,
    longitude FLOAT,
    PRIMARY KEY (business_id)
);

CREATE TABLE BusinessAttributes(
	business_ID CHAR(22),
	attribute VARCHAR,
	attribute_value varchar,
	PRIMARY KEY (business_ID, attribute),
	FOREIGN KEY (business_ID) REFERENCES Business(business_ID)
);
CREATE TABLE Hours(
	business_ID CHAR(22),
	day_name varchar,
	close_time time,
	open_time time,
	PRIMARY KEY (business_ID, day_name),
	FOREIGN KEY (business_ID) REFERENCES Business(business_ID)
);
CREATE TABLE Check_Ins(
	business_ID CHAR(22),
	time_stamp timestamp,
	PRIMARY KEY (business_ID, time_stamp),
	FOREIGN KEY (business_ID) REFERENCES Business(business_ID)
);

CREATE TABLE Categories(
business_ID CHAR(22),
category VARCHAR,
PRIMARY KEY (category, business_ID),
FOREIGN KEY (business_ID) REFERENCES Business(business_ID)
);

CREATE TABLE Users(
    user_id CHAR(22),
    name VARCHAR,
    average_stars REAL,
    funny_score INTEGER,
    useful_score INTEGER,
    cool_score INTEGER,
    fans INTEGER,
    friends INTEGER,
    tips INTEGER,
    yelping_since DATE,
    PRIMARY KEY (user_id)
);

CREATE TABLE Friend (
    user1_id CHAR(22),
    user2_id CHAR(22),
    PRIMARY KEY (user1_id, user2_id),
    FOREIGN KEY (user1_id) REFERENCES Users(user_id),
    FOREIGN KEY (user2_id) REFERENCES Users(user_id)
);

CREATE TABLE Tip(
	business_id CHAR(22),
	tip_date timestamp,
	likes INTEGER,
	text VARCHAR,
	user_id CHAR(22),
    PRIMARY KEY (user_id, tip_date, business_id),
	FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE OR REPLACE FUNCTION geodistance(
    lat1 DOUBLE PRECISION, long1 DOUBLE PRECISION, lat2 DOUBLE PRECISION, long2 DOUBLE PRECISION
)
RETURNS DOUBLE PRECISION AS $$
DECLARE
    R DOUBLE PRECISION := 3959; -- radius of Earth in miles
    dLat DOUBLE PRECISION := radians(lat2 - lat1);
    dLon DOUBLE PRECISION := radians(long2 - long1);
    a DOUBLE PRECISION;
    c DOUBLE PRECISION;
BEGIN
    a := POWER(sin(dLat / 2), 2) + cos(radians(lat1)) * cos(radians(lat2)) * POWER(sin(dLon / 2), 2);
    c := 2 * atan2(sqrt(a), sqrt(1 - a));
    RETURN R * c;
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION count_categories(b1 CHAR(22), b2 CHAR(22))
RETURNS INTEGER AS $$
DECLARE
    result INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO result
    FROM (
        SELECT category
        FROM Categories
        WHERE business_id = b1
        INTERSECT
        SELECT category
        FROM Categories
        WHERE business_id = b2
    ) AS subquery;

    RETURN result;
END;
$$ LANGUAGE plpgsql;