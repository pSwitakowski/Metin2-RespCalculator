package RespCalculator;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;



public class Mob {
    private String name;
    private LocalTime lastSpawnTime;
    private LocalTime nextSpawnTime;
    private int timeBetweenSpawns;

    public VBox getMobVBox() {
        return this.mobVBox;
    }

    private VBox mobVBox;
    private Label nameLabel;
    private Label nextSpawnLabel;
    private Label timeBetweenSpawnsLabel;
    private Label timeTillNextSpawnLabel;

    private ArrayList<LocalTime> nextSpawns = new ArrayList<>();

    public Mob(String name, LocalTime lastSpawnTime, int timeBetweenSpawns){
        this.name=name;
        this.lastSpawnTime=lastSpawnTime;
        this.timeBetweenSpawns=timeBetweenSpawns;

        this.setNextSpawnTime();

        this.mobVBox = new VBox();
        this.mobVBox.setAlignment(Pos.BASELINE_CENTER);
        this.mobVBox.setPadding(new Insets(0,0,10,0));

        this.nameLabel = new Label(this.name);
        this.nameLabel.setFont(new Font("Arial",25));
        this.nameLabel.setStyle("-fx-text-fill: #ff0000");

        this.nextSpawnLabel = new Label("Next spawn: " + this.getNextSpawnTime().format(Main.currentTimeParser));
        this.nextSpawnLabel.setFont(new Font("Arial",15));
        this.nextSpawnLabel.setStyle("-fx-text-fill: #dabb00");

        this.timeBetweenSpawnsLabel = new Label("Spawn time: " + this.timeBetweenSpawns);
        this.timeBetweenSpawnsLabel.setFont(new Font("Arial",15));
        this.timeBetweenSpawnsLabel.setStyle("-fx-text-fill: #dabb00");

        this.timeTillNextSpawnLabel = new Label("Seconds to spawn: " + this.getTimeTillNextSpawn() + " seconds");
        this.timeTillNextSpawnLabel.setFont(new Font("Arial",15));
        this.timeTillNextSpawnLabel.setStyle("-fx-text-fill: #7fc371");

        this.mobVBox.getChildren().addAll(this.nameLabel,this.nextSpawnLabel,this.timeBetweenSpawnsLabel,this.timeTillNextSpawnLabel);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getLastSpawnTime() {
        return lastSpawnTime;
    }

    public void setLastSpawnTime(LocalTime lastSpawnTime) {
        this.lastSpawnTime = lastSpawnTime;
    }

    public int getTimeBetweenSpawns() {
        return timeBetweenSpawns;
    }

    public void setTimeBetweenSpawns(int timeBetweenSpawns) {
        this.timeBetweenSpawns = timeBetweenSpawns;
    }

    public LocalTime getNextSpawnTime() {
        return nextSpawnTime;
    }

    public void setNextSpawnTime(LocalTime nextSpawnTime) {
        this.nextSpawnTime = nextSpawnTime;
    }

    public ArrayList<LocalTime> getNextSpawns() {
        return nextSpawns;
    }

    public void setNextSpawns(ArrayList<LocalTime> nextSpawns) {
        this.nextSpawns = nextSpawns;
    }

    public long getTimeTillNextSpawn() {
        return Duration.between(LocalTime.now(),this.nextSpawnTime).toSeconds();
    }

    public void calculateNextSpawns(){
        this.nextSpawns.clear();
        for (int i = 0; i < 3; i++) {
            LocalTime nextSpawnTime = this.getNextSpawnTime().plusMinutes(i*timeBetweenSpawns);
            this.nextSpawns.add(nextSpawnTime);
        }
    }

    public void setNextSpawnTime(){
        LocalTime now = LocalTime.now();
        long elapsedSeconds = Duration.between(this.lastSpawnTime,LocalTime.now()).toSeconds();
        int differenceModulo = (int)elapsedSeconds % (this.timeBetweenSpawns * 60);
        int secondsTillNextSpawn = (this.timeBetweenSpawns *60) - differenceModulo;

        this.setNextSpawnTime((now.plusSeconds(secondsTillNextSpawn)));
    }

    public void updateSecondsToSpawnLabel(){
        this.timeTillNextSpawnLabel.setText("Seconds to spawn: " + this.getTimeTillNextSpawn());
    }
    public void updateNextSpawnTimeLabel(){
        this.nextSpawnLabel.setText("Next spawn: " + this.getNextSpawnTime().format(Main.currentTimeParser));
    }



}
