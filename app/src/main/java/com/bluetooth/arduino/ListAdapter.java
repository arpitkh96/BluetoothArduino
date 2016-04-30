package com.bluetooth.arduino;

/**
 * Created by arpitkh996 on 17-03-2016.
 */


/**
 * Created by arpitkh996 on 12-03-2016.
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by arpitkh996 on 12-02-2016.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.LocalViewHolder> {
    ArrayList<BluetoothDevice> arrayList;
    Context c;
    public ListAdapter(ArrayList<BluetoothDevice> arrayList, Context c) {
        this.arrayList = arrayList;
        this.c = c;
    }




    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.simplerow, parent, false);
        return new LocalViewHolder(itemView);
    }




    @Override
    public void onBindViewHolder(LocalViewHolder holder, final int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.title.setTextAppearance(R.style.TextAppearance_AppCompat_Menu);
        }else
            holder.title.setTextAppearance(c,R.style.TextAppearance_AppCompat_Menu);
        holder.title.setText(arrayList.get(position).getName()+"\n"+arrayList.get(position).getAddress());
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.get().btConnct(arrayList.get(position));
                Toast.makeText(c,"Connecting",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public void onViewDetachedFromWindow(final LocalViewHolder holder) {
        (holder).r.clearAnimation();
    }


    public static class LocalViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        View r;


        public LocalViewHolder(View itemView) {
            super(itemView);
            this.r = itemView;
            title = (TextView) itemView.findViewById(R.id.simpleText);
        }


        public View getView() {
            return r;
        }
    }
}
