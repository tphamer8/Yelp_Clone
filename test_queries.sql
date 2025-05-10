
SELECT DISTINCT c.category
FROM Business b
JOIN Categories c ON b.business_id = c.business_id
WHERE b.state = 'AZ' AND b.city = 'Scottsdale';

SELECT DISTINCT ba.attribute
FROM business b
JOIN businessattributes ba ON ba.business_id = b.business_id
WHERE b.state = 'AZ' AND b.city = 'Scottsdale';

SELECT b.business_id, b.business_name, b.street_address, COUNT(t.tip_date) AS num_tips
FROM Business b
JOIN Categories c ON b.business_id = c.business_id
LEFT JOIN Tip t ON b.business_id = t.business_id
WHERE b.state = 'AZ' AND b.city = 'Scottsdale'
AND c.category IN ('Restaurants', 'Breakfast & Brunch', 'Bakeries')
GROUP BY b.business_id, b.business_name, b.street_address
HAVING COUNT(DISTINCT c.category) = 3
ORDER BY b.business_name;

SELECT b.business_id, b.business_name, b.street_address, COUNT(t.tip_date) AS num_tips
FROM Business b
JOIN BusinessAttributes a ON b.business_id = a.business_id
LEFT JOIN Tip t ON b.business_id = t.business_id
WHERE b.state = 'AZ' AND b.city = 'Scottsdale'
AND (
    (a.attribute = 'BusinessAcceptsCreditCards' AND a.attribute_value = 'True')
    OR (a.attribute = 'ByAppointmentOnly' AND a.attribute_value = 'True')
    OR (a.attribute = 'WiFi' AND a.attribute_value = 'free')
)
GROUP BY b.business_id, b.business_name, b.street_address
HAVING COUNT(DISTINCT a.attribute) = 3
ORDER BY b.business_name;

SELECT b.business_id, b.business_name, b.street_address, COUNT(t.tip_date) AS num_tips
FROM Business b
JOIN Categories c ON b.business_id = c.business_id
JOIN BusinessAttributes a ON b.business_id = a.business_id
LEFT JOIN Tip t ON b.business_id = t.business_id
JOIN Hours h ON b.business_id = h.business_id
WHERE b.state = 'AZ' AND b.city = 'Scottsdale'
AND c.category IN ('Restaurants', 'Breakfast & Brunch', 'Bakeries')
AND (
    (a.attribute = 'BusinessAcceptsCreditCards' AND a.attribute_value = 'True')
    OR (a.attribute = 'RestaurantsPriceRange2' AND a.attribute_value = '2')
    OR (a.attribute = 'WiFi' AND a.attribute_value = 'free')
)
AND h.day_name = 'Monday'
AND h.open_time <= '10:30:00' AND h.close_time >= '13:30:00'
GROUP BY b.business_id, b.business_name, b.street_address
HAVING COUNT(DISTINCT c.category) = 3
    AND COUNT(DISTINCT a.attribute) = 3
ORDER BY b.business_name;

CREATE OR REPLACE FUNCTION count_categories(b1 CHAR(22), b2 CHAR(22))
RETURNS INTEGER AS $$
BEGIN
	RETURN
		(SELECT COUNT(*)
		FROM
			(SELECT category
			FROM Categories
			WHERE business_id = b1
			INTERSECT
			SELECT category
			FROM Categories
			WHERE business_id = b2));
END;
$$ LANGUAGE plpgsql;

SELECT count_categories('iPPzDL_oY8SJCjmycuXcVg', 'ncXQtqJT5Gk1QztwTrBrgw');

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

SELECT geodistance(33.6399735577, -112.1334044052, 33.5796797, -111.9275444);

WITH given_business AS (
    SELECT business_id, business_name, city, zipcode, latitude, longitude
    FROM Business
    WHERE business_id = 'iPPzDL_oY8SJCjmycuXcVg'
),
given_categories AS (
    SELECT category
    FROM Categories
    WHERE business_id = 'iPPzDL_oY8SJCjmycuXcVg'
),
business_with_common_categories AS (
    SELECT b.business_id, b.business_name, b.city, b.zipcode,
           count_categories(b.business_id, gb.business_id) AS rank
    FROM Business b
    JOIN given_business gb ON gb.zipcode = b.zipcode
    WHERE b.business_id != gb.business_id
      AND geodistance(b.latitude, b.longitude, gb.latitude, gb.longitude) <= 20
)
SELECT business_id, business_name, city, zipcode, rank
FROM business_with_common_categories
ORDER BY rank DESC
LIMIT 15;


SELECT b1.business_id, b1.business_name, b1.street_address, b1.num_tips
FROM Business b1
JOIN Categories c1 ON b1.business_id = c1.business_id
WHERE b1.zipcode =  '85251' AND c1.category = 'Restaurants'
  AND b1.num_tips = (
    SELECT MAX(b2.num_tips)
    FROM Business b2
    JOIN Categories c2 ON b2.business_id = c2.business_id
    WHERE b2.zipcode = '85251' AND c2.category = 'Restaurants'
);


SELECT u.name AS user_name, t1.tip_date AS tipdate, t1.text AS tiptext
FROM Tip t1
JOIN Users u ON t1.user_id = u.user_id
WHERE t1.user_id IN
	(SELECT user1_id
	FROM Friend
	WHERE user2_id = 'TiWF94rl8Q6jqQf2YZSFPA'
	UNION
	SELECT user2_id
	FROM Friend
	WHERE user1_id = 'TiWF94rl8Q6jqQf2YZSFPA')
AND t1.tip_date =
	(SELECT MAX(t2.tip_date)
	FROM Tip t2
	WHERE t2.user_id IN
		(SELECT user1_id
		FROM Friend
		WHERE user2_id = 'TiWF94rl8Q6jqQf2YZSFPA'
		UNION
		SELECT user2_id
		FROM Friend
		WHERE user1_id = 'TiWF94rl8Q6jqQf2YZSFPA'));

SELECT *
FROM (
    SELECT DISTINCT ON (t.user_id)
        u.user_id AS friend_id,
        u.name AS friend_name,
        t.tip_date,
        t.text AS tip_text
    FROM Tip t
    JOIN Users u ON t.user_id = u.user_id
    JOIN (
        SELECT user1_id AS user_id
        FROM Friend
        WHERE user2_id = 'TiWF94rl8Q6jqQf2YZSFPA'
        UNION
        SELECT user2_id AS user_id
        FROM Friend
        WHERE user1_id = 'TiWF94rl8Q6jqQf2YZSFPA'
    ) f ON t.user_id = f.user_id
    ORDER BY t.user_id, t.tip_date DESC
) recent_tips
ORDER BY tip_date DESC;

CREATE OR REPLACE FUNCTION number_of_tips()
RETURNS TRIGGER AS $$
BEGIN
UPDATE Users
SET tips = (tips + 1)
WHERE user_id = NEW.user_id;

UPDATE Business
SET num_tips = (num_tips + 1)
WHERE business_id = NEW.business_id;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER new_tip
AFTER INSERT ON Tip
FOR EACH ROW
EXECUTE FUNCTION number_of_tips();

DELETE FROM Tip WHERE business_id = 'gnKjwL_1w79qoiV3IC_xQQ' AND user_id = 'nRagjGVuSALgQ4KfGLn8Ig';
INSERT INTO Tip (business_id, tip_date, likes, text, user_id)
VALUES ('gnKjwL_1w79qoiV3IC_xQQ', '2025-04-23 10:42:30', 100, 'This business is cool!', 'nRagjGVuSALgQ4KfGLn8Ig');

SELECT num_tips, tips
FROM business, users
WHERE business_id = 'gnKjwL_1w79qoiV3IC_xQQ' AND user_id = 'nRagjGVuSALgQ4KfGLn8Ig';

CREATE or REPLACE FUNCTION businessNotOpen()
RETURNS TRIGGER AS $$
DECLARE
	business_open_time TIME;
	business_close_time TIME;
    check_in_day_name TEXT;
BEGIN
    check_in_day_name := TO_CHAR(NEW.time_stamp, 'Day');

	SELECT open_time, close_time
	INTO business_open_time, business_close_time
	FROM Hours h
	WHERE h.business_id = NEW.business_id
	AND h.day_name = check_in_day_name;

    IF NEW.time_stamp IS NOT NULL
        AND (NEW.time_stamp::TIME < business_open_time OR NEW.time_stamp::TIME > business_close_time)
        THEN RAISE EXCEPTION 'User can not check in to a business which is not open';
	END IF;
	RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE or REPLACE TRIGGER trigger_businessNotOpen
BEFORE INSERT OR UPDATE ON Check_Ins
FOR Each Row
EXECUTE PROCEDURE businessNotOpen();

INSERT INTO Check_Ins (business_id, time_stamp)
VALUES ('gnKjwL_1w79qoiV3IC_xQQ', '2025-04-23 22:00:00');