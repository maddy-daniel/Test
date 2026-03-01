package org.fdu;

import java.util.LinkedList;

public class TrackIncome {
    LinkedList<Income> incomeRecurring;
    LinkedList<Income> incomeIncidental;

    public TrackIncome() {
        this.incomeRecurring  = new LinkedList<>();
        this.incomeIncidental = new LinkedList<>();
    }

    // ── Add: flat amount ──────────────────────────────────────────────────────

    public Income addIncomeRecurring(String name, float amount, boolean isMonthly) {
        Income newIncome = new Income(name, amount, isMonthly);
        incomeRecurring.add(newIncome);
        return newIncome;
    }

    public Income addIncomeIncidental(String name, float amount, boolean isMonthly) {
        Income newIncome = new Income(name, amount, isMonthly);
        incomeIncidental.add(newIncome);
        return newIncome;
    }

    // ── Add: hourly (with pay frequency + overtime) ───────────────────────────

    public Income addIncomeRecurringHourly(String name, double rate,
                                           double hours, PayFrequency freq,
                                           double weeklyPreTax) {
        Income newIncome = new Income(name, rate, hours, freq, weeklyPreTax);
        incomeRecurring.add(newIncome);
        return newIncome;
    }

    public Income addIncomeIncidentalHourly(String name, double rate,
                                            double hours, PayFrequency freq,
                                            double weeklyPreTax) {
        Income newIncome = new Income(name, rate, hours, freq, weeklyPreTax);
        incomeIncidental.add(newIncome);
        return newIncome;
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteIncomeRecurring(Income income) {
        incomeRecurring.remove(income);
    }

    public void deleteIncomeIncidental(Income income) {
        incomeIncidental.remove(income);
    }

    // ── Totals ────────────────────────────────────────────────────────────────

    /** Total weekly gross across all entries. */
    public float totalWeeklyGross() {
        float total = 0f;
        for (Income i : incomeRecurring)  total += i.weeklyAmount();
        for (Income i : incomeIncidental) total += i.weeklyAmount();
        return total;
    }

    /**
     * Total weekly take-home.
     * - Flat entries: the user already knows their net, so the amount is used as-is.
     * - Hourly entries: tax is calculated via TaxCalculator on the gross.
     * To keep tax math accurate for hourly entries, each hourly entry's
     * after-tax amount is computed individually, then flat amounts are added on top.
     */
    public double totalWeeklyTakeHome() {
        double total = 0.0;
        for (Income i : incomeRecurring) {
            total += i.isHourly
                    ? TaxCalculator.weeklyTakeHome(i.weeklyGross(), i.weeklyPreTax)
                    : i.weeklyGross();
        }
        for (Income i : incomeIncidental) {
            total += i.isHourly
                    ? TaxCalculator.weeklyTakeHome(i.weeklyGross(), i.weeklyPreTax)
                    : i.weeklyGross();
        }
        return total;
    }

    /**
     * Weekly gross from hourly entries only — used to populate the tax breakdown card,
     * since flat entries don't need tax calculated.
     */
    public double hourlyOnlyWeeklyGross() {
        double total = 0.0;
        for (Income i : incomeRecurring)  if (i.isHourly) total += i.weeklyGross();
        for (Income i : incomeIncidental) if (i.isHourly) total += i.weeklyGross();
        return total;
    }

    /** Legacy alias. */
    public float totalIncome() {
        return totalWeeklyGross();
    }

    // ── Print ─────────────────────────────────────────────────────────────────

    public void printIncomeRecurring() {
        System.out.println("── Recurring Income ──────────────────────────────");
        if (incomeRecurring.isEmpty()) { System.out.println("  (none)"); return; }
        for (Income i : incomeRecurring) printEntry(i);
    }

    public void printIncomeIncidental() {
        System.out.println("── Incidental Income ─────────────────────────────");
        if (incomeIncidental.isEmpty()) { System.out.println("  (none)"); return; }
        for (Income i : incomeIncidental) printEntry(i);
    }

    private void printEntry(Income i) {
        System.out.printf("  %s%n", i);
        if (i.isHourly) {
            double weeksPerPeriod = i.payFrequency.weeksPerPeriod();
            double avgWeeklyHrs   = i.payFrequency.weeklyHours(i.hoursPerPeriod);
            double otHrsPerWeek   = Math.max(avgWeeklyHrs - 40.0, 0);
            double regHrsPerWeek  = Math.min(avgWeeklyHrs, 40.0);
            double periodGross    = (regHrsPerWeek * weeksPerPeriod * i.hourlyRate)
                    + (otHrsPerWeek  * weeksPerPeriod * i.hourlyRate * 1.5);
            double federal  = TaxCalculator.federalWithholdingWeekly(i.weeklyGross(), i.weeklyPreTax) * weeksPerPeriod;
            double nj       = TaxCalculator.njStateTax(i.weeklyGross() * 52) / 52 * weeksPerPeriod;
            double ss       = i.weeklyGross() * 0.062 * weeksPerPeriod;
            double medicare = i.weeklyGross() * 0.0145 * weeksPerPeriod;
            double periodNet = periodGross - federal - nj - ss - medicare;
            System.out.printf("    Hours/period   : %.1f hrs @ $%.2f/hr%s%n",
                    i.hoursPerPeriod, i.hourlyRate,
                    otHrsPerWeek > 0
                            ? String.format("  (%.1f OT hrs/wk avg @ $%.2f OT rate)", otHrsPerWeek, i.hourlyRate * 1.5)
                            : "");
            System.out.printf("    ── Per %s Paycheck ─────────────%n", i.payFrequency);
            System.out.printf("    Gross Pay      : $%.2f%n", periodGross);
            double sui      = i.weeklyGross() * 0.00425 * weeksPerPeriod;
            double sdi      = i.weeklyGross() * 0.00190 * weeksPerPeriod;
            double fli      = i.weeklyGross() * 0.00228 * weeksPerPeriod;
            double preTax   = i.weeklyPreTax * weeksPerPeriod;
            periodNet       = periodGross - federal - nj - ss - medicare - sui - sdi - fli - preTax;
            if (preTax > 0)
                System.out.printf("    401K (pre-tax) : -$%.2f%n", preTax);
            System.out.printf("    Federal Tax    : -$%.2f%n", federal);
            System.out.printf("    NJ State Tax   : -$%.2f%n", nj);
            System.out.printf("    Social Security: -$%.2f%n", ss);
            System.out.printf("    Medicare       : -$%.2f%n", medicare);
            System.out.printf("    NJ SUI         : -$%.2f%n", sui);
            System.out.printf("    NJ SDI         : -$%.2f%n", sdi);
            System.out.printf("    NJ FLI         : -$%.2f%n", fli);
            System.out.printf("    Net Pay        : $%.2f%n", periodNet);
        } else {
            System.out.printf("    Amount         : $%.2f/wk  (entered as net — no tax applied)%n",
                    i.weeklyGross());
        }
        System.out.println();
    }

    public void printIncomeAll() {
        printIncomeRecurring();
        System.out.println();
        printIncomeIncidental();
        System.out.println();
        System.out.printf("  Total Weekly Gross:     $%.2f%n", totalWeeklyGross());
        System.out.printf("  Total Weekly Take-Home: $%.2f%n", totalWeeklyTakeHome());
        double hourlyGross = hourlyOnlyWeeklyGross();
        if (hourlyGross > 0) {
            System.out.println();
            System.out.println(TaxCalculator.getTaxBreakdown(hourlyGross * 52.0));
        }
    }
}