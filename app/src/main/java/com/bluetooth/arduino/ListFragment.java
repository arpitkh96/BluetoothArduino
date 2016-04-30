package com.bluetooth.arduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by arpitkh996 on 12-02-2016.
 */
public class ListFragment extends Fragment {
    RecyclerView recyclerView;
    ListAdapter listAdapter;
    Toolbar toolbar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.str);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        loadlist();

        return rootView;
    }


    BluetoothAdapter mBluetoothAdapter;

    void loadlist() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        int i = 0;
        ArrayList<BluetoothDevice> arrayList = new ArrayList<>();
        while (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON || i == 0) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            i = 1;
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if (!arrayList.contains(device))
                        arrayList.add(device);
                }
            }
        }
        listAdapter = new ListAdapter(arrayList, getActivity());
        recyclerView.setAdapter(listAdapter);
    }


}
