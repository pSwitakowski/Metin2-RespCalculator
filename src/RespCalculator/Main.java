package RespCalculator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;


public class Main extends Application{

    private static int WIDTH=600;
    private static int HEIGHT=800;

    String fileSeparator = System.getProperty("file.separator");
    private static FileWriter fileWriter;
    File jsonFile = new File("resources" + fileSeparator + filePath);
    private static JSONObject jsonMobList = new JSONObject();
    private static JSONArray jsonMobArray = new JSONArray();

    public static DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm");
    public static DateTimeFormatter currentTimeParser = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static VBox spawnTimesVBox = new VBox();

    public static ArrayList<Mob> mobList = new ArrayList<>();

    public static String filePath = "Spawns.txt";


    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Metin2 Resp Calculator");
        StackPane mainPane = new StackPane();
        mainPane.setAlignment(Pos.BASELINE_CENTER);
        Scene mainScene = new Scene(mainPane, WIDTH,HEIGHT);
        primaryStage.setScene(mainScene);


        mainPane.setStyle("-fx-background-image: url('file:resources/background.jpg')");

        if(jsonFile.exists()){

        JSONParser jsonParser = new JSONParser();
            JSONObject object = null;
            FileReader fr = new FileReader("resources" + fileSeparator + filePath);
            try {
                object = (JSONObject) jsonParser.parse(fr);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }finally{
                fr.close();
            }

            JSONArray mobs = (JSONArray) object.get("mob");

            for(Object o : mobs){
                JSONObject mobJsonObject = (JSONObject) o;

                jsonMobArray.add(mobJsonObject);
                jsonMobList.put("mob",jsonMobArray);

                String mobName = (String) mobJsonObject.get("Name");
                String mobLastSpawnTime = (String) mobJsonObject.get("LastSpawnTime");
                String mobTimeBetweenSpawns = (String) mobJsonObject.get("TimeBetweenSpawns");

                LocalTime lastSpawnTime = LocalTime.parse(mobLastSpawnTime, currentTimeParser);
                int timeBetweenSpawns = Integer.parseInt(mobTimeBetweenSpawns);

                Mob mob = new Mob(mobName,lastSpawnTime,timeBetweenSpawns);
                if(!(mobList.contains(mob)))
                    mobList.add(mob);

            }
            updateSpawnsVBox();
            System.out.println("MOB LIST SUCCESSFULLY LOADED FROM FILE.");

        }



        //UI
        Label chooseMobLabel = new Label("Mob name");
        TextField mobNameTextField = new TextField();
        mobNameTextField.setMaxWidth(75);
        VBox mobVBox = new VBox();
        mobVBox.getChildren().addAll(chooseMobLabel,mobNameTextField);
        mobVBox.setAlignment(Pos.BASELINE_CENTER);
        mobVBox.setPadding(new Insets(20));

        Label insertLastSpawnTimeLabel = new Label("Last spawn time (HH:mm:ss):");
        TextField insertLastSpawnTimeTextField = new TextField();
        insertLastSpawnTimeTextField.setMaxWidth(75);
        VBox lastSpawnTimeVBox = new VBox();
        lastSpawnTimeVBox.getChildren().addAll(insertLastSpawnTimeLabel,insertLastSpawnTimeTextField);
        lastSpawnTimeVBox.setAlignment(Pos.BASELINE_CENTER);
        mobVBox.setPadding(new Insets(20));


        Label insertSpawnTimeLabel = new Label("Spawn time (minutes):");
        TextField insertSpawnTimeTextField = new TextField();
        insertSpawnTimeTextField.setMaxWidth(75);
        VBox spawnTimeVBox = new VBox();
        spawnTimeVBox.getChildren().addAll(insertSpawnTimeLabel,insertSpawnTimeTextField);
        spawnTimeVBox.setAlignment(Pos.BASELINE_CENTER);
        spawnTimeVBox.setPadding(new Insets(20));

        Button calculateButton = new Button("Calculate spawns!");
        calculateButton.setMaxWidth(75);
        calculateButton.setWrapText(true);

        ToggleButton nextSpawnNotificationToggle = new ToggleButton("Powiadomienie");
        nextSpawnNotificationToggle.setSelected(true);

        nextSpawnNotificationToggle.setStyle("-fx-background-color: rgb(0,255,0)");
        nextSpawnNotificationToggle.setOnAction(e->{

            if(nextSpawnNotificationToggle.isSelected()) nextSpawnNotificationToggle.setStyle("-fx-background-color: rgb(255,0,0)");
            else nextSpawnNotificationToggle.setStyle("-fx-background-color: rgb(0,255,0)");


        });



        Label spawnTimesLabel = new Label("Spawns:");
        spawnTimesLabel.setFont(new Font("Arial",15));
        spawnTimesLabel.setStyle("-fx-text-fill: #3c51ec");
        spawnTimesLabel.setPadding(new Insets(10,0,0,0));

        ScrollPane spawnTimesScrollPane = new ScrollPane();
        spawnTimesScrollPane.setContent(spawnTimesVBox);

        Label time = new Label();
        time.setStyle("-fx-text-fill: #98d110");
        time.setFont(new Font("Consolas",15));




            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                LocalTime currentTime = LocalTime.now();

                time.setText(currentTime.format(currentTimeParser));

                Platform.runLater(() -> {
                    spawnTimesVBox.getChildren().clear();
                    updateSpawnsVBox();
                });


            }),
                    new KeyFrame(Duration.seconds(1))
            );
            clock.setCycleCount(Animation.INDEFINITE);
            clock.play();



        VBox UIVBox = new VBox(time,mobVBox,lastSpawnTimeVBox,spawnTimeVBox,calculateButton,nextSpawnNotificationToggle,spawnTimesLabel,spawnTimesVBox);
        UIVBox.setAlignment(Pos.TOP_CENTER);


        calculateButton.setOnAction(e->{
            if(!(mobNameTextField.getText().equals("")) || !(insertLastSpawnTimeTextField.getText().equals("")) || !(insertLastSpawnTimeTextField.getText().equals(""))) {

                String mobName = mobNameTextField.getText();
                LocalTime lastSpawnTime = LocalTime.parse(insertLastSpawnTimeTextField.getText(), currentTimeParser);
                int timeBetweenSpawns = Integer.parseInt(insertSpawnTimeTextField.getText());

                Mob mob = new Mob(mobName,lastSpawnTime,timeBetweenSpawns);
                mobList.add(mob);



                //CHECKING IF FILE EXISTS
                /*
                try {
                    if(jsonFile.createNewFile()){
                        System.out.println("Creating new file...");
                    }else{
                        System.out.println("File already exists.");

                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                 */



                //CREATING MOB OBJECT AND SAVING IT TO THE CREATED FILE
                JSONObject jsonMob = new JSONObject();
                jsonMob.put("Name",mobName);
                jsonMob.put("LastSpawnTime",lastSpawnTime+"");
                jsonMob.put("TimeBetweenSpawns",timeBetweenSpawns+"");
                jsonMobArray.add(jsonMob);
                jsonMobList.put("mob",jsonMobArray);

                try {
                    fileWriter = new FileWriter("resources" + fileSeparator + filePath);
                    fileWriter.write(jsonMobList.toJSONString());
                    System.out.println("Succesfully saved mob to a file.");

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }finally {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }



            }
        });


        mainPane.getChildren().add(UIVBox);

        primaryStage.show();
    }


    public void updateSpawnsVBox(){

        mobList.sort((o1, o2) -> Integer.parseInt(String.valueOf(o1.getTimeTillNextSpawn() - o2.getTimeTillNextSpawn())));

        for(Mob mob : mobList){
            if(mob.getTimeTillNextSpawn()==0) {
                mob.setNextSpawnTime(LocalTime.now().plusMinutes(mob.getTimeBetweenSpawns()));
                mob.calculateNextSpawns();
                mob.updateNextSpawnTimeLabel();
            }
            mob.updateSecondsToSpawnLabel();
            spawnTimesVBox.getChildren().add(mob.getMobVBox());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
