package de.letorat.voclern.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Erstellmodus {
	
	private ArrayList<Card> cards = new ArrayList<Card>();
	private TextArea frageArea;
	private TextArea antwortArea;
	private Button naechsteKarte;
	private Button speichern;
	private CheckBox zweiKarten;
	
	public Tab buildErstellenGui() {
		Tab tab = new Tab("Card creation");
		
		Label frage = new Label("Question:");
		frageArea = new TextArea();
		frageArea.addEventFilter(KeyEvent.KEY_PRESSED, new frageHandler());
		Label antwort = new Label("Awnser:");
		antwortArea = new TextArea();
		antwortArea.addEventFilter(KeyEvent.KEY_PRESSED, new antwortHandler());
		naechsteKarte = new Button("Next card");
		speichern = new Button("Save this set of cards");
		zweiKarten = new CheckBox(" Create two cards?");
		zweiKarten.setSelected(true);
		
		naechsteKarte.setOnAction(new buttonHandler());
		speichern.setOnAction(new buttonHandler());
		
		naechsteKarte.setPrefWidth(160);
		speichern.setPrefWidth(160);
		
		frageArea.setWrapText(true);
		antwortArea.setWrapText(true);
		
		HBox hbox = new HBox(20);
		hbox.getChildren().add(naechsteKarte);
		hbox.getChildren().add(speichern);
		hbox.setAlignment(Pos.CENTER);
		
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(0,0,10,0));
		vbox.getChildren().add(frage);
		vbox.getChildren().add(frageArea);
		vbox.getChildren().add(antwort);
		vbox.getChildren().add(antwortArea);
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(zweiKarten);
		vbox.setAlignment(Pos.CENTER);
		
		tab.setContent(vbox);
		return tab;
	}
	
	public class buttonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			if(event.getSource().equals(naechsteKarte)) {
				addCard();
				frageArea.clear();
				antwortArea.clear();
				frageArea.requestFocus();
				event.consume();
			}
			else {
				if(frageArea.getText() != null && antwortArea.getText() != null && !frageArea.getText().equals("") && !antwortArea.getText().equals("")) {
					addCard();
				}
				FileChooser fc = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Cards (*.kar)", "*.kar");
				fc.getExtensionFilters().add(extFilter);
				fc.setTitle("Save this set of cards");
				File file = fc.showSaveDialog(null);
				if(file != null) {
					frageArea.clear();
					antwortArea.clear();
					String name = file.getName();
					int index = name.lastIndexOf("."); //Endung entfernen (falls vorhanden)
					if(index > 0) {
						name = name.substring(0, index);
					}
					String newName = name + ".kar";  //"kar" - Endung anhängen
					File newFile = new File(file.getParentFile(), newName);
					saveFile(cards,newFile);
				}
			}
		}
	}
	
	public class frageHandler implements EventHandler<KeyEvent> {
		public void handle(KeyEvent event) {
			if(event.getCode() == KeyCode.TAB) {
				antwortArea.requestFocus();
				event.consume();
			}
		}
	}
	
	public class antwortHandler implements EventHandler<KeyEvent> {
		public void handle(KeyEvent event) {
			if(event.getCode() == KeyCode.TAB) {
				naechsteKarte.requestFocus();
				event.consume();
			}
		}
	}
	
	 private void saveFile(ArrayList<Card> list, File file){
	        try {
	            FileOutputStream fileStream = new FileOutputStream(file);
	            ObjectOutputStream os = new ObjectOutputStream(fileStream);
	            os.writeObject(list);
	            os.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	         
	    }
	 
	 private void addCard() {
		 Card card = new Card(frageArea.getText(), antwortArea.getText(), zweiKarten.isSelected());
		 cards.add(card);
	 }
}
