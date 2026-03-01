package org.fdu;

/**
 * Calculates NJ paycheck deductions for a standardized estimate.
 * Federal withholding uses IRS Publication 15-T (2024) weekly table,
 * applied to (gross - 401K contribution) since 401K is pre-tax for federal.
 * NJ deductions included:
 *   - NJ State Income Tax       (progressive brackets)
 *   - NJ SUI  State Unemployment Insurance  0.4250%
 *   - NJ SDI  State Disability Insurance    0.1900%
 *   - NJ FLI  Paid Family Leave Insurance   0.2280%
 * FICA: Social Security 6.2% (wage base $168,600) + Medicare 1.45%
 * Note: SS and Medicare are applied to full gross — 401K does not reduce them.
 * Limitation: Federal estimate assumes a standard W-4 with no extra allowances
 * or deductions in Steps 3/4. Actual federal withholding may be lower if your
 * W-4 claims dependents or additional deductions. Results are a reliable
 * planning baseline for comparing jobs.
 */
public class TaxCalculator {

    // ── IRS Pub 15-T 2024: Weekly Payroll, Single, Percentage Method ──────────
    // Applied to (weeklyGross - preTaxDeductions).
    private static final double[][] FEDERAL_WEEKLY_TABLE = {
            {    0,     0.00, 0.00 },
            {   87,     0.00, 0.10 },
            {  462,    37.50, 0.12 },
            { 1054,   108.54, 0.22 },
            { 2278,   377.82, 0.24 },
            { 3681,   714.06, 0.32 },
            { 4669,   930.22, 0.35 },
            {11424,  3294.47, 0.37 },
    };

    // ── NJ State Income Tax Brackets (2024, Single, Annual) ──────────────────
    private static final double[][] NJ_BRACKETS = {
            {20000,            0.0140},
            {35000,            0.0175},
            {40000,            0.0350},
            {75000,            0.05525},
            {500000,           0.0637},
            {1000000,          0.0897},
            {Double.MAX_VALUE, 0.1075}
    };

    // ── FICA (2024) ───────────────────────────────────────────────────────────
    private static final double SS_RATE       = 0.0620;
    private static final double SS_WAGE_BASE  = 168600.0;
    private static final double MEDICARE_RATE = 0.0145;

    // ── NJ Additional Payroll Taxes (2024) ────────────────────────────────────
    // These appear on every NJ paycheck regardless of employer.
    private static final double NJ_SUI_RATE = 0.00425;  // State Unemployment Insurance
    private static final double NJ_SDI_RATE = 0.00190;  // State Disability Insurance
    private static final double NJ_FLI_RATE = 0.00228;  // Paid Family Leave Insurance

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Federal withholding for one weekly paycheck.
     * preTaxWeekly = 401K or other pre-tax deductions for the week.
     * 401K reduces federal taxable income but NOT SS/Medicare.
     */
    public static double federalWithholdingWeekly(double weeklyGross, double preTaxWeekly) {
        double taxable = Math.max(weeklyGross - preTaxWeekly, 0);
        for (int i = FEDERAL_WEEKLY_TABLE.length - 1; i >= 0; i--) {
            if (taxable > FEDERAL_WEEKLY_TABLE[i][0]) {
                return FEDERAL_WEEKLY_TABLE[i][1]
                        + (taxable - FEDERAL_WEEKLY_TABLE[i][0]) * FEDERAL_WEEKLY_TABLE[i][2];
            }
        }
        return 0;
    }

    /** Federal withholding with no pre-tax deductions (standardized baseline). */
    public static double federalWithholdingWeekly(double weeklyGross) {
        return federalWithholdingWeekly(weeklyGross, 0);
    }

    /** Annual federal tax — annualizes the weekly withholding. */
    public static double federalTax(double annualGross) {
        return federalWithholdingWeekly(annualGross / 52.0) * 52.0;
    }

    /** Annual federal tax accounting for annual pre-tax deductions (e.g. 401K). */
    public static double federalTax(double annualGross, double annualPreTax) {
        return federalWithholdingWeekly(annualGross / 52.0, annualPreTax / 52.0) * 52.0;
    }

    // ── NJ State Income Tax ───────────────────────────────────────────────────

    /** Annual NJ state income tax (progressive brackets, full gross — 401K doesn't reduce NJ). */
    public static double njStateTax(double annualGross) {
        double tax = 0, prev = 0;
        for (double[] b : NJ_BRACKETS) {
            if (annualGross <= prev) break;
            tax += (Math.min(annualGross, b[0]) - prev) * b[1];
            prev = b[0];
        }
        return tax;
    }

    // ── FICA ─────────────────────────────────────────────────────────────────

    /** Annual Social Security tax (capped at wage base). Applied to full gross. */
    public static double socialSecurityTax(double annualGross) {
        return Math.min(annualGross, SS_WAGE_BASE) * SS_RATE;
    }

    /** Annual Medicare tax. Applied to full gross. */
    public static double medicareTax(double annualGross) {
        return annualGross * MEDICARE_RATE;
    }

    // ── NJ Additional Taxes ───────────────────────────────────────────────────

    /** Annual NJ SUI (State Unemployment Insurance). */
    public static double njSuiTax(double annualGross) {
        return annualGross * NJ_SUI_RATE;
    }

    /** Annual NJ SDI (State Disability Insurance). */
    public static double njSdiTax(double annualGross) {
        return annualGross * NJ_SDI_RATE;
    }

    /** Annual NJ FLI (Paid Family Leave Insurance). */
    public static double njFliTax(double annualGross) {
        return annualGross * NJ_FLI_RATE;
    }

    // ── Totals ────────────────────────────────────────────────────────────────

    /**
     * Total annual taxes.
     * annualPreTax = annual 401K or other pre-tax contributions (0 if none).
     */
    public static double totalTax(double annualGross, double annualPreTax) {
        return federalTax(annualGross, annualPreTax)
                + njStateTax(annualGross)
                + socialSecurityTax(annualGross)
                + medicareTax(annualGross)
                + njSuiTax(annualGross)
                + njSdiTax(annualGross)
                + njFliTax(annualGross);
    }

    public static double totalTax(double annualGross) {
        return totalTax(annualGross, 0);
    }

    /**
     * Annual take-home after all taxes and pre-tax deductions.
     */
    public static double annualTakeHome(double annualGross, double annualPreTax) {
        return annualGross - totalTax(annualGross, annualPreTax) - annualPreTax;
    }

    public static double annualTakeHome(double annualGross) {
        return annualTakeHome(annualGross, 0);
    }

    /**
     * Weekly take-home after all taxes.
     * weeklyPreTax = weekly 401K contribution (0 if none).
     */
    public static double weeklyTakeHome(double weeklyGross, double weeklyPreTax) {
        double annual    = weeklyGross * 52.0;
        double federal   = federalWithholdingWeekly(weeklyGross, weeklyPreTax);
        double nj        = njStateTax(annual)        / 52.0;
        double ss        = socialSecurityTax(annual) / 52.0;
        double medicare  = medicareTax(annual)       / 52.0;
        double sui       = njSuiTax(annual)          / 52.0;
        double sdi       = njSdiTax(annual)          / 52.0;
        double fli       = njFliTax(annual)          / 52.0;
        return weeklyGross - federal - nj - ss - medicare - sui - sdi - fli - weeklyPreTax;
    }

    public static double weeklyTakeHome(double weeklyGross) {
        return weeklyTakeHome(weeklyGross, 0);
    }

    /** Console tax breakdown string. */
    public static String getTaxBreakdown(double annualGross, double annualPreTax) {
        double federal  = federalTax(annualGross, annualPreTax);
        double nj       = njStateTax(annualGross);
        double ss       = socialSecurityTax(annualGross);
        double medicare = medicareTax(annualGross);
        double sui      = njSuiTax(annualGross);
        double sdi      = njSdiTax(annualGross);
        double fli      = njFliTax(annualGross);
        double net      = annualTakeHome(annualGross, annualPreTax);
        double effRate  = (annualGross > 0)
                ? (totalTax(annualGross, annualPreTax) / annualGross) * 100 : 0;

        return String.format(
                "=== Pay Stub Estimate (NJ, Single Filer, Standard W-4) ===%n" +
                        "  Gross Income:         $%,.2f%n" +
                        (annualPreTax > 0
                                ? String.format("  401K (pre-tax):       -$%,.2f%n", annualPreTax)
                                : "") +
                        "  Federal Income Tax:  -$%,.2f%n" +
                        "  NJ State Tax:        -$%,.2f%n" +
                        "  Social Security:     -$%,.2f%n" +
                        "  Medicare:            -$%,.2f%n" +
                        "  NJ SUI:              -$%,.2f%n" +
                        "  NJ SDI:              -$%,.2f%n" +
                        "  NJ FLI:              -$%,.2f%n" +
                        "  ──────────────────────────────%n" +
                        "  Total Deducted:      -$%,.2f%n" +
                        "  Effective Rate:       %.1f%%%n" +
                        "  Take-Home (Annual):  $%,.2f%n" +
                        "  Take-Home (Weekly):  $%,.2f%n",
                annualGross, federal, nj, ss, medicare, sui, sdi, fli,
                totalTax(annualGross, annualPreTax) + annualPreTax,
                effRate, net, net / 52.0
        );
    }

    public static String getTaxBreakdown(double annualGross) {
        return getTaxBreakdown(annualGross, 0);
    }
}