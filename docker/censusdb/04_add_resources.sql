

--
-- table for the resources occurrences
--
create table census_resource (
	id varchar(36),
	daystat varchar(36),
	name varchar(25),
	userid varchar(100),
	value_nbr numeric(9,0),
	value_txt varchar(1000),
	value_txtlng varchar(5000),
	hits numeric(9,0),
	constraint census_resource_pk primary key (id),
	constraint census_resource_f1 foreign key (daystat) references census_daystats(id)
);
