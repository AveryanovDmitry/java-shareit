delete
from ITEMS;
delete
from USERS;
delete
from bookings;
delete
from comments;
delete
from requests;

ALTER TABLE USERS
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE ITEMS
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE bookings
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE comments
    ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE requests
    ALTER COLUMN request_id RESTART WITH 1;