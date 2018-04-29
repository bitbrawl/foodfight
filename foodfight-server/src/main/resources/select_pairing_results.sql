SELECT second_competitor.id AS second_competitor_id,
       result,
       COUNT(*)
FROM   pairwise_result
       JOIN competitor
         ON pairwise_result.first_version_id = competitor.version_id
       JOIN competitor AS second_competitor
         ON pairwise_result.second_version_id = second_competitor.version_id
WHERE  competitor.id = ?
       AND second_competitor.division_id <= ?
GROUP  BY second_competitor_id,
          result
