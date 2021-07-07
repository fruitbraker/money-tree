/*
 * This file is generated by jOOQ.
 */
package moneytree.persist.db.generated.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExpenseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID       expenseCategoryId;
    private String     name;
    private BigDecimal targetAmount;

    public ExpenseCategory() {}

    public ExpenseCategory(ExpenseCategory value) {
        this.expenseCategoryId = value.expenseCategoryId;
        this.name = value.name;
        this.targetAmount = value.targetAmount;
    }

    public ExpenseCategory(
        UUID       expenseCategoryId,
        String     name,
        BigDecimal targetAmount
    ) {
        this.expenseCategoryId = expenseCategoryId;
        this.name = name;
        this.targetAmount = targetAmount;
    }

    /**
     * Getter for <code>mtdev.expense_category.expense_category_id</code>.
     */
    public UUID getExpenseCategoryId() {
        return this.expenseCategoryId;
    }

    /**
     * Setter for <code>mtdev.expense_category.expense_category_id</code>.
     */
    public ExpenseCategory setExpenseCategoryId(UUID expenseCategoryId) {
        this.expenseCategoryId = expenseCategoryId;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense_category.name</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for <code>mtdev.expense_category.name</code>.
     */
    public ExpenseCategory setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Getter for <code>mtdev.expense_category.target_amount</code>.
     */
    public BigDecimal getTargetAmount() {
        return this.targetAmount;
    }

    /**
     * Setter for <code>mtdev.expense_category.target_amount</code>.
     */
    public ExpenseCategory setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ExpenseCategory (");

        sb.append(expenseCategoryId);
        sb.append(", ").append(name);
        sb.append(", ").append(targetAmount);

        sb.append(")");
        return sb.toString();
    }
}
