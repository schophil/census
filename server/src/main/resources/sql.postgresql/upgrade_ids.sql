alter table census_daystats alter column id set data type varchar(36);

alter table census_userstats alter column id set data type varchar(36);
alter table census_userstats alter column daystat set data type varchar(36);

alter table census_sourceip alter column id set data type varchar(36);

alter table census_dayactivity alter column id set data type varchar(36);
alter table census_dayactivity alter column daystat set data type varchar(36);

alter table census_useractivity alter column id set data type varchar(36);
alter table census_useractivity alter column daystat set data type varchar(36);