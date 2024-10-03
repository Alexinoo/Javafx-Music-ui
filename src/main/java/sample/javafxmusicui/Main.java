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
 *
 *
 * ////////////
 * Add Artists
 * ////////////
 *
 * So when the application starts, we want display all the artists in the artists table
 * It's also possible that the user might explicitly ask to see all the artists after they've performed some other query
 * So consequently, we need to query all the artists when the application starts and also potentially in response to user input
 * Remember that we always want to perform long tasks on a background thread and not on the Main.java fx application thread
 * If we recall from the JavaFX section, that when we want to run background threads from a JavaFX application , we need to use Helper
 *  classes in the javafx.concurrent package
 *
 * We'll perform the following steps and not necessarily in any order that Tim is going to read them out
 *
 *  1. Create a Task that's going to perform the Database action - Query , Insert or whatever we're doing
 *  2. Initialize the Task with values required to perform the action ,  if that's necessary
 *  3. Implement task.call() to perform the action
 *  4. Bind the call results to the TableView items property
 *  5. Invoke the Task
 *
 * Since we may need to use this task in 2 places,
 *  i) At start up
 *  ii) When the user explicitly asks to see all artists,
 *
 * And that's why we're not going to use an Anonymous Task class that we create in response to user input
 * Instead, we'll create GetAllArtistTask class to the controller
 *
 * The GetAllArtistsTask is extending Task class, and if we want to use the Data binding to populate the table, the call() which we've
 *  reconfigured has to return an ObservableArrayList<Artist> - check the imports

 * We've overwritten the call() to call the queryArtist() in Datasource.java file
 *
 * This method if we can recall returns a List<Artist>
 * We don't want to change it to return an ObservableArrayList , because that would violate the separation between the model & UI code.
 * Instead, the Task is creating an observable list from the list that the queryArtist() returns
 * We're doing this by calling the observableArrayList() from the FXCollections class and then passing in the list that is returned from
 *  our Datasource which will then ultimately will give us an ObservableList
 *
 * Tim said we can use the Model class as is, and that would be true if we didn't want to take advantage of a data binding,
 * But since we do want to take advantage of that, we do need to make 1 small change to the Artist class to achieve that
 * Instead of storing the Artist name in a String, we need to store it as SimpleStringProperty
 *
 * This doesn't violate the Model-UI separation because SimpleStringProperty isn't a UI specific class
 * We could have used it all along actually and we'll do the same for the artist id even though we won't be displaying it
 * Since the field will now be properties, the Getter and Setter methods are going to change as well
 *
 * So let's proceed to the Artist class and make those changes in the Artist class
 *
 *      class Artist {
 *
 *          private SimpleIntegerProperty id;
            private final SimpleStringProperty name;
 *
 *
 *          public SimpleIntegerProperty getId() {
                return id.get();
            }

            public void setId(SimpleIntegerProperty id) {
                this.id.set(id);
            }

            public SimpleStringProperty getName() {
                return name.get();
            }

            public void setName(SimpleStringProperty name) {
                this.name.set(name);
            }
 *
 *      }
 *
 * Again we're only doing this because we want to take advantage of data binding
 *
 * Since this is a simple application, if we wanted to keep our model classes exactly as they were, then we could do that and we
 *  would explicitly then set the table items when the task completes
 *
 *
 * There are 2 ways we could set the items
 *  1. Covered in the Threading section of the course : We could perform Platform.runLater when the queryArtist()
 *      returns it's results
 *      - In the Runnable we pass to runLater , we could set the table items
 *
 *          artistTable.getItems().setAll(artistResults);
 *
 *      - Tim also mentioned that we can do more with tasks than we covered, including running code when the call()
 *         completes
 *      - To do that we need to call Task.setOnSucceeded which takes an event handler
 *      - In our case, we'll pass a lambda and do something like this
 *
 *          task.setOnSucceeded(e -> artistTable.getItems().setAll(artistResults));
 *
 *      - That's ultimately what we'll going to be doing, making this a little bit easier to call when the call()
 *         completes
 *
 *
 * //////////
 * Whenever data binding makes sense to use it , because we don't have to do anything when the task completes,
 *  it's all handled automatically
 *
 * We need to do 1 more thing for data binding to work though, and that is mapping the name field in the Artist
 *  class to the name column in the table
 *
 * We accomplish this by adding a CellValueFactory to the table column in our fxml file
 * So let's go ahead and update our fxml file
 *
 *      <TableColumn prefWidth="${artistTable.width}" text="Name" >
          <cellValueFactory>
            <PropertyValueFactory property="name" />
          </cellValueFactory>
        </TableColumn>
 *
 * So basically we've added a PropertyValueFactory for the name and that maps to the name field in the Artist class
 *
 *
 * ///////////
 * Controller
 *
 * Now we can add a method to the controller that get's the artists
 *
 * Create a reference to the artistTable

 *      private TableView<Artist> artistTable;
 *
 * Then we need to add the actual method listArtists()
 *
 *      Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
 *
 *      - We've created a new Task obj by creating GetAllArtistsTask instance
 *
 *      - Then we're binding the result of the task, the Artist ObservableList to the TableView items property so
 *          that they're bound to each other
 *
 * We'll start looking at how the listArtists() is going to be invoked because the Controller isn't created until the fxml is
 *  loaded
 *
 *
 * /////////////////////////////////////////////
 * /// Fix Artist and Preload records //////////
 * ////////////////////////////////////////////
 *
 *
 * We have binded the results of the Task, the artist's observable list to the TableView items property through the listArtist() ,
 *
 *      Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
 *
 *
 * and also adding <cellValueFactory> using the <PropertyValueFactory> in the main.fxml
 *
 *       <TableColumn prefWidth="${artistTable.width}" text="Name" >
          <cellValueFactory>
            <PropertyValueFactory property="name" />
          </cellValueFactory>
        </TableColumn>
 *
 *
 * Because the Controller isn't created until this fxml is loaded, and that happens in the start() and also because we want to
 * be sure that the UI has been built before we try and load these results
 *
 * We'll initiate the query of the artists from the start() in our Main.java class
 * We'll change our code to load fxml first :
 *
 *      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Parent root = fxmlLoader.load();

 * Then we'll need access to our Controller , by getting the instance of the controller
 *
        Controller controller = fxmlLoader.getController();

 * And then query the artist now by calling listArtists()

        controller.listArtists();
 *
 *
 * At the moment if we run this, we're getting an error because we're sending and receiving setters and getters SimpleIntegerProperty,
 *   but we should actually be sending the type, the int and the String to other classes that are using this
 *
 *  - Update them to return int via id.get() and String via name.get()
 *
 *       public int getId() {
            return id.get();
          }
 *
 * And
 *       public void setId(int id) {
            this.id.set(id);
        }
 *
 * In terms of the Setters, we do the same , pass as int and as String arguments
 * And then to save that back into a SimpleIntegerProperty, we use "this.id.set(id)" and "this.name.set(name)" respectively
 *
 *        public void setId(int id) {
            this.id.set(id);
          }
 *
 *        public void setName(String name) {
             this.name.set(name);
          }
 *
 *
 * Then lastly, we need to create a no args constructor to initialize both the id and the name to avoid getting NullPointerException
 *
 *      public Artist() {
            this.id = new SimpleIntegerProperty();
            this.name = new SimpleStringProperty();
        }
 *
 * And now the errors disappeared from the Datasource class - queryArtist() -
 * We are creating a new Artist object, using the constructor we've added above , which we can then be sure will initialize both id
 *  and name fields
 *
 *       Artist artist = new Artist();
         artist.setId(results.getInt(INDEX_ARTIST_ID));
         artist.setName(results.getString(INDEX_ARTIST_NAME));
 *
 *
 * ///////////
 *
 * Now, if we run this, it won't work and we won't be able to see our artists
 *
 * So Tim poses a challenge to us , Why are we not seeing artists showing here automatically ?
 *  - We've changed our start() to invoke the listArtists()
 *  - We've also set CellValueFactory and a PropertyValueFactory to associate that and we should be getting from our artist, should return a name
 *
 * /// Solution
 * We're invoking listArtists() from the start() which is seemingly creating a Task and binds it to our fxml TableView
 * But the problem here is that we're not kicking off the task
 *
 * So what we need to do here is to add below line
 *
 *      new Thread(task).start();
 *
 * So we need to kick off the task so that it actually goes through and start that for us, and go through the process of retrieving the records
 *  from SQLite database
 *
 * /////
 * And now if we run this, we're able to get the artists loaded correctly when we boot up our music database
 *
 *
 *
 * ///////
 * At this point now, we now know how to perform a potentially long-running query, and bind the results to a table
 *
 *
 *
 * /////////////////////////
 *  Implement Artist Query
 * /////////////////////////
 *
 * //////////// Implement show Albums(artist)
 *
 * So, what do we need to do when a user presses this button ?
 * We need to get the id of the selected artist and so the user will select the potential artist
 * So we need to get the id, then we need to query the database for all albums by that artist and then we need to populate the TableView
 *
 * ///
 * But hang on a minute, our current TableView is showing artists, we'll actually look at how to deal with that in a minute
 * We already have a method in the Datasource that queries the albums by an artist, but the problem is that it uses the artist name passed
 *  to it as an argument and does an INNER JOIN
 *
 * We could use it, but since we know what the artist id is, let's use that instead
 * This is because querying based on an integer and without using a JOIN will actually be much quicker and that could make a significant
 *  difference especially if you're dealing with large data sets
 *
 * We'll still use a PreparedStatement and since the user can't sort the data or tell us how they want it sorted, we'll always display the
 *  results in ASC order
 *
 * When it comes to sorting, we may want the results return sorted as we do, or we may want the application to sort them.
 * We then have to decide based on the characteristics of the application we're working on for example, how large we expect return data sets
 *  to be or whether the user can change the sort order
 *
 * So let's proceed and add the Constant for querying based on the artist id
 *
 *       public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM " + TABLE_ALBUMS +
            " WHERE " + COLUMN_ALBUM_ARTIST + " = ? ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";
 *
 *
 * Then add PreparedStatement instance variable
 *
 *      private PreparedStatement queryAlbumByArtistId;
 *
 * Then initialize our instance variable in the open()
 *
 *      queryAlbumByArtistId = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
 *
 * And close the associated resources related to queryAlbumByArtistId ResultSet
 *
 *       if (queryAlbumByArtistId != null)
            queryAlbumByArtistId.close();
 *
 * Then add queryAlbumsForArtistId(int id) to get the albums for an artist
 *  - Takes an artist id
 *  - Return a List<Album> objects
 *
 *       queryAlbumByArtistId.setInt(1,id);
         ResultSet resultSet = queryAlbumByArtistId.executeQuery();

         List<Album albumsList = new ArrayList<>();
         while(resultSet.next()){
            Album album = new Album();

            album.setId(resultSet.getInt(1));
            album.setName(resultSet.getString(2));
            album.setArtistId(id);

            albumsList.add(album);
        }
        return albumsList;
 *  - Catch any SQLException if any
 *      - Print error message to the user
 *      - return null
 *
 * ///////
 * At this point we can now get a List<Album> - a list of albums - based on an artist id
 * But now we need a way to display them ,
 * What we're going to do is reuse the existing table to do this and only display the album name
 *
 * Fortunately, the Artist and Album classes , stores the artist name in a variable name
 * Therefore the Data Binding will work for both types of data
 * This is probably bit of a hack, and we wouldn't really do this in a real world application
 * We'd probably get around that by having multiple tables each on a tab or perhaps have multiple tables but only 1 visible at a time
 *
 * Another way to do this is to use a Combo box, like the SQLite Browser does and display the selected table , but since this is not
 *  a UI lecture, we're going to go down the easiest route
 *
 * We could refactor the fx-id and the artistTable variable names, but we're going to leave them as they are as well
 *
 * //////// Event Handler
 *
 * But we'll need an event handler that will respond when the user selects an artist and clicks the "Show Albums(artist) Button" in our
 *  GUI interface
 * And it's going to be very similar to the one displaying the List of Artists
 * But since we're only going to call it when the button is pressed, we can go ahead and use an anonymous task for this
 *
 * So, let's go to the Controller.java and write the event handler for the queryAlbumsByArtist button
 *
 *      @FXML
        public void listAlbumsForArtist(){
        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();

        if (artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }
        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(
                        Datasource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };
        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }
 *
 * We then need to do some changes in 2 places
 *
 *  1. In the Albums class and update the fields to use SimpleIntegerProperty and SimpleStringProperty respectively
 *      - Similar to what we did in the Artist class
 *
 *      private SimpleIntegerProperty id;
        private SimpleStringProperty name;
        private SimpleIntegerProperty artistId;

        public Album() {
            this.id = new SimpleIntegerProperty();
            this.name = new SimpleStringProperty();
            this.artistId = new SimpleIntegerProperty();
        }

         public int getId() {
            return id.get();
        }

        public void setId(int id) {
            this.id.set(id);
        }

        - Update the other getters and setters respectively
        - Ensure they still return primitive , int or String,
        - And then likewise for the Setters, takes primitive types of : int or String

 * But notice we're still getting an error that the property cannot be applied from the sample.model.Artist because it's looking for
 *  Album
 * And that's because of the definition below, because we've made it specific to an Artist
 *
 *       private TableView<Artist> artistTable;
 *
 * This is a little bit of a hack  but to get around it, we'll just remove the type and be more generic with what we're saying the TableView
 *  contains , the error then disappears
 * And because we've got the same property name in Album, name here which we'll be displaying on the Screen and also for artist, this will
 *  then work
 *
 *  2. Bind the event handler method to our "Show Albums (artist)" button in the fxml file using onAction property
 *
 *      <Button onAction="#listAlbumsForArtist" maxWidth="Infinity" mnemonicParsing="false" text="Show Albums (artist)" />
 *
 *
 * ///////
 * And now if we run this, we should firstly find the list of artists that comes up correctly
 * And then now if we select a particular artist, say "AC DC" for example and click on "Show Albums (artist)" button we get a list in this
 *  case 2 entries for "AC DC" for the Albums
 * And clearly we can see that it's working okay
 *
 *
 *
 *
 * ////////////////////////
 * Add Progress Bar
 * ////////////////////////
 * ////////////////////////
 *
 * So let's run the project again
 * We get our artists loaded at the start and we can select a particular artist, say Fleetwood Mac and if we click "Show Album(artist)" button
 *  we get the albums for that artist displayed
 * Obviously, the contents of the table are being replaced by the 5 Fleetwood Mac albums that are in the database
 *
 * If we wanted to go back to the List of Artists, we'd have to click on the "List Artists" Button
 * But first, we need to assign an event handler to that button
 * What we can do is to use the same method we call from start() which is the listArtist() in this case
 *
 * So, first we need to annotate it so that we can attach it in our fxml file to the "List Artists" Button
 *
 *      <Button onAction="#listArtists" maxWidth="Infinity" mnemonicParsing="false" text="List Artists" />
 *
 * Now if we run this and select an artist, and click "Show Albums(artist)" , the albums for the artist are displayed
 * And now if we click "List Artists" Button , we get the List of artists populated back on the screen
 *
 * One thing we have not tested is , if we haven't selected an artist in the table and we click on "Show Albums(artist)" Button,
 * And if we do that , you notice down at the console, we get a message popping up saying "No Artist Selected"
 *
 * And if we select "AC DC " again, and click "Show Albums(artist)" , we get that this works and obviously we don't get another message and
 *  then if we click "List Artists" we get the Same List of Artists populated again
 *
 * ////
 * It's a bit of a hack what we've done here by using the same table for different data and we've managed it to work
 * This would probably be the sort of thing you would do as a developer while prototyping
 *
 * Now let's turn to the Progress Bar that's currently in the layout but not visible
 * We covered how to use a Progress Bar with a background task in the Threading Section, and we'll not spend too much time on the implementation
 *  here.
 * When we're fetching a large no of records in the database, it would be nice to let the user know that the application is working
 *
 * We use an indeterminate Progress Bar, because we fetch the records using one query and we don't know how many records there are
 * Even if we queried the count, we still couldn't report progress because we get the records back in one lump rather than individually
 * Another way of doing it possibly would be to query the records in blocks
 * For example,
 *  - We could ask for records 1 - 100 , then 101 - 200 etc and report progress between each query but that's probably outside of the scope
 *     of this course
 * So all we can really do is let the user know that the application is working
 *
 * First,
 *  - Let's go to our main.fxml file and add an fx:id to our ProgressBar control and set that equal to progressBar
 *
 *      fx:id="progressBar"
 *
 * Second
 *  - Is go back to the Controller.java and add an instance/entry/property for it
 *
 *      private ProgressBar progressBar;
 *
 * So when we're fetching data from the database, we need to make the progress bar visible
 * And when the task completes, we need to make it invisible by setting it's visible property to false
 *
 *
 * //// Implementation
 * So let's implement this for the GetAllArtistsTask
 * But keep in mind that we can't set the visibility from inside the call() , and that's because any code that touches a UI control has to
 *  run on the UI thread
 * So we'll use the task.setOnSucceeded() and task.setOnFailed() methods to set the progress bar visibility back to false
 * Then we need to bind the progress bar to the task
 *
 * Note we're calling GetAllArtistsTask from listArtists() and mentioned we can't put the code in the call() because any code that touches the
 *  a UI control has to run in the UI thread and therefore, we'll have to do it in listArtists()
 *
 * We'll just do this after binding artistTable (TableView) with the task
 *
 * So let's bind our progress bar to the Task
 *
 *      progressBar.progressProperty().bind(task.progressProperty());
 *
 * Then we'll set the visibility property to true
 *
 *      progressBar.setVisible(true);
 *
 * And finally call task.setOnSucceeded() and task.setOnFailed() and set visibility back to false
 *
 *      task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

 * This means whether we succeed or fail, we want the progress bar to disappear
 * We're also using lambda expressions to set the progress bar visibility
 *
 *
 * //////
 * There are other setOn methods in the Task class,
 * For example:
 *  - We could set code to run if the task is cancelled perhaps due to user action,
 * But in this case we only have to worry about the success and failure cases for this demo application
 * Because our task doesn't report any progress, the progress bar will be indeterminate
 *
 * We could run this now, but the query completes so quickly that we wouldn't see the Progress bar
 * So what we can do is slow things down a little bit by adding a call to Thread.sleep while we're looping through the result set in the
 *  queryArtist() as follows
 *
 *      try{
              Thread.sleep(20);
           }catch (InterruptedException ie){
              System.out.println("Interrupted: "+ ie.getMessage());
        }
 *
 * We'll add this in the while loop and we're going to simulate a bit of a delay , so that we can see progress bar in operation
 * We've added a code, so that for every artist, the thread is going to sleep for 20 milliseconds
 *
 * /////
 *
 * And now if we run this, we can see the progress bar down at the bottom and then it disappears once the artists are loaded up
 *  successfully
 * Again, we saw that it was an indeterminate progress bar, rather than showing increasing progress bar, the bar's going to animate
 *  backwards and forwards as we saw, from left to right and then from right to left
 * Obviously it then disappeared when the table was finished loading
 *
 * ///////
 * So that's it for queries
 *
 * The important thing to remember is that we always want to run a query on a background thread using something like a task
 * When we're ready to update the User Interface, we have to do so on the JavaFX application UI thread
 * And if we're using Data Binding, the UI code will run on the UI thread automatically
 *
 * If not, you'd want to use Platform.runLater or a method like Task.setOnSucceeded to run the UI code on the UI thread
 *
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