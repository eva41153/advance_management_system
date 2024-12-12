import java.sql.Date;

public class AdvanceSalaryRequest {
    private int advanceId;
    private int employeeId;
    private int companyId;
    private double balance;
    private double advanceAmount;
    private Date requestDate;
    private String status;

    // Getters and Setters
    public int getAdvanceId() { return advanceId; }
    public void setAdvanceId(int advanceId) { this.advanceId = advanceId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(double advanceAmount) { this.advanceAmount = advanceAmount; }
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

