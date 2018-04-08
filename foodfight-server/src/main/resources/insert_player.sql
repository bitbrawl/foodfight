INSERT INTO player
            (team_id,
             symbol,
             version_id)
VALUES      (?,
             ?,
             ?);

SELECT LAST_INSERT_ID(); 
