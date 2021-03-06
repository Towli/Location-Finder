package uk.ac.uea.roomfinder.activities;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import uk.ac.uea.framework.implementation.AndroidCSVParser;
import uk.ac.uea.framework.implementation.AndroidInternalFileIO;
import uk.ac.uea.framework.implementation.Building;
import uk.ac.uea.framework.implementation.DeviceLocation;
import uk.ac.uea.framework.implementation.Point;
import uk.ac.uea.framework.implementation.Site;
import uk.ac.uea.roomfinder.R;
import uk.ac.uea.roomfinder.fragments.BrowseFragment;
import uk.ac.uea.roomfinder.fragments.DetailsFragment;
import uk.ac.uea.roomfinder.fragments.HelpFragment;
import uk.ac.uea.roomfinder.fragments.HomeFragment;
import uk.ac.uea.roomfinder.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener,
        SearchFragment.OnFragmentInteractionListener, BrowseFragment.OnFragmentInteractionListener,
        DetailsFragment.OnFragmentInteractionListener {

    private static Site site;
    private static FragmentManager fragmentManager;
    private static DeviceLocation deviceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get DeviceLocation */
        deviceLocation = new DeviceLocation(this);
        deviceLocation.getCurrentLocation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        site = new Site();
        List<List<String>> csv = new AndroidCSVParser().parse("map_data.csv", this);
        List<Building> buildings = new ArrayList<>();

        for(List<String> line : csv) {
            if(line.get(0).equals("building")) {
                buildings.add(new Building(line.get(1), new Point(line.get(2), line.get(3)),
                        line.get(5), line.get(4)));
            }
        }

        site.setBuildings(buildings);

        new AndroidInternalFileIO().writeObject(buildings, "test", this);

        List<Building> test = (List<Building>) new AndroidInternalFileIO().readObject("test", this);
        System.out.println(test.get(0).getName());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Replace fragment container with HomeFragment layout */
        fragmentManager = getSupportFragmentManager();

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit();

        /* Handle incoming app-external intent */
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        /* Handle incoming app-external intent */
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleIncomingText(intent); // Handle text being sent
            }
        }
    }

    public void browseActivity(View view) {
        fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putSerializable("site", site);
        BrowseFragment browseFragment = new BrowseFragment();
        browseFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, browseFragment).addToBackStack(null).commit();
    }

    public void searchActivity(View view) {
        fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putSerializable("site", site);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, searchFragment).addToBackStack(null).commit();
    }

    /**
     *
     * @param intent
     */
    private void handleIncomingText(Intent intent) {
        String incomingText = intent.getStringExtra(Intent.EXTRA_TEXT);

        for (Building b : site.getBuildings()) {
            if (b.getName().equals(incomingText)) {
                Intent i = new Intent(this, MapsActivity.class);
                i.putExtra("building", b);
                Point point = new Point(1.000, 1.000);
                i.putExtra("currentLocation", point);
                startActivity(i);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();

        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_browse) {
            bundle.putSerializable("site", site);
            BrowseFragment browseFragment = new BrowseFragment();
            browseFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, browseFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_search) {
            bundle.putSerializable("site", site);
            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, searchFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_help) {
            HelpFragment helpFragment = new HelpFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, helpFragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBuildingSelected(Building building) {
        Building selected = building;

        Point currentLocation = new Point(deviceLocation.getCurrentLocation().getLatitude(),
                deviceLocation.getCurrentLocation().getLongitude());

        /* Pass intent to MapsActivity */
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("building", selected);
        i.putExtra("currentLocation", currentLocation);
        startActivity(i);
    }

    @Override
    public void onBuildingSelected(int id) {
        Building selected = site.getBuildings().get(id);

        Point currentLocation = new Point(deviceLocation.getCurrentLocation().getLatitude(),
                deviceLocation.getCurrentLocation().getLongitude());

        /* Pass intent to MapsActivity */
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("building", selected);
        i.putExtra("currentLocation", currentLocation);
        startActivity(i);
    }

    @Override
    public void onRoutePressed() {
        System.out.println("Route pressed");
    }
}
