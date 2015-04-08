package edu.msu.wegschei.flocking;



import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spencer on 4/7/2015 in Flocking.
 * This class handles all client-side communication to the server
 */
class FlockCloud {
    private static final String MAGIC = "3XrP3Q4IMunPcp";
    private static final String LOGIN_URL = "http://webdev.cse.msu.edu/~salpeka1/cse476/project2/login_user.php";
    private static final String CREATE_USER_URL = "http://webdev.cse.msu.edu/~salpeka1/cse476/project2/create_user.php";


    /**
     * Check user id and password from the server.
     * @param id id for user
     * @param pw password for user
     * @return reference to an input stream or null if this fails
     */
    public InputStream loginToServer(String id, String pw) {

        // Create a get query
        String query = LOGIN_URL + "?user=" + id + "&magic=" + MAGIC + "&pw=" + pw;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Create a new user
     * @param id id for the new user
     * @param pw password for the new user
     * @return reference to an input stream or null if this fails
     */
    public InputStream createUser(String id, String pw) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(CREATE_USER_URL);

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("user", id));
        pairs.add(new BasicNameValuePair("magic", MAGIC));
        pairs.add(new BasicNameValuePair("pw", pw));
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            return null;
        }
        InputStream stream = null;
        try {
            stream = response.getEntity().getContent();
        } catch (IOException e) {
            return null;
        }
        return stream;
    }
}
