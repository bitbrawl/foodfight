INSERT INTO competitor_score
            (
                        division_id,
                        type_id,
                        competitor_id,
                        wins,
                        ties,
                        losses,
                        score
            )
            VALUES
            (
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        ?
            )
ON DUPLICATE KEY
UPDATE wins=VALUES
       (
              wins
       )
       ,
       ties = VALUES
       (
              ties
       )
       ,
       losses = VALUES
       (
              losses
       )
       ,
       score = VALUES
       (
              score
       )
