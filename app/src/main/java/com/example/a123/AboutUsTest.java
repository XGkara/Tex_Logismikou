package com.example.a123;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@Config(manifest = Config.NONE)
public class AboutUsTest {

    @Mock
    private DrawerLayout mockDrawerLayout;

    @Mock
    private View.OnClickListener mockOnClickListener;

    @Mock
    private ImageView mockGitHubImageView;

    @Mock
    private NavigationView.OnNavigationItemSelectedListener mockNavigationItemSelectedListener;

    @Mock
    private MenuItem mockMenuItem;

    private AboutUs aboutUsActivity;

    @Before
    public void setUp() {
        aboutUsActivity = Robolectric.buildActivity(AboutUs.class).create().get();
        aboutUsActivity.drawerLayout = mockDrawerLayout;
        aboutUsActivity.GitHub = mockGitHubImageView;
    }

    @Test
    public void testGitHubImageViewClick() {
        aboutUsActivity.GitHub.setOnClickListener(mockOnClickListener);
        aboutUsActivity.GitHub.performClick();
        verify(mockOnClickListener).onClick(aboutUsActivity.GitHub);

        // Verify the call to openGitHubLink
        verify(aboutUsActivity).openGitHubLink();
    }

    @Test
    public void testOpenGitHubLink() {
        aboutUsActivity.openGitHubLink();

        String expectedUrl = "https://github.com/XGkara/Tex_Logismikou";
        Intent expectedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(expectedUrl));

        verify(aboutUsActivity).startActivity(expectedIntent);
    }

    @Test
    public void testNavigationItemSelected() {
        Mockito.when(mockMenuItem.getItemId()).thenReturn(R.id.nav_aboutus);
        aboutUsActivity.onNavigationItemSelected(mockMenuItem);

        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), AboutUs.class);
        verify(aboutUsActivity).startActivity(expectedIntent);
        verify(aboutUsActivity).finish();
    }
}
