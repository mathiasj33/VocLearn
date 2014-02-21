package de.letorat.voclern.code;

import java.io.Serializable;

public class Card implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2526051651510336406L;
	private String question;
	private String awnser;
	private boolean doppelt;
	
	public Card(String question, String awnser, boolean d) {
		this.question = question;
		this.awnser = awnser;
		setDoppelt(d);
	}
	
	public void setQuestion(String q) {
		question = q;
	}
	
	public void setAwnser(String a) {
		awnser = a;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAwnser() {
		return awnser;
	}
	
	public Card invert() {
		return new Card(awnser, question, doppelt);
	}

	public boolean isDoppelt() {
		return doppelt;
	}

	public void setDoppelt(boolean doppelt) {
		this.doppelt = doppelt;
	}
}
