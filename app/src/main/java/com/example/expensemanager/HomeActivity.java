package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private FirebaseAuth mAuth;
//Added by sir
    Fragment fragment;

    //Fragment
    private DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);


        mAuth=FirebaseAuth.getInstance();

        loadFragment(new DashBoardFragment());

        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
        bottomNavigationView.setOnItemSelectedListener(this);
        frameLayout = findViewById(R.id.dashboard);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);

        dashBoardFragment = new DashBoardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        new DashBoardFragment();

/*

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.dashboard:
                    setFragment(new DashBoardFragment());
                    bottomNavigationView.setItemBackgroundResource(R.color.dashboard);

                    return true;

                case R.id.income:
                    setFragment(new IncomeFragment());
                    bottomNavigationView.setItemBackgroundResource(R.color.income);

                    return true;

                case R.id.expense:
                    setFragment(new ExpenseFragment());
                    bottomNavigationView.setItemBackgroundResource(R.color.expense);

                    return true;

                default:
                    return false;
            }
        });

*/

    }
//method altered by sir
    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = null;
        new FragmentManager() {
            @NonNull
            @Override
            public FragmentTransaction beginTransaction() {
                assert false;
                fragmentManager.beginTransaction().replace(R.id.relativetry,
                                fragment, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                return super.beginTransaction();
            }
        };


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId) {

        switch (itemId) {
            case R.id.dashboard:
                fragment = new DashBoardFragment();
                break;

            case R.id.income:
                fragment = new IncomeFragment();
                break;

            case R.id.expense:
                fragment = new ExpenseFragment();
                break;

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }

        if (fragment != null) {

            loadFragment(fragment);
//comments done by sir
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.relativetry, fragment);
//            ft.commit();
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
//new method created by sir
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.relativetry, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
