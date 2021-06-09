package com.fourkups.finden;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Search_Activity extends AppCompatActivity implements OnItemClickListener{
    private RecyclerView courseRV;
    AlertDialog.Builder builder,Loading_builder,image_builder;
    AlertDialog loadinDialog,alertDialog,imageDialog;
    // variable for our adapter
    // class and array list
    private CourseAdapter adapter;
    private ArrayList<CourseModal> courseModalArrayList;
    Button dialogyes,dialogno;
    ImageView close;
    //
    private static Bundle mBundleRecyclerViewState;


    InputImage image;
    int time=5000;

    ArrayList<Uri> mArrayUri;
    ArrayList<String> dupUri;


    LottieAnimationView loadinbar;
    ImageView dimage;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);




        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        LayoutInflater li = LayoutInflater.from(this);
        LayoutInflater Lo=LayoutInflater.from(this);
        LayoutInflater Im=LayoutInflater.from(this);


        View promptsView = li.inflate(R.layout.dialog_layout, null);
        View Loadview=Lo.inflate(R.layout.loadingbar,null);
        View imageview=Im.inflate(R.layout.activity_image,null);

        dialogyes=promptsView.findViewById(R.id.dialogyes);
        dialogno=promptsView.findViewById(R.id.dialogno);
        close=imageview.findViewById(R.id.close);
        dimage=imageview.findViewById(R.id.Image);

        builder = new AlertDialog.Builder(this);
        Loading_builder=new AlertDialog.Builder(this);
        image_builder=new AlertDialog.Builder(this);


        builder.setView(promptsView);
        Loading_builder.setView(Loadview);
        image_builder.setView(imageview);

         alertDialog = builder.create();
         loadinDialog=Loading_builder.create();
         imageDialog=image_builder.create();



        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        loadinDialog.setCanceledOnTouchOutside(false);
        loadinDialog.show();
        dupUri=new ArrayList<>();
        mArrayUri=new ArrayList<>();
        dupUri= getIntent().getStringArrayListExtra("SELECTED_URI");
        if(dupUri.size()>100 && dupUri.size()<200){
            time=((dupUri.size()/5)*1000)+5000;
        }
        else if(dupUri.size()>=200){
            time = ((dupUri.size() / 5) * 1000) + 10000;
        }else{
            time = ((dupUri.size() / 5) * 1000) + 2000;
        }
        loadingbar(time);
        for (String i:dupUri) {
            //Toast.makeText(this, i, Toast.LENGTH_SHORT).show();
            try {
                image = InputImage.fromFilePath(this, Uri.parse(i));
                recognizeText(image,Uri.parse((i)));
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        EditText search=findViewById(R.id.search);
        ImageView cancel=findViewById(R.id.cancel);
        courseRV=findViewById(R.id.Recyler);
        ImageView back=findViewById(R.id.back);
        loadinbar=findViewById(R.id.loadingbar);
        cancel.setVisibility(View.INVISIBLE);

        dialogyes.setOnClickListener(v -> {
            Intent intent=new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
        dialogno.setOnClickListener(v -> alertDialog.dismiss());






        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search.length()==0){
                    cancel.setVisibility(View.INVISIBLE);
                }else{
                    cancel.setVisibility(View.VISIBLE);
                }
                filter(String.valueOf(search.getText()));


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancel.setOnClickListener(v -> search.setText(""));

        back.setOnClickListener(v -> alertDialog.show());
        close.setOnClickListener(v -> imageDialog.cancel());

    }

    private void loadingbar(int time) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           //loadinbar=findViewById(R.id.loadingbar);
            loadinDialog.cancel();


          // loadinbar.setVisibility(View.INVISIBLE);

        }).start();
    }




    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<CourseModal> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (CourseModal item : courseModalArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getCourseContent().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    private void buildRecyclerView() {

        // initializing our adapter class.
        adapter = new CourseAdapter(courseModalArrayList, Search_Activity.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRV.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        courseRV.setLayoutManager(manager);

        // setting adapter to
        // our recycler view.
        courseRV.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    private void recognizeText(InputImage image, Uri uri) {
        // below line we are creating a new array list
        courseModalArrayList = new ArrayList<>();

        //Toast.makeText(this,image.toString(), Toast.LENGTH_SHORT).show();
        // [START get_detector_default]
        TextRecognizer recognizer = TextRecognition.getClient();
        // [END get_detector_default]

        // [START run_detector]
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {

                                String text="";
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    text += block.getText();
                                    for (Text.Line line : block.getLines()) {
                                        // ...
                                        for (Text.Element element : line.getElements()) {
                                            // ...
                                        }
                                    }
                                }


                                courseModalArrayList.add(new CourseModal(uri,text));
                                //Toast.makeText(Search_Activity.this,courseModalArrayList.toString(), Toast.LENGTH_SHORT).show();



                                buildRecyclerView();

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }
    @Override
    public void onBackPressed() {
        alertDialog.show();
    }

    @Override
    public void onClick(View view, int position) {
        Picasso.get().load(dupUri.get(position)).into(dimage);
        imageDialog.show();
       /* Intent intent = new Intent(getBaseContext(),ImageActivity.class);
        //Toast.makeText(this, dupUri.get(position), Toast.LENGTH_SHORT).show();
        intent.putExtra("SELECTED_Image", dupUri.get(position));
        startActivity(intent);*/

    }

}
