package org.fdu;

import java.time.LocalDateTime;

public class Income {

    // ── Common fields ─────────────────────────────────────────────────────────
    String        incomeName;
    float         incomeAmount;   // flat amount (used when !isHourly)
    boolean       isMonthly;      // flat-amount mode: true = monthly figure
    LocalDateTime timestamp;

    // ── Hourly mode fields (only used when isHourly = true) ───────────────────
    boolean      isHourly;
    double       hourlyRate;       // $ per hour
    double       hoursPerPeriod;   // hours worked in ONE pay period
    PayFrequency payFrequency;     // how often they are paid
    double       weeklyPreTax;     // optional 401K/pre-tax deduction per week (0 if none)

    // ── Constructor: flat amount ──────────────────────────────────────────────
    public Income(String incomeName, float incomeAmount, boolean isMonthly) {
        this.incomeName   = incomeName;
        this.incomeAmount = incomeAmount;
        this.isMonthly    = isMonthly;
        this.isHourly     = false;
        this.timestamp    = LocalDateTime.now();
    }

    // ── Constructor: hourly ───────────────────────────────────────────────────
    public Income(String incomeName, double hourlyRate,
                  double hoursPerPeriod, PayFrequency payFrequency) {
        this(incomeName, hourlyRate, hoursPerPeriod, payFrequency, 0.0);
    }

    public Income(String incomeName, double hourlyRate,
                  double hoursPerPeriod, PayFrequency payFrequency, double weeklyPreTax) {
        this.incomeName      = incomeName;
        this.hourlyRate      = hourlyRate;
        this.hoursPerPeriod  = hoursPerPeriod;
        this.payFrequency    = payFrequency;
        this.weeklyPreTax    = weeklyPreTax;
        this.isHourly        = true;
        this.isMonthly       = false;
        this.timestamp       = LocalDateTime.now();
    }

    // ── Weekly gross (handles both modes + overtime) ──────────────────────────

    /**
     * Returns weekly gross pay.
     * Flat mode:   respects isMonthly flag (monthly → weekly via ×12/52).
     * Hourly mode: converts pay-period hours to weekly hours, then applies
     *              overtime at 1.5× for any weekly hours beyond 40.
     */
    public double weeklyGross() {
        if (!isHourly) {
            return isMonthly ? (incomeAmount * 12.0 / 52.0) : incomeAmount;
        }
        double avgWeeklyHours = payFrequency.weeklyHours(hoursPerPeriod);
        double regularHours   = Math.min(avgWeeklyHours, 40.0);
        double overtimeHours  = Math.max(avgWeeklyHours - 40.0, 0.0);
        return (regularHours * hourlyRate) + (overtimeHours * hourlyRate * 1.5);
    }

    /** Annualised gross — used by TaxCalculator. */
    public double annualGross() {
        return weeklyGross() * 52.0;
    }

    /** Legacy float accessor (keeps TrackIncome totals working). */
    public float weeklyAmount() {
        return (float) weeklyGross();
    }

    @Override
    public String toString() {
        if (isHourly) {
            double avgWeeklyHrs = payFrequency.weeklyHours(hoursPerPeriod);
            double overtimeHrs  = Math.max(avgWeeklyHrs - 40.0, 0.0);
            return String.format(
                    "%s: $%.2f/hr  |  %.1f hrs/%s%s  |  Weekly gross: $%.2f  [added %s]",
                    incomeName, hourlyRate, hoursPerPeriod, payFrequency,
                    overtimeHrs > 0
                            ? String.format("  (incl. %.1f OT hrs/wk avg)", overtimeHrs) : "",
                    weeklyGross(),
                    timestamp.toLocalTime().toString().substring(0, 8)
            );
        }
        return String.format("%s: $%.2f/%s  [added %s]",
                incomeName, incomeAmount,
                isMonthly ? "month" : "week",
                timestamp.toLocalTime().toString().substring(0, 8));
    }
}