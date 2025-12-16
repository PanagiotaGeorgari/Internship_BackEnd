ALTER TABLE cards
ADD COLUMN created_at TIMESTAMP DEFAULT NOW();

ALTER TABLE cards ADD COLUMN updated_by int;
UPDATE cards SET updated_by = created_by;

ALTER TABLE cards
ADD COLUMN updated_at TIMESTAMP DEFAULT NOW();
