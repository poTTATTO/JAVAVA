package entity;
import java.math.BigDecimal;

public enum LoanProduct {
	NORMAL("일반", "3.0"),
	VIP("VIP", "2.5"),
	YOUTH("청년","2.0");
	
	private final String productName;
	private final BigDecimal rate;
	
	LoanProduct(String name, String ratePercent){
		this.productName = name;
		this.rate = new BigDecimal(ratePercent).divide(BigDecimal.valueOf(100));
		
	}
	
	public BigDecimal getRate() {
		return rate;
	}
	
	public String getProductName() {
		return productName;
	}
}
