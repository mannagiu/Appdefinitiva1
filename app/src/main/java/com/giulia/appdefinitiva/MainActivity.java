package com.giulia.appdefinitiva;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity  {

    //ARRAY DI IMMAGINI:
    ArrayList<Integer> arrayImages;
    //ArrayList con il nome delle cartelle e/o file:
    ArrayList<String> arrayItemNames;
    //Gli array sopra dichiarati mi servono per riempire la listview
    //Struttura dati b-albero per tenere in memoria le cartelle ecc
    ArrayList<String> arraySelectedItems;
    //BTree<Myobject> allMyfiles;
    ArrayList<Myobject> arrayMyObjects;

    TextView textView;
    ListView listView;

    FileInputStream fIn = null;
    FileOutputStream fOut = null;
    private final static File FILE_PATH_SDCARD=Environment.getExternalStorageDirectory();
    private final static File FILE_DIRECTORIES=new File(FILE_PATH_SDCARD,"Documents/alldirectories.json" );
    private final static File FILE_FILES=new File(FILE_PATH_SDCARD,"Documents/allfiles.json" );
    File fileDir=new File("");
    ListView lv0;
    ArrayList<Integer>list1;
    ArrayList<String> list2;
    JSONObject obj;
    JSONArray arrayExt,arrayInt;
    String res,res1;
    private final static String ROOT="Tutti i file";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv0=(ListView) findViewById(R.id.listView0);
        list1=new ArrayList<>();
        list2=new ArrayList<>();
        final Myadapter ma=new Myadapter(MainActivity.this,list1,list2);
        lv0.setAdapter(ma);
        obj=new JSONObject();
        res=leggifile(FILE_DIRECTORIES);
        res1=leggifile(FILE_FILES);
        if(res.isEmpty()&&res1.isEmpty()) {

            arrayInt = new JSONArray();
            arrayExt = new JSONArray();
            try {
                obj.put(ROOT, arrayInt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            arrayExt.put(obj);
            scrivifile(arrayExt.toString(), FILE_DIRECTORIES);
            scrivifile(arrayExt.toString(), FILE_FILES);
        }


        list1.add(R.drawable.folder);
        list2.add("Tutti i file");

        lv0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nomeCartella = (String) lv0.getItemAtPosition(position);
                Intent pagina2 = new Intent(MainActivity.this, Main2Activity.class);
                pagina2.putExtra("dato", nomeCartella);
                startActivity(pagina2);


            }
        });

    }





    private String leggifile(File f){
        FileInputStream stream;
        String temp="";
        int c;
        try {
            stream = new FileInputStream(f);
            while((c=stream.read())!=-1){
                temp=temp+ Character.toString((char)c);
            }

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }


    public void scrivifile(String scrivi, File f){
        FileOutputStream fOut1;
        try {

            fOut1 = new FileOutputStream(f);
            fOut1.write(scrivi.getBytes());
            fOut1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private JSONArray parseJSON(String risultato_lettura, ArrayList<Integer> immagini, ArrayList <String> nomi) {
        JSONArray parse=new JSONArray();
        JSONObject obj =new JSONObject();
        int i=0;
        try{
            parse = new JSONArray(risultato_lettura);
            for (i = 0; i < parse.length(); i++) {
                //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                obj = parse.getJSONObject(i);
                //ottengo le chiavi che ci sono nel mio array json
                Iterator iterator = obj.keys();
                while(iterator.hasNext()) {
                    String key = (String) iterator.next();
                   if(key.equals("Tutti i file")){
                    nomi.add(key);
                    immagini.add(R.drawable.folder);

                }}}
        } catch (JSONException e) {
            e.printStackTrace();
        }return parse;

    }


}