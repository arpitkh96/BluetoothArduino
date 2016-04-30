package com.bluetooth.arduino;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Fragment f=new ListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,f).commit();
        Global.get().setBtReader(new Global.BluetoothListener() {
            @Override
            public void bluetoothRead(int i) {
                if(i!=4)return;
                Fragment f=new NavigationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,f).commit();
            }
        });
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())
        bluetoothAdapter.enable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().findFragmentById(R.id.content_frame).getClass().getName().contains("ListFragment"))
            finish();
        else {
            Fragment f=new ListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,f).commit();
        }
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
}
