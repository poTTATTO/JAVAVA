package main;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import entity.*;
import service.Bank;

public class Main {
    
    private static Bank bank = new Bank();
    private static Scanner sc = new Scanner(System.in);
    private static Map<String, String> accountPasswordStore = new HashMap<>(); 
    private static Customer currentCustomer = null; //로그인 상태 확인
    
    public static void main(String[] args) {
        
        initializeCustomer();
        printHeader();
        
        while(true) {            
            if (currentCustomer == null) {
                System.out.println("\n========= [로그인] =========");
                System.out.print("고객 번호(ID) 입력 (종료: q): ");
                String idInput = sc.next();
                
                if(idInput.equalsIgnoreCase("q")) {
                    System.out.println("시스템을 종료합니다.");
                    break;
                }
                
                System.out.print("로그인 비밀번호 입력: ");
                String pwInput = sc.next();
                
                try {
                    Customer tempCustomer = bank.getCustomer(idInput);
                    if (tempCustomer.matchPassword(pwInput)) {             
                        currentCustomer = tempCustomer;
                        System.out.println("\n>>> 로그인 성공! 환영합니다, " + currentCustomer.getName() + "님.");
                        System.out.println(">>> 신용등급: " + currentCustomer.getCreditRating());
                    } else {
                        System.out.println("!!! 비밀번호가 틀렸습니다.");
                    }
                    
                } catch (RuntimeException e) {
                    System.out.println("!!! 존재하지 않는 고객 ID입니다.");
                }   
            } 
            else {
                processMenu();
            }
        }
    }
        
    private static void processMenu() {
        printMenu();
        String choice = sc.next();
        try {
            switch (choice) {
                case "1": uiOpenAccount(); break;
                case "2": uiDeposit(); break;
                case "3": uiWithdraw(); break;
                case "4": uiTransfer(); break;
                case "5": uiCloseAccount(); break;
                case "6": uiCheckBalance(); break;
                case "7": 
                    System.out.println(">>> [" + currentCustomer.getName() + "]님 로그아웃 되었습니다.");
                    currentCustomer = null; // 로그아웃 처리
                    break;
                default: System.out.println("잘못된 입력입니다.");
            }
        } catch (Exception e) {
            System.out.println("[오류] " + e.getMessage());
        }
    }
    
    private static void uiOpenAccount() {
        System.out.println("\n--- [계좌 개설] ---");
        System.out.println("1.일반통장 2.마이너스 3.정기예금 4.정기적금 5.대출");
        System.out.print("선택 >> ");
        String type = sc.next();
        
        System.out.print("사용할 [계좌번호] 입력: ");
        String accountNo = sc.next();
        System.out.print("계좌 [비밀번호] 설정(6자리): ");
        String password = sc.next();
        
        String customerId = String.valueOf(currentCustomer.getCustomerId());

        try {
            switch (type) {
                case "1": 
                    bank.openAccount(customerId, new Account(password, accountNo));
                    System.out.println(">> [일반 통장] 개설 완료!");
                    break;
                    
                case "2": 
                    CreditRating rating = currentCustomer.getCreditRating();
                    BigDecimal limit = BigDecimal.ZERO;
                 
                    if(rating == CreditRating.GRADE_1) 
                    	limit = new BigDecimal("15000000");
                    else if(rating == CreditRating.GRADE_2) 
                    	limit = new BigDecimal("10000000");
                    else {
                        System.out.println("신용등급 부족으로 개설 불가 (등급: " + rating + ")");
                        return;
                    }
                    bank.openAccount(customerId, new MinusAccount(accountNo, password, limit));
                    System.out.println(">> [마이너스 통장] 개설 완료! (한도: " + limit + ")");
                    break;
                    
                case "3": // 예금
                    System.out.print("거치 금액: ");
                    BigDecimal saveAmt = new BigDecimal(sc.next());
                    System.out.print("만기 시 입금받을 계좌번호: ");
                    String linkedAccountNo1 = sc.next();
                    bank.openAccount(customerId, new SavingAccount(SavingsProduct.NORMAL, accountNo, password, saveAmt, 12,currentCustomer.getAccount(linkedAccountNo1)));
                    System.out.println(">> [정기 예금] 개설 완료!");
                    break;
                    
                case "4": // 적금
                    System.out.print("월 납입액: ");
                    BigDecimal monthly = new BigDecimal(sc.next());
                    System.out.print("만기 시 입금받을 계좌번호: ");
                    String linkedAccountNo = sc.next();
                    bank.openAccount(customerId, new InstallmentSavingsAccount(accountNo, password, SavingsProduct.NORMAL, monthly, 24,currentCustomer.getAccount(linkedAccountNo)));
                    System.out.println(">> [정기 적금] 개설 완료!");
                    break;
                    
                case "5": // 대출
                    System.out.print("대출 신청액: ");
                    BigDecimal loanAmt = new BigDecimal(sc.next());
                    System.out.print("연결할 계좌번호: ");
                    String linkNo = sc.next();
                    Account linkAcc = bank.getAccount(linkNo);
                    if(linkAcc == null) { System.out.println("연결 계좌 없음"); return; }
                    
                    bank.openAccount(customerId, new LoanAccount(accountNo, password, LoanProduct.NORMAL, loanAmt, linkAcc));
                    System.out.println(">> [대출 계좌] 개설 완료!");
                    break;
            }
            accountPasswordStore.put(accountNo, password);
            
        } catch (Exception e) {
            System.out.println("개설 실패: " + e.getMessage());
        }
    }

    private static void uiDeposit() {
        System.out.print("입금할 [계좌번호]: ");
        String accountNo = sc.next();
        if(bank.getAccount(accountNo) == null) {
            System.out.println("존재하지 않는 계좌입니다."); return;
        }
        System.out.print("입금액: ");
        BigDecimal amount = new BigDecimal(sc.next());
        bank.depositToAccount(accountNo, amount);
        System.out.println("입금 완료. 잔액: " + bank.getAccount(accountNo).getPrettyBalance());
    }

    // 3. 출금
    private static void uiWithdraw() {
        System.out.print("출금할 [계좌번호]: ");
        String accountNo = sc.next();
        if(!checkAccountPassword(accountNo)) return; 

        System.out.print("출금액: ");
        BigDecimal amount = new BigDecimal(sc.next());
        bank.withdrawFromAccount(accountNo, amount);
        System.out.println("출금 완료. 잔액: " + bank.getAccount(accountNo).getPrettyBalance());
    }

    // 4. 이체
    private static void uiTransfer() {
        System.out.print("내 [계좌번호]: ");
        String myNo = sc.next();
        if(!checkAccountPassword(myNo)) return;

        System.out.print("상대방 [계좌번호]: ");
        String targetNo = sc.next();
        if(bank.getAccount(targetNo) == null) {
            System.out.println("상대 계좌 없음."); return;
        }

        System.out.print("이체할 금액: ");
        BigDecimal amt = new BigDecimal(sc.next());
        bank.getAccount(myNo).transfer(bank.getAccount(targetNo), amt);
    }

    // 5. 계좌 해지
    private static void uiCloseAccount() {
        System.out.print("해지할 [계좌번호]: ");
        String accountNo = sc.next();
        if(!checkAccountPassword(accountNo)) return;
        
        Account account = bank.getAccount(accountNo);
        if(account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("미상환 금액(" + account.getBalance().abs() + "원)이 있습니다.");
            System.out.print("지금 상환하시겠습니까? (1.예 2.아니오): ");
            
            if(sc.next().equals("1")) {
                System.out.print("입금액: ");
                bank.depositToAccount(accountNo, new BigDecimal(sc.next()));
                
                if(account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("상환 부족으로 해지 불가."); return;
                }
                
            } else {
                return;
            }
        }
        
        bank.closeAccount(String.valueOf(currentCustomer.getCustomerId()), accountNo);
        accountPasswordStore.remove(accountNo);
    }

    // 6. 잔액 확인
    private static void uiCheckBalance() {
        System.out.print("확인할 [계좌번호]: ");
        String accountNo = sc.next();
        if(!checkAccountPassword(accountNo)) return;
        System.out.println("현재 잔액: " + bank.getAccount(accountNo).getPrettyBalance());
    }
    
    private static boolean checkAccountPassword(String accountNo) {
        System.out.print("계좌 비밀번호: ");
        String input = sc.next();
        String real = accountPasswordStore.get(accountNo);
        
        if(real == null) {
            System.out.println("존재하지 않는 계좌이거나 비밀번호가 설정되지 않았습니다.");
            return false;
        }
        if(!real.equals(input)) {
            System.out.println("비밀번호 불일치!");
            return false;
        }
        return true;
    }

    private static void initializeCustomer() {
        try {
            bank.registerCustomer(new Customer("김철수", "010-1111-1111","서울",450,"10290")); // ID: 1
            bank.registerCustomer(new Customer("이영희", "010-2222-2222", "마포", 300,"10291")); // ID: 2
            bank.registerCustomer(new Customer("박민수", "010-3333-3333", "성남", 200,"10292")); // ID: 3
            bank.registerCustomer(new Customer("박철현", "010-4444-4444", "용인", 150,"10293")); // ID: 4
            bank.registerCustomer(new Customer("김민수", "010-5555-5555", "진주", 99,"10294")); // ID: 5
            
            System.out.println("====== [시스템] 고객 5명 등록 완료 ======");
            
        } catch(Exception e) {
            System.out.println("고객 등록 오류 : "+e.getMessage());
        }
    }
    
    private static void printHeader() {
        System.out.println("\n☆★☆★ㅇㅇ은행에 오신 걸 환영합니다 ☆★☆★");
    }
    
    private static void printMenu() {
        System.out.println("\n-------------------------");
        System.out.println("1. 계좌 개설");
        System.out.println("2. 입금");
        System.out.println("3. 출금");
        System.out.println("4. 이체");
        System.out.println("5. 계좌 해지");
        System.out.println("6. 잔금확인");
        System.out.println("7. 취소(로그아웃)");
        System.out.print("이용하려고 하시는 기능의 번호를 입력해주세요: ");
    }
}