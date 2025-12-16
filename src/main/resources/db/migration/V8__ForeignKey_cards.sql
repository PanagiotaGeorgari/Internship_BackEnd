ALTER TABLE cards
ADD FOREIGN KEY (created_by) REFERENCES users(user_id);