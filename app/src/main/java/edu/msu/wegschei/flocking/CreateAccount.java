package edu.msu.wegschei.flocking;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


public class CreateAccount extends ActionBarActivity {

    private EditText userName;
    private EditText userPassword;
    private EditText userPasswordConfirm;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_user_create);

        this.userName = (EditText)findViewById(R.id.userName);
        this.userPassword = (EditText)findViewById(R.id.userPassword);
        this.userPasswordConfirm = (EditText)findViewById(R.id.userPasswordConfirm);
    }

    public void onCreateUser(final View view) {


        // onReturnToLogin();  //temp call for testing, does not keep only 1 copy of activity on stack atm

        if(userPassword.getText().toString().equals(userPasswordConfirm.getText().toString()) && userName.getText().toString().length() > 0) {
            //Requirements met, query server to create this user
            Toast.makeText(view.getContext(), R.string.creatingUser, Toast.LENGTH_SHORT).show();

            // Create a thread to save the hatting
            new Thread(new Runnable() {

                @Override
                public void run() {
                    FlockCloud cloud = new FlockCloud();
                    InputStream stream = cloud.createUser(userName.getText().toString(),userPassword.getText().toString());
                    //cloud.logStream(stream);
                    boolean fail = stream == null;
                    boolean nameTaken = false;
                    if(!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");

                            xml.nextTag();      // Advance to first tag
                            xml.require(XmlPullParser.START_TAG, null, "flocking");
                            String status = xml.getAttributeValue(null, "status");
                            if(status.equals("yes")) {
                                // User Created!
                            } else {
                                // Failed creating a new user, check if it is because the name is already taken.
                                String msg = xml.getAttributeValue(null, "msg");
                                if(msg.equals("name already taken")) {
                                    // name is already taken!
                                    nameTaken = true;
                                }
                                fail = true;
                            }

                        } catch(IOException ex) {
                            fail = true;
                        } catch(XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch(IOException ex) {
                            }
                        }
                    }

                    final boolean fail1 = fail;
                    final boolean nameTaken1 = nameTaken;
                    if(fail) {
                        /*
                         * If we fail to save, display a toast
                         */
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                if(nameTaken1) {
                                    Toast.makeText(view.getContext(), R.string.nameTaken, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(view.getContext(), R.string.createFail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.createSuccess, Toast.LENGTH_SHORT).show();
                                onReturnToLogin();
                            }
                        });

                    }

                }
            }).start();

        }
        else {
            if(!userPassword.getText().toString().equals(userPasswordConfirm.getText().toString())) {
                //Password and confirm password does not match error message
                Toast.makeText(view.getContext(), R.string.noPwMatch, Toast.LENGTH_SHORT).show();
            }
            else if(userName.length() == 0) {
                //Username must be at least 1 character error message
                Toast.makeText(view.getContext(), R.string.userIdWarning, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onReturnToLogin(){
        this.finish();
    }

}
