package entity;

public enum CreditRating {
	GRADE_1,
	GRADE_2,
	GRADE_3,
	GRADE_4,
	GRADE_5;
	
	public static CreditRating getRatingByScore(int score) {
		if(score>=400) return GRADE_1;
		else if(score>=300) return GRADE_2;
		else if(score>=200) return GRADE_3;
		else if(score>=100) return GRADE_4;
		else return GRADE_5;
	}
}
