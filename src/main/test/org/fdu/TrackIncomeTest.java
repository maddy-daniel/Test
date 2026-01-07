package org.fdu;

import static org.junit.jupiter.api.Assertions.*;

class TrackIncomeTest {

    @org.junit.jupiter.api.Test
    void addIncomeRecurring() {
        TrackIncome income = new TrackIncome();
        income.addIncomeRecurring("rent", 300.00F);
        income.addIncomeRecurring("car",  100.50F);
        assertEquals(300F, income.getIncomeRecurring("rent"));
        assertEquals(100.5F, income.getIncomeRecurring("car"));
        assertNotEquals(100.5F, income.getIncomeRecurring("rent"));
    }

    @org.junit.jupiter.api.Test
    void addIncomeRecurringDuplicateName() {
        TrackIncome income = new TrackIncome();
        income.addIncomeRecurring("rent", 300.00F);
        income.addIncomeRecurring("rent",  100.50F);
        assertEquals(100.50F, income.getIncomeRecurring("rent"));
    }

    @org.junit.jupiter.api.Test
    void addIncomeIncidental() {
    }
}