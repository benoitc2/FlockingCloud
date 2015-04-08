package edu.msu.wegschei.flocking;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Spencer on 4/7/2015 in Flocking.
 * This class handles all client-side communication to the server
 */
public class FlockCloud {
    private static final String MAGIC = "3XrP3Q4IMunPcp";
    private static final String LOGIN_URL = "http://webdev.cse.msu.edu/~salpeka1/cse476/project2/login_user.php";
    private static final String CREATE_USER_URL = ""; // empty for now

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     * @param xml the parser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static void skipToEndTag(XmlPullParser xml)
            throws IOException, XmlPullParserException {
        int tag;
        do
        {
            tag = xml.next();
            if(tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while(tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }

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

            InputStream stream = conn.getInputStream();

            return stream;

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
