delete from CENSUS_RESOURCE where
DAYSTAT = (select ID from CENSUS_DAYSTATS where SDATE = to_date('02102017', 'DDMMYYYY') and SUBJECT = 'egamesng');

delete from CENSUS_USERSTATS where
DAYSTAT = (select id from CENSUS_DAYSTATS where SDATE = to_date('02102017', 'DDMMYYYY') and SUBJECT = 'egamesng');

delete from CENSUS_USERACTIVITY where
DAYSTAT = (select id from CENSUS_DAYSTATS where SDATE = to_date('02102017', 'DDMMYYYY') and SUBJECT = 'egamesng');

delete from CENSUS_DAYACTIVITY where
DAYSTAT = (select id from CENSUS_DAYSTATS where SDATE = to_date('02102017', 'DDMMYYYY') and SUBJECT = 'egamesng');

delete from CENSUS_DAYSTATS where SDATE = to_date('02102017', 'DDMMYYYY') and SUBJECT = 'egamesng';

-- delete all
delete from CENSUS_RESOURCE where 
DAYSTAT in (select ID from CENSUS_DAYSTATS where SDATE in (to_date('03102017', 'DDMMYYYY'), to_date('02102017', 'DDMMYYYY')));

delete from CENSUS_USERSTATS where 
DAYSTAT in (select id from CENSUS_DAYSTATS where SDATE in (to_date('03102017', 'DDMMYYYY'), to_date('02102017', 'DDMMYYYY')));

delete from CENSUS_USERACTIVITY where 
DAYSTAT in (select id from CENSUS_DAYSTATS where SDATE in (to_date('03102017', 'DDMMYYYY'), to_date('02102017', 'DDMMYYYY')));

delete from CENSUS_DAYACTIVITY where 
DAYSTAT in (select id from CENSUS_DAYSTATS where SDATE in (to_date('03102017', 'DDMMYYYY'), to_date('02102017', 'DDMMYYYY')));

delete from CENSUS_DAYSTATS where SDATE in (to_date('03102017', 'DDMMYYYY'), to_date('02102017', 'DDMMYYYY'));
