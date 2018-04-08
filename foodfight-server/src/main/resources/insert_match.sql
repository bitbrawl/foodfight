INSERT INTO game_match
            (type_id,
             is_finished)
VALUES      (?,
             false);

SELECT LAST_INSERT_ID();
