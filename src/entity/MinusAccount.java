package entity;
import java.math.BigDecimal;

public class MinusAccount extends Account{

	private BigDecimal limitAmount;
	
	public MinusAccount(String accountNo,String password, BigDecimal limitAmount) {
		super(password, accountNo);
		this.limitAmount = limitAmount;
	}
	
	
	@Override
	public void withdraw(BigDecimal amount) {
		
		BigDecimal potentialBalance = balance.subtract(amount);
		
		if(potentialBalance.compareTo(limitAmount.negate())<0) {
			throw new RuntimeException("마이너스 한도 초과입니다.");
		}
		balance = balance.subtract(amount);
	}
	
}
