INSERT INTO team
            (match_id,
             symbol)
VALUES      (?,
             ?);

SELECT LAST_INSERT_ID();
