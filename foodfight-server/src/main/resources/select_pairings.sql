SELECT    first_competitor.id  AS first_competitor,
          second_competitor.id AS second_competitor
FROM      competitor first_competitor
JOIN      competitor second_competitor
JOIN      match_type
LEFT JOIN pairwise_result
ON        pairwise_result.first_version_id = first_competitor.version_id
AND       pairwise_result.second_version_id = second_competitor.version_id
AND       pairwise_result.type_id = match_type.id
where     match_type.id = ?
AND       first_competitor.id < second_competitor.id
AND       first_competitor.division_id <= ?
AND       second_competitor.division_id <= ?
AND       (
                    first_competitor.division_id = ?
          OR        second_competitor.division_id = ?)
AND       ifnull(focus_count, 0) =
          (
                    SELECT    min(ifnull(focus_count, 0))
                    FROM      competitor first_competitor
                    JOIN      competitor second_competitor
                    JOIN      match_type
                    LEFT JOIN pairwise_result
                    ON        pairwise_result.first_version_id = first_competitor.version_id
                    AND       pairwise_result.second_version_id = second_competitor.version_id
                    AND       pairwise_result.type_id = match_type.id
                    WHERE     match_type.id = ?
                    AND       first_competitor.id < second_competitor.id
                    AND       first_competitor.division_id <= ?
                    AND       second_competitor.division_id <= ?
                    AND       (
                                        first_competitor.division_id = ?
                              OR        second_competitor.division_id = ?));