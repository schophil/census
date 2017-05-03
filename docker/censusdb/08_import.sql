copy census_users from '/docker-entrypoint-initdb.d/data_users.csv' with delimiter ',' quote '"' csv;
copy census_daystats from '/docker-entrypoint-initdb.d/data_daystats.csv' with delimiter ',' quote '"' csv;
copy census_dayactivity from '/docker-entrypoint-initdb.d/data_dayactivity.csv' with delimiter ',' quote '"' csv;
copy census_resource from '/docker-entrypoint-initdb.d/data_resource.csv' with delimiter ',' quote '"' csv;
copy census_userstats from '/docker-entrypoint-initdb.d/data_userstats.csv' with delimiter ',' quote '"' csv;
copy census_useractivity from '/docker-entrypoint-initdb.d/data_useractivity.csv' with delimiter ',' quote '"' csv;
