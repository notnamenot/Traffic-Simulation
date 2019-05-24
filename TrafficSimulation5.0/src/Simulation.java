import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

class Lights extends TimerTask {
    public void run() {
        if (0 == Simulation.lights) {
            Simulation.lights = 1;
            Road.lights_off[0] =  Road.nodes[0];
            Road.lights_off[1] =  Road.nodes[1];
            Road.lights_on[0] = Road.nodes[2];
        }
        else if (1 == Simulation.lights) {
            Simulation.lights = 2;
            Road.lights_off[0] =  Road.nodes[2];
            Road.lights_off[1] =  Road.nodes[0];
            Road.lights_on[0] = Road.nodes[1];
        }
        else  {
            Simulation.lights = 0;
            Road.lights_off[0] =  Road.nodes[1];
            Road.lights_off[1] =  Road.nodes[2];
            Road.lights_on[0] = Road.nodes[0];
        }

        System.out.println("Lights flag:"+Simulation.lights);
    }
}

public class Simulation extends Application {

    private final int LANE_NUMBER = 6;
    private final int LANE_LENGTH = 100;
    int lan = 0;//do określania pasa
    public static int lights = 0; //światła

    GridPane gridPane = new GridPane();
    Random random_seed = new Random();
    Road road = new Road();




    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Traffic Simulation");


        //gridPane.setHgap(5);
        gridPane.setVgap(5);
        //gridPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 0 is 100 wide
        //gridPane.getColumnConstraints().add(new ColumnConstraints(200)); // column 1 is 200 wide

        for (int i = 0; i < LANE_LENGTH; ++i) {
            for (int j = 0; j < LANE_NUMBER; ++j) {
                //Label lbl = new Label(" " + Integer.toString(i) + "" + Integer.toString(j) + " ");
                //lbl.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                //gridPane.add(lbl, i, j, 1, 1);  //node,column index ,row index,row span , column span

                Rectangle r = new Rectangle();
                r.setHeight(10);
                r.setWidth(10);
                //r.setStroke(Color.RED);     //ramka
                r.setFill(Color.DARKGRAY);
                gridPane.add(r, i, j, 1, 1);

                if (j==5 && (i == 10 ||i==40 ||i == 80)) {
                    Rectangle re = new Rectangle();
                    re.setHeight(10);
                    re.setWidth(10);
                    re.setFill(Color.CYAN);
                    gridPane.add(re, i+2, j+2, 1, 1);

                }
            }
            //gridPane.getColumnConstraints().add(new ColumnConstraints(10)); // column 0 is 100 wide
            //gridPane.getRowConstraints().add(new RowConstraints(10)); // column 0 is 100 wide
        }




        //https://riptutorial.com/javafx/example/7291/updating-the-ui-using-platform-runlater
        // longrunning operation runs on different thread
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        simulate();
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }

                    // UI update is run on the Application thread
                    Platform.runLater(updater);
                }
            }

        });
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();


        //Scene scene = new Scene(root, 600, 600);
        Scene scene = new Scene(gridPane, 1000, 120);//1250x1600
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    void simulate () {

        remove_cars();
        print_lane(road.third_lane_back,3);
        System.out.println();
        print_lane(road.second_lane_back,2);
        System.out.println();
        print_lane(road.first_lane_back,1);
        System.out.println("\n");
        print_lane(road.first_lane,4);
        System.out.println();
        print_lane(road.second_lane,5);
        System.out.println();
        print_lane(road.third_lane,6);
        System.out.println("\n\n\n\n");
        //TimeUnit.SECONDS.sleep(1);


        road.generate_car( 3);//probability od 5 do 2
        road.move_cars();

        /*for (int i = 0; i < road.left_lane.size(); ++i) {
            if (road.left_lane.get(i) != null) {
                System.out.print("X(" + road.left_lane.get(i).getV_current() + ")");
                Rectangle car = (Rectangle) getNodeByRowColumnIndex(0, i, gridPane);
                car.setFill(Color.RED);
            }
            else
                System.out.print(" ____ ");
        }
        System.out.println();*/

        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n\n\n");

        /*boolean gen_car = random_seed.nextBoolean();
        if (gen_car) {
            int v_max = random_seed.nextInt(3) + 4;
            road.generate_car(v_max);
        }*/

        //road.move_cars();


    }

    public static void main(String[] args) {
        Timer timer = new Timer(true);
        timer.schedule(new Lights(), 0, 20000);
        launch(args);
    }


    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    void remove_cars() {
        for (int i = 0; i < LANE_LENGTH; ++i) {
            for (int j = 0; j < LANE_NUMBER; ++j) {
                Rectangle car = (Rectangle) getNodeByRowColumnIndex(j, i, gridPane);
                if (car.getFill() == Color.RED )
                    //car.setFill(Color.BROWN);
                    car.setFill(Color.DARKGRAY);
            }
        }

    }


    /*public static void main(String[] args) throws Exception {


        while (true) {
            print_lane(road.first_lane);
            System.out.println();
            print_lane(road.second_lane);
            System.out.println();
            print_lane(road.third_lane);
            System.out.println("\n\n\n\n");
            TimeUnit.SECONDS.sleep(1);

            road.generate_car();
            road.move_cars();
        }
    }*/

    private void print_lane(ArrayList<Car> lane,int row)  {
        /*int row;
        if (1 == nr) {
            row = 0;
            //lan = 1;
        }
        else if (1 == lan) {
            row = 1;
            lan = 2;
        }
        else if (2 == lan){
            row = 2;
            lan = 0;
        }*/
        --row;


        for (int i = 0; i < lane.size(); ++i) {
            if (lane.get(i) != null) {
                System.out.print("X(" + lane.get(i).getV_current() + ").");
                Rectangle car = (Rectangle) getNodeByRowColumnIndex(row, i, gridPane);
                car.setFill(Color.RED);
            }
            else
                System.out.print("____.");
        }
        System.out.println();

    }
}




