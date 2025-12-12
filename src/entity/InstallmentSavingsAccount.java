package entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class InstallmentSavingsAccount extends Account{

	private LocalDate maturityDate;
	private BigDecimal monthlyAmount;
	private BigDecimal interestRate;
	private String productName;
	private boolean isSettled;
	private Account linkedAccount;
	
	
	public InstallmentSavingsAccount(String accountNo,String password, SavingsProduct product, BigDecimal monthlyAmount, int periodMonths,Account linkedAccount) {
		super(password,accountNo);
		this.monthlyAmount = monthlyAmount;
		this.maturityDate = LocalDate.now().plusMonths(periodMonths);
		this.productName = product.getProductName();
		this.interestRate = product.getRate();
		this.isSettled = false;
	}
	
	@Override
	public void deposit(BigDecimal amount) {
		if(amount.compareTo(this.monthlyAmount)!=0) {
			throw new RuntimeException("적금 입금액은 약정된 금액(" + monthlyAmount +"원)과 같아야 합니다.");
		}
		super.deposit(amount);
	}
	
	public void maturitySettlement() {
		if(this.isSettled) {
			throw new RuntimeException("이미 만기 정산이 완료된 계좌입니다.");
		}
		if(LocalDate.now().isBefore(maturityDate)) {
			throw new RuntimeException("만기일이 되지 않았습니다.");
		}
		
		BigDecimal interest = this.balance.multiply(interestRate)
                .setScale(0, RoundingMode.FLOOR);
		
		this.balance = this.balance.add(interest);
		BigDecimal totalAmount = this.balance;
		this.transfer(linkedAccount, totalAmount);
		this.isSettled = true;

		System.out.println("만기 축하합니다! 상품명 : " + productName);
		System.out.println("이자 " + interest + "원이 지급되었습니다.");
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
