--
-- Census stores information about multiple applications or subjects.
-- This tables stores the list of known subjects.
--
create table census_subjects (
	id varchar(20),
	name varchar(50),
	constraint census_subjects_pk primary key (id)
);

--
-- Census calculates daily statistics based on the collected logs.
-- This table stores the statistics of a specific day.
-- This table is also the main table for a specific day; all other
-- data will reference this table.
--
create table census_daystats (
	id varchar(25),
	subject varchar(20),
	sdate date,
	rtotal numeric(9,0),
	rerror numeric(9,0),
	raverage decimal(11,2),
	rmax decimal(11,2),
	rmin decimal(11,2),
	utotal numeric(9,0),
	constraint census_daystats_pk primary key (id),
	constraint census_daystats_u1 unique (sdate, subject),
	constraint census_daystats_f1 foreign key (subject) references census_subjects(id)
);

--
-- table for the user statistics
--
create table census_userstats (
	id varchar(25),
	daystat varchar(25),
	userid varchar(100),
	rtotal numeric(9,0),
	rerror numeric(9,0),
	constraint census_userstats_pk primary key (id),
	constraint census_userstats_f1 foreign key (daystat) references census_daystats(id)
);

--
-- Stores the list of known users.
--
create table census_users (
	userid varchar(100),
	name varchar(100),
	category varchar(100),
	bdate date,
	edate date,
	subject varchar(20),
	constraint census_user_pk primary key (userid)
);

--
-- Stores recorded IPs per user.
--
create table census_sourceip (
	id varchar(25),
	userid varchar(100),
	ip varchar(100),
	lused date,
	subject varchar(20),
	constraint census_sourceip_pk primary key (id),
	constraint census_sourceip_u1 unique (userid, ip, subject),
	constraint census_sourceip_f1 foreign key (subject) references census_subjects(id)
);

--
-- create table for day activities (activity = stats per hour)
--
create table census_dayactivity (
	id varchar(25),
	daystat varchar(25),
	dhour numeric(9,0),
	hits numeric(9,0),
	constraint census_dayactivity_pk primary key (id),
	constraint census_dayactivity_f1 foreign key (daystat) references census_daystats(id)
);

--
-- create table for activity per user (activity = stats per hour)
--
create table census_useractivity (
	id varchar(25),
	daystat varchar(25),
	dhour numeric(9,0),
	hits numeric(9,0),
	userid varchar(100),
	constraint census_useractivity_pk primary key (id),
	constraint census_useractivity_f1 foreign key (daystat) references census_daystats(id)
);
