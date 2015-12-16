package caged.coaa.com.cagedspace.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import caged.coaa.com.cagedspace.R;

/**
 * Created by SaideepReddy on 12/11/2015.
 */
public class PerformersAdapter extends ArrayAdapter<Performer> {

    ArrayList<Performer> performers;
    Context mContext;
    int mResource;

    public PerformersAdapter(Context context, int resource, ArrayList<Performer> objects) {
        super(context, resource, objects);
        this.performers = objects;
        this.mContext =context;
        this.mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent,false );
        }

        TextView tv1 = (TextView) convertView.findViewById(R.id.textView8);
        TextView tv2 = (TextView) convertView.findViewById(R.id.textView9);
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);

        Performer performer = performers.get(position);

            tv1.setText(performer.getName());
            tv2.setText("\t\t\t\""+performer.getCaption()+"\"");
        Picasso.with(mContext)
                .load(performer.getImage()).into(iv);

        return convertView;
    }
}
