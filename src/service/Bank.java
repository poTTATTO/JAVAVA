package service;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import entity.*;
import interfaces.BankInterface;

public class Bank implements BankInterface {
	
	private final DecimalFormat moneyFormat = new DecimalFormat("#,###");
	private BigDecimal total_balance = new BigDecimal("1000000000000000000000000000");
	private Map<String, Account> allAccounts = new HashMap<>();
	private Map<String, Customer> allCustomers = new HashMap<>();
	
	public void registerCustomer(Customer customer) {
		if(allCustomers.containsKey(Integer.toString(customer.getCustomerId()))) {
			throw new RuntimeException("이미 등록된 고객 ID입니다.");
		}
		allCustomers.put(Integer.toString(customer.getCustomerId()), customer);
		System.out.println("고객 등록 완료: "+customer.getName());
	}
	
	public Customer getCustomer(String customerId){
		if(!allCustomers.containsKey(customerId)){
			throw new RuntimeException("고객 정보를 찾을 수 없습니다.");
		}
		return allCustomers.get(customerId);
	}
	
	public boolean chekCustomer(String name) {
		if(!allCustomers.containsValue(name)) {
			throw new IllegalArgumentException("고객이 없습니다.");
		}
		
		return true;
		
	}
	
	@Override
	public void openAccount(String customerId, Account newAccount) {
		Customer customer = getCustomer(customerId);
		
		if(allAccounts.containsKey(newAccount.getAccountNo())) {
			throw new RuntimeException("이미 존재하는 계좌번호 입니다.");
		}
		
		allAccounts.put(newAccount.getAccountNo(), newAccount);
		customer.addAccount(newAccount);

		if (newAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
			this.addTotalBalance(newAccount.getBalance());
		}
		System.out.println("계좌 개설 완료["+newAccount.getAccountNo() + "]");
		System.out.println("소유주 : "+customer.getName());
	}
	
	@Override
	public void closeAccount(String customerId, String accountNo) {
		if(!allCustomers.containsKey(customerId)) {
			throw new IllegalArgumentException("존재하지 않는 고객입니다.");
		}
		if(!allAccounts.containsKey(accountNo)) {
			throw new IllegalArgumentException("존재하지 않는 계좌번호입니다.");
		}
		
		Customer customer = allCustomers.get(customerId);
		Account targetAccount = allAccounts.get(accountNo);
		
		if(customer.getAccount(accountNo) ==null) {
			throw new IllegalArgumentException("해당 계좌는 고객 소유가 아닙니다.");
		}
		
		if(targetAccount.getBalance().compareTo(BigDecimal.ZERO)!=0) {
			throw new IllegalArgumentException("계좌 폐쇄 실패 : 잔액이 남아있습니다.\n"+"현재 잔액 : "+targetAccount.getBalance()+"원\n"+"전액을 출금하거나 이체한 뒤 시도하세요.");
		}
		
		customer.removeAccount(accountNo);
		allAccounts.remove(accountNo);
		System.out.println("계좌 폐쇄 완료\n"+"[계좌번호] : "+accountNo+"\n"+"[소유주] :"+customer.getName());
	}
	
	public String getPrettyBalance() {
		return moneyFormat.format(this.total_balance);
	}
	@Override
	public void printBalance() {
		System.out.println("은행 총 보유 자산 : " + getPrettyBalance() + "원");
	}
	
	@Override
	public Account getAccount(String accountNo) {
		return allAccounts.get(accountNo);
		
	}
	
	@Override
	public BigDecimal getBalance() {
		return total_balance;
	}
	
	@Override
	public void addTotalBalance (BigDecimal amount) {
		total_balance = total_balance.add(amount);
	}
	public void subTotalBalance(BigDecimal amount ) {
		total_balance = total_balance.subtract(amount);
	} 
	
	public void openMinusAccount(String customerId) {
		Scanner sc = new Scanner(System.in);
		Customer customer = getCustomer(customerId);
		CreditRating rating = customer.getCreditRating();
		BigDecimal limitAmount;
		
		if(rating == CreditRating.GRADE_1) {
			limitAmount= new BigDecimal("1500");
		}else if(rating == CreditRating.GRADE_2) {
			limitAmount= new BigDecimal("1000");
		}else {
			sc.close();
			throw new IllegalArgumentException("마이너스 통장 개설 불가 : 신용등급이 낮습니다.\n"+"(현재 등급 : "+rating+")");
		}
		
		System.out.print("계좌번호를 입력해주세요( ' - ' 제외) : ");
		String accountNo = sc.nextLine();
		System.out.print("비밀번호를 입력해주세요( 7자리 이상 )");
		String password = sc.nextLine();
		openAccount(customerId, new MinusAccount(accountNo, password, limitAmount));
		System.out.println("마이너스 통장 개설 완효! (한도 : "+moneyFormat.format(limitAmount)+"원");
		
		
	}
	
	public void openLoanAccount(String customerId, BigDecimal requestedAmount, String linkedAccountNo,LoanProduct product) {
		Customer customer = getCustomer(customerId);
		
		if(customer.getAccount(linkedAccountNo) == null) {
			throw new IllegalArgumentException("본인 명의의 계좌만 연결할 수 있습니다.");
		}
		if(!allAccounts.containsKey(linkedAccountNo)) {
			throw new IllegalArgumentException("연결할 출금 계좌가 존재하지 않습니다.");
		}
		
		Account linkedAccount = allAccounts.get(linkedAccountNo);
		
		if(linkedAccount instanceof LoanAccount) {
			throw new IllegalArgumentException("대출 계좌를 연결계좌로 사용할 수 없습니다.");
		}else if(linkedAccount instanceof SavingAccount) {
			throw new IllegalArgumentException("적금 계좌를 연결계좌로 사용할 수 없습니다.");
		}else if(linkedAccount instanceof InstallmentSavingsAccount){
			throw new IllegalArgumentException("예금 계좌를 연결계좌로 사용할 수 없습니다.");
		}
		
		CreditRating rating = customer.getCreditRating();
		BigDecimal maxLimit;
		switch (rating) {
        case GRADE_1: 
            maxLimit = new BigDecimal("100000000");
            break;
        case GRADE_2: 
            maxLimit = new BigDecimal("50000000");
            break;
        default:    
            throw new IllegalArgumentException("대출 반려: 3등급 이하는 대출이 불가능합니다. (현재: " + rating + ")");
		}		
		
		if (requestedAmount.compareTo(maxLimit) > 0) {
            throw new IllegalArgumentException(
                "대출 반려: 한도 초과입니다. \n" +
                "고객 등급: " + rating + "\n" +
                "최대 한도: " + maxLimit + "원\n" +
                "신청 금액: " + requestedAmount + "원"
            );
        }
		Scanner sc = new Scanner(System.in);
		
		System.out.print("계좌번호를 입력해주세요( ' - ' 제외) : ");
		String accountNo = sc.nextLine();
		System.out.print("비밀번호를 입력해주세요( 7자리 이상 )");
		String password = sc.nextLine();
		
	
		
		LoanAccount newAccount = new LoanAccount(accountNo,password,product,requestedAmount,linkedAccount);
		openAccount(customerId,newAccount);
		this.subTotalBalance(requestedAmount);
		linkedAccount.deposit(requestedAmount);

		System.out.println("대출 계좌 개설 완료! [상품 : " + product.getProductName()+", 이자율 : "+product.getRate()+")");
	}
	
	public void depositToAccount(String accountNo, BigDecimal amount) {
		Account account = getAccount(accountNo);
		if(account!=null) {
			account.deposit(amount);
			this.addTotalBalance(amount);
		}
	}
	
	public void withdrawFromAccount(String accountNo, BigDecimal amount) {
		if(this.total_balance.compareTo(amount)<0) {
			throw new RuntimeException("은행 지급 준비금이 부족하여 출금할 수 없습니다.");
		}
		Account account = getAccount(accountNo);
		if(account!=null) {
			account.withdraw(amount);
			this.subTotalBalance(amount);
		}
		
	}
	
}
