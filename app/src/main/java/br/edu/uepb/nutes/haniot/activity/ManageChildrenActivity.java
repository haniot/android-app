package br.edu.uepb.nutes.haniot.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.ManageChildrenAdapter;
import br.edu.uepb.nutes.haniot.model.Children;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageChildrenActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerViewChildren)
    RecyclerView recyclerViewChildren;

    private List<Children> childrenList = new ArrayList<>();
    private ManageChildrenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_children);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.manage_children));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();

        adapter = new ManageChildrenAdapter(this.childrenList, getApplicationContext());
        recyclerViewChildren.setHasFixedSize(true);
        recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChildren.setItemAnimator(new DefaultItemAnimator());
        recyclerViewChildren.setAdapter(adapter);

    }

    private void loadData() {
        List<String> l = new ArrayList<String>();
        l.add("joao");
        l.add("tiago");
        l.add("jonatas");
        l.add("felipe");
        l.add("maria");
        l.add("jose");
        l.add("lucas");
        l.add("ramom");
        l.add("luis");
        l.add("josue");

        Children child;
        SimpleDateFormat spn = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < 10; i++) {
            child = new Children();
            child.set_id(String.valueOf(i * 65478));
            child.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
            child.setName(l.get(i));
            childrenList.add(child);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Adiciona o menu a activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_children, menu);

        //Botão search na toolbar
        MenuItem searchBtn = menu.findItem(R.id.btnSearchChild);
        final SearchView searchView = (SearchView) searchBtn.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.LTGRAY);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();

                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setSearchQuerry(newText);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
