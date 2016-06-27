package de.liersch.android.bday.ui.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.ContactUtil;

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

    // TODO re-use converView
    // http://stackoverflow.com/questions/10120119/how-does-the-getview-method-work-when-creating-your-own-custom-adapter
    View view = super.getView(position, convertView, parent);
    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

    final ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
    final HashMap<String, String> hashMap = arrayList.get(position);

    final long contactId = Long.parseLong(hashMap.get("contactId"), 10);
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(contentResolver, contactId);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
    }

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(context, hashMap.get("name"), Toast.LENGTH_SHORT).show();
      }
    });
    return view;
  }

}
