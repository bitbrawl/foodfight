SELECT 
    competitor.id,
    username,
    major,
    minor,
    revision,
    jar_file,
    main_class
FROM
    competitor
        LEFT JOIN
    competitor_version ON competitor.version_id = competitor_version.id
