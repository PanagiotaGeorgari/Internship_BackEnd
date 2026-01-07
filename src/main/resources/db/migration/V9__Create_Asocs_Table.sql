CREATE TABLE card_assocs (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    lcard_id INT ,
    assoc VARCHAR(50) NOT NULL,
    rcard_id INT ,
    FOREIGN KEY (lcard_id) REFERENCES cards(card_id),
    FOREIGN KEY (rcard_id) REFERENCES cards(card_id)
);