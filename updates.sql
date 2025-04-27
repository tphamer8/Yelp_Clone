-- Task 2
DROP TABLE IF EXISTS business_temp_tip_count;
DROP TABLE IF EXISTS temp_tip_count;

-- Create temp table
CREATE TEMP TABLE business_temp_tip_count AS
SELECT business_id, COUNT(DISTINCT tip_date) AS num_tips
FROM Tip
GROUP BY business_id;

-- Update Business
UPDATE Business
SET num_tips = temp.num_tips
FROM business_temp_tip_count temp
WHERE Business.business_id = temp.business_id;

--  Create temp table
CREATE TEMP TABLE temp_tip_count AS
SELECT user_id, COUNT(DISTINCT tip_date) AS tip_count
FROM Tip
GROUP BY user_id;

-- Update User
UPDATE Users
SET tips = temp.tip_count
FROM temp_tip_count temp
WHERE Users.user_id = temp.user_id;

-- Test Business
SELECT business_id, num_tips
FROM Business b
WHERE b.business_id = 'gnKjwL_1w79qoiV3IC_xQQ';

-- Test User
SELECT user_id, tips
FROM Users u
WHERE u.user_id = '4XChL029mKr5hydo79Ljxg'