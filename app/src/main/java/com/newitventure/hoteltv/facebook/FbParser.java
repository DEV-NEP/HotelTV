package com.newitventure.hoteltv.facebook;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.newitventure.hoteltv.R;
import com.newitventure.hoteltv.adapter.CustomListAdapter;
import com.newitventure.hoteltv.utils.GetData;
import com.newitventure.hoteltv.utils.HttpHandler;
import com.newitventure.hoteltv.utils.LinkConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Leviathan on 5/31/2017.
 */

public class FbParser extends Activity {

    Context context;
    String id,access_token;
    final static String TAG = FbParser.class.getSimpleName();
    private ArrayAdapter<GetData> listAdapter;
    ListView listView;
    ProgressBar progressBar;
    Toast toast;
    TextView text;


    public FbParser(Context context,ListView listView,String id,ProgressBar progressBar,String access_token,Toast toast,TextView text){
        this.context = context;
        this.listView = listView;
        this.id = id;
        this.progressBar = progressBar;
        this.access_token = access_token;
        this.toast = toast;
        this.text = text;
    }

    public void jsonParser(){
        new GetPosts().execute();
    }

    public class GetPosts extends AsyncTask<Void, Void, Void> {

            /*if app's secret key is change then app access token also changes*/

        /*use this for accessing page using user's access token*/
//        String facebook_url = "https://graph.facebook.com/"+id+"/feed?fields=created_time,id,message&access_token="+access_token+"&limit=15";

        String facebook_url = "https://graph.facebook.com/"+id+"/feed?fields=created_time,id,message&access_token="+ LinkConfig.APP_ACCESS_TOKEN+"&limit=15";
        List<GetData> getDataList = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "GetPosts: facebook_url: "+facebook_url);
            HttpHandler handler = new HttpHandler();
            String jsonStr = handler.makeServiceCall(facebook_url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray data = jsonObject.getJSONArray("data");
                    Log.d(TAG, "doInBackground: data : "+data);

                    for (int i = 0; i < data.length(); i++) {

                        GetData getData = new GetData();

                        JSONObject jsonObject1 = data.getJSONObject(i);
                        String postTime = jsonObject1.getString("created_time").substring(0,10);
                        getData.setTime(postTime);

                        getData.setMessage(jsonObject1.getString("message"));

                        getDataList.add(getData);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get JSON from server");
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listAdapter = new CustomListAdapter(context, R.layout.list_item, getDataList);
            listView.setAdapter(listAdapter);
            progressBar.setVisibility(View.GONE);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view0, int i, long l) {
                    text.setText("Item "+i+" Clicked!");
                    toast.show();
                }
            });

        }
    }
}
