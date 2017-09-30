package com.giulia.appdefinitiva;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class Main2Activity extends AppCompatActivity  {

    //ARRAY DI IMMAGINI:
    ArrayList<Integer> arrayImages;
    //ArrayList con il nome delle cartelle e/o file:
    ArrayList<String> arrayItemNames;
    //Gli array sopra dichiarati mi servono per riempire la listview
    //Struttura dati b-albero per tenere in memoria le cartelle ecc
    ArrayList<String> arraySelectedItems;
    //BTree<Myobject> allMyfiles;
    ArrayList<Myobject> arrayMyObjects;

    TextView textView1,textView11;
    ListView listView1;


     File FILE_PATH_SDCARD=Environment.getExternalStorageDirectory();

     File FILE_DIRECTORIES=new File(FILE_PATH_SDCARD,"Documents/tre.json");
     File FILE_FILES=new File(FILE_PATH_SDCARD,"Documents/allfiles.json" );

    int l=0;
    JSONArray arrayDirectory,arrayFile ;


    String directoryName,reading,reading2;
    int count=0;
    int j=0;
    Bundle datipassati;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //ImageView im = (ImageView) findViewById(R.id.imageView);
        //im.setImageResource(R.drawable.addfolder);
        listView1 = (ListView) findViewById(R.id.listView1);
        arrayImages= new ArrayList<>();
        arrayItemNames = new ArrayList<>();
        arraySelectedItems= new ArrayList<>();
        // arrayobj = new ArrayList<>();
        arrayDirectory=new JSONArray();
        arrayFile=new JSONArray();
        arrayMyObjects=new ArrayList<>();

        final Myadapter myAdapter = new Myadapter(Main2Activity.this,arrayImages,arrayItemNames);
        listView1.setAdapter(myAdapter);

        textView1=(TextView) findViewById(R.id.textView1);
        textView11=(TextView) findViewById(R.id.textView11);

        reading=leggiFile(FILE_DIRECTORIES);
        reading2=leggiFile(FILE_FILES);
        arrayDirectory=parseJson(reading,arrayImages,arrayItemNames,"dir");
        arrayFile= parseJson(reading2,arrayImages,arrayItemNames,"file");

        textView1.setText(arrayDirectory.toString());
        textView11.setText(arrayFile.toString());


        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {
                // recupero il titolo memorizzato nella riga tramite l'ArrayAdapter
                directoryName = (String) listView1.getItemAtPosition(pos);
                Intent pagina3 = new Intent(Main2Activity.this, Main3Activity.class);
                pagina3.putExtra("nome cartella", directoryName);
                startActivity(pagina3);

            }
        });

        listView1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView1.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {

                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.contextmenu, menu);


                return true;

            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

                switch (arraySelectedItems.size()) {
                    case 1:

                        menu.getItem(0).setEnabled(true);
                        menu.getItem(1).setEnabled(true);
                        return true;
                    default:
                        menu.getItem(0).setEnabled(true);
                        menu.getItem(1).setEnabled(false);
                        return true;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete:
                        reading=leggiFile(FILE_DIRECTORIES);
                        eliminaCartella(arraySelectedItems,arrayImages,arrayItemNames,reading);
                        Toast.makeText(getBaseContext(), count + "elementi eliminati", Toast.LENGTH_SHORT).show();
                        //textView1.setText(arrayRoot.toString());
                        String r= leggiFile(FILE_DIRECTORIES);
                        textView1.setText(r);
                        count = 0;
                        mode.finish();
                        return true;
                    case R.id.rename:
                        rename(arraySelectedItems,arrayItemNames);
                        count=0;
                        mode.finish();
                        return true;


                    default:
                        count=0;
                        return true;
                }



            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                count =  count+1;
                mode.setTitle(count + "elementi selezionati");
                arraySelectedItems.add(arrayItemNames.get(position));


            }});
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addfolder:

                createDirectory(arrayDirectory,arrayImages,arrayItemNames);
                //textview.setText(str);
                return true;
            case R.id.addfile:
                createFile(arrayFile,arrayImages,arrayItemNames);
                return true;
            case R.id.home:
                Intent primaPagina=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(primaPagina);
                return true;
            //case R.id.precedente:
            //  return true;
            default:
                return false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.optionsmenu,menu);
        return true;

    }


    private void createDirectory(final JSONArray parse, final ArrayList<Integer> immagini, final ArrayList<String> nomi) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main2Activity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_newfolder, null);
        final EditText cartella = (EditText) mView.findViewById(R.id.nome_cartella);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String r;
                JSONObject obj_nuovo1,obj_nuovo2,obj_nuovo3;
                JSONArray array_nuovo1,array_nuovo2;
                obj_nuovo1 = new JSONObject();
                array_nuovo1 = new JSONArray();
                String risultato;
                int flag=0;

                if (!cartella.getText().toString().isEmpty()) {
                    Toast.makeText(Main2Activity.this, "Cartella aggiunta", Toast.LENGTH_SHORT).show();
                    r = cartella.getText().toString();
                    //Creare un nuovo oggetto e aggiungerlo all'albero
                    Myobject o=new Myobject(r,"dir",false);
                    arrayMyObjects.add(o);

                    //AGGIUNGO ALLA LISTVIEW GLI ELEMENTI
                    nomi.add(r);
                    immagini.add(R.drawable.folder);
                    //CREO L'OGGETTO JSON CORRISPONDENTE ALLA SCRITTURA

                    try {

                        obj_nuovo1.put(r, array_nuovo1);
                        parse.put(obj_nuovo1);

                        //aggiorno il file
                        datipassati = getIntent().getExtras();
                        String dato1 = datipassati.getString("dato");

                        if(dato1.equals("Tutti i file")) {
                            obj_nuovo2=new JSONObject();
                            obj_nuovo3=new JSONObject();
                            array_nuovo2=new JSONArray();

                            obj_nuovo3.put(r,"dir");

                            for(int i=0;i<parse.length()&&flag==0;i++){
                                obj_nuovo2=parse.getJSONObject(i);
                                Iterator iterator=obj_nuovo2.keys();
                                while(iterator.hasNext()&&flag==0){
                                    String key=(String) iterator.next();
                                    if(key.equals("Tutti i file")){
                                        flag=1;
                                        array_nuovo2=obj_nuovo2.getJSONArray(key);
                                        array_nuovo2.put(obj_nuovo3);

                                    }
                                }


                            }}   } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    risultato = parse.toString();
                    textView1.setText(risultato);
                    scriviFile(risultato, FILE_DIRECTORIES);

                }

            }

        });


        mBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void createFile(final JSONArray parse, final ArrayList<Integer> immagini, final ArrayList<String> nomi) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main2Activity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_newfile, null);
        final EditText file = (EditText) mView.findViewById(R.id.nomefile);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject obj_nuovo1,obj_nuovo2,obj_nuovo3;
                JSONArray array_nuovo1,array_nuovo2;
                obj_nuovo1 = new JSONObject();
                array_nuovo1 = new JSONArray();

                String risultato,key,r;
                int flag=0;

                if (!file.getText().toString().isEmpty()) {
                    Toast.makeText(Main2Activity.this, "File aggiunto", Toast.LENGTH_SHORT).show();
                    r = file.getText().toString();
                    //Creare un nuovo oggetto e aggiungerlo all'albero
                    Myobject o=new Myobject(r,"file",false);
                    arrayMyObjects.add(o);

                    //AGGIUNGO ALLA LISTVIEW GLI ELEMENTI
                    nomi.add(r);
                    immagini.add(R.drawable.file);
                    //CREO L'OGGETTO JSON CORRISPONDENTE PER LA SCRITTURA

                    try {
                        datipassati = getIntent().getExtras();
                        String dato1 = datipassati.getString("dato");
                        obj_nuovo2 = new JSONObject();
                        obj_nuovo3 = new JSONObject();
                        array_nuovo2 = new JSONArray();


                           for(int i=0;i<parse.length()&&flag==0;i++){
                                obj_nuovo2=parse.getJSONObject(i);
                                Iterator iterator=obj_nuovo2.keys();
                                while(iterator.hasNext()&&flag==0){
                                    key=(String) iterator.next();
                                    if(key.equals(dato1)){
                                        flag=1;
                                        array_nuovo2=obj_nuovo2.getJSONArray(key);
                                        obj_nuovo3.put(r, "file");
                                        array_nuovo2.put(obj_nuovo3);

                                    }
                                }
                                if(flag==0)
                                {   flag=1;
                                    i=0;
                                    obj_nuovo1.put(dato1,array_nuovo1);
                                    parse.put(obj_nuovo1);

                                }


                            }}    catch (JSONException e) {
                        e.printStackTrace();
                    }


                    risultato = parse.toString();
                    textView1.setText(risultato);
                    scriviFile(risultato, FILE_FILES);

                }

            }

        });


        mBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void rename(final ArrayList<String> selezionati, final ArrayList<String> nomi){ //devo passare l'array contenente i miei oggetti

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main2Activity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_rename, null);
        final EditText rinomina = (EditText) mView.findViewById(R.id.rinomina_cartella);


        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int flag = 0;
                int flag1=0;
                int i=0;
                JSONArray parse=new JSONArray();
                String temp,temp1;
                JSONArray elem1=new JSONArray();
                JSONObject obj_nuovo1,obj_nuovo2,obj_nuovo3,obj_nuovo4;
                JSONArray array_nuovo1,array_nuovo2,array_nuovo3;
                Myobject o=new Myobject();
                JSONArray parse1=new JSONArray();

                if (!rinomina.getText().toString().isEmpty()) {
                    Toast.makeText(Main2Activity.this, "Elemento rinominato", Toast.LENGTH_SHORT).show();
                    String s;
                    s = rinomina.getText().toString();
                    String elem = selezionati.get(0);
                    int k = arrayMyObjects.indexOf(o.getNome().equals(elem));
                    //int k=nomi.indexOf(elem);
                    nomi.set(k, s);
                    count = 0;
                    selezionati.clear();
                    if (arrayMyObjects.get(k).getTipo().equals("dir")) {
                        temp = leggiFile(FILE_DIRECTORIES);

                        try {
                            parse = new JSONArray(temp);
                            JSONObject obj = new JSONObject();
                            for (i = 0; i < parse.length() && flag == 0; i++) {
                                //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                                obj = parse.getJSONObject(i);
                                //ottengo le chiavi che ci sono nel mio array json
                                Iterator iterator = obj.keys();
                                while (iterator.hasNext() && flag == 0) {
                                    String key = (String) iterator.next();
                                    if (elem.equals(key)) {
                                        flag = 1;
                                        JSONArray a = obj.getJSONArray(key);
                                        obj.remove(key);
                                        obj.put(s, a);
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            flag = 0;
                            datipassati = getIntent().getExtras();
                            String dato1 = datipassati.getString("dato");

                            if (dato1.equals("Tutti i file")) {
                                obj_nuovo2 = new JSONObject();
                                obj_nuovo3 = new JSONObject();
                                obj_nuovo4 = new JSONObject();
                                array_nuovo2 = new JSONArray();
                                array_nuovo3 = new JSONArray();

                                for (int index = 0; index < parse.length() && flag == 0; index++) {
                                    obj_nuovo2 = parse.getJSONObject(index);
                                    Iterator iterator = obj_nuovo2.keys();
                                    while (iterator.hasNext() && flag == 0) {
                                        String key = (String) iterator.next();
                                        if (key.equals("Tutti i file")) {
                                            flag = 1;
                                            array_nuovo2 = obj_nuovo2.getJSONArray(key);
                                            for (int j = 0; j < array_nuovo2.length() && flag1 == 0; j++) {
                                                obj_nuovo3 = array_nuovo2.getJSONObject(j);
                                                Iterator iterator1 = obj_nuovo3.keys();
                                                while (iterator1.hasNext() && flag1 == 0) {
                                                    String key1 = (String) iterator1.next();

                                                    if (elem.equals(key1)) {
                                                        flag1 = 1;
                                                        obj_nuovo3.remove(key1);
                                                        obj_nuovo3.put(s, "dir");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //MODIFICO ALL'INTERNO DEL FILE CONTENENTE I FILE
                                temp1=leggiFile(FILE_FILES);
                                try {
                                    parse1= new JSONArray(temp1);
                                    JSONObject obj = new JSONObject();
                                    for (i = 0; i < parse1.length() && flag == 0; i++) {
                                        //Estraggo l' oggetto json dall'array, in particolare l'i-esimo oggetto json
                                        obj = parse1.getJSONObject(i);
                                        //ottengo le chiavi che ci sono nel mio array json
                                        Iterator iterator = obj.keys();
                                        while (iterator.hasNext() && flag == 0) {
                                            String key = (String) iterator.next();
                                            if (elem.equals(key)) {
                                                flag = 1;
                                                JSONArray a = obj.getJSONArray(key);
                                                obj.remove(key);
                                                obj.put(s, a);
                                            }

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            scriviFile(parse.toString(), FILE_DIRECTORIES);
                            scriviFile(parse1.toString(),FILE_FILES);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        try {
                            temp = leggiFile(FILE_FILES);
                            for (int index = 0; index < parse.length() && flag == 0; index++) {
                                obj_nuovo2 = parse.getJSONObject(index);
                                Iterator iterator = obj_nuovo2.keys();
                                while (iterator.hasNext() && flag == 0) {
                                    String key = (String) iterator.next();
                                    if (key.equals("Tutti i file")) {
                                        flag = 1;
                                        array_nuovo2 = obj_nuovo2.getJSONArray(key);
                                        for (int j = 0; j < array_nuovo2.length() && flag1 == 0; j++) {
                                            obj_nuovo3 = array_nuovo2.getJSONObject(j);
                                            Iterator iterator1 = obj_nuovo3.keys();
                                            while (iterator1.hasNext() && flag1 == 0) {
                                                String key1 = (String) iterator1.next();

                                                if (elem.equals(key1)) {
                                                    flag1 = 1;
                                                    obj_nuovo3.remove(key1);
                                                    obj_nuovo3.put(s, "file");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            scriviFile(parse.toString(), FILE_FILES);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//RINOMINA L'ELEMENTO NELL'ARRAY JSON ESTERNO


                    textView1.setText(parse.toString());
                }
            }
        });

        mBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void eliminaCartella(ArrayList<String> daEliminare, ArrayList<Integer> immagini , ArrayList<String> nomi, String temp){
        JSONArray parse=new JSONArray();
        JSONObject obj_nuovo1,obj_nuovo2,obj_nuovo3;
        JSONArray array_nuovo1,array_nuovo2;
        int i,j,k,d;

        for (String msg : daEliminare) {
            d = nomi.indexOf(msg);
            immagini.remove(d);
            nomi.remove(msg);
            //DEVO MODIFICARE QUI IL FILE
            //modificafile();
            //leggifile(FILE_DIRECTORIES);
            int flag = 0;
            int flag1=0;
            try {
                parse = new JSONArray(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject obj = new JSONObject();
                for (i = 0; i < parse.length() && flag == 0; i++) {
                    try {
                        obj = parse.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Iterator iterator = obj.keys();
                    while (iterator.hasNext() && flag == 0) {
                        String key = (String) iterator.next();
                        if (msg.equals(key)) {
                            flag = 1;
                            obj.remove(key);
                            parse.remove(i);

                        }

                    }
                }
                flag=0;
                datipassati = getIntent().getExtras();
                String dato1 = datipassati.getString("dato");

                if(dato1.equals("Tutti i file")){
                    obj_nuovo2=new JSONObject();
                    obj_nuovo3=new JSONObject();
                    array_nuovo2=new JSONArray();

                    for( k=0;k<parse.length()&&flag==0;k++){
                        try {
                            obj_nuovo2=parse.getJSONObject(k);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Iterator iterator=obj_nuovo2.keys();
                        while(iterator.hasNext()&&flag==0) {
                            String key = (String) iterator.next();
                            if (key.equals("Tutti i file")) {
                                flag = 1;
                                try {
                                    array_nuovo2 = obj_nuovo2.getJSONArray(key);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                for( j=0;j<array_nuovo2.length()&&flag1==0;j++) {
                                    try {
                                        obj_nuovo3 = array_nuovo2.getJSONObject(j);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Iterator iterator1=obj_nuovo3.keys();
                                    while(iterator1.hasNext()&&flag1==0){
                                        String key1 = (String) iterator1.next();
                                        if(key1.equals(msg))
                                        {
                                            flag1=1;
                                            obj_nuovo3.remove(key1);
                                            array_nuovo2.remove(j);

                                        }


                                    }
                                }

                            }}}}



                scriviFile(parse.toString(),FILE_DIRECTORIES);
              //  return parse;

        }

            daEliminare.clear();

    }


    private String leggiFile(File f){
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


    public void scriviFile(String scrivi, File f){
        FileOutputStream fOut1;
        try {

            fOut1 = new FileOutputStream(f);
            fOut1.write(scrivi.getBytes());
            fOut1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray parseJson(String risultato_lettura, ArrayList<Integer> immagini, ArrayList <String> nomi,String v) {
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
                    //uso le chiavi per aggiungerle alla lista che mi serve poi per rappresentare il tutto
                   if(!key.equals("Tutti i file")) {
                       nomi.add(key);
                       if(v.equals("dir"))
                       immagini.add(R.drawable.folder);
                       else
                           immagini.add(R.drawable.file);
                   }

                }}
        } catch (JSONException e) {
            e.printStackTrace();
        }return parse;

    }


}

//POSSO FARE LA LETTURA DEL FILE ANCHE COSÌ,
/*
*   try {
            stream = new FileInputStream(yourFile);

            try {
                fc = stream.getChannel();
                bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
                tv.setText(jsonStr);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/





