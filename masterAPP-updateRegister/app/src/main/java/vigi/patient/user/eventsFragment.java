package vigi.patient.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vigi.patient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class eventsFragment extends Fragment {


    public eventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

 
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_events, container, false);
    }

}
