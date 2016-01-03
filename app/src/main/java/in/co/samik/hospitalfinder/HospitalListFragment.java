package in.co.samik.hospitalfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HospitalListFragment extends Fragment {
    public ArrayAdapter<String> mHospitalAdapter;
    public JSONArray hospitalJSONArray;
    private static final String LOG_TAG = HospitalListFragment.class.getSimpleName();

    public void setHospitalArray(JSONArray hospitalArray) {
        this.hospitalJSONArray = hospitalArray;
    }



    public HospitalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FetchHospitalTask task = new FetchHospitalTask();
        task.execute();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] data = {"Hospital 1", "Hospital 2", "Hospital 3"};

        List<String> weekForecast= new ArrayList<String>(Arrays.asList(data));

        mHospitalAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_hospital,
                R.id.list_item_forecast_textview,
                weekForecast);


        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mHospitalAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    JSONObject hospitalObject = hospitalJSONArray.getJSONObject(position);
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT,hospitalObject.toString());
                    startActivity(detailIntent);
                }
                catch (JSONException e){
                    Log.d(LOG_TAG,e.getMessage(),e);
                }
            }
        });

        return rootView;
    }

    public class FetchHospitalTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchHospitalTask.class.getSimpleName();
        private final String RESOURCE_ID = "7d208ae4-5d65-47ec-8cb8-2a7a7ac89f8c";
        private final String API_KEY="0b2e47ab36b36a89ffc458deaef7442f";
        JSONArray hospitalArray;

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String hospitalJsonStr = null;


            int numDays = 7;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri uri = Uri.parse("https://data.gov.in/api/datastore/resource.json?").buildUpon()
                        .appendQueryParameter("resource_id", RESOURCE_ID)
                        .appendQueryParameter("api-key", API_KEY)
                        .build();

                URL url = new URL(uri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    hospitalJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                    Log.i(LOG_TAG, line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    hospitalJsonStr = null;
                }
                hospitalJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                hospitalJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            Log.v(LOG_TAG,hospitalJsonStr);

            try {
                return getHospitalDataFromJson(hospitalJsonStr);
            }catch(JSONException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                return null;
            }
        }

        /**
         * Take the String representing the complete hospital list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         */
        private String[] getHospitalDataFromJson(String hospitalJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String JSON_RECORDS = "records";
            final String JSON_HOSPITAL_NAME = "hospitalname";

            JSONObject hospitalJSON = new JSONObject(hospitalJsonStr);
            hospitalArray = hospitalJSON.getJSONArray(JSON_RECORDS);

            String[] resultStrs = new String[hospitalArray.length()];
            for(int i = 0; i < hospitalArray.length(); i++) {
                // Get the JSON object representing the hospital
                JSONObject hospitalObject = hospitalArray.getJSONObject(i);
                resultStrs[i] = hospitalObject.getString(JSON_HOSPITAL_NAME);
            }

            return resultStrs;

        }

        @Override
        protected void onPostExecute(String[] resultStrs) {
            hospitalJSONArray=hospitalArray;
            mHospitalAdapter.clear();
            mHospitalAdapter.addAll(Arrays.asList(resultStrs));
        }
    }
}
