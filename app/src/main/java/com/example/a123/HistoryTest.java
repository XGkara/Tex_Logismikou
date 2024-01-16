package com.example.a123;

import android.content.Intent;
import android.view.MenuItem;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.a123.AboutUs;
import com.example.a123.History;
import com.example.a123.Login;
import com.example.a123.MainActivity;
import com.example.a123.Settings;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class HistoryTest {

    @Mock
    private DrawerLayout mockDrawerLayout;

    @Mock
    private NavigationView.OnNavigationItemSelectedListener mockNavigationItemSelectedListener;

    @Mock
    private MenuItem mockMenuItem;

    private History historyActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        historyActivity = new History();
        historyActivity.drawerLayout = mockDrawerLayout;
    }

    @Test
    public void testNavigationItemSelected() {
        Mockito.when(mockMenuItem.getItemId()).thenReturn(R.id.nav_home);
        historyActivity.onNavigationItemSelected(mockMenuItem);

        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        verify(historyActivity).startActivity(expectedIntent);
        verify(historyActivity).finish();
    }

    @Test
    public void testLogoutItemSelected() {
        Mockito.when(mockMenuItem.getItemId()).thenReturn(R.id.nav_logout);
        historyActivity.onNavigationItemSelected(mockMenuItem);


        verify(FirebaseAuth.getInstance()).signOut();

        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), Login.class);
        verify(historyActivity).startActivity(expectedIntent);
        verify(historyActivity).finish();
    }

    @Test
    public void testHistoryItemSelected() {
        Mockito.when(mockMenuItem.getItemId()).thenReturn(R.id.nav_history);
        historyActivity.onNavigationItemSelected(mockMenuItem);

        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), History.class);
        verify(historyActivity).startActivity(expectedIntent);
        verify(historyActivity).finish();
    }

    @Test
    public void testSettingsItemSelected() {
        Mockito.when(mockMenuItem.getItemId()).thenReturn(R.id.nav_settings);
        historyActivity.onNavigationItemSelected(mockMenuItem);

        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), Settings.class);
        verify(historyActivity).startActivity(expectedIntent);
        verify(historyActivity).finish();
    }

    @Test
    public void testAboutUsItemSelected() {
        Mockito.when(mockMenuItem.getItemId()).thenReturn(R.id.nav_aboutus);
        historyActivity.onNavigationItemSelected(mockMenuItem);

        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), AboutUs.class);
        verify(historyActivity).startActivity(expectedIntent);
        verify(historyActivity).finish();
    }
}