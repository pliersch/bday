package de.liersch.android.bday.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.notification.LoggingNotificationBuilder;
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
        new LoggingNotificationBuilder().showNotification(getContext(), "Debug Test by button");
      }
    });
    Button btnResetDB = (Button) view.findViewById(R.id.buttonResetDB);
    btnResetDB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DatabaseManager.getInstance(getContext()).reset();
      }
    });
    return view;
  }
}
