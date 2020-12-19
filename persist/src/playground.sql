set schema 'mtdev';

INSERT INTO EXPENSE_CATEGORY(name, target_amount) VALUES ('EXPENSE_CATEGORY_1', 100.00);
INSERT INTO EXPENSE_CATEGORY(name, target_amount) VALUES ('EXPENSE_CATEGORY_2', 200.00);

INSERT INTO VENDOR(name) VALUES ('VENDOR_1');
INSERT INTO VENDOR(name) VALUES ('VENDOR_2');

INSERT INTO EXPENSE(
	transaction_date,
	transaction_amount,
	vendor,
	expense_category,
	notes,
	hide)
VALUES(
	'2020-12-20',
	100.00,
	'2e802672-9109-464f-9830-89652f737153',
	'a6d0acd0-b74f-4075-96ab-f7c471a5514e',
	'Notes for expense 1',
	false);

INSERT INTO EXPENSE(
	transaction_date,
	transaction_amount,
	vendor,
	expense_category,
	notes,
	hide)
VALUES(
	'2020-12-21',
	200.00,
	'f6fbc8b6-d520-43fa-8682-e81b1790c966',
	'a6d0acd0-b74f-4075-96ab-f7c471a5514e',
	'Notes for expense 2',
	false);

SELECT * FROM EXPENSE_CATEGORY;
SELECT * FROM VENDOR;
SELECT * FROM EXPENSE;
