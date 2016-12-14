package net.numa08.updaterealmadapter;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.numa08.updaterealmadapter.databinding.ActivityMainBinding;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    RealmConfiguration realmConfiguration;
    Realm realm;
    boolean stop = false;
    ActivityMainBinding binding;
    Adapter adapter;
    boolean isShownTop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                isShownTop = firstVisibleItemPosition == 0;
            }
        });
        adapter = new Adapter();
        binding.list.setAdapter(adapter);
        Realm.init(this);
        realmConfiguration = new RealmConfiguration.Builder()
                .inMemory()
                .build();
        realm = Realm.getInstance(realmConfiguration);
        final RealmResults<User> result = realm.where(User.class).findAllSorted("createdAt", Sort.DESCENDING);
        result.addChangeListener(listener);
        adapter.updateData(result);
        updateUser.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change) {
            final RealmResults<User> result = realm.where(User.class).findAllSorted("createdAt", Sort.DESCENDING);
            final User user = result.get(0);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    user.setName("changed");
                }
            });
            adapter.notifyItemChanged(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        realm.close();
        stop = true;
        super.onDestroy();
    }

    final RealmChangeListener<RealmResults<User>> listener = new RealmChangeListener<RealmResults<User>>() {

        int previousCount = 0;

        @Override
        public void onChange(RealmResults<User> element) {
            Toast.makeText(MainActivity.this, "item added", Toast.LENGTH_SHORT).show();
            final int difference = element.size() - previousCount;
            Log.d("test", "difference " + difference);
            if (difference > 0) {
                adapter.notifyItemRangeInserted(0, difference);
            }
            previousCount = element.size();
            if (isShownTop) {
                binding.list.smoothScrollToPosition(0);
            }
        }
    };

    final Thread updateUser = new Thread("update-user") {

        Realm realm;

        @Override
        public void run() {
            super.run();
            if (realm == null) {
                realm = Realm.getInstance(realmConfiguration);
            }
            while (!stop) {
                final String name = UUID.randomUUID().toString();
                final String image = "https://placeholdit.imgix.net/~text?txtsize=33&txt=" + name.substring(0, 3) + "&w=72&h=72";
                final User user = new User();
                user.setId(name);
                user.setName(name);
                user.setImage(image);
                user.setCreatedAt(new Date());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(user);
                    }
                });
                try {
                    sleep(TimeUnit.SECONDS.toMillis(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            realm.close();
        }
    };

    @BindingAdapter("android:icon")
    public static void setIcon(PicassoTextView tv, String icon) {
        final Picasso picasso = Picasso.with(tv.getContext());
        picasso.load(Uri.parse(icon))
                .placeholder(R.mipmap.ic_launcher)
                .into(tv);
    }
}
