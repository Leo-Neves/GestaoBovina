package br.agr.terras.corelibrary.infraestructure.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.FloatingActionButton;
import br.agr.terras.materialdroid.Spinner;
import br.agr.terras.materialdroid.utils.ViewUtil;

public class SqliteBrowser extends Activity implements OnItemClickListener {

    //a static class to save cursor,table values etc which is used by functions to share data in the program.
    static class indexInfo {
        public static int index = 10;
        public static int numberofpages = 0;
        public static int currentpage = 0;
        public static String table_name = "";
        public static Cursor maincursor;
        public static int cursorpostion = 0;
        public static ArrayList<String> value_string;
        public static ArrayList<String> tableheadernames;
        public static ArrayList<String> emptytablecolumnnames;
        public static boolean isEmpty;
        public static boolean isCustomQuery;
    }

// all global variables

    //in the below line Change the text 'yourCustomSqlHelper' com your custom sqlitehelper class name.
    //Do not change the variable name dbm
    private static SQLiteOpenHelper dbm;
    private LinearLayout father;
    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TableLayout tableLayout;
    private LayoutParams tableRowParams;
    private HorizontalScrollView horizontalScrollView;
    private ScrollView mainscrollview;
    private TextView tvmessage;
    private Button previous;
    private Button next;
    private Spinner spinner;
    private TextView tv;
    private FloatingActionButton fab;
    private int CINZA = Color.parseColor("#e2e2e2");
    private MenuItem menuItemDeleteTable;
    private MenuItem menuItemDropTable;

    indexInfo info = new indexInfo();

    public static void setSqliteOpenHelper(SQLiteOpenHelper dbm) {
        SqliteBrowser.dbm = dbm;
    }

    public static void open(Activity activity) {
        if (dbm == null)
            throw new NullPointerException("SQLiteOpenHelper not setted");
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        Intent intent = new Intent(activity, SqliteBrowser.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("colorPrimary", colorPrimary);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int colorPrimary = getIntent().getExtras().getInt("colorPrimary");

        RelativeLayout relativeLayout = new RelativeLayout(this);
        fab = new FloatingActionButton(this);
        fab.setColorNormal(colorPrimary);
        fab.setColorPressed(colorPrimary);
        fab.setImageResource(R.drawable.fab_add);
        final RelativeLayout.LayoutParams fabParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        fabParams.setMargins(0, 0, 15, 15);
        fab.setLayoutParams(fabParams);
        father = new LinearLayout(this);
        father.setOrientation(LinearLayout.VERTICAL);
        toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(colorPrimary);
        toolbar.setTitle(getClass().getSimpleName());
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.md_nav_back);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.menu_sqlitebrowser);
        menuItemDeleteTable = toolbar.getMenu().findItem(R.id.action_delete);
        menuItemDropTable = toolbar.getMenu().findItem(R.id.action_drop);
        menuItemDropTable.setVisible(false);
        menuItemDeleteTable.setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==menuItemDeleteTable.getItemId()){
                    deleteTable();
                }
                if (item.getItemId()==menuItemDropTable.getItemId()){
                    dropTable();
                }
                return true;
            }
        });
        mainscrollview = new ScrollView(SqliteBrowser.this);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainscrollview.setLayoutParams(scrollParams);
        relativeLayout.addView(mainscrollview);
        relativeLayout.addView(fab);
        //the main linear layout to which all tables spinners etc will be added.In this activity every element is created dynamically  to avoid using xml file
        mainLayout = new LinearLayout(SqliteBrowser.this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        mainLayout.setScrollContainer(true);
        mainscrollview.addView(mainLayout);

        //all required layouts are created dynamically and added to the main scrollview
        father.addView(toolbar);
        father.addView(relativeLayout);
        setContentView(father);

        //the first row of layout which has a text view and spinner
        final LinearLayout firstrow = new LinearLayout(SqliteBrowser.this);
        firstrow.setPadding(0, 10, 0, 20);
        LinearLayout.LayoutParams firstrowlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstrowlp.weight = 1;

        spinner = new Spinner(SqliteBrowser.this);
        spinner.setLayoutParams(firstrowlp);
        spinner.setFloatingLabelText("Select Table");

        firstrow.addView(spinner);
        mainLayout.addView(firstrow);

        ArrayList<Cursor> alc;

        //the horizontal scroll view for table if the table content doesnot fit into screen
        horizontalScrollView = new HorizontalScrollView(SqliteBrowser.this);

        //the main table layout where the content of the sql tables will be displayed when user selects a table
        tableLayout = new TableLayout(SqliteBrowser.this);
        tableLayout.setHorizontalScrollBarEnabled(true);
        horizontalScrollView.addView(tableLayout);

        //the second row of the layout which shows number of records in the table selected by user
        final LinearLayout secondrow = new LinearLayout(SqliteBrowser.this);
        secondrow.setPadding(0, 20, 0, 10);
        LinearLayout.LayoutParams secondrowlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        secondrowlp.weight = 1;
        TextView secondrowtext = new TextView(SqliteBrowser.this);
        secondrowtext.setText("No. Of Records : ");
        secondrowtext.setTextSize(20);
        secondrowtext.setLayoutParams(secondrowlp);
        tv = new TextView(SqliteBrowser.this);
        tv.setTextSize(20);
        tv.setLayoutParams(secondrowlp);
        secondrow.addView(secondrowtext);
        secondrow.addView(tv);
        mainLayout.addView(secondrow);
        //A button which generates a text view from which user can write custome queries
        final EditText customquerytext = new EditText(this);
        customquerytext.setVisibility(View.GONE);
        customquerytext.setHint("Enter Your Query here and Click on Submit Query Button .Results will be displayed below");
        mainLayout.addView(customquerytext);

        final Button submitQuery = new Button(SqliteBrowser.this);
        submitQuery.setVisibility(View.GONE);
        submitQuery.setText("Submit Query");

        submitQuery.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mainLayout.addView(submitQuery);

        final TextView help = new TextView(SqliteBrowser.this);
        help.setText("Click on the row below to update values or delete the tuple");
        help.setPadding(0, 5, 0, 5);

        // the spinner which gives user a option to add new row , drop or delete table
        mainLayout.addView(help);
        horizontalScrollView.setPadding(0, 10, 0, 10);
        horizontalScrollView.setScrollbarFadingEnabled(false);
        horizontalScrollView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        mainLayout.addView(horizontalScrollView);
        //the third layout which has buttons for the pagination of content from database
        final LinearLayout thirdrow = new LinearLayout(SqliteBrowser.this);
        previous = new Button(SqliteBrowser.this);
        previous.setText("Previous");

        previous.setBackgroundColor(Color.parseColor("#BAE7F6"));
        previous.setLayoutParams(secondrowlp);
        next = new Button(SqliteBrowser.this);
        next.setText("Next");
        next.setBackgroundColor(Color.parseColor("#BAE7F6"));
        next.setLayoutParams(secondrowlp);
        TextView tvblank = new TextView(this);
        tvblank.setLayoutParams(secondrowlp);
        thirdrow.setPadding(0, 10, 0, 10);
        thirdrow.addView(previous);
        thirdrow.addView(tvblank);
        thirdrow.addView(next);
        mainLayout.addView(thirdrow);

        //the text view at the bottom of the screen which displays error or success messages after a query is executed
        tvmessage = new TextView(SqliteBrowser.this);

        tvmessage.setText("Error Messages will be displayed here");
        String Query = "SELECT name _id FROM sqlite_master WHERE type ='table'";
        tvmessage.setTextSize(18);
        mainLayout.addView(tvmessage);

        final Button customQuery = new Button(SqliteBrowser.this);
        customQuery.setText("Custom Query");
        customQuery.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mainLayout.addView(customQuery);
        customQuery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //set drop down to custom Query
                indexInfo.isCustomQuery = true;
                secondrow.setVisibility(View.GONE);
                help.setVisibility(View.GONE);
                customquerytext.setVisibility(View.VISIBLE);
                submitQuery.setVisibility(View.VISIBLE);
                spinner.setPosition(0);
                customQuery.setVisibility(View.GONE);
            }
        });


        //when user enter a custom query in text view and clicks on submit query button
        //display results in tablelayout
        fab.setOnClickListener(onClickFab);
        submitQuery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                tableLayout.removeAllViews();
                customQuery.setVisibility(View.GONE);

                ArrayList<Cursor> alc2;
                String Query10 = customquerytext.getText().toString();
                Log.d("query", Query10);
                //pass the query to getdata method and get results

                alc2 = getData(Query10);
                final Cursor c4 = alc2.get(0);
                Cursor Message2 = alc2.get(1);
                Message2.moveToLast();

                //if the query returns results display the results in table layout
                if (Message2.getString(0).equalsIgnoreCase("Success")) {

                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                    if (c4 != null) {
                        tvmessage.setText("Queru Executed successfully.Number of rows returned :" + c4.getCount());
                        if (c4.getCount() > 0) {
                            indexInfo.maincursor = c4;
                            refreshTable(1);
                        }
                    } else {
                        tvmessage.setText("Queru Executed successfully");
                        refreshTable(1);
                    }

                } else {
                    //if there is any error we displayed the error message at the bottom of the screen
                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                    tvmessage.setText("Error:" + Message2.getString(0));

                }
            }
        });
        //layout parameters for each row in the table
        tableRowParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 0, 2, 0);

        // a query which returns a cursor com the list of tables in the database.We use this cursor to populate spinner in the first row
        alc = getData(Query);

        //the first cursor has reults of the query
        final Cursor c = alc.get(0);

        //the second cursor has error messages
        Cursor Message = alc.get(1);

        Message.moveToLast();
        String msg = Message.getString(0);
        Log.d("Message from sql = ", msg);

        final ArrayList<String> tablenames = new ArrayList<String>();

        if (c != null) {

            c.moveToFirst();
            tablenames.add("click here");
            do {
                //add names of the table to tablenames array list
                tablenames.add(c.getString(0));
            } while (c.moveToNext());
        }
        //an array adapter com above created arraylist
        /*ArrayAdapter<String> tablenamesadapter = new ArrayAdapter<String>(SqliteBrowser.this,
                android.R.layout.simple_spinner_item, tablenames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);
                TextView adap =(TextView)v;
                adap.setTextSize(20);

                return adap;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);

                return v;
            }
        };

        tablenamesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(tablenamesadapter!=null)
        {
            //set the adpater to spinner spinner
            spinner.setAdapter(tablenamesadapter);
        }*/
        spinner.setNomes(tablenames);
        spinner.setSelectionListener(new Spinner.SelectionListener() {
            @Override
            public void onItemSelected(Spinner spinner, int pos) {
                if (pos == 0 && !indexInfo.isCustomQuery) {
                    secondrow.setVisibility(View.GONE);
                    horizontalScrollView.setVisibility(View.GONE);
                    thirdrow.setVisibility(View.GONE);
                    help.setVisibility(View.GONE);
                    tvmessage.setVisibility(View.GONE);
                    customquerytext.setVisibility(View.GONE);
                    submitQuery.setVisibility(View.GONE);
                    customQuery.setVisibility(View.GONE);
                    toolbar.setSubtitle(null);
                    menuItemDropTable.setVisible(false);
                    menuItemDeleteTable.setVisible(false);
                }
                if (pos != 0) {
                    secondrow.setVisibility(View.VISIBLE);
                    help.setVisibility(View.VISIBLE);
                    customquerytext.setVisibility(View.GONE);
                    submitQuery.setVisibility(View.GONE);
                    customQuery.setVisibility(View.VISIBLE);
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    toolbar.setSubtitle(tablenames.get(pos));
                    menuItemDropTable.setVisible(true);
                    menuItemDeleteTable.setVisible(true);

                    tvmessage.setVisibility(View.VISIBLE);

                    thirdrow.setVisibility(View.VISIBLE);
                    c.moveToPosition(pos - 1);
                    indexInfo.cursorpostion = pos - 1;
                    //displaying the content of the table which is selected in the spinner spinner
                    Log.d("selected table name is", "" + c.getString(0));
                    indexInfo.table_name = c.getString(0);
                    tvmessage.setText("Error Messages will be displayed here");
                    tvmessage.setBackgroundColor(Color.WHITE);

                    //removes any data if present in the table layout
                    tableLayout.removeAllViews();
                    ArrayList<String> spinnertablevalues = new ArrayList<String>();
                    spinnertablevalues.add("Click here to change this table");
                    spinnertablevalues.add("Add row to this table");
                    spinnertablevalues.add("Delete this table");
                    spinnertablevalues.add("Drop this table");
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnertablevalues);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

                    // a array adapter which add values to the spinner which helps in user making changes to the table

                    String Query2 = "select * from " + c.getString(0);
                    Log.d("", "" + Query2);

                    //getting contents of the table which user selected from the spinner spinner
                    ArrayList<Cursor> alc2 = getData(Query2);
                    final Cursor c2 = alc2.get(0);
                    //saving cursor to the static indexinfo class which can be resued by the other functions
                    indexInfo.maincursor = c2;

                    // if the cursor returned form the database is not null we display the data in table layout
                    if (c2 != null) {
                        int counts = c2.getCount();
                        indexInfo.isEmpty = false;
                        Log.d("counts", "" + counts);
                        tv.setText("" + counts);


                        //the spinnertable has the 3 items to drop , delete , add row to the table selected by the user
                        //here we handle the 3 operations.

                        //display the first row of the table com column names of the table selected by the user
                        TableRow tableheader = new TableRow(getApplicationContext());

                        tableheader.setBackgroundColor(CINZA);
                        tableheader.setPadding(0, 0, 0, 2);
                        for (int k = 0; k < c2.getColumnCount(); k++) {
                            LinearLayout cell = new LinearLayout(SqliteBrowser.this);
                            cell.setBackgroundColor(CINZA);
                            cell.setLayoutParams(tableRowParams);
                            final TextView tableheadercolums = new TextView(getApplicationContext());
                            // tableheadercolums.setBackgroundDrawable(gd);
                            tableheadercolums.setPadding(0, 0, 2, 2);
                            tableheadercolums.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            tableheadercolums.setText("" + c2.getColumnName(k));
                            tableheadercolums.setTextColor(Color.parseColor("#000000"));

                            //columsView.setLayoutParams(tableRowParams);
                            cell.addView(tableheadercolums);
                            tableheader.addView(cell);

                        }
                        tableLayout.addView(tableheader);
                        c2.moveToFirst();

                        //after displaying columnnames in the first row  we display data in the remaining columns
                        //the below paginatetbale function will display the first 10 tuples of the tables
                        //the remaining tuples can be viewed by clicking on the proxima button
                        paginatetable(c2.getCount());

                    } else {
                        //if the cursor returned from the database is empty we show that table is empty
                        help.setVisibility(View.GONE);
                        tableLayout.removeAllViews();
                        getcolumnnames();
                        TableRow tableheader2 = new TableRow(getApplicationContext());
                        tableheader2.setBackgroundColor(Color.BLACK);
                        tableheader2.setPadding(0, 2, 0, 2);

                        LinearLayout cell = new LinearLayout(SqliteBrowser.this);
                        cell.setBackgroundColor(Color.WHITE);
                        cell.setLayoutParams(tableRowParams);
                        final TextView tableheadercolums = new TextView(getApplicationContext());

                        tableheadercolums.setPadding(0, 0, 4, 3);
                        tableheadercolums.setText("   Table   Is   Empty   ");
                        tableheadercolums.setTextSize(30);
                        tableheadercolums.setTextColor(Color.RED);

                        cell.addView(tableheadercolums);
                        tableheader2.addView(cell);


                        tableLayout.addView(tableheader2);

                        tv.setText("" + 0);
                    }
                }
            }
        });
    }

    private void dropTable() {
        // an alert dialog to confirm user selection
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {

                    new AlertDialog.Builder(SqliteBrowser.this)
                            .setTitle("Are you sure ?")
                            .setMessage("Pressing yes will drop " + indexInfo.table_name + " table in database")
                            .setPositiveButton("yes",
                                    new DialogInterface.OnClickListener() {
                                        // when user confirms by clicking on yes we drop the table by executing drop table query
                                        public void onClick(DialogInterface dialog, int which) {

                                            String Query6 = "Drop table " + indexInfo.table_name;
                                            ArrayList<Cursor> aldropt = getData(Query6);
                                            Cursor tempc = aldropt.get(1);
                                            tempc.moveToLast();
                                            Log.d("Drop table Mesage", tempc.getString(0));
                                            if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                tvmessage.setText(indexInfo.table_name + "Dropped successfully");
                                                refreshactivity();
                                            } else {
                                                //if there is any error we displayd the error message at the bottom of the screen
                                                tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                tvmessage.setText("Error:" + tempc.getString(0));
                                            }
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                            .create().show();
                }
            }
        });
    }

    private void deleteTable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {

                    new AlertDialog.Builder(SqliteBrowser.this)
                            .setTitle("Are you sure?")
                            .setMessage("Clicking on yes will delete all the contents of " + indexInfo.table_name + " table from database")
                            .setPositiveButton("yes",
                                    new DialogInterface.OnClickListener() {

                                        // when user confirms by clicking on yes we drop the table by executing delete table query
                                        public void onClick(DialogInterface dialog, int which) {
                                            String Query7 = "Delete  from " + indexInfo.table_name;
                                            Log.d("delete table query", Query7);
                                            ArrayList<Cursor> aldeletet = getData(Query7);
                                            Cursor tempc = aldeletet.get(1);
                                            tempc.moveToLast();
                                            Log.d("Delete table Mesage", tempc.getString(0));
                                            if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                tvmessage.setText(indexInfo.table_name + " table content deleted successfully");
                                                indexInfo.isEmpty = true;
                                                refreshTable(0);
                                            } else {
                                                tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                tvmessage.setText("Error:" + tempc.getString(0));
                                            }
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                            .create().show();
                }
            }
        });
    }

    private OnClickListener onClickFab = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //we create a layout which has textviews com column names of the table and edittexts where
            //user can enter value which will be inserted into the datbase.
            final LinkedList<TextView> addnewrownames = new LinkedList<TextView>();
            final LinkedList<EditText> addnewrowvalues = new LinkedList<EditText>();
            final ScrollView addrowsv = new ScrollView(SqliteBrowser.this);
            Cursor c4 = indexInfo.maincursor;
            if (indexInfo.isEmpty) {
                getcolumnnames();
                for (int i = 0; i < indexInfo.emptytablecolumnnames.size(); i++) {
                    String cname = indexInfo.emptytablecolumnnames.get(i);
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText(cname);
                    addnewrownames.add(tv);

                }
                for (int i = 0; i < addnewrownames.size(); i++) {
                    EditText et = new EditText(getApplicationContext());

                    addnewrowvalues.add(et);
                }

            } else {
                for (int i = 0; i < c4.getColumnCount(); i++) {
                    String cname = c4.getColumnName(i);
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText(cname);
                    addnewrownames.add(tv);

                }
                for (int i = 0; i < addnewrownames.size(); i++) {
                    EditText et = new EditText(getApplicationContext());

                    addnewrowvalues.add(et);
                }
            }
            final RelativeLayout addnewlayout = new RelativeLayout(SqliteBrowser.this);
            RelativeLayout.LayoutParams addnewparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            addnewparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            for (int i = 0; i < addnewrownames.size(); i++) {
                TextView tv = addnewrownames.get(i);
                EditText et = addnewrowvalues.get(i);
                int t = i + 400;
                int k = i + 500;
                int lid = i + 600;

                tv.setId(t);
                tv.setTextColor(Color.parseColor("#000000"));
                et.setBackgroundColor(Color.parseColor("#F2F2F2"));
                et.setTextColor(Color.parseColor("#000000"));
                et.setId(k);
                final LinearLayout ll = new LinearLayout(SqliteBrowser.this);
                LinearLayout.LayoutParams tvl = new LinearLayout.LayoutParams(0, 100);
                tvl.weight = 1;
                ll.addView(tv, tvl);
                ll.addView(et, tvl);
                ll.setId(lid);

                Log.d("Edit Text Value", "" + et.getText().toString());

                RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rll.addRule(RelativeLayout.BELOW, ll.getId() - 1);
                rll.setMargins(0, 20, 0, 0);
                addnewlayout.addView(ll, rll);

            }
            addnewlayout.setBackgroundColor(Color.WHITE);
            addrowsv.addView(addnewlayout);
            Log.d("Button Clicked", "");
            //the above form layout which we have created above will be displayed in an alert dialog
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        new AlertDialog.Builder(SqliteBrowser.this)
                                .setTitle("values")
                                .setCancelable(false)
                                .setView(addrowsv)
                                .setPositiveButton("Add",
                                        new DialogInterface.OnClickListener() {
                                            // after entering values if user clicks on add we take the values and run a insert query
                                            public void onClick(DialogInterface dialog, int which) {

                                                indexInfo.index = 10;
                                                //tableLayout.removeAllViews();
                                                //trigger select table listener to be triggerd
                                                String Query4 = "Insert into " + indexInfo.table_name + " (";
                                                for (int i = 0; i < addnewrownames.size(); i++) {

                                                    TextView tv = addnewrownames.get(i);
                                                    tv.getText().toString();
                                                    if (i == addnewrownames.size() - 1) {

                                                        Query4 = Query4 + tv.getText().toString();

                                                    } else {
                                                        Query4 = Query4 + tv.getText().toString() + ", ";
                                                    }
                                                }
                                                Query4 = Query4 + " ) VALUES ( ";
                                                for (int i = 0; i < addnewrownames.size(); i++) {
                                                    EditText et = addnewrowvalues.get(i);
                                                    et.getText().toString();

                                                    if (i == addnewrownames.size() - 1) {

                                                        Query4 = Query4 + "'" + et.getText().toString() + "' ) ";
                                                    } else {
                                                        Query4 = Query4 + "'" + et.getText().toString() + "' , ";
                                                    }


                                                }
                                                //this is the insert query which has been generated
                                                Log.d("Insert Query", Query4);
                                                ArrayList<Cursor> altc = getData(Query4);
                                                Cursor tempc = altc.get(1);
                                                tempc.moveToLast();
                                                Log.d("Add New Row", tempc.getString(0));
                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    tvmessage.setText("New Row added succesfully to " + indexInfo.table_name);
                                                    refreshTable(0);
                                                } else {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    tvmessage.setText("Error:" + tempc.getString(0));
                                                }

                                            }
                                        })
                                .setNegativeButton("close",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                .create().show();
                    }
                }
            });
        }
    };


    //get columnnames of the empty tables and save them in a array list
    public void getcolumnnames() {
        ArrayList<Cursor> alc3 = getData("PRAGMA table_info(" + indexInfo.table_name + ")");
        Cursor c5 = alc3.get(0);
        indexInfo.isEmpty = true;
        if (c5 != null) {
            indexInfo.isEmpty = true;

            ArrayList<String> emptytablecolumnnames = new ArrayList<String>();
            c5.moveToFirst();
            do {
                emptytablecolumnnames.add(c5.getString(1));
            } while (c5.moveToNext());
            indexInfo.emptytablecolumnnames = emptytablecolumnnames;
        }


    }

    //displays alert dialog from which use can update or delete a row
    public void updateDeletePopup(int row) {
        Cursor c2 = indexInfo.maincursor;
        // a spinner which gives options to update or delete the row which user has selected
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Click Here to Change this row");
        spinnerArray.add("Update this row");
        spinnerArray.add("Delete this row");

        //create a layout com text values which has the column names and
        //edit texts which has the values of the row which user has selected
        final ArrayList<String> value_string = indexInfo.value_string;
        final LinkedList<TextView> columnames = new LinkedList<TextView>();
        final LinkedList<EditText> columvalues = new LinkedList<EditText>();

        for (int i = 0; i < c2.getColumnCount(); i++) {
            String cname = c2.getColumnName(i);
            TextView tv = new TextView(getApplicationContext());
            tv.setText(cname);
            columnames.add(tv);

        }
        for (int i = 0; i < columnames.size(); i++) {
            String cv = value_string.get(i);
            EditText et = new EditText(getApplicationContext());
            value_string.add(cv);
            et.setText(cv);
            columvalues.add(et);
        }

        int lastrid = 0;
        // all text views , edit texts are added to this relative layout lp
        final RelativeLayout lp = new RelativeLayout(SqliteBrowser.this);
        lp.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        final ScrollView updaterowsv = new ScrollView(SqliteBrowser.this);
        LinearLayout lcrud = new LinearLayout(SqliteBrowser.this);

        LinearLayout.LayoutParams paramcrudtext = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        paramcrudtext.setMargins(0, 20, 0, 0);

        //spinner which displays update , delete options
        final Spinner crud_dropdown = new Spinner(getApplicationContext());

        ArrayAdapter<String> crudadapter = new ArrayAdapter<String>(SqliteBrowser.this,
                android.R.layout.simple_spinner_item, spinnerArray) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);
                TextView adap = (TextView) v;
                adap.setTextSize(20);

                return adap;
            }


            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);

                return v;
            }
        };


        crudadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        crud_dropdown.setAdapter(crudadapter);
        lcrud.setId(ViewUtil.generateViewId());
        lcrud.addView(crud_dropdown, paramcrudtext);

        RelativeLayout.LayoutParams rlcrudparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlcrudparam.addRule(RelativeLayout.BELOW, lastrid);

        lp.addView(lcrud, rlcrudparam);
        for (int i = 0; i < columnames.size(); i++) {
            TextView tv = columnames.get(i);
            EditText et = columvalues.get(i);
            int t = i + 100;
            int k = i + 200;
            int lid = i + 300;

            tv.setId(t);
            tv.setTextColor(Color.parseColor("#000000"));
            et.setBackgroundColor(Color.parseColor("#F2F2F2"));

            et.setTextColor(Color.parseColor("#000000"));
            et.setId(k);
            Log.d("text View Value", "" + tv.getText().toString());
            final LinearLayout ll = new LinearLayout(SqliteBrowser.this);
            ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ll.setId(lid);
            LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(0, 100);
            lpp.weight = 1;
            tv.setLayoutParams(lpp);
            et.setLayoutParams(lpp);
            ll.addView(tv);
            ll.addView(et);

            Log.d("Edit Text Value", "" + et.getText().toString());

            RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rll.addRule(RelativeLayout.BELOW, ll.getId() - 1);
            rll.setMargins(0, 20, 0, 0);
            lastrid = ll.getId();
            lp.addView(ll, rll);

        }

        updaterowsv.addView(lp);
        //after the layout has been created display it in a alert dialog
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    new AlertDialog.Builder(SqliteBrowser.this)
                            .setTitle("values")
                            .setView(updaterowsv)
                            .setCancelable(false)
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        //this code will be executed when user changes values of edit text or spinner and clicks on ok button
                                        public void onClick(DialogInterface dialog, int which) {

                                            //get spinner value
                                            String spinner_value = crud_dropdown.getSelectedItem().toString();

                                            //it he spinner value is update this row get the values from
                                            //edit text fields generate a update query and execute it
                                            if (spinner_value.equalsIgnoreCase("Update this row")) {
                                                indexInfo.index = 10;
                                                String Query3 = "UPDATE " + indexInfo.table_name + " SET ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    EditText etc = columvalues.get(i);

                                                    if (!etc.getText().toString().equals("null")) {

                                                        Query3 = Query3 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {

                                                            Query3 = Query3 + "'" + etc.getText().toString() + "'";

                                                        } else {

                                                            Query3 = Query3 + "'" + etc.getText().toString() + "' , ";

                                                        }
                                                    }

                                                }
                                                Query3 = Query3 + " where ";
                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    if (!value_string.get(i).equals("null")) {

                                                        Query3 = Query3 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {

                                                            Query3 = Query3 + "'" + value_string.get(i) + "' ";

                                                        } else {
                                                            Query3 = Query3 + "'" + value_string.get(i) + "' and ";
                                                        }

                                                    }
                                                }
                                                Log.d("Update Query", Query3);
                                                //dbm.getData(Query3);
                                                ArrayList<Cursor> aluc = getData(Query3);
                                                Cursor tempc = aluc.get(1);
                                                tempc.moveToLast();
                                                Log.d("Update Mesage", tempc.getString(0));

                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    tvmessage.setText(indexInfo.table_name + " table Updated Successfully");
                                                    refreshTable(0);
                                                } else {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    tvmessage.setText("Error:" + tempc.getString(0));
                                                }
                                            }
                                            //it he spinner value is delete this row get the values from
                                            //edit text fields generate a delete query and execute it

                                            if (spinner_value.equalsIgnoreCase("Delete this row")) {

                                                indexInfo.index = 10;
                                                String Query5 = "DELETE FROM " + indexInfo.table_name + " WHERE ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    if (!value_string.get(i).equals("null")) {

                                                        Query5 = Query5 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {

                                                            Query5 = Query5 + "'" + value_string.get(i) + "' ";

                                                        } else {
                                                            Query5 = Query5 + "'" + value_string.get(i) + "' and ";
                                                        }

                                                    }
                                                }
                                                Log.d("Delete Query", Query5);

                                                getData(Query5);

                                                ArrayList<Cursor> aldc = getData(Query5);
                                                Cursor tempc = aldc.get(1);
                                                tempc.moveToLast();
                                                Log.d("Update Mesage", tempc.getString(0));

                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    tvmessage.setText("Row deleted from " + indexInfo.table_name + " table");
                                                    refreshTable(0);
                                                } else {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    tvmessage.setText("Error:" + tempc.getString(0));
                                                }
                                            }
                                        }

                                    })
                            .setNegativeButton("close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                            .create().show();
                }
            }
        });
    }

    public void refreshactivity() {

        finish();
        startActivity(getIntent());
    }

    public void refreshTable(int d) {
        Cursor c3 = null;
        tableLayout.removeAllViews();
        if (d == 0) {
            String Query8 = "select * from " + indexInfo.table_name;
            ArrayList<Cursor> alc3 = getData(Query8);
            c3 = alc3.get(0);
            //saving cursor to the static indexinfo class which can be resued by the other functions
            indexInfo.maincursor = c3;
        }
        if (d == 1) {
            c3 = indexInfo.maincursor;
        }
        // if the cursor returened form tha database is not null we display the data in table layout
        if (c3 != null) {
            int counts = c3.getCount();

            Log.d("counts", "" + counts);
            tv.setText("" + counts);
            TableRow tableheader = new TableRow(getApplicationContext());

            tableheader.setBackgroundColor(Color.BLACK);
            tableheader.setPadding(0, 2, 0, 2);
            for (int k = 0; k < c3.getColumnCount(); k++) {
                LinearLayout cell = new LinearLayout(SqliteBrowser.this);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(tableRowParams);
                final TextView tableheadercolums = new TextView(getApplicationContext());
                tableheadercolums.setPadding(0, 0, 4, 3);
                tableheadercolums.setText("" + c3.getColumnName(k));
                tableheadercolums.setTextColor(Color.parseColor("#000000"));
                cell.addView(tableheadercolums);
                tableheader.addView(cell);

            }
            tableLayout.addView(tableheader);
            c3.moveToFirst();

            //after displaying column names in the first row  we display data in the remaining columns
            //the below paginate table function will display the first 10 tuples of the tables
            //the remaining tuples can be viewed by clicking on the proxima button
            paginatetable(c3.getCount());
        } else {

            TableRow tableheader2 = new TableRow(getApplicationContext());
            tableheader2.setBackgroundColor(Color.BLACK);
            tableheader2.setPadding(0, 2, 0, 2);

            LinearLayout cell = new LinearLayout(SqliteBrowser.this);
            cell.setBackgroundColor(Color.WHITE);
            cell.setLayoutParams(tableRowParams);

            final TextView tableheadercolums = new TextView(getApplicationContext());
            tableheadercolums.setPadding(0, 0, 4, 3);
            tableheadercolums.setText("   Table   Is   Empty   ");
            tableheadercolums.setTextSize(30);
            tableheadercolums.setTextColor(Color.RED);

            cell.addView(tableheadercolums);
            tableheader2.addView(cell);


            tableLayout.addView(tableheader2);

            tv.setText("" + 0);
        }

    }

    //the function which displays tuples from database in a table layout
    public void paginatetable(final int number) {


        final Cursor c3 = indexInfo.maincursor;
        indexInfo.numberofpages = (c3.getCount() / 100) + 1;
        indexInfo.currentpage = 1;
        c3.moveToFirst();
        int currentrow = 0;

        //display the first 10 tuples of the table selected by user
        int color = Color.WHITE;
        do {

            final TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setBackgroundColor(color);
            tableRowParams.setMargins(5,7,5,6);
            //tableRow.setPadding(0, 2, 0, 2);

            for (int j = 0; j < c3.getColumnCount(); j++) {
                LinearLayout cell = new LinearLayout(this);
                cell.setBackgroundColor(Color.TRANSPARENT);
                cell.setLayoutParams(tableRowParams);
                final TextView columsView = new TextView(getApplicationContext());
                String column_data = "";
                try {
                    column_data = c3.getString(j);
                } catch (Exception e) {
                    // Column data is not a string , do not display it
                }
                columsView.setText(column_data);
                columsView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                columsView.setTextColor(Color.parseColor("#000000"));
                columsView.setPadding(0, 0, 0, 3);
                cell.addView(columsView);
                tableRow.addView(cell);

            }

            tableRow.setVisibility(View.VISIBLE);
            currentrow = currentrow + 1;
            //we create listener for each table row when clicked a alert dialog will be displayed
            //from where user can update or delete the row
            tableRow.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {

                    final ArrayList<String> value_string = new ArrayList<String>();
                    for (int i = 0; i < c3.getColumnCount(); i++) {
                        LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(i);
                        TextView tc = (TextView) llcolumn.getChildAt(0);

                        String cv = tc.getText().toString();
                        value_string.add(cv);

                    }
                    indexInfo.value_string = value_string;
                    //the below function will display the alert dialog
                    updateDeletePopup(0);
                }
            });
            tableLayout.addView(tableRow);

        color = color==Color.WHITE?CINZA:Color.WHITE;
        } while (c3.moveToNext() && currentrow < 100);

        indexInfo.index = currentrow;


        // when user clicks on the previous button update the table com the previous 10 tuples from the database
        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int tobestartindex = (indexInfo.currentpage - 2) * 10;

                //if the tbale layout has the first 10 tuples then toast that this is the first page
                if (indexInfo.currentpage == 1) {
                    Toast.makeText(getApplicationContext(), "This is the first page", Toast.LENGTH_LONG).show();
                } else {
                    indexInfo.currentpage = indexInfo.currentpage - 1;
                    c3.moveToPosition(tobestartindex);

                    boolean decider = true;
                    for (int i = 1; i < tableLayout.getChildCount(); i++) {
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i);


                        if (decider) {
                            tableRow.setVisibility(View.VISIBLE);
                            for (int j = 0; j < tableRow.getChildCount(); j++) {
                                LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(j);
                                TextView columsView = (TextView) llcolumn.getChildAt(0);

                                columsView.setText("" + c3.getString(j));

                            }
                            decider = !c3.isLast();
                            if (!c3.isLast()) {
                                c3.moveToNext();
                            }
                        } else {
                            tableRow.setVisibility(View.GONE);
                        }

                    }

                    indexInfo.index = tobestartindex;

                    Log.d("index =", "" + indexInfo.index);
                }
            }
        });

        // when user clicks on the proxima button update the table com the proxima 10 tuples from the database
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //if there are no tuples to be shown toast that this the last page
                if (indexInfo.currentpage >= indexInfo.numberofpages) {
                    Toast.makeText(getApplicationContext(), "This is the last page", Toast.LENGTH_LONG).show();
                } else {
                    indexInfo.currentpage = indexInfo.currentpage + 1;
                    boolean decider = true;


                    for (int i = 1; i < tableLayout.getChildCount(); i++) {
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i);


                        if (decider) {
                            tableRow.setVisibility(View.VISIBLE);
                            for (int j = 0; j < tableRow.getChildCount(); j++) {
                                LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(j);
                                TextView columsView = (TextView) llcolumn.getChildAt(0);

                                columsView.setText("" + c3.getString(j));

                            }
                            decider = !c3.isLast();
                            if (!c3.isLast()) {
                                c3.moveToNext();
                            }
                        } else {
                            tableRow.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = dbm.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }

}
