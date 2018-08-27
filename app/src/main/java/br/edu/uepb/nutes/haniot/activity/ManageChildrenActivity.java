package br.edu.uepb.nutes.haniot.activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    @BindView(R.id.textNoDataFound)
    TextView textNoDataFound;

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

        adapter = new ManageChildrenAdapter(this.childrenList,getApplicationContext());
        recyclerViewChildren.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewChildren.getContext(),
                LinearLayout.VERTICAL);
        recyclerViewChildren.addItemDecoration(dividerItemDecoration);
        recyclerViewChildren.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChildren.setItemAnimator(new DefaultItemAnimator());
        recyclerViewChildren.setAdapter(adapter);

    }

    private void loadData(){
        Children child;
        SimpleDateFormat spn = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0;i < 10;i++){
            child = new Children();
            child.set_id(String.valueOf(i*1578));
            child.setRegisterDate(spn.format(Calendar.getInstance().getTime()));
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
        searchView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
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

    public void setTextNoDataFoundVisibility(Boolean visibility){
        if (visibility){
            this.textNoDataFound.setVisibility(View.VISIBLE);
            this.recyclerViewChildren.setVisibility(View.GONE);
            return;
        }else {
            this.textNoDataFound.setVisibility(View.INVISIBLE);
            this.recyclerViewChildren.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
