package entity;
import java.math.BigDecimal;

public enum SavingsProduct {
	NORMAL("일반", 3.0),
	VIP("VIP", 4.0),
	YOUTH("청년",5.0);
	
	private final String productName;
	private final BigDecimal rate;
	
	SavingsProduct(String name, double ratePercent){
		this.productName = name;
		this.rate = BigDecimal.valueOf(ratePercent).divide(BigDecimal.valueOf(100));
		
	}
	
	public BigDecimal getRate() {
		return rate;
	}
	
	public String getProductName() {
		return productName;
	}
}
