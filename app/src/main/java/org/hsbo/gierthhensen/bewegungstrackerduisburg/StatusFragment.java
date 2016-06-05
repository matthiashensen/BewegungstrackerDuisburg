package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * FragmentClass for the status fragment.
 * Show the state of the GPS tracking service
 */
public class StatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    View view;

    /**
     * Required empty public constructor
     */
    public StatusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_status, container, false);

        TextView statusText = (TextView) view.findViewById(R.id.statusText);
        TextView coordinateText = (TextView) view.findViewById(R.id.coordinates);

        view.setBackgroundColor(Color.GRAY);

        return view;
    }
}
