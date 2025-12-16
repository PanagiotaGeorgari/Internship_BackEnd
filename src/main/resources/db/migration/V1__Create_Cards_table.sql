CREATE TABLE cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    color VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    user_id INT
);
