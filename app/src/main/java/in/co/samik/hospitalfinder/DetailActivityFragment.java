package in.co.samik.hospitalfinder;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.samik.hospitalfinder.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG=DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();

        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);

        if (intent !=null && intent.hasExtra(Intent.EXTRA_TEXT)){
            try{
                String hospitalJSONStr=intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.v(LOG_TAG, hospitalJSONStr);
                JSONObject hospitalObject = new JSONObject(hospitalJSONStr);
                TextView nameTextView = (TextView)rootView.findViewById(R.id.nameTextView);
                nameTextView.setText(hospitalObject.getString("hospitalname"));

                TextView addressTextView = (TextView) rootView.findViewById(R.id.addressTextView);
                addressTextView.setText(hospitalObject.getString("address First Line"));

                TextView categoryView = (TextView) rootView.findViewById(R.id.categoryView);
                categoryView.setText(hospitalObject.getString("Hospital Category"));

                TextView cateTypeView = (TextView) rootView.findViewById(R.id.careTypeView);
                cateTypeView.setText(hospitalObject.getString("hostipalcaretype"));

                TextView medicineSystemView = (TextView) rootView.findViewById(R.id.medicineSystemView);
                medicineSystemView.setText(hospitalObject.getString("Systems of Medicine"));
            }
           catch(JSONException e){
               Log.d(LOG_TAG,e.getMessage(),e);
           }
        }

        return rootView;
    }
}
