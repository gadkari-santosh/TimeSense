drop table user;
drop table auth_log;

create table auth_log (
	id integer UNSIGNED NOT NULL AUTO_INCREMENT,
    session_id varchar(4000),
	auth_string varchar(1000),
    pin varchar(100),
    status varchar(100),
    ts date ,
    PRIMARY KEY (id)
); 


CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(20) NOT NULL,
  `session_id` varchar(4000) NOT NULL,
  `email` varchar(4000) DEFAULT NULL,
  `gcm_code` varchar(4000) DEFAULT NULL,
  `time_zone` varchar(4000) DEFAULT NULL,
  `status` varchar(4000) DEFAULT NULL,
  `time_zone_update_id` int(10) unsigned DEFAULT NULL,
  `status_update_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) 


CREATE TABLE user (
  id integer UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id varchar(20) NOT NULL,
  session_id varchar(4000) NOT NULL,
  email varchar(4000) DEFAULT NULL,
  gcm_code varchar(4000) DEFAULT NULL,
  time_zone varchar(4000),
  status varchar(4000),
  time_zone_update_id integer UNSIGNED,
  status_update_id integer UNSIGNED,
  PRIMARY KEY (id),
  UNIQUE KEY (user_id)
);
