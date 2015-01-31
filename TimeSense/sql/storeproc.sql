DELIMITER $$
CREATE PROCEDURE create_user(
p_in_user_id varchar(1000),
p_in_email varchar(1000),
p_in_gcm_hash varchar(4000),
out p_out_id integer 
)
BEGIN

DECLARE CONTINUE HANDLER FOR 1062
select id into p_out_id from master_user
where user_id=p_in_user_id;

insert into master_user (user_id,email,gcm_code) values (p_in_user_id,p_in_email,p_in_gcm_hash);

select id into p_out_id from master_user
where user_id=p_in_user_id;

INSERT INTO authentication 
	(auth_string, status, attempt_ts)
VALUES (p_in_user_id, 'Success', sysdate()); 

END$$
DELIMITER ;

-- Second

DELIMITER $$
CREATE PROCEDURE create_authentication(
p_in_phone varchar(100),
p_in_pin varchar(100),
p_in_status varchar(100)
)
BEGIN

INSERT INTO authentication 
	(auth_string, pin, status, start_ts)
VALUES (p_in_phone, p_in_pin, p_in_status, sysdate());    


END$$
DELIMITER ;