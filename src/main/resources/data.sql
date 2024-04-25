ALTER TABLE search_room.comment ADD CONSTRAINT fk_comment_house FOREIGN KEY (post_id) REFERENCES search_room.house(id);

ALTER TABLE search_room.rating ADD CONSTRAINT fk_rating_house FOREIGN KEY (post_id) REFERENCES search_room.house(id);

ALTER TABLE search_room.house ADD CONSTRAINT fk_house_user FOREIGN KEY (created_by) REFERENCES search_room.user(username);