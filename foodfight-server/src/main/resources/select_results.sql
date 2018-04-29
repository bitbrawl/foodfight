SELECT   division.id AS division_id,
         result,
         COUNT(*)
FROM     pairwise_result
JOIN     competitor
ON       pairwise_result.first_version_id = competitor.version_id
JOIN     competitor AS second_competitor
ON       pairwise_result.second_version_id = second_competitor.version_id
JOIN     division
WHERE    competitor.id = ?
AND      pairwise_result.first_version_id = competitor.version_id
AND      type_id = ?
AND      competitor.division_id <= division.id
AND      second_competitor.division_id <= division.id
GROUP BY division_id,
         result
