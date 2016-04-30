package com.bluetooth.arduino;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by arpitkh996 on 17-03-2016.
 */

/**
 * Created by arpitkh996 on 17-03-2016.
 */
public class NavigationFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialising views
        final View rootView = inflater.inflate(R.layout.message, container, false);
        Button up=(Button)rootView.findViewById(R.id.up);
        Button left=(Button)rootView.findViewById(R.id.left);
        Button right=(Button)rootView.findViewById(R.id.right);
        Button down=(Button)rootView.findViewById(R.id.down);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.get().sendMessage("w");
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.get().sendMessage("d");
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.get().sendMessage("s");
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.get().sendMessage("a");
            }
        });
        Button send=(Button)rootView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string=((EditText)rootView.findViewById(R.id.message)).getText().toString();
                Global.get().sendMessage(string);
            }
        });
        return rootView;
    }

}


