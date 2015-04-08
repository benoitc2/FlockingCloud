package edu.msu.wegschei.flocking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Spencer on 4/7/2015 in Flocking.
 */
public class LogInDlg extends DialogFragment {

    /**
     * Error code for failed login
     */
    private int errorCode;

    private static final int WRONG_ID = 1;
    private static final int WRONG_PW = 2;
    private static final int NO_ID = 3;
    private static final int NO_PW = 4;
    private static final int LOGIN_FAIL = 5;

    /**
     * Id for the user
     */
    private String userId;

    /**
     * Setter for 'userId'
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
     * Password for the user
     */
    private String userPw;

    /**
     * Setter for 'userPw'
     * @param userPw
     */
    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.wait);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        // Create the dialog box
        final AlertDialog dlg = builder.create();

        // Get a reference to the current view
        final View view = getActivity().getWindow().getDecorView().getRootView();

        // Create a thread to load the catalog
        new Thread(new Runnable() {

            @Override
            public void run() {
                FlockCloud cloud = new FlockCloud();
                InputStream stream = cloud.loginToServer(userId, userPw);

                // Test for an error
                boolean fail = stream == null;
                if(!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");

                        xml.nextTag();      // Advance to first tag
                        xml.require(XmlPullParser.START_TAG, null, "flocking");
                        String status = xml.getAttributeValue(null, "status");
                        if(status.equals("yes")) {
                            // Login success!
                        } else {
                            // Failed login, parse error message from the server
                            String msg = xml.getAttributeValue(null, "msg");
                            if(msg.equals("password error")) {
                                errorCode = WRONG_PW;
                            }
                            else if(msg.equals("user does not exist")) {
                                errorCode = WRONG_ID;
                            }
                            else if(msg.equals("no pw")) {
                                errorCode = NO_PW;
                            }
                            else if(msg.equals("no user")) {
                                errorCode = NO_ID;
                            }
                            else {
                                errorCode = LOGIN_FAIL;
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
                boolean post = view.post(new Runnable() {

                    @Override
                    public void run() {
                        dlg.dismiss();
                        if (fail1) {
                            switch (errorCode) {
                                case WRONG_ID:
                                    Toast.makeText(view.getContext(), R.string.userIdError, Toast.LENGTH_SHORT).show();
                                    break;

                                case WRONG_PW:
                                    Toast.makeText(view.getContext(), R.string.pwError, Toast.LENGTH_SHORT).show();
                                    break;

                                case NO_ID:
                                    Toast.makeText(view.getContext(), R.string.noUserId, Toast.LENGTH_SHORT).show();
                                    break;

                                case NO_PW:
                                    Toast.makeText(view.getContext(), R.string.noUserPw, Toast.LENGTH_SHORT).show();
                                    break;

                                case LOGIN_FAIL:
                                    Toast.makeText(view.getContext(), R.string.loginError, Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        } else {

                            if (getActivity() instanceof LoginActivity) {
                                LoginActivity la = (LoginActivity) getActivity();
                                Intent intent = new Intent(la, MainActivity.class);
                                la.startActivity(intent);
                            }
                        }

                    }

                });

            }

        }).start();

        return dlg;
    }
}
