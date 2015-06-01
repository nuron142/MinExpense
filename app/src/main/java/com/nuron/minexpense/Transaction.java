package com.nuron.minexpense;

/**
 * Created by sunil on 24-May-15.
 */
public class Transaction {

    private long id;
    private String name;
    private String amount;
    private String category;
    private String artId;
    private String time;
    private String incomeOrExpense;

    public Transaction(String dataNmame,String dataAmount, String dataCategory,
                       String dataArtId,String dataTime,String dataIncomeOrExpense)
    {
        this.name = dataNmame;
        this.amount = dataAmount;
        this.category = dataCategory;
        this.artId = dataArtId;
        this.time = dataTime;
        this.incomeOrExpense = dataIncomeOrExpense;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getArtId() {
        return artId;
    }

    public String getTime() {
        return time;
    }

    public String getIncomeOrExpense() {
        return incomeOrExpense;
    }
}

