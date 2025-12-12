package entity;

import java.util.HashMap;
import java.util.Map;

public class Customer extends AbstractPerson{
	private static int numOfCustomer=0;
	private final int customerId;
	private int creditScore;
	private String password;
	private Map<String, Account> accounts = new HashMap<>();
	
	public Customer(String name, String phoneNumber, String address, int creditScore,String password) {
		super(name, phoneNumber, address);
		this.customerId = ++numOfCustomer;
		this.creditScore = creditScore;
		this.password = password;
		this.accounts = new HashMap<>();
	}
	
	public boolean matchPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
	
	
	public CreditRating getCreditRating() {
		return CreditRating.getRatingByScore(this.creditScore);
	}
	public void addAccount(Account account) {
		this.accounts.put(account.getAccountNo(), account);
	}
	
	public void removeAccount(String AccountNo) {
		accounts.remove(AccountNo);
	}
	public int getCustomerId() {
		return customerId;
	}
	public String getName() {
		return name;
	}
	public Map<String, Account> getAccounts(){
		return this.accounts;
	}
	public Account getAccount(String accountNo) {
		return this.accounts.get(accountNo);
	}
	
	@Override
	public void showProfile() {
		System.out.println("=== 고객 프로필 ===");
		System.out.println("ID: " + customerId);
		System.out.println("이름: "+ name);
		System.out.println("전화: " +phoneNumber);
		System.out.println("주소: "+address);
		System.out.println("신용점수: "+creditScore);
		System.out.println("신용등급: "+ CreditRating.getRatingByScore(creditScore));
		System.out.println("계좌 수: "+accounts.size() + "개");
		System.out.println("==============");
	}
}
