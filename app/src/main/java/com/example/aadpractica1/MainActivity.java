package com.example.aadpractica1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 111;

    TextView tvResult;
    Button btSaveInternal, btSaveExternal, btShowCallLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        init();

        btSaveExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermissions();
                if(!askForPermissions()){
                    askForPermissions();
                }else{
                    writeToFileExternal(getCallDetails("externa"));
                }
            }
        });

        btSaveInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermissions();
                if(!askForPermissions()){
                    askForPermissions();
                }else{
                    writeToFileInternal(getCallDetails("interna"));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return true;
    }

    private boolean askForPermissions() {
        boolean result = false;

        // Almacenamos en variables el estado actual de los permisos correspondientes
        int readPhonePermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
        int readCallLogPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG);
        int readContactsPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);

        // En caso de que los permisos no estén garantizados, entra en la cláusula
        if(readPhonePermission != PackageManager.PERMISSION_GRANTED || readCallLogPermission != PackageManager.PERMISSION_GRANTED || readContactsPermission != PackageManager.PERMISSION_GRANTED){
            // Comprobamos si la version SDK es mayor o igual a la M
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // En caso de ser mayor, solicitamos los permisos
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }else{
            loadDefaultData();
        }

        if(readPhonePermission == PackageManager.PERMISSION_GRANTED || readCallLogPermission == PackageManager.PERMISSION_GRANTED || readContactsPermission == PackageManager.PERMISSION_GRANTED){
            loadDefaultData();
        }

        return result;
    }


    public String getCallDetails(String location){
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int cachedName = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        while(managedCursor.moveToNext()){
            String phoneNumber = managedCursor.getString(number);
            String savedContactName = managedCursor.getString(cachedName);
            String callDate = managedCursor.getString(date);
            long seconds = Long.parseLong(callDate);
            SimpleDateFormat formatter = new SimpleDateFormat("YY; MM; dd; HH; mm; ss");
            String callDateFinal = formatter.format(new Date(seconds));

            if(location.equals("interna")){
                if(savedContactName == null){
                    sb.append(callDateFinal + "; " + phoneNumber + "; " + "desconocido;" + "\n");
                }else{
                    sb.append(callDateFinal + "; " + phoneNumber + "; " + savedContactName + ";" + "\n");
                }
            }else{
                if(savedContactName == null){
                    sb.append("desconocido" + "; " + callDateFinal + "; " + phoneNumber + ";" + "\n");
                }else{
                    sb.append(savedContactName + "; " + callDateFinal + "; " + phoneNumber + ";" + "\n");
                }
            }

            sb.append("\n");

        }
        managedCursor.close();
        return sb.toString();
    }

    private void writeToFileInternal(String content) {
        File file = new File(getFilesDir(), "historial.csv");
        FileWriter fw;
        try{
            fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
            Toast writedIntoInternalNotificationSuccess = Toast.makeText(getApplicationContext(), "Se ha guardado el archivo historial.csv en la memoria interna", Toast.LENGTH_SHORT);
            writedIntoInternalNotificationSuccess.show();
        }catch(IOException e){
            Toast writedIntoInternalNotificationError = Toast.makeText(getApplicationContext(), "Ha ocurrido un error al guardar el archivo historial.csv en la memoria interna", Toast.LENGTH_SHORT);
            writedIntoInternalNotificationError.show();
            Log.e("xyzyx", e.toString());
        }
    }

    private void writeToFileExternal(String content) {
        File file = new File(getExternalFilesDir(null), "llamadas.csv");
        FileWriter fw;
        try{
            fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
            Toast writedIntoExternalNotificationSuccess = Toast.makeText(getApplicationContext(), "Se ha guardado el archivo llamadas.csv en la memoria externa", Toast.LENGTH_SHORT);
            writedIntoExternalNotificationSuccess.show();
        }catch(IOException e){
            Toast writedIntoExternalNotificationError = Toast.makeText(getApplicationContext(), "Ha ocurrido un error al guardar el archivo llamadas.csv en la memoria externa", Toast.LENGTH_SHORT);
            writedIntoExternalNotificationError.show();
            Log.e("xyzyx", e.toString());
        }
    }

    public void loadDefaultData(){
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

        Log.v("xyzyx", preferences.getString("storage", "abc"));

        if(!preferences.getString("storage", "abc").equals("abc")){
            switch (preferences.getString("storage", "abc")){
                case "External":
                    tvResult.setText(getCallDetails("externa"));
                    break;
                case "Internal":
                    tvResult.setText(getCallDetails("interna"));
                    break;
            }
        }else{
            preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("storage", "Internal");
            editor.apply();
            tvResult.setText(getCallDetails("interna"));
        }
    }

    public void init(){
        tvResult = findViewById(R.id.tvResult);
        btShowCallLogs = findViewById(R.id.btShowCallLogs);
        btSaveInternal = findViewById(R.id.btSaveInternal);
        btSaveExternal = findViewById(R.id.btSaveExternal);
        askForPermissions();
    }


    /*
    public String getContacts(){
        StringBuffer result = new StringBuffer();

        // Pasamos los contactos a un cursor
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // Recogemos los contactos del cursor
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            result.append("Nombre: " + name + "\nNúmero: " + phoneNumber + "\n");
            result.append("\n---------------------------------\n");
            alContacts.add("Nombre: " + name + "\nNúmero: " + phoneNumber + "\n");
        }
        cursor.close();

        return result.toString();
    }
     */


    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                System.out.println("Permiso concedido");
            }
        }
    }
     */
}