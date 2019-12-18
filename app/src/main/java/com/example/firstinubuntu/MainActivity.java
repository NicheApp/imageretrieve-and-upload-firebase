package com.example.firstinubuntu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.io.File;
public class MainActivity extends AppCompatActivity {
private Uri uri;
private Button choose ;
private Button upload ;
private Button rt;
private ImageView img;
public  Uri imageuri;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
private StorageTask uploadtask;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://retrieve-images-967b3.appspot.com").child("sc.jpeg");
    private StorageReference mStorageRef;
    public static  final int CAMERA_REQUEST_CODE=1;
public ProgressDialog pg;
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choose=findViewById(R.id.choose);
        img =findViewById(R.id.img);
        rt=findViewById(R.id.rt);
        upload=findViewById(R.id.upload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        pg=new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        rt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              try{
                final File localFile = File.createTempFile("image", "jpeg");
                storageRef.getFile(localFile).addOnSuccessListener(

                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                          Bitmap bitmap=BitmapFactory.decodeFile(localFile.getAbsolutePath());
                          img.setImageBitmap(bitmap);
                          // Local temp file has been created
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception exception) {
                          Toast.makeText(MainActivity.this,exception.toString(),Toast.LENGTH_LONG).show();
                      }
                  });
          }catch(Exception e)
              {
                   Log.i("///////////",e.toString());

              }}
      });
choose.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Filechooser();
    }
});
upload.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);

        Fileuploader();
    }
});
    }
    private  String getFileExtension(Uri uri)
    {

        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void Fileuploader()
    {
   StorageReference ref = storageRef.child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
       ref.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                    //    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(MainActivity.this,"Image uploaded succesfully",Toast.LENGTH_LONG);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });


    }
    public void Filechooser()
    {
Intent intent=new Intent();
intent.setType("image/*");
intent.setAction(Intent.ACTION_GET_CONTENT);
startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null);
        imageuri=data.getData();
        img.setImageURI(imageuri);
    }
}
