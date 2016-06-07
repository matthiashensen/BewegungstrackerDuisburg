package org.hsbo.gierthhensen.bewegungstrackerduisburg;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * FragmentClass for the status fragment.
 * Show the state of the GPS tracking service
 */
public class StatusFragment extends Fragment {

    View view;
    /**
     * Required empty public constructor
     */
    public StatusFragment() {
    }


    public static Fragment newInstance() {
        StatusFragment frg = new StatusFragment();
        return frg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState==null){

        }
        else {

        }
    }

    /**
     * On first creation of the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_status, container, false);
        view.setBackgroundColor(Color.GRAY);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
