CREATE SCHEMA IF NOT EXISTS mtdev;

SET SCHEMA 'mtdev';

CREATE EXTENSION "uuid-ossp";

CREATE TABLE IF NOT EXISTS income_category
(
	id uuid DEFAULT uuid_generate_v4(),
	name VARCHAR(32) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS expense_category
(
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    name VARCHAR(32) NOT NULL,
    target_amount DECIMAL(12,4) DEFAULT 0.00 NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vendor
(
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	name VARCHAR(32) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS expense
(
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_amount DECIMAL(12,4) NOT NULL,
    vendor uuid REFERENCES vendor(id) NOT NULL,
    expense_category uuid REFERENCES expense_category(id) NOT NULL,
    notes VARCHAR(256),
    hide boolean DEFAULT false NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS income
(
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	source VARCHAR(32) NOT NULL,
	income_category uuid REFERENCES income_category(id) NOT NULL,
    transaction_amount DECIMAL(12,4) NOT NULL,
    transaction_date DATE NOT NULL,
	notes VARCHAR(256),
	hide boolean DEFAULT false NOT NULL,
	PRIMARY KEY (id)
);
