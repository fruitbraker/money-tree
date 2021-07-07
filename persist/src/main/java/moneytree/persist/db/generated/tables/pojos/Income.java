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
public class Income implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID       incomeId;
    private String     source;
    private UUID       incomeCategoryId;
    private BigDecimal transactionAmount;
    private LocalDate  transactionDate;
    private String     notes;
    private Boolean    hide;

    public Income() {}

    public Income(Income value) {
        this.incomeId = value.incomeId;
        this.source = value.source;
        this.incomeCategoryId = value.incomeCategoryId;
        this.transactionAmount = value.transactionAmount;
        this.transactionDate = value.transactionDate;
        this.notes = value.notes;
        this.hide = value.hide;
    }

    public Income(
        UUID       incomeId,
        String     source,
        UUID       incomeCategoryId,
        BigDecimal transactionAmount,
        LocalDate  transactionDate,
        String     notes,
        Boolean    hide
    ) {
        this.incomeId = incomeId;
        this.source = source;
        this.incomeCategoryId = incomeCategoryId;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
        this.notes = notes;
        this.hide = hide;
    }

    /**
     * Getter for <code>mtdev.income.income_id</code>.
     */
    public UUID getIncomeId() {
        return this.incomeId;
    }

    /**
     * Setter for <code>mtdev.income.income_id</code>.
     */
    public Income setIncomeId(UUID incomeId) {
        this.incomeId = incomeId;
        return this;
    }

    /**
     * Getter for <code>mtdev.income.source</code>.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Setter for <code>mtdev.income.source</code>.
     */
    public Income setSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * Getter for <code>mtdev.income.income_category_id</code>.
     */
    public UUID getIncomeCategoryId() {
        return this.incomeCategoryId;
    }

    /**
     * Setter for <code>mtdev.income.income_category_id</code>.
     */
    public Income setIncomeCategoryId(UUID incomeCategoryId) {
        this.incomeCategoryId = incomeCategoryId;
        return this;
    }

    /**
     * Getter for <code>mtdev.income.transaction_amount</code>.
     */
    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    /**
     * Setter for <code>mtdev.income.transaction_amount</code>.
     */
    public Income setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    /**
     * Getter for <code>mtdev.income.transaction_date</code>.
     */
    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    /**
     * Setter for <code>mtdev.income.transaction_date</code>.
     */
    public Income setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    /**
     * Getter for <code>mtdev.income.notes</code>.
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Setter for <code>mtdev.income.notes</code>.
     */
    public Income setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    /**
     * Getter for <code>mtdev.income.hide</code>.
     */
    public Boolean getHide() {
        return this.hide;
    }

    /**
     * Setter for <code>mtdev.income.hide</code>.
     */
    public Income setHide(Boolean hide) {
        this.hide = hide;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Income (");

        sb.append(incomeId);
        sb.append(", ").append(source);
        sb.append(", ").append(incomeCategoryId);
        sb.append(", ").append(transactionAmount);
        sb.append(", ").append(transactionDate);
        sb.append(", ").append(notes);
        sb.append(", ").append(hide);

        sb.append(")");
        return sb.toString();
    }
}
