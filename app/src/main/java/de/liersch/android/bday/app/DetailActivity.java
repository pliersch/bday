package de.liersch.android.bday.app;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.ContactUtil;
import de.liersch.android.bday.ui.contacts.ContactListFragment;

public class DetailActivity extends AppCompatActivity {

  private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";
  private CollapsingToolbarLayout collapsingToolbarLayout;

  @SuppressWarnings("ConstantConditions")
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initActivityTransitions();
    setContentView(R.layout.activity_detail);

    ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
    supportPostponeEnterTransition();

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    final ImageView imageView = (ImageView) findViewById(R.id.image);
    String name = getIntent().getStringExtra(ContactListFragment.NAME);
    long contactId = Long.parseLong(getIntent().getStringExtra(ContactListFragment.CONTACT_ID));
    final ContentResolver contentResolver = getApplicationContext().getContentResolver();
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(contentResolver, contactId);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
    }

    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    collapsingToolbarLayout.setTitle(name);
    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

    TextView title = (TextView) findViewById(R.id.title);
    title.setText("Title");
  }

  @Override public boolean dispatchTouchEvent(MotionEvent motionEvent) {
    try {
      return super.dispatchTouchEvent(motionEvent);
    } catch (NullPointerException e) {
      return false;
    }
  }

  private void initActivityTransitions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Slide transition = new Slide();
      transition.excludeTarget(android.R.id.statusBarBackground, true);
      getWindow().setEnterTransition(transition);
      getWindow().setReturnTransition(transition);
    }
  }
}
