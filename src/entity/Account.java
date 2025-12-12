package entity;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import interfaces.AccountOperationInterface;

public class Account implements AccountOperationInterface{
	
	protected final DecimalFormat moneyFormat = new DecimalFormat("#,###");
	protected String accountNo;
	protected BigDecimal balance = BigDecimal.ZERO;
	protected String password;
	public Account(String password,String accountNo) {
		if(!checkPassword(password)) {
			throw new IllegalArgumentException("최소 6자리 이상");
		}
		this.password = password;
		this.accountNo = accountNo;
	}
	
	private boolean checkPassword(String password) {
		if(password == null || password.length() < 6) {
			return false;
		}
		return true;
	}
	
	

	public String getPrettyBalance() {
		return moneyFormat.format(this.balance);
	}
	@Override
	public String getAccountNo() {
		return this.accountNo;
	}
	@Override
	public void deposit (BigDecimal amount) {
		this.balance = balance.add(amount);
	}
	@Override
	public void withdraw(BigDecimal amount) {
		
		if(this.balance.compareTo(amount)>=0) {
			System.out.println(moneyFormat.format(amount)+"출금 성공");
			balance = balance.subtract(amount);
		}else {
			System.out.println("잔액이 부족합니다.");
		}
	}
	
	@Override
	public void transfer( Account to, BigDecimal amount) {
		if(this.balance.compareTo(amount)>=0) {
			to.deposit(amount);
			this.balance = this.balance.subtract(amount);
			System.out.println(to.getAccountNo()+"계좌로 "+ moneyFormat.format(amount));
		}else {
			System.out.println("잔액이 부족합니다.");
		}
	}
	
	@Override
	public BigDecimal getBalance() {
		return this.balance;
	}
}
