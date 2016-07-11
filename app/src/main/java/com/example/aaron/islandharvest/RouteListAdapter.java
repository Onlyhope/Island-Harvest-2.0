package com.example.aaron.islandharvest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aaron on 7/8/2016.
 */
public class RouteListAdapter extends ArrayAdapter<Route> {

    private List<Route> routes;

    public RouteListAdapter(Context context, int resource, List<Route> objects) {
        super(context, resource, objects);

        routes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.route_list_layout, parent, false);
        }

        Route route = routes.get(position);

        TextView routeIDTextView = (TextView) convertView.findViewById(R.id.route_id_TextView);
        routeIDTextView.setText("" + route.getID());

        return convertView;
    }
}
