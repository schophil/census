

--
-- table for the resources occurrences
--
create table census_resource (
	id varchar2(36 char),
	daystat varchar2(36 char),
	name varchar2(25 char),
	userid varchar2(100 char),
	value_nbr number(9,0),
	value_txt varchar2(1000 char),
	value_txtlng clob,
	hits number(9,0),
	constraint census_resource_pk primary key (id),
	constraint census_resource_f1 foreign key (daystat) references census_daystats(id)
);
