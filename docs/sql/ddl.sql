-- Generated 2023-10-16 15:45:24-0600 for database version 1

CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `created`      INTEGER                           NOT NULL,
    `oauth_key`    TEXT                              NOT NULL,
    `display_name` TEXT                              NOT NULL COLLATE NOCASE
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_user_oauth_key` ON `user` (`oauth_key`);

CREATE UNIQUE INDEX IF NOT EXISTS `index_user_display_name` ON `user` (`display_name`);

CREATE TABLE IF NOT EXISTS `score`
(
    `score_id`     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `created`      INTEGER                           NOT NULL,
    `started`      INTEGER                           NOT NULL,
    `duration`     INTEGER                           NOT NULL,
    `value`        INTEGER                           NOT NULL,
    `rows_removed` INTEGER                           NOT NULL,
    `player_id`    INTEGER                           NOT NULL,
    FOREIGN KEY (`player_id`) REFERENCES `user` (`user_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_score_player_id` ON `score` (`player_id`);