DELIMITER $$
CREATE PROCEDURE `create_user`(
p_in_user_id varchar(1000),
p_id_gcm_code varchar(4000),
p_in_session_id varchar(4000),
p_in_time_zone varchar(4000)
)
BEGIN

DECLARE p_out_id INT DEFAULT 0;

select id into p_out_id from user
where user_id=p_in_user_id;

if p_out_id <> 0 then
	
	update user set session_id=p_in_session_id, gcm_code=p_id_gcm_code, time_zone=p_in_time_zone
	where user_id=p_in_user_id;
	
else
  	insert into user (user_id,session_id,gcm_code,time_zone) 
  	values (p_in_user_id, p_in_session_id, p_id_gcm_code,p_in_time_zone);
  
end if;

UPDATE auth_log 
	set status='SUCCESS'
WHERE session_id=p_in_session_id;

END$$

