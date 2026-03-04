package org.fdu;

/**
 * Represents how often an employee is paid.
 * Used to convert pay-period hours → weekly hours for overtime and gross calculations.
 * ── Bi-Weekly vs Semi-Monthly ────────────────────────────────────────────────
 * These are often confused but are genuinely different:
 *   Bi-Weekly    = paid every 2 weeks          → 26 paychecks/year
 *   Semi-Monthly = paid twice a month          → 24 paychecks/year
 *                  (e.g. on the 1st and 15th)
 * The difference matters for hours-to-weekly conversion:
 *   Bi-Weekly:    52 weeks ÷ 26 periods = 2.000 weeks/period  (exact)
 *   Semi-Monthly: 52 weeks ÷ 24 periods = 2.167 weeks/period  (average)
 * Example — you enter 80 hours per pay period:
 *   Bi-Weekly:    80 ÷ 2.000 = 40.0 hrs/week  → no overtime
 *   Semi-Monthly: 80 ÷ 2.167 = 36.9 hrs/week  → no overtime, but lower weekly rate
 * In practice, semi-monthly periods don't always land exactly 2 weeks apart
 * (some months have 28–31 days), so the 2.167 figure is an average.
 * Bi-weekly periods are always exactly 14 days.
 * ── periodsPerMonth field ────────────────────────────────────────────────────
 *   Weekly:       ~4.33 periods/month  (52 weeks ÷ 12 months)
 *   Bi-Weekly:    ~2.17 periods/month  (26 periods ÷ 12 months)
 *   Semi-Monthly:  2.00 periods/month  (exactly 2 per month, by definition)
 *   Monthly:       1.00 period/month   (exactly 1 per month)
 * Note: periodsPerMonth is stored as a double for accuracy. All core math
 * uses periodsPerYear, which is the exact, authoritative value.
 */
public enum PayFrequency {

    //                    display name     periods/month  periods/year
    WEEKLY       ("Weekly",       52.0 / 12.0,  52),   // ~4.33/month
    BIWEEKLY     ("Bi-Weekly",    26.0 / 12.0,  26),   // ~2.17/month — every 14 days
    SEMI_MONTHLY ("Semi-Monthly",  2.0,          24),   //  2.00/month — 1st & 15th
    MONTHLY      ("Monthly",       1.0,          12);   //  1.00/month

    private final String displayName;
    private final double periodsPerMonth;  // for reference / display
    public  final int    periodsPerYear;   // used in all calculations

    PayFrequency(String displayName, double periodsPerMonth, int periodsPerYear) {
        this.displayName    = displayName;
        this.periodsPerMonth = periodsPerMonth;
        this.periodsPerYear  = periodsPerYear;
    }

    /** Average number of weeks in one pay period. */
    public double weeksPerPeriod() {
        return 52.0 / periodsPerYear;
    }

    /**
     * Converts hours entered for one pay period into average weekly hours.
     * Example: 80 hrs bi-weekly → 80 / 2.0 = 40.0 hrs/week
     *          80 hrs semi-monthly → 80 / 2.167 = 36.9 hrs/week
     */
    public double weeklyHours(double hoursPerPeriod) {
        return hoursPerPeriod / weeksPerPeriod();
    }

    @Override
    public String toString() { return displayName; }

    public static PayFrequency[] allValues() { return values(); }
}