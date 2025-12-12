package entity;

public abstract class AbstractPerson {
	protected String name;
	protected String phoneNumber;
	protected String address;
	
	public AbstractPerson(String name, String phoneNumber, String address) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}
	
	public void updateName(String name) {
		this.name = name;
		System.out.println("이름 수정 완료 : " + this.name);
	}
	
	public void updatePhoneNumber(String phone) {
		this.phoneNumber = phone;
		System.out.println("전화번호 수정 완료 : " + this.phoneNumber);
	}
	
	public void updateAddress(String address) {
		this.address = address;
		System.out.println("주소 수정 완료 : " + this.address);
	}
	
	public abstract void showProfile();
	
}
