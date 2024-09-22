package sample.javafxmusicui.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {
    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\JMC17\\Java-Masterclass-11\\"+ DB_NAME;

    // albums table
    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";

    //albums table - Columns Indices
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    //artists table
    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";

    //artists table - Columns Indices
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    //songs table
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";

    //artists table - Columns Indices
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    // sorting
    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    //Query albums by Artist Constant
    public static final String QUERY_ALBUMS_BY_ARTIST_START =
            "SELECT "+ TABLE_ALBUMS + "."+ COLUMN_ALBUM_NAME +" FROM "+ TABLE_ALBUMS +
                    " INNER JOIN "+ TABLE_ARTISTS +" ON "+ TABLE_ALBUMS +"."+ COLUMN_ALBUM_ARTIST +
                    " = "+ TABLE_ARTISTS +"."+COLUMN_ARTIST_ID+
                    " WHERE "+ TABLE_ARTISTS +"."+ COLUMN_ARTIST_NAME + " = \"";

    // Order by Constant
    public static final String QUERY_ALBUMS_BY_ARTIST_SORT =
            " ORDER BY "+ TABLE_ALBUMS +"."+ COLUMN_ALBUM_NAME +" COLLATE NOCASE ";

    // Query artist,album, song track - for a certain song
    public static final String QUERY_ARTISTS_FOR_SONG_START =
            "SELECT "+ TABLE_ARTISTS +"."+ COLUMN_ARTIST_NAME +", "+
                    TABLE_ALBUMS+ "."+ COLUMN_ALBUM_NAME +", "+
                    TABLE_SONGS+ "."+ COLUMN_SONG_TRACK + " FROM " + TABLE_SONGS +
                    " INNER JOIN "+ TABLE_ALBUMS +" ON "+
                            TABLE_SONGS +"."+COLUMN_SONG_ALBUM + "="+ TABLE_ALBUMS +"."+COLUMN_ALBUM_ID +
                    " INNER JOIN "+ TABLE_ARTISTS +" ON "
                            + TABLE_ALBUMS +"."+COLUMN_ALBUM_ARTIST + "="+ TABLE_ARTISTS +"."+COLUMN_ARTIST_ID+
                    " WHERE "+ TABLE_SONGS + "."+ COLUMN_SONG_TITLE +" =\"";

    public static final String QUERY_ARTISTS_FOR_SONG_SORT =
            " ORDER BY "+ TABLE_ARTISTS +"."+ COLUMN_ARTIST_NAME + ", "+
                    TABLE_ALBUMS+ "."+ COLUMN_ALBUM_NAME +" COLLATE NOCASE ";

    // Create artist_list view
    public static final String TABLE_ARTIST_SONG_VIEW = "artist_list";
    public static final String CREATE_ARTIST_FOR_SONG_VIEW =
            "CREATE VIEW IF NOT EXISTS "+ TABLE_ARTIST_SONG_VIEW +" AS SELECT "+
                    TABLE_ARTISTS +"."+ COLUMN_ARTIST_NAME + ", "+
                    TABLE_ALBUMS +"."+ COLUMN_ALBUM_NAME+ " AS album, "+
                    TABLE_SONGS +"."+ COLUMN_SONG_TRACK + ", "+
                    TABLE_SONGS + "."+ COLUMN_SONG_TITLE+
            " FROM "+ TABLE_SONGS +
            " INNER JOIN "+ TABLE_ALBUMS + " ON "+
                    TABLE_SONGS +"."+ COLUMN_SONG_ALBUM +" = "+ TABLE_ALBUMS +"."+COLUMN_ALBUM_ID +
            " INNER JOIN "+ TABLE_ARTISTS + " ON "+
                    TABLE_ALBUMS +"."+ COLUMN_ALBUM_ARTIST +" = "+ TABLE_ARTISTS +"."+COLUMN_ARTIST_ID +
            " ORDER BY "+ TABLE_ARTISTS +"."+ COLUMN_ARTIST_NAME + ", "+ TABLE_ALBUMS +"."+ COLUMN_ALBUM_NAME+
            ", "+ TABLE_SONGS +"."+ COLUMN_SONG_TRACK;
    public static final String QUERY_VIEW_SONG_INFO =
            "SELECT "+ COLUMN_ARTIST_NAME +" ,"+ COLUMN_SONG_ALBUM +", "+
                        COLUMN_SONG_TRACK +" FROM "+ TABLE_ARTIST_SONG_VIEW +
            " WHERE "+ COLUMN_SONG_TITLE  +"=\"";


    public static final String QUERY_VIEW_SONG_INFO_PREP =
            "SELECT "+ COLUMN_ARTIST_NAME +" ,"+ COLUMN_SONG_ALBUM +", "+
                    COLUMN_SONG_TRACK +" FROM "+ TABLE_ARTIST_SONG_VIEW +
                    " WHERE "+ COLUMN_SONG_TITLE  +"= ?";

    public static final String INSERT_ARTIST = "INSERT INTO "+ TABLE_ARTISTS +
            "("+ COLUMN_ARTIST_NAME +") VALUES(?)";

    public static final String INSERT_ALBUMS = "INSERT INTO "+ TABLE_ALBUMS +
            "("+ COLUMN_ALBUM_NAME + ", "+ COLUMN_ALBUM_ARTIST +") VALUES(?,?)";

    public static final String INSERT_SONGS = "INSERT INTO "+ TABLE_SONGS +
            "("+ COLUMN_SONG_TRACK + ", "+ COLUMN_SONG_TITLE + ", "+ COLUMN_SONG_ALBUM +") VALUES(?,?,?)";

    public static final String QUERY_ARTIST = "SELECT "+ COLUMN_ARTIST_ID +" FROM "+ TABLE_ARTISTS +
            " WHERE "+ COLUMN_ARTIST_NAME +" = ?";

    public static final String QUERY_ALBUM = "SELECT "+ COLUMN_ALBUM_ID +" FROM "+ TABLE_ALBUMS +
            " WHERE "+ COLUMN_ALBUM_NAME +" = ?";

    public static final String QUERY_SONG = "SELECT "+ COLUMN_SONG_ID +" FROM "+ TABLE_SONGS +
            " WHERE "+ COLUMN_SONG_TITLE +" = ?";


    // Initialize connection obj
    private Connection conn;
    private PreparedStatement querySongInfoView;

    //Add PreparedStatements instances for the 3 INSERTS
    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;
    private PreparedStatement insertIntoSongs;
    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;
    private PreparedStatement querySong;

    /*
     * Create a variable that will hold that 1 instance of the class , that every other class in the app will use
     * This variable would need to be static
     */
    private static Datasource instance = new Datasource();

    /* Singleton - No other class, should create an instance of this class */
    private Datasource(){

    }

    /* Singleton method
     *
     * Add a method that every other class will use to access the instance
     * We can create the singleton instance when we declare the variable or we can create it in the static access
     *  method
     * And we'll do just that in this case , and call it getInstance
     *
     * When an obj wants to use the Singleton instance, it calls getInstance()
     * The method checks to see if the singleton instance has been previously created.
     * IF it has:
     *  - it returns it
     * Otherwise
     *  - creates one and return it
     *
     * This is called lazy instantiation because the instance isn't created until the first time it's needed
     * This is perfectly valid code, but keep in mind that it's not Thread safe
     * It's possible for a thread to be interrupted after the check for null here
     * Another thread could run, check for null and create the instance
     * Then the first thread can run and create the 2nd instance and consequently, our application would have then
     *  2 instances on the go which then defeats the purpose of using a singleton
     * So, what we can do here is we can create the instance when the instance variable is declared and that will be
     *  Thread-safe
     *
     * Update
     *      private static Datasource instance;
     * To
     *      private static Datasource instance = new Datasource();
     *
     * This is also lazy instantiation because the instance won't be created until the first time the class is loaded
     * which will be the first time some other instances references the class by calling getInstance()
     * Now we can update the getInstance() to just return the instance only
     * Whenever a class now wants to call a method in the Datasource class, it will then do
     *     Datasource.getInstance().methodName()
     * That's going to be the calling convention used to access all the methods in this particular class
     */
    public static Datasource getInstance(){
        return instance;
    }
    public boolean open(){
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySongInfoView = conn.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);

            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST,Statement.RETURN_GENERATED_KEYS);
            insertIntoAlbums = conn.prepareStatement(INSERT_ALBUMS,Statement.RETURN_GENERATED_KEYS);
            insertIntoSongs = conn.prepareStatement(INSERT_SONGS);

            queryArtist = conn.prepareStatement(QUERY_ARTIST);
            queryAlbum = conn.prepareStatement(QUERY_ALBUM);
            querySong = conn.prepareStatement(QUERY_SONG);

            return true;
        }catch (SQLException exc){
            System.out.println("Couldn't connect to database : "+exc.getMessage());
            return false;
        }
    }

    public void close(){
        try{
            if (querySongInfoView != null)
                querySongInfoView.close();

            if (insertIntoArtists != null)
                insertIntoArtists.close();

            if (insertIntoAlbums != null)
                insertIntoAlbums.close();

            if (insertIntoSongs != null)
                insertIntoSongs.close();

            if (queryArtist != null)
                queryArtist.close();

            if (queryAlbum != null)
                queryAlbum.close();

            if (querySong != null)
                querySong.close();

            if (conn != null)
                conn.close();
        }catch (SQLException exc){
            System.out.println("Couldn't close connection "+exc.getMessage());
        }
    }

    // Query artists table - try-with-resources
    public List<Artist> queryArtist(int sortOrder){
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        if (sortOrder != ORDER_BY_NONE){
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_DESC)
                sb.append("DESC");
            else
                sb.append("ASC");
        }

        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sb.toString())) {

            List<Artist> artists = new ArrayList<>();
            while (results.next()){
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }
            return artists;
        }catch (SQLException exc){
            System.out.println("Query failed: "+exc.getMessage());
            return null;
        }
    }
    public List<String> queryAlbumsForArtist(String artistName , int sortOrder){

        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTIST_START);
        sb.append(artistName);
        sb.append("\"");

        if (sortOrder != ORDER_BY_NONE){
            sb.append(QUERY_ALBUMS_BY_ARTIST_SORT);
            if (sortOrder == ORDER_BY_DESC)
                sb.append("DESC");
            else
                sb.append("ASC");
        }
        System.out.println(sb.toString());

        try(Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sb.toString())){

            List<String> albums = new ArrayList<>();
            while (resultSet.next()){
                albums.add(resultSet.getString(1));
            }
            return albums;

        }catch (SQLException exc){
            System.out.println("Error retrieving albums");
            return null;
        }

    }

    public void querySongsMetadata(){
        String sql = "SELECT * FROM "+ TABLE_SONGS;

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sql)){

            ResultSetMetaData meta = results.getMetaData();
            int numOfColumns = meta.getColumnCount();
            for (int i = 1; i < numOfColumns; i++) {
               System.out.format("Column %d in the songs table is named %s\n", i , meta.getColumnName(i));
            }
        }catch (SQLException exc){
            System.out.println("Query failed: "+exc.getMessage());
        }
    }

    public int getCount(String table){
        String sql = "SELECT COUNT(*), MIN(_id) FROM "+ table;

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sql)){

            /* Using column index */
            int count = results.getInt(1);
            int min = results.getInt(2);

            System.out.printf("Count = %d , Min = %s\n",count , min);
            return count;
        }catch (SQLException exc){
            System.out.println("Query failed: "+exc.getMessage());
        }
        return -1;

    }

    public boolean createViewForSongArtists(){
        try(Statement statement = conn.createStatement()){

            statement.execute(CREATE_ARTIST_FOR_SONG_VIEW);
            return true;
        }catch (SQLException exc){
            System.out.println("Create View failed: "+exc.getMessage());
        }
        return false;
    }
    private int insertArtist(String name) throws SQLException{

        queryArtist.setString(1, name);
        ResultSet resultSet = queryArtist.executeQuery();
        // if a record was found - means artist already exists - we don't need to do anything
        // proceed with the insert in the else statement
        if (resultSet.next()){
            return resultSet.getInt(1);
        }else{
            // Insert the artist, since they're not on file
            insertIntoArtists.setString(1,name);
            int affectedRows = insertIntoArtists.executeUpdate(); // save and let us know how many rows inserted
            if (affectedRows != 1)
                throw new SQLException("Couldn't insert artist");
        }

        // Retrieve generated key after successful insertion
        ResultSet generatedKeys = insertIntoArtists.getGeneratedKeys();
        if (generatedKeys.next())
            return generatedKeys.getInt(1);
        else
            throw new SQLException("Couldn't get _id for artist");

    }

    private int insertAlbum(String name, int artistId) throws SQLException{

        queryAlbum.setString(1, name);
        ResultSet resultSet = queryAlbum.executeQuery();
        // if a record was found - means album already exists - we don't need to do anything
        // proceed with the insert in the else statement
        if (resultSet.next()){
            return resultSet.getInt(1);
        }else{
            // Insert the album, since it's not on file
            insertIntoAlbums.setString(1,name);
            insertIntoAlbums.setInt(2,artistId);
            int affectedRows = insertIntoAlbums.executeUpdate(); // save and let us know how many rows inserted
            if (affectedRows != 1)
                throw new SQLException("Couldn't insert album");
        }

        // Retrieve generated key after successful insertion
        ResultSet generatedKeys = insertIntoAlbums.getGeneratedKeys();
        if (generatedKeys.next())
            return generatedKeys.getInt(1);
        else
            throw new SQLException("Couldn't get _id for album");

    }
    public void insertSong(String title, String artist , String album ,int track){
        try{
            querySong.setString(1, title);
            ResultSet resultSet = querySong.executeQuery();
            // if a record was found - means album already exists - we don't need to do anything
            // proceed with the insert in the else statement
            if (resultSet.next()) {
                System.out.println("Song Already Exists, Duplicate not allowed!");
                return;
            }
            /*
             * start the transaction by turning off the auto-commit behaviour of the conn obj
             * default behavior is to commit every change, and the DB does that by running every update,delete and
                 insert statement as a transaction
             */
            conn.setAutoCommit(false);

            //get artistId for the artist passed to this method
            //will return artist id for an existing id, or the id for the newly inserted artist record
            int artistId = insertArtist(artist);

            //get albumId for the album passed to this method for a specific artist(passed artistId returned above)
            //will return album id for an existing id, or the id for the newly inserted album record
            int albumId = insertAlbum(album,artistId);

            //Set various filed for inserts : track, title, album
            insertIntoSongs.setInt(1, track);
            insertIntoSongs.setString(2,title);
            insertIntoSongs.setInt(3,albumId);

            // Insert and return affected rows
            int affectedRows = insertIntoSongs.executeUpdate(); // save and let us know how many rows inserted

            // If we get 1 row affected, successful insertion, commit the transaction
            if (affectedRows == 1)
                conn.commit();
            else
                throw new SQLException("The Song insert failed");

        }catch (Exception exc){
            // If something goes wrong, call rollback , which rolls back changes of out transaction
            System.out.println("Insert song exception: "+ exc.getMessage());
            try{
                System.out.println("Performing rollback");
                conn.rollback();
            }catch (SQLException exc2) {
                System.out.println("Oh boy!, Things are really bad: " + exc2.getMessage());
            }
        }finally {
            // setting auto-commit to true to return to the default auto-commit behavior
            // done here, whether the transaction succeeds or fails
            try{
                System.out.println("Resetting default commit behavior");
                conn.setAutoCommit(true);
            }catch (SQLException exc){
                System.out.println("Couldn't reset auto-commit! "+exc.getMessage());
            }
        }

    }
}
