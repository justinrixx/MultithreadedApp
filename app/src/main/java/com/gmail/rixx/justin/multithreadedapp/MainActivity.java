package com.gmail.rixx.justin.multithreadedapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

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


    /**
     * CreateFiles create the two text files to be read into the ListViews
     * @param view the button that started the method
     */
    public void createFiles(View view) {
        // create and execute the tasks
        new WriteEvensTask(this).execute("evens.txt");
        new WriteOddsTask(this).execute("odds.txt");
    }

    /**
     * ReadFiles
     * @param view the button that started the method
     */
    public void readFiles(View view) {
        new ReadFilesTask(this).execute("evens.txt", "odds.txt");
    }

    public void clear(View view) {
        // get the ListViews
        final ListView evens = (ListView) findViewById(R.id.evens);
        final ListView odds  = (ListView) findViewById(R.id.odds);

        evens.setAdapter(null);
        odds.setAdapter(null);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private abstract class WriteFileTask extends AsyncTask <String, Integer, Void> {

        protected int progress = 0;

        protected Context mContext;

        public WriteFileTask(Context context) {
            mContext = context;
        }

        protected abstract Void writeFile(File file);

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // update the progress bar
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setProgress(values[0] * 2);
        }

        @Override
        protected Void doInBackground(String... params) {

            File file = new File(mContext.getFilesDir(), params[0]);

            writeFile(file);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // show the progress bar
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);

//            Log.v("Threads", "In onPreExecute");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // hide the progress bar
            //ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            //bar.setVisibility(View.INVISIBLE);
        }
    }

    private class WriteEvensTask extends WriteFileTask {

        public WriteEvensTask(Context c) {
            super(c);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(mContext, "Finished writing file 1", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void writeFile(File file) {

            try {
                PrintWriter writer = new PrintWriter(file);

                for (int i = 2; i < 102; i += 2) {
                    writer.println(i);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    publishProgress(++progress);
                }
                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class WriteOddsTask extends WriteFileTask {

        public WriteOddsTask(Context c) {
            super(c);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(mContext, "Finished writing file 2", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void writeFile(File file) {

            try {
                PrintWriter writer = new PrintWriter(file);

                for (int i = 1; i < 101; i += 2) {
                    writer.println(i);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    publishProgress(++progress);
                }

                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class ReadFilesTask extends AsyncTask <String, Integer, List<List<Integer>>> {

        Context mContext;

        private int progress = 0;

        public ReadFilesTask(Context c) {
            mContext = c;
        }

        @Override
        protected List<List<Integer>> doInBackground(String... params) {

            List<List<Integer>> list = new ArrayList<>();

            try {
                for(int i = 0; i < params.length; i++) {
                    // open a file
                    File file = new File(mContext.getFilesDir(), params[i]);

                    BufferedReader reader = new BufferedReader(new FileReader(file));

                    list.add(new ArrayList<Integer>());

                    String temp;

                    // read everything into a List
                    while (null != (temp = reader.readLine())) {
                        list.get(i).add(Integer.parseInt(temp));

                        // simulate a long process
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // update the progress bar
                        publishProgress(++progress);
                    }
                    reader.close();
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(mContext, "File doesn't exist", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                Toast.makeText(mContext, "File error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            }

            return list;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // update the progress bar
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<List<Integer>> lists) {
            // get the ListViews
            final ListView evens = (ListView) findViewById(R.id.evens);
            final ListView odds  = (ListView) findViewById(R.id.odds);

            // fill the adapters with the datasets
            ArrayAdapter<Integer> evenAdapter =
                    new ArrayAdapter<>(mContext, R.layout.listview_item, R.id.list_item_text, lists.get(0));
            ArrayAdapter<Integer> oddAdapter =
                    new ArrayAdapter<>(mContext, R.layout.listview_item, R.id.list_item_text, lists.get(1));

            // the the adapters to the ListViews
            evens.setAdapter(evenAdapter);
            odds.setAdapter(oddAdapter);

            evenAdapter.notifyDataSetChanged();
            oddAdapter.notifyDataSetChanged();

            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);
        }
    }
}
