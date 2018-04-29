DROP DATABASE IF EXISTS bitbrawl;

CREATE DATABASE bitbrawl;

USE bitbrawl;

CREATE TABLE division
  (
     id            TINYINT UNSIGNED,
     division_name VARCHAR(11) NOT NULL,
     PRIMARY KEY(id),
     UNIQUE(division_name)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE competitor
  (
     id          SMALLINT UNSIGNED AUTO_INCREMENT,
     username    VARCHAR(255) NOT NULL,
     division_id TINYINT UNSIGNED NOT NULL,
     version_id  MEDIUMINT UNSIGNED,
     PRIMARY KEY(id),
     FOREIGN KEY(division_id) REFERENCES division(id),
     UNIQUE(username),
     UNIQUE(version_id)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE competitor_version
  (
     id            MEDIUMINT UNSIGNED AUTO_INCREMENT,
     competitor_id SMALLINT UNSIGNED NOT NULL,
     version_name  VARCHAR(255) NOT NULL,
     PRIMARY KEY(id),
     FOREIGN KEY(competitor_id) REFERENCES competitor(id),
     UNIQUE( competitor_id, version_name )
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

ALTER TABLE competitor
  ADD FOREIGN KEY(version_id) REFERENCES competitor_version(id);

CREATE TABLE match_type
  (
     id        TINYINT UNSIGNED,
     type_name VARCHAR(12),
     PRIMARY KEY(id),
     UNIQUE(type_name)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE pairwise_result
  (
     id                MEDIUMINT UNSIGNED AUTO_INCREMENT,
     type_id           TINYINT UNSIGNED NOT NULL,
     first_version_id  MEDIUMINT UNSIGNED NOT NULL,
     second_version_id MEDIUMINT UNSIGNED NOT NULL,
     total_matches     MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
     wins              MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
     result            ENUM('win', 'tie', 'loss') NOT NULL DEFAULT 'tie',
     PRIMARY KEY(id),
     FOREIGN KEY(type_id) REFERENCES match_type(id),
     FOREIGN KEY(first_version_id) REFERENCES competitor_version(id),
     FOREIGN KEY(second_version_id) REFERENCES competitor_version(id),
     UNIQUE( type_id, first_version_id, second_version_id )
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE competitor_score
  (
     division_id TINYINT UNSIGNED NOT NULL,
     type_id     TINYINT UNSIGNED NOT NULL,
     competitor_id  SMALLINT UNSIGNED NOT NULL,
     wins        MEDIUMINT NOT NULL DEFAULT 0,
     ties        MEDIUMINT NOT NULL DEFAULT 0,
     losses      MEDIUMINT NOT NULL DEFAULT 0,
     score       MEDIUMINT NOT NULL DEFAULT 0,
     PRIMARY KEY(division_id, type_id, competitor_id),
     FOREIGN KEY(division_id) REFERENCES division(id),
     FOREIGN KEY(type_id) REFERENCES match_type(id),
     FOREIGN KEY(competitor_id) REFERENCES competitor(id)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE game_match
  (
     id           MEDIUMINT UNSIGNED AUTO_INCREMENT,
     type_id      TINYINT UNSIGNED NOT NULL,
     is_finished  BOOLEAN NOT NULL,
     trace_link   VARCHAR(256),
     video_status ENUM('none', 'generating', 'done'),
     video_link   VARCHAR(256),
     PRIMARY KEY(id),
     FOREIGN KEY(type_id) REFERENCES match_type(id)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE match_result
  (
     match_id           MEDIUMINT UNSIGNED,
     pairwise_result_id MEDIUMINT UNSIGNED,
     did_win            BOOLEAN NOT NULL,
     PRIMARY KEY(match_id, pairwise_result_id),
     FOREIGN KEY(match_id) REFERENCES game_match(id),
     FOREIGN KEY(pairwise_result_id) REFERENCES pairwise_result(id)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE team
  (
     id       MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
     match_id MEDIUMINT UNSIGNED NOT NULL,
     symbol   CHAR(1) NOT NULL,
     points   MEDIUMINT,
     PRIMARY KEY(id),
     UNIQUE(match_id, symbol),
     UNIQUE(match_id, points)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

CREATE TABLE player
  (
     team_id    MEDIUMINT UNSIGNED NOT NULL,
     symbol     CHAR(1) NOT NULL,
     version_id MEDIUMINT UNSIGNED NOT NULL,
     PRIMARY KEY(team_id, symbol),
     FOREIGN KEY(team_id) REFERENCES team(id),
     FOREIGN KEY(version_id) REFERENCES competitor_version(id),
     UNIQUE(team_id, version_id),
     UNIQUE(team_id, symbol)
  )
ENGINE = INNODB
DEFAULT CHARACTER SET = UTF8
COLLATE = UTF8_GENERAL_CI;

INSERT INTO division
            (division_name,
             id)
VALUES      ('high-school',
             1),
            ('college',
             2),
            ('none',
             3);

INSERT INTO competitor
            (username,
             division_id)
VALUES      ('sample-dummy',
             1),
            ('sample-random',
             1),
            ('sample-hiding',
             1),
            ('sample-walls',
             1);

INSERT INTO match_type
VALUES      (0,
             'overall'),
            (2,
             'duel'),
            (3,
             'free-for-all'),
            (4,
             'team');
