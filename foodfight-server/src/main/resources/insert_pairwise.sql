INSERT INTO pairwise_result
            (
                        type_id,
                        first_version_id,
                        second_version_id,
                        total_matches,
                        wins,
                        result
            )
            VALUES
            (
                        ?,
                        ?,
                        ?,
                        1,
                        1,
                        'win'
            )
ON DUPLICATE KEY
UPDATE id = id;
