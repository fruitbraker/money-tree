/*
 * This file is generated by jOOQ.
 */
package moneytree.persist.db.generated.tables.daos;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import moneytree.persist.db.generated.tables.Expense;
import moneytree.persist.db.generated.tables.records.ExpenseRecord;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExpenseDao extends DAOImpl<ExpenseRecord, moneytree.persist.db.generated.tables.pojos.Expense, UUID> {

    /**
     * Create a new ExpenseDao without any configuration
     */
    public ExpenseDao() {
        super(Expense.EXPENSE, moneytree.persist.db.generated.tables.pojos.Expense.class);
    }

    /**
     * Create a new ExpenseDao with an attached configuration
     */
    public ExpenseDao(Configuration configuration) {
        super(Expense.EXPENSE, moneytree.persist.db.generated.tables.pojos.Expense.class, configuration);
    }

    @Override
    public UUID getId(moneytree.persist.db.generated.tables.pojos.Expense object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfId(UUID lowerInclusive, UUID upperInclusive) {
        return fetchRange(Expense.EXPENSE.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchById(UUID... values) {
        return fetch(Expense.EXPENSE.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public moneytree.persist.db.generated.tables.pojos.Expense fetchOneById(UUID value) {
        return fetchOne(Expense.EXPENSE.ID, value);
    }

    /**
     * Fetch records that have <code>transaction_date BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfTransactionDate(LocalDate lowerInclusive, LocalDate upperInclusive) {
        return fetchRange(Expense.EXPENSE.TRANSACTION_DATE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>transaction_date IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchByTransactionDate(LocalDate... values) {
        return fetch(Expense.EXPENSE.TRANSACTION_DATE, values);
    }

    /**
     * Fetch records that have <code>transaction_amount BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfTransactionAmount(BigDecimal lowerInclusive, BigDecimal upperInclusive) {
        return fetchRange(Expense.EXPENSE.TRANSACTION_AMOUNT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>transaction_amount IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchByTransactionAmount(BigDecimal... values) {
        return fetch(Expense.EXPENSE.TRANSACTION_AMOUNT, values);
    }

    /**
     * Fetch records that have <code>vendor BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfVendor(UUID lowerInclusive, UUID upperInclusive) {
        return fetchRange(Expense.EXPENSE.VENDOR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>vendor IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchByVendor(UUID... values) {
        return fetch(Expense.EXPENSE.VENDOR, values);
    }

    /**
     * Fetch records that have <code>expense_category BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfExpenseCategory(UUID lowerInclusive, UUID upperInclusive) {
        return fetchRange(Expense.EXPENSE.EXPENSE_CATEGORY, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>expense_category IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchByExpenseCategory(UUID... values) {
        return fetch(Expense.EXPENSE.EXPENSE_CATEGORY, values);
    }

    /**
     * Fetch records that have <code>notes BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfNotes(String lowerInclusive, String upperInclusive) {
        return fetchRange(Expense.EXPENSE.NOTES, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>notes IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchByNotes(String... values) {
        return fetch(Expense.EXPENSE.NOTES, values);
    }

    /**
     * Fetch records that have <code>hide BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchRangeOfHide(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(Expense.EXPENSE.HIDE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>hide IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.Expense> fetchByHide(Boolean... values) {
        return fetch(Expense.EXPENSE.HIDE, values);
    }
}
