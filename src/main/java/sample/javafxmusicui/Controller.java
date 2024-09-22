package sample.javafxmusicui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import sample.javafxmusicui.model.Album;
import sample.javafxmusicui.model.Artist;
import sample.javafxmusicui.model.Datasource;

/*
 * The reason we've created GetAllArtistsTask class outside the Controller, is because we might need it in 2
 *  places
 *  i)  At startup
 *  ii) When the user explicitly asks to see all artists
 * That's why we're not using an anonymous Task class
 *
 * The GetAllArtistsTask is extending Task class, and if we want to use the Data binding to populate the table,
 *  the call() which we've reconfigured has to return an ObservableArrayList<Artist>
 * We've overwritten the call() to call the queryArtist() which returns a list in our Datasource.java file
 * We don't want to change it to return an ObservableArrayList , because that would violate the separation between
 *  the model & UI code.Instead, we're creating an observable list from the list that the queryArtist() returns
 *
 * Tim said we can use the Model class as is, and that would be true if we didn't want to take advantage of a data
 *  binding,
 * But since we do want to take advantage of that, we do need to make 1 small change to the Artist class to achieve
 *  that
 * Instead of storing the Artist name in a String, we need to store it as SimpleStringProperty
 * This doesn't violate the Model-UI separation because SimpleStringProperty isn't a UI specific class
 * We could have used it all along actually and we'll do the same for both the artist id even though we won't be
 *  displaying it
 * Since the field will now be properties, the Getter and Setter methods are going to change as well
 *
 * So let's proceed to the Artist class and make those changes in the Artist class
 *
 * Again we're only doing this because we want to take advantage of data binding
 * Since this is a simple application, if we wanted to keep our model classes exactly as they were, then we could do
 *  that and we would explicitly then set the table items when the task completes
 *
 * There are 2 ways we could set the items
 *  1. Covered in the Threading section of the course : We could perform Platform.runLater when the queryArtist()
 *      returns it's results
 *      - In the Runnable we pass to runLater , we could set the table items
 *      - Tim also mentioned that we can do more with tasks than we covered, including running code when the call()
 *         completes
 *      - To do that we need to call Task.setOnSucceeded which takes an event handler
 *      - In our case, we'll pass a lambda and do something like this
 *
 *          task.setOnSucceeded(e -> artistTable.getItems().setAll(artistResults));
 *      - That's ultimately what we'll going to be doing, making this a little bit easier to call when the call()
 *         completes
 *
 * ////
 * Whenever data binding makes sense to use it , because we don't have to do anything when the task completes,
 *  it's all handled automatically
 * We need to do 1 more thing for data binding to work though, and that is mapping the name field in the Artist
 *  class to the name column in the table
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
 * Controller
 * Now we can add a method to the controller that get's the artists
 * Create a reference to the artistTable
    private TableView<Artist> artistTable;
 *
 * And add the actual method listArtists
 *  - We've created a new Task obj by creating GetAllArtistsTask instance
 *  - Then we're binding the result of the task, the Artist ObservableList to the TableView items property so
 *     that they're bound to each other
 *
 *
 *
 * /// Fix Artist and Preload records
 * We have binded the results of the Task, the artist's observable list to the TableView items property through
 *   the listArtist() below , and also adding <cellValueFactory> using the <PropertyValueFactory> in the main.fxml
 * Because the Controller isn't created until this fxml is loaded, and that happens in the start() and also
 *   because we want to be sure that the UI has been built before we try and load these results
 * We'll initiate the query of the artists from the start() in our Main.java class
 * We'll need access to the controller and change that code to :
 *
 *      Parent root = fxmlLoader.load();

        Controller controller = fxmlLoader.getController();
        controller.listArtists();
 *
 *
 * At the moment if we run this, we're getting an error from our Datasource class and that's because we didn't set
 *  up our getters properly
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
 *  - Do the same thing for the Artist class
 *
 * Then create a no args constructor to initialize both the id and the name to avoid getting NullPointerException
 *
 * And now the errors disappeared from the Datasource class
 */
public class Controller {
    @FXML
    private TableView artistTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void listArtists(){
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);
        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();
    }

    @FXML
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
}



class GetAllArtistsTask extends Task {
    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(
                Datasource.getInstance().queryArtist(Datasource.ORDER_BY_ASC));
    }
}