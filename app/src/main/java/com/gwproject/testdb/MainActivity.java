package com.gwproject.testdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    EditText name;
    EditText addr;
    ListView list;

    Button btnInsert;
    Button btnUpdate;
    Button btnDelete;

    AAdapter adapter;
    SQLiteDatabase db;
    MyDBHelper myDBHelper;

    ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void init(){
        name = (EditText)findViewById(R.id.name);
        addr = (EditText)findViewById(R.id.addr);
        list = (ListView)findViewById(R.id.listview);
        btnInsert = (Button)findViewById(R.id.insert);
        btnUpdate = (Button)findViewById(R.id.update);
        btnDelete = (Button)findViewById(R.id.delete);
        //db = openOrCreateDatabase("myDB", Context.MODE_PRIVATE, null);
        myDBHelper = new MyDBHelper(this, "myDB",null,1);

        btnInsert.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        itemList = new ArrayList<>();

        adapter = new AAdapter(this, 0, itemList);
        list.setAdapter(adapter);
        selectAll();
    }

    public void selectAll(){
        db = myDBHelper.getReadableDatabase();

        Cursor cursor =  db.rawQuery("select * from usertable;", null);
        cursor.moveToFirst();
        itemList.clear();
        if(cursor.getCount() <= 0 ) return;
        do{
            itemList.add(new Item(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }while(cursor.moveToNext());
        adapter.notifyDataSetChanged();
        cursor.close();
        db.close();
    }

    @Override
    public void onClick(View v) {

        String username = name.getText().toString();
        String useraddr = addr.getText().toString();
        String sql = "";
        switch(v.getId()){
            case R.id.insert:
                sql = "insert into usertable(name, addr) values ('"+username+"','"+useraddr+"');";
                break;
            case R.id.delete:
                sql = "delete from usertable where name = '" + username+"';";
                break;
            case R.id.update:
                sql = "update usertable set addr = '" + useraddr+"' where name = '"+username+"';";
                break;
        }
        if(sql!= null){
            db = myDBHelper.getWritableDatabase();
            db.execSQL(sql);
            Cursor cursor = db.rawQuery("select * from usertable;",null);
            if(cursor != null)
                selectAll();
        }
    }

    class MyDBHelper extends SQLiteOpenHelper{

        MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists usertable("+
                    "_id integer primary key autoincrement,"+
                    "name char(15), addr char(20));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS usertable;");
            onCreate(db);
        }
    }

    class Item{
        int _id;
        String name;
        String addr;

        Item(int _id, String name, String addr) {
            this._id = _id;
            this.name = name;
            this.addr = addr;
        }

        public int get_id() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getAddr() {
            return addr;
        }
    }

    class AAdapter extends ArrayAdapter<Item>{
        ArrayList<Item> data;
        LayoutInflater inflater;
        Context context;
        AAdapter(Context context, int resource, ArrayList<Item> objects) {
            super(context, resource, objects);
            this.data = objects;
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if(view == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.row, parent, false);
            }

            final TextView id = (TextView)view.findViewById(R.id.listid);
            final TextView name = (TextView)view.findViewById(R.id.listname);
            final TextView email = (TextView)view.findViewById(R.id.listemail);
            id.setText("아이디 : " + data.get(position).get_id());
            name.setText("이름 : " + data.get(position).getName());
            email.setText("이메일 : " + data.get(position).getAddr());

            return view ;

        }
    }

}