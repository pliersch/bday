package de.liersch.android.bday.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.liersch.android.bday.R;
import de.liersch.android.bday.notification.Notifier;

public class DevFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_dev_tools, container, false);
    Button btnNotify = (Button) view.findViewById(R.id.buttonNotifier);
    btnNotify.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new Notifier(getContext()).notifyBirthdays();
      }
    });
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }
}
