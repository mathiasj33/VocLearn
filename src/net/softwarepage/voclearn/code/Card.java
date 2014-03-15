package net.softwarepage.voclearn.code;

import java.io.Serializable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Card implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2526051651510336406L;
	private transient StringProperty question;
	private String question_;
	private transient StringProperty anwser;
	private String anwser_;
	private transient BooleanProperty doppelt;
	private boolean doppelt_;
	
	public Card(String question, String anwser, boolean doppelt) {
		setQuestion(question);
		setAnwser(anwser);
		setDoppelt(doppelt);
	}
	
	public void setQuestion(String question) {
		question_ = question;
		this.question = new SimpleStringProperty(question);
	}
	
	public void setAnwser(String anwser) {
		anwser_ = anwser;
		this.anwser = new SimpleStringProperty(anwser);
	}
	
	public void setDoppelt(boolean doppelt) {
		doppelt_ = doppelt;
		this.doppelt = new SimpleBooleanProperty(doppelt);
	}
	
	public String getQuestion() {
		if(question == null) {
			question = new SimpleStringProperty(question_);
		}
		return question.get();
	}
	
	public String getAnwser() {
		if(anwser == null) {
			anwser = new SimpleStringProperty(anwser_);
		}
		return anwser.get();
	}
	
	public boolean isDoppelt() {
		if(doppelt == null) {
			doppelt = new SimpleBooleanProperty(doppelt_);
		}
		return doppelt.get();
	}
	
	public Card invert() {
		return new Card(getAnwser(), getQuestion(), isDoppelt());
	}
	
	public StringProperty questionProperty() {
		return question;
	}
	
	public StringProperty anwserProperty() {
		return anwser;
	}
	
	public BooleanProperty doppeltProperty() {
		return doppelt;
	}
}
