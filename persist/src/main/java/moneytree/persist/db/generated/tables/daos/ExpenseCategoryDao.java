/*
 * This file is generated by jOOQ.
 */
package moneytree.persist.db.generated.tables.daos;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import moneytree.persist.db.generated.tables.ExpenseCategory;
import moneytree.persist.db.generated.tables.records.ExpenseCategoryRecord;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExpenseCategoryDao extends DAOImpl<ExpenseCategoryRecord, moneytree.persist.db.generated.tables.pojos.ExpenseCategory, UUID> {

    /**
     * Create a new ExpenseCategoryDao without any configuration
     */
    public ExpenseCategoryDao() {
        super(ExpenseCategory.EXPENSE_CATEGORY, moneytree.persist.db.generated.tables.pojos.ExpenseCategory.class);
    }

    /**
     * Create a new ExpenseCategoryDao with an attached configuration
     */
    public ExpenseCategoryDao(Configuration configuration) {
        super(ExpenseCategory.EXPENSE_CATEGORY, moneytree.persist.db.generated.tables.pojos.ExpenseCategory.class, configuration);
    }

    @Override
    public UUID getId(moneytree.persist.db.generated.tables.pojos.ExpenseCategory object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.ExpenseCategory> fetchRangeOfId(UUID lowerInclusive, UUID upperInclusive) {
        return fetchRange(ExpenseCategory.EXPENSE_CATEGORY.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.ExpenseCategory> fetchById(UUID... values) {
        return fetch(ExpenseCategory.EXPENSE_CATEGORY.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public moneytree.persist.db.generated.tables.pojos.ExpenseCategory fetchOneById(UUID value) {
        return fetchOne(ExpenseCategory.EXPENSE_CATEGORY.ID, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.ExpenseCategory> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(ExpenseCategory.EXPENSE_CATEGORY.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.ExpenseCategory> fetchByName(String... values) {
        return fetch(ExpenseCategory.EXPENSE_CATEGORY.NAME, values);
    }

    /**
     * Fetch records that have <code>target_amount BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.ExpenseCategory> fetchRangeOfTargetAmount(BigDecimal lowerInclusive, BigDecimal upperInclusive) {
        return fetchRange(ExpenseCategory.EXPENSE_CATEGORY.TARGET_AMOUNT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>target_amount IN (values)</code>
     */
    public List<moneytree.persist.db.generated.tables.pojos.ExpenseCategory> fetchByTargetAmount(BigDecimal... values) {
        return fetch(ExpenseCategory.EXPENSE_CATEGORY.TARGET_AMOUNT, values);
    }
}
