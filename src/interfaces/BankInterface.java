package interfaces;
import java.math.BigDecimal;

import entity.Account;

public interface BankInterface {
	public void openAccount(String Id,Account account);
	public void closeAccount(String customerId, String accountNo);
	public void printBalance();
	public Account getAccount(String accountNo);
	public BigDecimal getBalance();
	public void addTotalBalance (BigDecimal amount);
	public void subTotalBalance( BigDecimal amount ); 
}

