package de.letorat.voclern.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Gui extends Application {
	
	private Button erstellen;
	private Button abfragen;
	private Button bearbeiten;
	private Button change;
	private TabPane tabPane;
	private Tab erstellenTab;
	private Tab abfragenTab;
	private Tab bearbeitenTab;
	private Scene scene;
	private Stage secondaryStage;
	
	private static final double VERSION = 2.0;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) {
		
		java.util.Locale.setDefault(new java.util.Locale("en"));
		
		primaryStage.setTitle("VocLern");
		
		Label vocLern = new Label("VocLern");
		erstellen = new Button("Create a set of cards");
		abfragen = new Button("Test your knowledge");
		bearbeiten = new Button("Edit a set of cards");
		change = new Button("Change design");
		
		erstellen.setPrefWidth(300);
		abfragen.setPrefWidth(300);
		bearbeiten.setPrefWidth(300);
		change.setPrefWidth(300);
		erstellen.setOnAction(new buttonHandler());
		abfragen.setOnAction(new buttonHandler());
		bearbeiten.setOnAction(new buttonHandler());
		change.setOnAction(new buttonHandler());
		
		vocLern.setFont(new Font(75));
		vocLern.setAlignment(Pos.TOP_CENTER);
		
		VBox buttonBox = new VBox(30);
		buttonBox.getChildren().add(erstellen);
		buttonBox.getChildren().add(abfragen);
		buttonBox.getChildren().add(bearbeiten);
		buttonBox.getChildren().add(change);
		
		buttonBox.setAlignment(Pos.CENTER);
		
		VBox vbox = new VBox(50);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(vocLern);
		vbox.getChildren().add(buttonBox);
		
		tabPane = new TabPane();
		
		Tab menuTab = new Tab("Menu");
		menuTab.setClosable(false);
		menuTab.setContent(vbox);
		
		tabPane.getTabs().add(menuTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		scene = new Scene(tabPane,500,500);
		scene.getStylesheets().add(Gui.class.getResource("MenuStyle.css").toExternalForm());
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		
		primaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream("VocLernLogo.png")));
		
		primaryStage.show();
		
		checkForUpdates();
	}
	
	private void checkForUpdates() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Socket socket = new Socket();
				try {
					socket.connect(new InetSocketAddress("localhost", 50001), 2000);
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer.println("$version" + VERSION);
					String msg;
					while((msg = reader.readLine()) != null) {
						if(msg.equals("$updateAvailable")) {
							Platform.runLater(new Runnable() {
							      @Override public void run() {
							    	  Stage secondaryStage = new Stage();  //TODO: unschlieﬂbar
										Label label = new Label("An update for VocLern is available. Do you want to receive it now?");
										Button yes = new Button("Install it");
										Button no = new Button("Don't install it");
										HBox hbox = new HBox(10);
										hbox.getChildren().addAll(yes, no);
										VBox vbox = new VBox(10);
										vbox.getChildren().addAll(label, hbox);
										vbox.setAlignment(Pos.CENTER);
										hbox.setAlignment(Pos.CENTER);
										vbox.setId("updateVBox");
										Scene s = new Scene(vbox,400,100);
										s.getStylesheets().add(Gui.class.getResource("MenuStyle.css").toExternalForm());
										secondaryStage.setScene(s);
										secondaryStage.initModality(Modality.APPLICATION_MODAL);
										secondaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream("VocLernLogo.png")));
										secondaryStage.show(); 
							      }
							    });
						}
						else {
							return;
						}
					}
				} catch (IOException e) {
					return;
				}
			}
			
		}).start();
	}

	public class buttonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if(e.getSource() == erstellen) {
				for(Tab t : tabPane.getTabs()) {
					if(t.equals(erstellenTab)) {
						tabPane.getSelectionModel().select(t);
						return;
					}
				}
				erstellenTab = new Erstellmodus().buildErstellenGui();
				tabPane.getTabs().add(erstellenTab);
				tabPane.getSelectionModel().select(erstellenTab);
			}
			else if(e.getSource() == abfragen) {
				for(Tab t : tabPane.getTabs()) {
					if(t.equals(abfragenTab)) {
						tabPane.getSelectionModel().select(t);
						return;
					}
				}
				abfragenTab = new Abfragemodus().buildAbfrageGui();
				tabPane.getTabs().add(abfragenTab);
				tabPane.getSelectionModel().select(abfragenTab);
			}
			else if(e.getSource() == bearbeiten) {
				for(Tab t : tabPane.getTabs()) {
					if(t.equals(bearbeitenTab)) {
						tabPane.getSelectionModel().select(t);
						return;
					}
				}
				bearbeitenTab = new Bearbeitenmodus().buildBearbeitenGui();
				tabPane.getTabs().add(bearbeitenTab);
				tabPane.getSelectionModel().select(bearbeitenTab);
			}
			else if(e.getSource() == change){
				if(scene.getStylesheets().contains(Gui.class.getResource("MenuStyle.css").toExternalForm())) {
					scene.getStylesheets().remove(Gui.class.getResource("MenuStyle.css").toExternalForm());
					scene.getStylesheets().add(Gui.class.getResource("OtherStyle.css").toExternalForm());
				}
				else {
					scene.getStylesheets().remove(Gui.class.getResource("OtherStyle.css").toExternalForm());
					scene.getStylesheets().add(Gui.class.getResource("MenuStyle.css").toExternalForm());
				}
			}
			else {
				secondaryStage.close();
			}
		}
	}
}
