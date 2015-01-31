drop table master_user;
drop table contact_relation;

CREATE TABLE master_user (
  id integer UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id varchar(20) NOT NULL,
  email varchar(1000) DEFAULT NULL,
  gcm_code varchar(4000) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (user_id)
);

create table authentication (
	id integer UNSIGNED NOT NULL AUTO_INCREMENT,
	auth_string varchar(1000),
    pin varchar(100),
    status varchar(100),
    start_ts date,
    attempt_ts date,  
    attempt integer,
	PRIMARY KEY (id)
);

create table contact_relation ( 
id integer UNSIGNED NOT NULL AUTO_INCREMENT,
owner_id integer REFERENCES master_user (id) on delete cascade,
relation_id integer REFERENCES master_user (id) on delete cascade,
PRIMARY KEY (id)
);