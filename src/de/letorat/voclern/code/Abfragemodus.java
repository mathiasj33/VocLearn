package de.letorat.voclern.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Abfragemodus {
	
	private Button laden;
	private Button button;
	private ArrayList<Card> cards = new ArrayList<Card>();
	private TextArea frageArea;
	private TextArea antwortArea;
	private Card currentCard;
	private Label uebrigLabel;
	private int uebrig = 0;
	
	public Tab buildAbfrageGui() {
		
        Tab tab = new Tab("Knowledge testing");
        uebrigLabel = new Label("Remaining cards: /");
		
        laden = new Button("Load a set of cards");
		Label frage = new Label("Question:");
		frageArea = new TextArea();
		Label antwort = new Label("Awnser:");
		antwortArea = new TextArea();
		button = new Button("Check");
		
		frageArea.setWrapText(true);
		antwortArea.setWrapText(true);
		frageArea.setEditable(false);
		antwortArea.addEventFilter(KeyEvent.KEY_PRESSED, new antwortHandler());
		
		button.setPrefWidth(160);
		laden.setPrefWidth(160);
		laden.setOnAction(new buttonHandler());
		button.setOnAction(new buttonHandler());
		
		HBox hbox = new HBox(30);
		hbox.getChildren().addAll(button,uebrigLabel);
		hbox.setAlignment(Pos.CENTER);
		
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10,0,10,0));
		vbox.getChildren().add(laden);
		vbox.getChildren().add(frage);
		vbox.getChildren().add(frageArea);
		vbox.getChildren().add(antwort);
		vbox.getChildren().add(antwortArea);
		vbox.getChildren().add(hbox);
		vbox.setAlignment(Pos.CENTER);
		
		tab.setContent(vbox);
		return tab;
	}
	
	public class buttonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if(e.getSource().equals(laden)) {
				FileChooser fc = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Cards (*.kar)", "*.kar");
				fc.getExtensionFilters().add(extFilter);
				fc.setTitle("Load a set of cards");
				File file = fc.showOpenDialog(null);
				if(file != null) {
					button.setDisable(false);
					button.setText("Check");
					dateiLaden(file);
					for(Card card : cards) {
						uebrig++;
						if(card.isDoppelt()) uebrig++;
					}
					uebrigLabel.setText("Remaining cards: " + uebrig);
					ArrayList<Card> cards2 = new ArrayList<Card>();
					for(Card c : cards) {
						if(c.isDoppelt()) {
							cards2.add(c.invert());
						}
					}
					for(Card c : cards2) {
						cards.add(c);
					}
					Collections.shuffle(cards);
					currentCard = cards.get(0);
					zeigeNaechsteKarte();
				}
			}
			else {
				if(button.getText().equals("Check") && currentCard.getAwnser().contains("/")  && antwortArea.getText().contains("/")) {  //Bei einem Schrägstrich sollen alle Bedeutungen in jeder Reihenfolge funktionieren
					String[] antworten = currentCard.getAwnser().split("/");
					String[] benutzerAntworten = antwortArea.getText().split("/");
					ArrayList<String> antwortenListe = new ArrayList<String>();
					if(benutzerAntworten.length != antworten.length) {
						frageArea.setText("Wrong. You wrote ''" + antwortArea.getText() + "'' instead of ''" + currentCard.getAwnser() + "''.");
						button.setText("Next card");
					}
					else {
						for(int i = 0; i < antworten.length; i++) {
							antwortenListe.add(antworten[i]);
						}
						for(int i = 0; i < antworten.length; i++) {
							for(int j = 0; j < benutzerAntworten.length; j++) {
								if(benutzerAntworten[j].equals(antworten[i])) {
									antwortenListe.remove(antworten[i]);
								}
							}
						}
						if(antwortenListe.size() == 0) { //Dann ist alles richtig
							uebrig--;
							uebrigLabel.setText("Remaining cards: " + uebrig);
							cards.remove(currentCard);
							frageArea.setText("Correct!");
							button.setText("Next card");
						}
						else {
							frageArea.setText("Wrong. You wrote ''" + antwortArea.getText() + "'' instead of ''" + currentCard.getAwnser() + "''.");
							button.setText("Next card");
						}
					}
				}
				else if(button.getText().equals("Check") && antwortArea.getText().equalsIgnoreCase(currentCard.getAwnser())) {
					uebrig--;
					uebrigLabel.setText("Remaining cards: " + uebrig);
					cards.remove(currentCard);
					frageArea.setText("Corrrect!");
					button.setText("Next card");
				}
				else if(button.getText().equals("Check")) {  //Bei falscher Eingabe
					frageArea.setText("Wrong. You wrote ''" + antwortArea.getText() + "'' instead of ''" + currentCard.getAwnser() + "''.");
					button.setText("Next card");
				}
				else if(button.getText().equals("Next card")) {
					if(frageArea.getText().equals("Correct!")) {
						zeigeNaechsteKarte();
					}
					else {
						Collections.shuffle(cards);
						zeigeNaechsteKarte();
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void dateiLaden(File file) {
		if(file.getName().endsWith(".kar")) {
			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));
				cards = (ArrayList<Card>) os.readObject();
				os.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void zeigeNaechsteKarte() {
		if(cards.size() > 0) {
			currentCard = cards.get(0);
			frageArea.setText(currentCard.getQuestion());
			antwortArea.clear();
			button.setText("Check");
			antwortArea.requestFocus();
		}
		else {
			frageArea.setText("You awnsered all cards.");
			antwortArea.clear();
			button.setDisable(true);
		}
	}
	
	public class antwortHandler implements EventHandler<KeyEvent> {
		public void handle(KeyEvent event) {
			if(event.getCode() == KeyCode.TAB) {
				button.requestFocus();
				event.consume();
			}
		}
	}

}
