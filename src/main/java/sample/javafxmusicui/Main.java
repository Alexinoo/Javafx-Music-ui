package sample.javafxmusicui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.javafxmusicui.model.Datasource;

import java.io.IOException;

/*
 * JDBC with a GUI Program
 *
 * Let's start looking at JDBC from within a UI application
 *
 * //// QUESTIONS ////
 * The JDBC calls themselves are the same but when do we create the Datasource class and how do classes in the Application access it ?
 * How do we perform database operations so that the user interface doesn't freeze ?
 * And how do we report the results back to the user ?
 *
 * The purpose of this video is to answer these questions
 * The goal here isn't to build a pretty UI but rather to answer the above questions using a Functional User Interface
 * Tim will assume that we have watched the JavaFX section of this course and then he will explain the User Interface specific concept
 *  if it's new and hasn't been previously covered
 * Check the JavaFX section of the course first before diving into this one
 *
 *
 *
 * /// User Interface ///
 *
 * The User interface we'll create is going to be fairly simple
 * We'll use a BorderPane with a TableView in the center position
 * We'll also have a ProgressBar which won't be visible when the application starts for reasons that will become apparent , and will reside
 *  in the bottom position , in a HBox
 *
 * In the right position, we'll place a VBox containing several buttons
 *  - Pressing a button is going to essentially perform a SQL operation
 *  - We'll have 3 buttons :
 *      1. List Artists button
 *      2. Show Albums button - For a selected artist
 *      3. Update Artist button
 *
 * May not sound like much but each button here is going to demonstrate something different that can be extended to similar operations
 *  - The List Artists Button will query all the data in the table and present that to the user
 *  - The Show Albums Button will need to do a query based on a selected record and then display the results
 *  - The Update Artist Button will demonstrate what's involved when performing one of the CRUD operations that changes existing data in a
 *     table
 *
 *
 *
 * /////  main.fxml
 *
 * Rename sample.fxml to main.fxml
 * COpy and paste main.fxml file in the resource section which Tim created using a Scene Builder
 *
 *
 * /// Main.java
 *
 * We'll make a few changes to the start() and set the Main windows title and size
 *
 * Change title to "Music Database"
 *
 *      stage.setTitle("Music Database");
 *
 * Change the size fo the scene to 800 by 600
 *
        stage.setScene(new Scene(root, 800, 600));
 *
 * Run the application just to see what the User interface looks like
 *  - We've got an area for the content
 *  - Our buttons to the right
 *  - And a Progress bar which is going to be invisible and was left there to show us how it looks like and where it's positioned
 *      - And now that we've seen it, let's add a visible property and set it to false in the main.fxml
 *
 * Re-run and confirm that the Progress bar has disappeared and we'll show it when it's appropriate in the application that we're building
 *
 * ///
 * Incidentally, in a real-world application, we'll probably do it in such a way that there wasn't an empty space near the bottom, but we're
 *  not focusing on UI in these videos
 * The other thing is that if we look at our TableView, notice how we're setting the TableColumn prefWidth property
 *
 *       <TableColumn prefWidth="${artistTable.width}" text="Name">

 *      - We're setting the width to the same width as the TableView so that the name column will occupy the entire table width
 *      - We've used an annotation "${artistTable.width}" and this means that we want to set the prefWidth to the width of the control with
 *         an fx-id of "artistTable" , which in this case is our TableView
 *      - ANd we can actually do that with any property
 *
 *
 * //////
 * JDBC
 *
 * We'll first copy the model classes we created in previous set of videos i.e. Album , Artist and Datasource class
 * We don't actually need the Song and SongArtist classes and we can just go ahead and delete them
 *
 * We want the controller to be able to use the Datasource class
 * In a real world app, we'll probably have more than 1 controller and that all need access
 * We could have each controller create an instance of the class, but which one then would be responsible for managing the connection by
 *  calling open() and close()
 * When working with a datasource, it's quite common to use a Singleton pattern, for the Datasource class
 * It's called a Singleton because we use it when we want an application to create only 1 instance of a particular class
 * So every object that needs to call methods in the Singleton will use the same instance of the Datasource to do so
 *
 *
 *
 * ///// Datasource Class - Make it a Singleton /////
 *
 * To actually turn our Datasource class into a Singleton is quite straightforward
 * We need to add a private constructor before the open()
 *
 *      private Datasource() {

        }
 *
 * Since it's private, only the class will be able to create instances of itself
 * In other words, no other class will be able to construct an instance of data source, and that's what we want here
 * We then need to create a variable that will hold that 1 instance of the class , that every other class in the app will use
 * This variable would need to be static
 *
 *      private static Datasource instance;
 *
 * We now need to add the method that every other class will use to access the instance
 * We can create the singleton instance when we declare the variable or we can create it in the static access method and that's what we're
 *  going to do in this case , and call it getInstance()
 *
 *      public static Datasource getInstance() {
 *          if (instance == null)
 *              instance = new DataSource();
 *
            return instance;
        }
 *
 * So, when an obj wants to use the Singleton instance, it calls getInstance()
 * The method checks to see if the Singleton instance has been previously created
 * IF it has:
 *  - it returns the return
 * Otherwise
 *  - creates the instance and then returns it

 * This is called lazy instantiation because the instance isn't created until the first time it's needed
 *
 * This is perfectly valid code, but keep in mind that it's not Thread safe
 *
 * It's possible for a thread to be interrupted after the check for null here
 *
 * Another thread could run, check for null and create the instance
 *
 * Then the first thread can run and create the 2nd instance and consequently, our application would have then
 *  2 instances on the go which then defeats the purpose of using a singleton
 *
 * So consequently, instead what we can do here is we can create the instance when the instance variable is declared and that will be
 *  thread-safe
 *
 * Update
 *
 *      private static Datasource instance;
 * To
 *      private static Datasource instance = new Datasource();

 * This is also lazy instantiation because the instance won't be created until the first time the class is loaded which will be the first
 *  time some other instances references the class by calling getInstance()
 *
 * Now we can update the getInstance() to just return the instance only
 *
 *      public static Datasource getInstance() {
             return instance;
        }
 *
 * Whenever a class now wants to call a method in the Datasource class, it will then do
 *
 *  	Datasource.getInstance().methodName();
 *
 *      - That's going to be the calling convention used to access all the methods in this particular class
 *
 *
 *
 * /////////// init() and stop() JavaFX methods ////////
 * We still have a question of where to call the open and close()
 * It would make sense to open a connection to the database when an application is started because our Main window wants to show data
 *  from the database,
 * It would also make sense to close the connection when the user shuts down the application by closing the Main window
 * So what we're going to do here is to make use of the JavaFX Lifecycle methods to open and close the database connection
 *
 * IF we recall the Application class , contains lifecycle methods that are either abstract or concrete , but don't actually do anything
 * So consequently, we're going to override the init() to call the Datasource.open()
 * We'll also override stop() to call Datasource.close()
 *
 * The init() runs before the start() that creates the user interface
 * The stop() runs when the application is shutting down either because the application has explicitly done something to close it or
 *  because the code called Platform.exit()
 *
 * Both init and close() are implemented in the Applications class, but by default they don't do anything
 *
 *
 * ////////
 * Override init() and stop() to create method stubs
 *
 * In the init()
 *  - There's no need to call super.init() , both super.init() and super.stop() don't really do anything
 *  - But we'll leave them there just in case things changes in the future
 *  - So we can now go ahead and call open() as follows
 *
 *      Datasource.getInstance().open();
 *
 * In the stop()
 *  - We can do something similar, but this time call close() as follows
 *
 *      Datasource.getInstance().close();
 *
 * Run it and make sure everything is working
 * Note that we get an error : Couldn't connect to database : No suitable driver found for jdbc:sqlite.....
 * Tim deliberately did that because he wanted to show us how the UI still appeared even though we actually got that error
 * If we can't connect to the database, we don't want the user interface to appear because anything else the user tries to do would fail
 * What we want to do is to inform the user that there's been a fatal error of some kind and then shut down the application
 * Normally we'd do this using pop up dialog, but since this isn't a UI set of videos , we'll just print out a message to the console
 * The init() will have to check if the return value from the open() and take appropriate action when necessary
 *
 * Change this and check whether the connection failed and if so, shutdown the application also and print something to the user
 *
 *        if(!Datasource.getInstance().open()){
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
 *
 * And if we run this now, we get the same error message we had previously about not able to connect to the database, and this time round
 *  the USer Interface doesn't appear because we couldn't get access to the database
 */

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Parent root = fxmlLoader.load();

        Controller controller = fxmlLoader.getController();
        controller.listArtists();

        stage.setTitle("Music Database");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        if(!Datasource.getInstance().open()){
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Datasource.getInstance().close();
    }

    public static void main(String[] args) {
        launch();
    }
}