package com.newitventure.hoteltv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.newitventure.hoteltv.R;
import com.newitventure.hoteltv.utils.GetData;

import java.util.List;

/**
 * Created by NITV VNE on 4/1/2018.
 */

public class CustomListAdapter extends ArrayAdapter<GetData> {

    static String TAG = CustomListAdapter.class.getSimpleName();

    TextView textView,textView1;
    Button button;

    private Context context;
    private List<GetData> dataList;

    public CustomListAdapter(Context context, int id, List<GetData> dataList) {
        super(context,id,dataList);

        this.context = context;

        this.dataList = dataList;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {


            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            textView = (TextView) convertView.findViewById(R.id.listtext);
            textView1 = (TextView) convertView.findViewById(R.id.text1);
            button = (Button) convertView.findViewById(R.id.listbtn);
//
            textView.setText(dataList.get(position).getMessage());
            textView1.setText(dataList.get(position).getTime());
            button.setText("×");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dataList.remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
    }
}

