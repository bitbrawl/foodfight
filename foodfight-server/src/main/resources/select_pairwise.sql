SELECT id
FROM   pairwise_result
WHERE  type_id = ?
AND    first_version_id = ?
AND    second_version_id = ?;
