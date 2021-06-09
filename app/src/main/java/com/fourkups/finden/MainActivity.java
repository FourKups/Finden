package com.fourkups.finden;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button uploadFile,feedback;
    int PICK_IMAGE_MULTIPLE = 1;
    TextView greet;
    TextView greetDes;
    ArrayList<Uri> ArrayUri;
    CardView info;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        greet = findViewById(R.id.greet);
        greetDes = findViewById(R.id.greetDes);
        info=findViewById(R.id.info);

        LayoutInflater layoutInflater=LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.about_dialog, null);
        builder = new AlertDialog.Builder(this);
        TextView link=promptsView.findViewById(R.id.link);
        feedback=promptsView.findViewById(R.id.feedback);
        link.setText(Html.fromHtml("<a href=\"https://www.fourkups.com\">www.fourkups.com</a>"));
        link.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(promptsView);
        alertDialog = builder.create();


        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        info.setOnClickListener(v -> alertDialog.show());
        feedback.setOnClickListener(v -> {

            String mailto = "mailto:contact.fourkups@gmail.com";

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            emailIntent.setData(Uri.parse(mailto));
            startActivity(emailIntent);
         /*Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","contact.fourkups@gmail.com", null));
         intent.setType("message/rfc822");
         intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "contact.fourkups@gmail.com" });
         intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
         startActivity(Intent.createChooser(intent, "Feedback"));*/


        });
        getCurrentTime();

        uploadFile = findViewById(R.id.uploadFile);

        ArrayUri = new ArrayList<Uri>();
        ArrayUri.clear();

        uploadFile.setOnClickListener(v -> {

            // initialising intent
            Intent intent = new Intent();

            // setting type to select to be image
            intent.setType("image/*");

            // allowing multiple image to be selected
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    ArrayUri.add(imageurl);
                  //  Toast.makeText(this, "Selecting "+ ArrayUri.get(i), Toast.LENGTH_SHORT).show();
                }
            } else {
                Uri imageurl = data.getData();
                ArrayUri.add(imageurl);
                //Toast.makeText(this, "I'm used "+ ArrayUri.size(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
        startActiv(ArrayUri);
    }

    private void startActiv(ArrayList<Uri> arrayUri) {
        ArrayList<String> dupUri=new ArrayList<>();
        for (Uri i:arrayUri) {
            dupUri.add(i.toString());
        }
        Intent intent = new Intent(getBaseContext(), Search_Activity.class);
        intent.putExtra("SELECTED_URI", dupUri);
        if(arrayUri.size()>0){
            startActivity(intent);
        }


    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat customFormat = new SimpleDateFormat("HH");
        String time = customFormat.format(calendar.getTime());
        //Toast.makeText(this, time, Toast.LENGTH_SHORT).show();
        if((0 <= Integer.parseInt(time)) &&(Integer.parseInt(time) < 12)){
            greet.setText("Good Morning !");
            greetDes.setText("Wish you a great day ahead");
        }
        else if((12 <= Integer.parseInt(time)) &&(Integer.parseInt(time) < 16)){
            greet.setText("Good Afternoon !");
            greetDes.setText("Have you had your lunch ");
        }
        else if((16 <= Integer.parseInt(time)) &&(Integer.parseInt(time) < 20)){
            greet.setText("Happy Evening !");
            greetDes.setText("Hope you had a great day");
        }
        else if((20 <= Integer.parseInt(time)) &&(Integer.parseInt(time) < 24)){
            greet.setText("Good Night");
            greetDes.setText("It's bedtime, still you're on work ?");
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}