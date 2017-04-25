package de.liersch.android.bday.app;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.widget.ImageView;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.db.ContactUtil;

public class DetailActivity extends AppCompatActivity {
  
  private static final String EXTRA_IMAGE = "de.liersch.extraImage";
  private Contact mContact;
  DetailFragment mDetailFragment;
  
  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    mDetailFragment.updateView(mContact);
  }
  
  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    long userID = Long.parseLong(getIntent().getStringExtra(ContactListFragment.CONTACT_ID));
    setContentView(R.layout.activity_detail);
    mDetailFragment = new DetailFragment();
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.detail_container, mDetailFragment)
        .commit();
  
    initActivityTransitions();
  
    mContact = new ContactController(getApplicationContext()).getContact(userID);
    final ImageView imageView = (ImageView) findViewById(R.id.image);
    final ContentResolver contentResolver = getApplicationContext().getContentResolver();
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(contentResolver, mContact.userID);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
      imageView.setPadding(0, 0, 0, 0);
    }
    
    ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
    supportPostponeEnterTransition();
    
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
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
