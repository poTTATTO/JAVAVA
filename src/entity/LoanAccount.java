package entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanAccount extends Account{
	private LoanProduct product;
	private BigDecimal totalLoanAmount;
	private BigDecimal interestRate;
	private Account linkedAccount;
	
	public LoanAccount(String accountNo,String password, LoanProduct product, BigDecimal loanAmount, Account linkedAccount) {
		super(password, accountNo);
		this.product = product;
		this.totalLoanAmount = loanAmount;
		this.interestRate = product.getRate();
		this.balance = loanAmount.negate();
		this.linkedAccount = linkedAccount;
	}
	
	public void showTotalLoanAmount() {
		System.out.println(moneyFormat.format(totalLoanAmount));
	}
	public void showProductInfo() {
		System.out.println(product);
	}
	@Override
	public void deposit(BigDecimal amount) {
		if(this.balance.add(amount).compareTo(BigDecimal.ZERO)>0) {
			throw new RuntimeException("대출 원금보다 더 많이 상환할 수 없습니다.");
		}
		super.deposit(amount);
		System.out.println(amount+"원이 상환 되었습니다. 남은 대출금 :"+this.balance);
	}
	
	@Override
	public void withdraw(BigDecimal amount) {
		throw new RuntimeException("대출 계좌에서는 출금할 수 없습니다.(상환만 가능)");
	}
	
	public void payInterest() {
		BigDecimal interest = this.balance.abs().multiply(interestRate).setScale(0, RoundingMode.FLOOR);
		try {
			linkedAccount.withdraw(interest);			
		}catch (RuntimeException e) {
			System.out.println("이자 납부 실패: " + e.getMessage());
		}
	
	}
	
	public void payPrincipalFromLinkedAccount(BigDecimal amount) {
		linkedAccount.withdraw(amount);
		this.deposit(amount);
	}
}
