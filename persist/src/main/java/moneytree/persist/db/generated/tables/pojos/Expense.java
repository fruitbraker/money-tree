/*
 * This file is generated by jOOQ.
 */
package moneytree.persist.db.generated.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID       id;
    private LocalDate  transactionDate;
    private BigDecimal transactionAmount;
    private UUID       vendor;
    private UUID       expenseCategory;
    private String     notes;
    private Boolean    hide;

    public Expense() {}

    public Expense(Expense value) {
        this.id = value.id;
        this.transactionDate = value.transactionDate;
        this.transactionAmount = value.transactionAmount;
        this.vendor = value.vendor;
        this.expenseCategory = value.expenseCategory;
        this.notes = value.notes;
        this.hide = value.hide;
    }

    public Expense(
        UUID       id,
        LocalDate  transactionDate,
        BigDecimal transactionAmount,
        UUID       vendor,
        UUID       expenseCategory,
        String     notes,
        Boolean    hide
    ) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.vendor = vendor;
        this.expenseCategory = expenseCategory;
        this.notes = notes;
        this.hide = hide;
    }

    /**
     * Getter for <code>mtdev.expense.id</code>.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Setter for <code>mtdev.expense.id</code>.
     */
    public Expense setId(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense.transaction_date</code>.
     */
    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    /**
     * Setter for <code>mtdev.expense.transaction_date</code>.
     */
    public Expense setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense.transaction_amount</code>.
     */
    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    /**
     * Setter for <code>mtdev.expense.transaction_amount</code>.
     */
    public Expense setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense.vendor</code>.
     */
    public UUID getVendor() {
        return this.vendor;
    }

    /**
     * Setter for <code>mtdev.expense.vendor</code>.
     */
    public Expense setVendor(UUID vendor) {
        this.vendor = vendor;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense.expense_category</code>.
     */
    public UUID getExpenseCategory() {
        return this.expenseCategory;
    }

    /**
     * Setter for <code>mtdev.expense.expense_category</code>.
     */
    public Expense setExpenseCategory(UUID expenseCategory) {
        this.expenseCategory = expenseCategory;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense.notes</code>.
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Setter for <code>mtdev.expense.notes</code>.
     */
    public Expense setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense.hide</code>.
     */
    public Boolean getHide() {
        return this.hide;
    }

    /**
     * Setter for <code>mtdev.expense.hide</code>.
     */
    public Expense setHide(Boolean hide) {
        this.hide = hide;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Expense (");

        sb.append(id);
        sb.append(", ").append(transactionDate);
        sb.append(", ").append(transactionAmount);
        sb.append(", ").append(vendor);
        sb.append(", ").append(expenseCategory);
        sb.append(", ").append(notes);
        sb.append(", ").append(hide);

        sb.append(")");
        return sb.toString();
    }
}
