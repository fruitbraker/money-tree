/*
 * This file is generated by jOOQ.
 */
package moneytree.persist.db.generated.tables.pojos;


import java.io.Serializable;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IncomeCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID   incomeCategoryId;
    private String name;

    public IncomeCategory() {}

    public IncomeCategory(IncomeCategory value) {
        this.incomeCategoryId = value.incomeCategoryId;
        this.name = value.name;
    }

    public IncomeCategory(
        UUID   incomeCategoryId,
        String name
    ) {
        this.incomeCategoryId = incomeCategoryId;
        this.name = name;
    }

    /**
     * Getter for <code>mtdev.income_category.income_category_id</code>.
     */
    public UUID getIncomeCategoryId() {
        return this.incomeCategoryId;
    }

    /**
     * Setter for <code>mtdev.income_category.income_category_id</code>.
     */
    public IncomeCategory setIncomeCategoryId(UUID incomeCategoryId) {
        this.incomeCategoryId = incomeCategoryId;
        return this;
    }

    /**
     * Getter for <code>mtdev.income_category.name</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for <code>mtdev.income_category.name</code>.
     */
    public IncomeCategory setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IncomeCategory (");

        sb.append(incomeCategoryId);
        sb.append(", ").append(name);

        sb.append(")");
        return sb.toString();
    }
}
