/*
 * This file is generated by jOOQ.
 */
package moneytree.persist.generated.tables.records;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import moneytree.persist.generated.tables.Income;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IncomeRecord extends UpdatableRecordImpl<IncomeRecord> implements Record7<UUID, String, UUID, BigDecimal, LocalDate, String, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>mtdev.income.id</code>.
     */
    public IncomeRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>mtdev.income.source</code>.
     */
    public IncomeRecord setSource(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.source</code>.
     */
    public String getSource() {
        return (String) get(1);
    }

    /**
     * Setter for <code>mtdev.income.income_category</code>.
     */
    public IncomeRecord setIncomeCategory(UUID value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.income_category</code>.
     */
    public UUID getIncomeCategory() {
        return (UUID) get(2);
    }

    /**
     * Setter for <code>mtdev.income.transaction_amount</code>.
     */
    public IncomeRecord setTransactionAmount(BigDecimal value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.transaction_amount</code>.
     */
    public BigDecimal getTransactionAmount() {
        return (BigDecimal) get(3);
    }

    /**
     * Setter for <code>mtdev.income.transaction_date</code>.
     */
    public IncomeRecord setTransactionDate(LocalDate value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.transaction_date</code>.
     */
    public LocalDate getTransactionDate() {
        return (LocalDate) get(4);
    }

    /**
     * Setter for <code>mtdev.income.notes</code>.
     */
    public IncomeRecord setNotes(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.notes</code>.
     */
    public String getNotes() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mtdev.income.hide</code>.
     */
    public IncomeRecord setHide(Boolean value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>mtdev.income.hide</code>.
     */
    public Boolean getHide() {
        return (Boolean) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<UUID, String, UUID, BigDecimal, LocalDate, String, Boolean> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<UUID, String, UUID, BigDecimal, LocalDate, String, Boolean> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return Income.INCOME.ID;
    }

    @Override
    public Field<String> field2() {
        return Income.INCOME.SOURCE;
    }

    @Override
    public Field<UUID> field3() {
        return Income.INCOME.INCOME_CATEGORY;
    }

    @Override
    public Field<BigDecimal> field4() {
        return Income.INCOME.TRANSACTION_AMOUNT;
    }

    @Override
    public Field<LocalDate> field5() {
        return Income.INCOME.TRANSACTION_DATE;
    }

    @Override
    public Field<String> field6() {
        return Income.INCOME.NOTES;
    }

    @Override
    public Field<Boolean> field7() {
        return Income.INCOME.HIDE;
    }

    @Override
    public UUID component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getSource();
    }

    @Override
    public UUID component3() {
        return getIncomeCategory();
    }

    @Override
    public BigDecimal component4() {
        return getTransactionAmount();
    }

    @Override
    public LocalDate component5() {
        return getTransactionDate();
    }

    @Override
    public String component6() {
        return getNotes();
    }

    @Override
    public Boolean component7() {
        return getHide();
    }

    @Override
    public UUID value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getSource();
    }

    @Override
    public UUID value3() {
        return getIncomeCategory();
    }

    @Override
    public BigDecimal value4() {
        return getTransactionAmount();
    }

    @Override
    public LocalDate value5() {
        return getTransactionDate();
    }

    @Override
    public String value6() {
        return getNotes();
    }

    @Override
    public Boolean value7() {
        return getHide();
    }

    @Override
    public IncomeRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public IncomeRecord value2(String value) {
        setSource(value);
        return this;
    }

    @Override
    public IncomeRecord value3(UUID value) {
        setIncomeCategory(value);
        return this;
    }

    @Override
    public IncomeRecord value4(BigDecimal value) {
        setTransactionAmount(value);
        return this;
    }

    @Override
    public IncomeRecord value5(LocalDate value) {
        setTransactionDate(value);
        return this;
    }

    @Override
    public IncomeRecord value6(String value) {
        setNotes(value);
        return this;
    }

    @Override
    public IncomeRecord value7(Boolean value) {
        setHide(value);
        return this;
    }

    @Override
    public IncomeRecord values(UUID value1, String value2, UUID value3, BigDecimal value4, LocalDate value5, String value6, Boolean value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached IncomeRecord
     */
    public IncomeRecord() {
        super(Income.INCOME);
    }

    /**
     * Create a detached, initialised IncomeRecord
     */
    public IncomeRecord(UUID id, String source, UUID incomeCategory, BigDecimal transactionAmount, LocalDate transactionDate, String notes, Boolean hide) {
        super(Income.INCOME);

        setId(id);
        setSource(source);
        setIncomeCategory(incomeCategory);
        setTransactionAmount(transactionAmount);
        setTransactionDate(transactionDate);
        setNotes(notes);
        setHide(hide);
    }
}