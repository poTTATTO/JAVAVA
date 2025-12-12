package entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class SavingAccount extends Account {
	private String productName;
    private LocalDate maturityDate;
    private BigDecimal interestRate;
    private boolean isSettled;
	private Account linkedAccount;
    
    public SavingAccount(SavingsProduct product,String accountNo,String password, BigDecimal amount,int periodMonths,Account linkedAccount) {
    	super(password, accountNo);
    	this.productName = product.getProductName();
    	this.interestRate = product.getRate();
    	this.maturityDate = LocalDate.now().plusMonths(periodMonths);
    	this.isSettled = false;
    	this.balance = amount;
    }
    
    @Override
    public void deposit(BigDecimal amount) {
    	throw new RuntimeException("정기 예금은 추가 임금을 할 수 없습니다.");
    }
    
    public void maturitySettlement() {
    	if(isSettled) {
    		throw new RuntimeException("이미 정산된 계좌입니다.");
    	}
    	if(LocalDate.now().isBefore(maturityDate)) {
    		throw new RuntimeException("만기일이 지나지 않았습니다.");
    	}
    	BigDecimal interest = this.balance.multiply(interestRate)
                .setScale(0, RoundingMode.FLOOR);

    	this.balance = this.balance.add(interest);
		BigDecimal totalAmount = this.balance;
		this.transfer(linkedAccount, totalAmount);
    	this.isSettled = true;
	
    	System.out.println("예금 만기! " + productName);
    	System.out.println("원금 " + (balance.subtract(interest)) + "원에 이자 " + interest + "원이 더해졌습니다.");
		System.out.println("연결 계좌(" + linkedAccount.getAccountNo() + ")로 자동 송금되었습니다.");
    }
    
    @Override
    public void withdraw(BigDecimal amount) {
    	if(!isSettled && LocalDate.now().isBefore(maturityDate)) {
    		throw new RuntimeException("만기 전에는 출금할 수 없습니다.");
    	}
    	
    	super.withdraw(amount);
    }
}
