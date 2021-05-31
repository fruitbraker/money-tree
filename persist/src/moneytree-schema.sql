CREATE SCHEMA IF NOT EXISTS mtdev;

SET SCHEMA 'mtdev';

CREATE TABLE IF NOT EXISTS income_category
(
	income_category_id uuid NOT NULL,
	name VARCHAR(256) NOT NULL,
	PRIMARY KEY (income_category_id)
);

CREATE TABLE IF NOT EXISTS expense_category
(
    expense_category_id uuid NOT NULL,
    name VARCHAR(256) NOT NULL,
    target_amount DECIMAL(12,4) DEFAULT 0.00 NOT NULL,
	PRIMARY KEY (expense_category_id)
);

CREATE TABLE IF NOT EXISTS vendor
(
	vendor_id uuid NOT NULL,
	name VARCHAR(256) NOT NULL,
	PRIMARY KEY (vendor_id)
);

CREATE TABLE IF NOT EXISTS expense
(
    expense_id uuid NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_amount DECIMAL(12,4) NOT NULL,
    vendor_id uuid REFERENCES vendor(vendor_id) NOT NULL,
    expense_category_id uuid REFERENCES expense_category(expense_category_id) NOT NULL,
    notes VARCHAR(256) NOT NULL,
    hide boolean DEFAULT false NOT NULL,
	PRIMARY KEY (expense_id)
);

CREATE TABLE IF NOT EXISTS income
(
	income_id uuid NOT NULL,
	source VARCHAR(256) NOT NULL,
	income_category_id uuid REFERENCES income_category(income_category_id) NOT NULL,
    transaction_amount DECIMAL(12,4) NOT NULL,
    transaction_date DATE NOT NULL,
	notes VARCHAR(256) NOT NULL,
	hide boolean DEFAULT false NOT NULL,
	PRIMARY KEY (income_id)
);
