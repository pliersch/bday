package de.liersch.android.bday.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.liersch.android.bday.R;

public class ContactsAdapter extends SimpleAdapter {
  Context context;
  ArrayList<HashMap<String, String>> arrayList;

  public ContactsAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
    super(context, data, resource, from, to);
    this.context = context;
    this.arrayList = data;
    LayoutInflater.from(context);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view = super.getView(position, convertView, parent);
    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();
      }
    });
    return view;
  }

}
