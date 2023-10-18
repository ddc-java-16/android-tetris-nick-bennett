SELECT s.player_id,
       u.display_name,
       s.value,
       s.rows_removed,
       s.created
FROM user AS u
         JOIN score AS s ON u.user_id = s.player_id
ORDER BY value DESC