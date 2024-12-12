import java.sql.Date;

public class CompanyConfiguration {
    private int configurationId;
    private int companyId;
    private Date payrollStartDate;
    private Date payrollEndDate;

    // Getters and Setters
    public int getConfigurationId() { return configurationId; }
    public void setConfigurationId(int configurationId) { this.configurationId = configurationId; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public Date getPayrollStartDate() { return payrollStartDate; }
    public void setPayrollStartDate(Date payrollStartDate) { this.payrollStartDate = payrollStartDate; }
    public Date getPayrollEndDate() { return payrollEndDate; }
    public void setPayrollEndDate(Date payrollEndDate) { this.payrollEndDate = payrollEndDate; }
}

