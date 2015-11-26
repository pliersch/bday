package de.liersch.android.bday.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RemoteViews;

import de.liersch.android.bday.R;

public class ConfigurationActivity extends AppCompatActivity {

  int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setResult(RESULT_CANCELED);
    setContentView(R.layout.activity_configuration);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showWidget();
      }
    });
  }

  private void showWidget() {
    mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
      RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_card_layout);
      appWidgetManager.updateAppWidget(mAppWidgetId, views);

      Intent resultValue = new Intent();
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
      setResult(RESULT_OK, resultValue);
      finish();

//          AppWidgetProviderInfo providerInfo = AppWidgetManager.getInstance(
//              getBaseContext()).getAppWidgetInfo(mAppWidgetId);
//          String appWidgetLabel = providerInfo.label;

    }

  }

}
