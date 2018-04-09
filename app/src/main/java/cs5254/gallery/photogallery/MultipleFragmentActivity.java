package cs5254.gallery.photogallery;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public abstract class MultipleFragmentActivity extends AppCompatActivity {

    private Map<String, Fragment> mFragmentMap;

    protected void registerFragment(String tag, Fragment f) {
        mFragmentMap.put(tag, f);
    }

    protected void switchFragment(String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mFragmentMap.get(tag), tag)
                .commit();
    }

    protected abstract String getStartingFragmentTag();

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentMap = new HashMap<>();
        setContentView(getLayoutResId());
    }
}
