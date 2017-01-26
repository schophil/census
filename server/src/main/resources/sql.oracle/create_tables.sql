--
-- Census stores information about multiple applications or subjects.
-- This tables stores the list of known subjects.
--
create table census_subjects (
	id varchar2(20 char),
	name varchar2(50 char),
	constraint census_subjects_pk primary key (id)
);

-- 
-- Census calculates daily statistics based on the collected logs.
-- This table stores the statistics of a specific day.
-- This table is also the main table for a specific day; all other 
-- data will reference this table.
-- 
create table census_daystats (
	id varchar2(36 char),
	subject varchar2(20 char),
	sdate date,
	rtotal number(9,0),
	rerror number(9,0),
	raverage decimal(11,2),
	rmax decimal(11,2),
	rmin decimal(11,2),
	utotal number(9,0),
	constraint census_daystats_pk primary key (id),
	constraint census_daystats_u1 unique (sdate, subject),
	constraint census_daystats_f1 foreign key (subject) references census_subjects(id)
);

--
-- table for the user statistics
--
create table census_userstats (
	id varchar2(36 char),
	daystat varchar2(25 char),
	userid varchar(100),
	rtotal number(9,0),
	rerror number(9,0),
	constraint census_userstats_pk primary key (id),
	constraint census_userstats_f1 foreign key (daystat) references census_daystats(id)
);

--
-- Stores the list of known users.
--
create table census_users (
	userid varchar2(100 char),
	name varchar2(100 char),
	category varchar2(100 char),
	bdate date,
	edate date,
	subject varchar2(20 char),
	constraint census_user_pk primary key (userid),
	constraint census_users_f1 foreign key (subject) references census_subjects(id)
);

--
-- Stores recorded IPs per user.
--
create table census_sourceip (
	id varchar2(36 char),
	userid varchar2(100 char),
	ip varchar2(40 char),
	lused date,
	subject varchar2(20 char),
	constraint census_sourceip_pk primary key (id),
	constraint census_sourceip_u1 unique (userid, ip, subject),
	constraint census_sourceip_f1 foreign key (subject) references census_subjects(id)
);

--
-- create table for day activities
--
create table census_dayactivity (
	id varchar2(36 char),
	daystat varchar2(25 char),
	dhour number(9,0),
	hits number(9,0),
	constraint census_dayactivity_pk primary key (id),
	constraint census_dayactivity_f1 foreign key (daystat) references census_daystats(id)
);

--
-- create table for activity per user
--
create table census_useractivity (
	id varchar2(36 char),
	daystat varchar2(25 char),
	dhour number(9,0),
	hits number(9,0),
	userid varchar2(100 char),
	constraint census_useractivity_pk primary key (id),
	constraint census_useractivity_f1 foreign key (daystat) references census_daystats(id)
);
