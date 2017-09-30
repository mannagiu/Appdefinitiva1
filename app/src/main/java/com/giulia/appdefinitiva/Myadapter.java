package com.giulia.appdefinitiva;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by giulia on 26/06/17.
 */

public class Myadapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> listid;
    private ArrayList<String> nameList;
    // boolean[] itemChecked;


    public Myadapter(Context context, ArrayList<Integer> listid, ArrayList<String> nameList) {
        super();
        this.context = context;
        this.listid = listid;
        this.nameList = nameList;
        //itemChecked = new boolean[nameList.size()];
    }
    /*private class ViewHolder {
        ImageView img;
        TextView apkName;
        CheckBox ck1;
    }*/

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //  final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate( context, R.layout.items_list, null );


        }
        ImageView images = (ImageView) convertView.findViewById( R.id.imageView );
        TextView text = (TextView) convertView.findViewById( R.id.textView );
        images.setImageResource( listid.get( position ) );
        text.setText( nameList.get( position ) );
        return convertView;
    }
}

        /*holder = new ViewHolder();

            holder.img=(ImageView) convertView.findViewById(R.id.imageView);
            holder.apkName= (TextView) convertView.findViewById(R.id.textView);
            holder.ck1=(CheckBox) convertView.findViewById(R.id.cb);
            holder.img.setImageResource(listid.get(position));
            holder.apkName.setText(nameList.get(position));
            holder.ck1.setOnCheckedChangeListener(MainActivity,context);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.ck1.setChecked(false);

        if (itemChecked[position])
            holder.ck1.setChecked(true);
        else
            holder.ck1.setChecked(false);

        holder.ck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.ck1.isChecked())
                    itemChecked[position] = true;
                else
                    itemChecked[position] = false;
            }
        });
*/





