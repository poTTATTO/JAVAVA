package interfaces;
import java.math.BigDecimal;

import entity.Account;

public interface AccountOperationInterface {
	 public String getAccountNo();
	 public void deposit (BigDecimal amount);
	 public void withdraw(BigDecimal amount);
	 public void transfer(Account to, BigDecimal amount);
	 public BigDecimal getBalance ();	 
}