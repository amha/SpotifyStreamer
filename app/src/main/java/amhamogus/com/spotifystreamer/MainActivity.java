package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import amhamogus.com.spotifystreamer.net.SpotifyRequest;


public class MainActivity extends Activity {

    private String TAG = "SPOTIFY APP ERROR";
    protected String query;
    protected TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to text input
        EditText searchInput = (EditText)findViewById(R.id.editText);
        searchInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean imeActionHandled = false;
                if(actionId == EditorInfo.IME_ACTION_GO){
                    if(v.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please Enter Artist Name", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new SpotifyWorkerTask().execute(v.getText().toString());
                        imeActionHandled = true;
                    }
                }
                return imeActionHandled;
            }
        });


        // Get Reference to temporary output
        mTextView = (TextView)findViewById(R.id.networkText);

//        // Show keyboard to optimize ux
//        InputMethodManager inputMethodManager =
//                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);

        //new SpotifyWorkerTask().execute(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SpotifyWorkerTask extends AsyncTask<String, String, String>{

        String networkResponse;
        SpotifyRequest call;

        protected String doInBackground(String...strings){

            try {
                call = new SpotifyRequest();
                networkResponse = call.run(strings[0]);
                return networkResponse;

            } catch (IOException e){
                Log.d(TAG, "WE HAVE A PROBLEM:" + e.toString());
                networkResponse = "Soemthing went wrong wrong with network request!";
            }

            return networkResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "network response is: " + s);
            mTextView.setText(s);
        }
    }
}
