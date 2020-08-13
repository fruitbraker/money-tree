set schema 'mtdev';

INSERT INTO expense_category(id, target_amount, metadata) VALUES ('EXPENSE_CATEGORY_1', 100.00, 
'{
    "date_created": "2020-08-02",
    "date_modified": "2020-08-10",
    "notes": "Expense Category Note"
}');

INSERT INTO vendor(vendor_name) VALUES ('VENDOR_1');

INSERT INTO expense(transaction_date, transaction_amount, vendor, category, metadata, hide) VALUES (
    now(),
    1.23,
    1,
    'EXPENSE_CATEGORY_1',
    '{
        "date_created": "2020-08-10",
        "date_modified": "2020-08-10",
        "notes": "Expense Note"
    }',
    false
);

SELECT * FROM EXPENSE_CATEGORY;
SELECT * FROM EXPENSE;
SELECT * FROM VENDOR;
