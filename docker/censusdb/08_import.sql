copy census_users from '/docker-entrypoint-initdb.d/data_users.csv' with (format csv, delimiter ';', quote '"');
copy census_daystats from '/docker-entrypoint-initdb.d/data_daystats.csv' with (format csv, delimiter ';', quote '"');
copy census_dayactivity from '/docker-entrypoint-initdb.d/data_dayactivity.csv' with (format csv, delimiter ';', quote '"');
copy census_resource from '/docker-entrypoint-initdb.d/data_resource.csv' with (format csv, delimiter ';', quote '"');
copy census_userstats from '/docker-entrypoint-initdb.d/data_userstats.csv' with (format csv, delimiter ';', quote '"');
copy census_useractivity from '/docker-entrypoint-initdb.d/data_useractivity.csv' with (format csv, delimiter ';', quote '"');
copy census_sourceip from '/docker-entrypoint-initdb.d/data_sourceip.csv' with (format csv, delimiter ';', quote '"');
