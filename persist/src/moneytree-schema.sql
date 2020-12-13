CREATE SCHEMA IF NOT EXISTS mtdev;

SET SCHEMA 'mtdev';

CREATE TABLE IF NOT EXISTS expense_category
(
    id VARCHAR(32) PRIMARY KEY NOT NULL,
    target_amount DECIMAL(12,4) DEFAULT 0.00 NOT NULL,
    metadata VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS income_category
(
	id VARCHAR(32) PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS vendor
(
	id SERIAL PRIMARY KEY NOT NULL,
	vendor_name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS expense
(
    id SERIAL PRIMARY KEY NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_amount DECIMAL(12,4) NOT NULL,
    vendor INT REFERENCES vendor(id) NOT NULL,
    category VARCHAR(32) REFERENCES expense_category(id) NOT NULL,
    metadata VARCHAR(256) NOT NULL,
    hide boolean DEFAULT false NOT NULL
);

CREATE TABLE IF NOT EXISTS income
(
	id SERIAL PRIMARY KEY NOT NULL,
	source VARCHAR(32) NOT NULL,
	category VARCHAR(32) REFERENCES income_category(id) NOT NULL,
    transaction_amount DECIMAL(12,4) NOT NULL,
    transaction_date DATE NOT NULL,
	metadata VARCHAR(256) NOT NULL,
	hide boolean DEFAULT false NOT NULL
);
