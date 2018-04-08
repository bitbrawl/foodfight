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

SELECT id
FROM   competitor_version
WHERE  competitor_id = ?
AND    version_name = ?;
