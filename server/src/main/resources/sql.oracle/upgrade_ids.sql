alter table census_daystats modify (id varchar2(char 36));

alter table census_userstats modify (id varchar2(char 36));
alter table census_userstats modify (daystat varchar2(char 36));

alter table census_sourceip modify (id varchar2(char 36));

alter table census_dayactivity modify (id varchar2(char 36));
alter table census_dayactivity modify (daystat varchar2(char 36));

alter table census_useractivity modify (id varchar2(char 36));

