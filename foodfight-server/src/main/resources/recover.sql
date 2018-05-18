DELETE FROM game_match WHERE is_finished = 0;

UPDATE game_match SET video_status = 'none' WHERE video_status = 'generating';
