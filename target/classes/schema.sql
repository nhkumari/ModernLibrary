DROP TABLE IF EXISTS TBL_MODERN_BOOKS;

CREATE TABLE TBL_MODERN_BOOKS (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  title VARCHAR(250) NOT NULL,
  copies INT,
  user_id INT
);


