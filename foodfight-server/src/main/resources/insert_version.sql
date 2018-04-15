INSERT INTO competitor_version
            (
                        competitor_id,
                        version_name
            )
            VALUES
            (
                        ?,
                        ?
            )
ON DUPLICATE KEY
UPDATE id = id;
