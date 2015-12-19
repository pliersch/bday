package de.liersch.android.bday.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.util.CircleTransform;
import de.liersch.android.bday.app.util.ViewModel;
import de.liersch.android.bday.notification.DateService;
import de.liersch.android.bday.db.ContactService;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

  public static final String AVATAR_URL = "http://lorempixel.com/200/200/people/1/";

  private static List<ViewModel> items = new ArrayList<>();

  static {
    for (int i = 1; i <= 10; i++) {
      items.add(new ViewModel("Item " + i, "http://lorempixel.com/500/500/animals/" + i));
    }
  }

  private DrawerLayout drawerLayout;
  private View content;
  private RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    initRecyclerView();
    initFab();
    initToolbar();
    setupDrawerLayout();

    startService(new Intent(this, DateService.class));
    startService(new Intent(this, ContactService.class));

    content = findViewById(R.id.content);

    NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
    View header = navigationView.getHeaderView(0);
    final ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
    Picasso.with(this).load(AVATAR_URL).transform(new CircleTransform()).into(avatar);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setRecyclerAdapter(recyclerView);
    }
  }

  @Override
  protected void onDestroy() {
    stopService(new Intent(this, DateService.class));
    stopService(new Intent(this, ContactService.class));
    super.onDestroy();
  }

  @Override public void onEnterAnimationComplete() {
    super.onEnterAnimationComplete();
    setRecyclerAdapter(recyclerView);
    recyclerView.scheduleLayoutAnimation();
  }

  private void initRecyclerView() {
    recyclerView = (RecyclerView) findViewById(R.id.recycler);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

  }

  private void setRecyclerAdapter(RecyclerView recyclerView) {
    RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
    adapter.setOnItemClickListener(this);
    recyclerView.setAdapter(adapter);
  }

  private void initFab() {
    findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Snackbar.make(content, "FAB Clicked", Snackbar.LENGTH_SHORT).show();
      }
    });
  }

  private void initToolbar() {
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void setupDrawerLayout() {
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
    view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
        Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onItemClick(View view, ViewModel viewModel) {
    DetailActivity.navigate(this, view.findViewById(R.id.image), viewModel);
  }

}
