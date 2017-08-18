CREATE TABLE IF NOT EXISTS test(
  date DATE,
  name CHAR(20),
  arrive TIMESTAMP(6) NOT NULL,
  depart TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE (date, name)
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS abnormal (
  date DATE,
  name CHAR(20),
  time TIMESTAMP(2) NOT NULL,
  UNIQUE (date, name)
) CHARACTER SET = utf8mb4;
