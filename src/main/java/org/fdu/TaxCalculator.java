package org.fdu;

/**
 * Calculates NJ paycheck deductions for a standardized estimate.
 * Federal withholding uses IRS Publication 15-T (2026) Percentage Method,
 * Single filer, Standard W-4 (Step 2 box unchecked), weekly annualized.
 * Applied to (gross - 401K contribution) since 401K is pre-tax for federal.
 * NJ deductions included:
 *   - NJ State Income Tax       (progressive brackets, 2026)
 *   - NJ SUI  State Unemployment Insurance  0.4250%  (wage base $44,800)
 *   - NJ SDI  State Disability Insurance    0.1900%  (wage base $171,000)
 *   - NJ FLI  Paid Family Leave Insurance   0.2300%  (wage base $171,000)
 * FICA (2026): Social Security 6.2% (wage base $184,500) + Medicare 1.45%
 * Note: SS, Medicare, and NJ taxes are applied to full gross —
 *       401K does NOT reduce them.
 * Limitation: Federal estimate assumes a standard W-4 with no extra allowances
 * or deductions in Steps 3/4. Actual federal withholding may be lower if your
 * W-4 claims dependents or additional deductions. Results are a reliable
 * planning baseline for comparing jobs.
 * Sources:
 *   - IRS Publication 15-T (2026)
 *   - NJ Division of Taxation (2026)
 *   - NJ Department of Labor — 2026 UI/DI/FLI rates
 */
public class TaxCalculator {

    // ── IRS Pub 15-T 2026: Percentage Method, Single, Standard W-4 ───────────
    // Annualized wages are looked up, then divided by WEEKS_PER_YEAR for weekly.
    // 4.2 weeks/month × 12 months = 50.4 weeks/year (more accurate than 52).
    public static final double WEEKS_PER_YEAR = 50.4;
    // Table columns: [annualFloor, baseTax, marginalRate]
    // Source: IRS Pub 15-T (2026), Table for Single / Standard Withholding
    private static final double[][] FEDERAL_ANNUAL_TABLE_SINGLE = {
            {      0,        0.00, 0.00 },
            {   6300,        0.00, 0.10 },   // 10% bracket starts at $6,300
            {  18600,     1230.00, 0.12 },   // 12% bracket
            {  50850,     5100.00, 0.22 },   // 22% bracket
            { 100375,    15956.00, 0.24 },   // 24% bracket
            { 199150,    39700.00, 0.32 },   // 32% bracket
            { 248900,    55564.00, 0.35 },   // 35% bracket
            { 621050,   185777.50, 0.37 },   // 37% bracket
    };

    // Standard deduction adjustment for Single filer (2026): $8,600 annually
    // The percentage method subtracts this before applying brackets.
    private static final double FEDERAL_STD_DEDUCTION_SINGLE = 8600.0;

    // ── NJ State Income Tax Brackets (2026, Single, Annual) ──────────────────
    // NJ brackets are unchanged from 2024/2025 — rates are set by statute.
    private static final double[][] NJ_BRACKETS = {
            {  20000,            0.0140 },
            {  35000,            0.0175 },
            {  40000,            0.0350 },
            {  75000,            0.05525},
            { 500000,            0.0637 },
            {1000000,            0.0897 },
            {Double.MAX_VALUE,   0.1075 }
    };

    // ── FICA (2026) ───────────────────────────────────────────────────────────
    private static final double SS_RATE       = 0.0620;
    private static final double SS_WAGE_BASE  = 184500.0;   // up from $168,600 in 2024
    private static final double MEDICARE_RATE = 0.0145;

    // ── NJ Additional Payroll Taxes (2026) ────────────────────────────────────
    private static final double NJ_SUI_RATE       = 0.00425;  // unchanged, wage base $44,800
    private static final double NJ_SUI_WAGE_BASE  = 44800.0;  // up from $42,300 in 2024
    private static final double NJ_SDI_RATE       = 0.00190;  // unchanged, wage base $171,000
    private static final double NJ_SDI_WAGE_BASE  = 171000.0;
    private static final double NJ_FLI_RATE       = 0.00230;  // down from 0.228% — now 0.23%
    private static final double NJ_FLI_WAGE_BASE  = 171000.0;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Federal withholding for one weekly paycheck (2026 Pub. 15-T Percentage Method).
     * Annualizes the weekly gross, subtracts the standard deduction, applies brackets,
     * then divides result back to weekly.
     * preTaxWeekly = 401K or other pre-tax deductions for the week.
     * 401K reduces federal taxable income but NOT SS/Medicare/NJ taxes.
     */
    public static double federalWithholdingWeekly(double weeklyGross, double preTaxWeekly) {
        double annualTaxable = Math.max((weeklyGross - preTaxWeekly) * WEEKS_PER_YEAR
                - FEDERAL_STD_DEDUCTION_SINGLE, 0);
        double annualTax = 0;
        for (int i = FEDERAL_ANNUAL_TABLE_SINGLE.length - 1; i >= 0; i--) {
            if (annualTaxable > FEDERAL_ANNUAL_TABLE_SINGLE[i][0]) {
                annualTax = FEDERAL_ANNUAL_TABLE_SINGLE[i][1]
                        + (annualTaxable - FEDERAL_ANNUAL_TABLE_SINGLE[i][0])
                        * FEDERAL_ANNUAL_TABLE_SINGLE[i][2];
                break;
            }
        }
        return annualTax / WEEKS_PER_YEAR;
    }

    /** Federal withholding with no pre-tax deductions. */
    public static double federalWithholdingWeekly(double weeklyGross) {
        return federalWithholdingWeekly(weeklyGross, 0);
    }

    /** Annual federal tax. */
    public static double federalTax(double annualGross) {
        return federalWithholdingWeekly(annualGross / WEEKS_PER_YEAR) * WEEKS_PER_YEAR;
    }

    /** Annual federal tax with annual pre-tax deductions (e.g. 401K). */
    public static double federalTax(double annualGross, double annualPreTax) {
        return federalWithholdingWeekly(annualGross / WEEKS_PER_YEAR, annualPreTax / WEEKS_PER_YEAR) * WEEKS_PER_YEAR;
    }

    // ── NJ State Income Tax ───────────────────────────────────────────────────

    /** Annual NJ state income tax. 401K does NOT reduce NJ taxable income. */
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

    /** Annual Social Security tax (capped at $184,500 wage base for 2026). */
    public static double socialSecurityTax(double annualGross) {
        return Math.min(annualGross, SS_WAGE_BASE) * SS_RATE;
    }

    /** Annual Medicare tax (no wage cap). */
    public static double medicareTax(double annualGross) {
        return annualGross * MEDICARE_RATE;
    }

    // ── NJ Additional Taxes ───────────────────────────────────────────────────

    /** Annual NJ SUI — 0.425% up to $44,800 wage base (2026). */
    public static double njSuiTax(double annualGross) {
        return Math.min(annualGross, NJ_SUI_WAGE_BASE) * NJ_SUI_RATE;
    }

    /** Annual NJ SDI — 0.19% up to $171,000 wage base (2026). */
    public static double njSdiTax(double annualGross) {
        return Math.min(annualGross, NJ_SDI_WAGE_BASE) * NJ_SDI_RATE;
    }

    /** Annual NJ FLI — 0.23% up to $171,000 wage base (2026). */
    public static double njFliTax(double annualGross) {
        return Math.min(annualGross, NJ_FLI_WAGE_BASE) * NJ_FLI_RATE;
    }

    // ── Totals ────────────────────────────────────────────────────────────────

    /** Total annual taxes including all 7 deductions. */
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

    /** Annual take-home after all taxes and pre-tax deductions. */
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
        double annual   = weeklyGross * WEEKS_PER_YEAR;
        double federal  = federalWithholdingWeekly(weeklyGross, weeklyPreTax);
        double nj       = njStateTax(annual)        / WEEKS_PER_YEAR;
        double ss       = socialSecurityTax(annual) / WEEKS_PER_YEAR;
        double medicare = medicareTax(annual)       / WEEKS_PER_YEAR;
        double sui      = njSuiTax(annual)          / WEEKS_PER_YEAR;
        double sdi      = njSdiTax(annual)          / WEEKS_PER_YEAR;
        double fli      = njFliTax(annual)          / WEEKS_PER_YEAR;
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
                "=== Pay Stub Estimate (NJ, Single Filer, Standard W-4, 2026) ===%n" +
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
                effRate, net, net / WEEKS_PER_YEAR
        );
    }

    public static String getTaxBreakdown(double annualGross) {
        return getTaxBreakdown(annualGross, 0);
    }
}