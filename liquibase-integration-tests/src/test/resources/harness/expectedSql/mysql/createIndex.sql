CREATE TABLE lbcat.test_table (test_id INT NOT NULL, test_column VARCHAR(50) NOT NULL, CONSTRAINT PK_TEST_TABLE PRIMARY KEY (test_id))
CREATE VIEW lbcat.test_view AS select * from test_table