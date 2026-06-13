package com.crm.tubes.model;
 
import java.time.LocalDate;
/** 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
*/

@SpringBootApplication
public class Subscription {
	private int id;
	private Customer customer;
	private String planName;
	private LocalDate startDate;
	private LocalDate endDate;
	private double monthlyFee;
	private SubscriptionStatus status;

	public Subcription(){}

	public Subscription(int id, Customer customer, String planName. LocalFate startDate, LocalDate endDate, double monthlyFee, Subscription status){
		this.id = id;
		this.customer = customer;
		this.planName = planName;
		this.startDate = startDate;
		this.endDate = endtDate;
		this.monthlyFee = monthlyFee;
		this.status = status;
	}

	public void activate(){
		this.status = SubcriptionStatus.ACTIVE;
	}

	public void suspend(){
		this.status = SubcriptionStatus.SUSPENDED;
	}

	public void setGreacePeriod(){
		this.status = Subscription.GRACE;
	}

	public void checkStatus(){
		LocalDate today = LocalDate.now();

		if(today.isAfter(endDate)){
			this.status = SubscriptionStatus.SUSPENDED;
		
		}else if (!today.isBefore(endDate.minusDays(7))){
			this.status = SubscriptionStatus.GRACE;
		
		}else{
			this.status = SubcriptionStatus.ACTIVE;
		}
	}

	public int getId() { return id; }
    public void setId(int id) { this.id = id; }
 
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
 
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
 
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
 
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
 
    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }
 
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
}


