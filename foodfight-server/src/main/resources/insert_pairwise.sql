INSERT INTO pairwise_result
            (
                        type_id,
                        first_version_id,
                        second_version_id
            )
            VALUES
            (
                        ?,
                        ?,
                        ?
            )
ON DUPLICATE KEY
UPDATE id = id;
