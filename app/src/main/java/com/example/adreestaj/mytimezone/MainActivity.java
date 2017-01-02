package com.example.adreestaj.mytimezone;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adreestaj.mytimezone.Model.TimeZone;
import com.example.adreestaj.mytimezone.Model.TimeZoneList;
import com.example.adreestaj.mytimezone.Utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  PlaceSelectionListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    PlaceAutocompleteFragment autocompleteFragment;
    ArrayList<com.example.adreestaj.mytimezone.Model.TimeZone> timeZoneArrayList;
    ProgressDialog dialog;
    //ArrayList<String> cities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeZoneArrayList = TimeZoneList.getInstance().getTimeZoneList();
        //cities = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Fetching..");

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        autocompleteFragment.setHint(getString(R.string.search_str));
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(timeZoneArrayList , this);
        mRecyclerView.setAdapter(mAdapter);
        //Internet availability check
        if (!Utils.isNetworkAvailable(this))
         Toast.makeText(getApplicationContext() ,"Connect internet to add Time Zone",Toast.LENGTH_LONG).show();

    }
    //get time zone against city/ country from google time zone API
    private void getTimeZone(String latLng , Long timestamp , final String city){
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="https://maps.googleapis.com/maps/api/timezone/json?location="+latLng+"&timestamp="+timestamp+"&key="+getString(R.string.google_api_key);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //parsing of JSON Object
                            JSONObject jsonObject = new JSONObject(response);
                                boolean flag = false;
                                for (com.example.adreestaj.mytimezone.Model.TimeZone timeZone:timeZoneArrayList){
                                    if (timeZone.getTimeZoneId().equals(jsonObject.getString("timeZoneId"))){

                                        flag = true;
                                        if (!timeZone.getCities().contains(city))
                                                 timeZone.getCities().add(city);
                                        else
                                            Toast.makeText(getApplicationContext() , city+ " Already Entered ",Toast.LENGTH_LONG).show();
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                            if (!flag){
                                ArrayList<String> cities = new ArrayList<>();
                                cities.add(city);
                                //object making of timezone to add in list
                                com.example.adreestaj.mytimezone.Model.TimeZone tm =
                                        new com.example.adreestaj.mytimezone.Model.TimeZone(
                                                jsonObject.getString("timeZoneId")
                                                , jsonObject.getString("timeZoneName")
                                                ,cities);
                                timeZoneArrayList.add(tm);
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // com.example.adreestaj.mytimezone.Model.TimeZone timeZone = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //Place selection listener
    @Override
    public void onPlaceSelected(Place place) {
        dialog.show();
        //Log.i("TAG", "Place: " + place.getName());
        String latLng =  place.getLatLng().toString();
        latLng = latLng.substring(latLng.indexOf("(") + 1, latLng.indexOf(")"));
        long timestamp = System.currentTimeMillis() / 1000;
        autocompleteFragment.setText("");
        getTimeZone(latLng ,timestamp , place.getName().toString() );


    }
    //error listener of place selection
    @Override
    public void onError(Status status) {
        Log.i("TAG", "An error occurred: " + status);

    }

    //Adapter class of recycler view
    public class MyAdapter extends RecyclerView.Adapter<myViewHolder>{

        List<TimeZone> timeZones;
        Context sContext;
        public MyAdapter(List<TimeZone> tz , Context context){
            timeZones = tz;
            sContext = context;
        }
        @Override
        public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_item_layout, parent, false);
            return new myViewHolder(view);
        }

        @Override
        public void onBindViewHolder(myViewHolder holder, int position) {
            TimeZone tz = timeZones.get(position);
            holder.bindData(tz, position , sContext);
        }


        @Override
        public int getItemCount() {
            return timeZones.size();
        }


    }
    //ViewHolder of recycler view
    class myViewHolder extends RecyclerView.ViewHolder {

        TextView sCity, sTimeZoneText;
        TimeZone sTimeZone;
        CardView sCardView;
        LinearLayout linearLayout;
        int sPosition;


        public myViewHolder(View itemView) {
            super(itemView);
            sCity = (TextView)itemView.findViewById(R.id.city_text);
            sTimeZoneText = (TextView)itemView.findViewById(R.id.time_zone);
            sCardView = (CardView)itemView.findViewById(R.id.card_view);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.city_container);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You want to delete?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TimeZoneList.getInstance().getTimeZoneList().remove(sPosition);
                            if (linearLayout.getChildCount()>1){
                                for (int i = linearLayout.getChildCount(); i>=1;i--)
                                    linearLayout.removeView(linearLayout.getChildAt(i));
                            }

                            mAdapter.notifyDataSetChanged();
                            // User clicked OK button
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    }).show();
//                   AlertDialog dialog = builder.create();

                   // mAdapter.notifyItemRemoved(sPosition);

                    return false;
                }
            });
        }
        public  void updateView(Context context , String string){
            TextView textView = new TextView(context);
            textView.setText(string);
            textView.setTextSize(15);

            textView.setPadding(20,0,0,0);
            textView.setHeight(190);
            textView.setTag(linearLayout.getChildCount());
            // textView.setBackgroundColor(Color.GREEN);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.addView(textView);
        }


        public void bindData(TimeZone tz ,int  position , Context context) {
            sTimeZone = tz;
            sPosition = position;
            if (Utils.getCurrentTimeZoneId().equals(sTimeZone.getTimeZoneId())) {
                sCardView.setCardBackgroundColor(Color.RED);
            }else
                sCardView.setCardBackgroundColor(Color.WHITE);

            if (sTimeZone.getCities().size()>linearLayout.getChildCount()){
                updateView(context ,sTimeZone.getCities().get(linearLayout.getChildCount()) );
            }
               sCity.setText(sTimeZone.getCities().get(0));
            sTimeZoneText.setText(sTimeZone.getTimeZoneId()+"\n"+sTimeZone.getTimeZoneName());

        }


    }



}

