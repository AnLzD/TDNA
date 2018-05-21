package com.group_8.app.reminder.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.group_8.app.reminder.R;
import com.group_8.app.reminder.adapter.PriorityAdapter;
import com.group_8.app.reminder.model.AlarmReceiver;
import com.group_8.app.reminder.model.ConstKey;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskManager;
import com.group_8.app.reminder.model.TaskPriority;
import com.group_8.app.reminder.model.TaskProgress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static android.provider.MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;

public class TaskActivity extends AppCompatActivity {
    private static final int GET_PIC = 1;
    private static final int TAKE_PIC = 2;
    private static final int START_TIME_FLAG = 0;
    private static final int END_TIME_FLAG = 1;
    private static final int NOTIFY_TIME_FLAG = 2;
    private static final int REQUEST_PERMISSION_CODE = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_VIDEO_CAPTURE = 5;

    @BindView(R.id.timeStart)
    TextView metTimeStart;
    @BindView(R.id.timeEnd)
    TextView metTimeEnd;
    @BindView(R.id.timeNotify)
    TextView metTimeNotify;
    @BindView(R.id.camera)
    ImageButton mibCamera;
    @BindView(R.id.sprogress)
    Spinner mspprogress;
    @BindView(R.id.ivpicture)
    ImageView mivpicture;
    @BindView(R.id.task_name)
    EditText metname;
    @BindView(R.id.description)
    EditText metdescription;
    @BindView(R.id.cbNotify)
    CheckBox mcbnotify;
    @BindView(R.id.ibDelete)
    ImageButton mibdelete;
    @BindView(R.id.image)
    ImageButton mibimage;
    @BindView(R.id.generateText)
    ImageButton mibCaText;
    @BindView(R.id.pgb_load_picture)
    ProgressBar pgbLoadImage;
    @BindView(R.id.vvvideo)
    VideoView mvvVideo;

    private TaskManager taskManager = TaskManager.getInstance();
    private Task task;
    private MenuItem menuItem = null;
    private boolean isNewCreate = true;
    private Uri imagePath;
    private Uri videoPath;

    //0: Start time, 1: End time, 2: Notify time
    private int mTypeTime;
    private Calendar myCalendar = Calendar.getInstance();

    RecyclerView recyclerView;
    PriorityAdapter recyclerViewAdapter;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);


        //Init value start time and notify time

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mibCamera.setEnabled(false);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            } else{
                initialize();
            }
        } else {
            initialize();
        }
    }

    private void initialize(){
        final List<TaskProgress> listProgress = taskManager.getAllTaskProgresses();
        final List<TaskPriority> listPriority = taskManager.getAllTaskPriorities();
        Intent intent = getIntent();

        /**
         * Set spinner progress
         */
        {
            String[] arraySpinner = new String[listProgress.size()];

            for(int i = 0; i< listProgress.size();i++){
                arraySpinner[i] = listProgress.get(i).getProgress();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_item_text, arraySpinner);
            adapter.setDropDownViewResource(R.layout.spinner_item_text);
            mspprogress.setAdapter(adapter);

            mspprogress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    task.setProgress(listProgress.get(position));
                    Logger.log("Thien","Progress position in Oncreate: " + (task.getProgress().getId() - 1));
                    if (menuItem != null)
                        menuItem.setIcon(ContextCompat.getDrawable(TaskActivity.this, R.drawable.ic_checked));
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });
        }

        /**
         * Set list priority task
         */
        {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
            LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            if(recyclerView != null){
                recyclerViewAdapter = new PriorityAdapter(this, listPriority,
                        new PriorityAdapter.OnClickListener() {
                            @Override
                            public void onClick(int position) {
                                task.setPriority(listPriority.get(position));
                            }
                        });

                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }

        /**
         * CheckBox On click
         */
//        mcbnotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//            {
//
//            }
//        });

        /**
         * Get Object Task from intent
         * Save into intentTask
         */
        String jsonMyObject = "";
        Bundle extrs = getIntent().getExtras();
        if (extrs != null) {
            jsonMyObject = extrs.getString(ConstKey.EXTRA_TASK);
        }
        Task intentTask = new Gson().fromJson(jsonMyObject, Task.class);


        if (intentTask != null) {
            isNewCreate = false;
            mibdelete.setEnabled(true);
            //clone task
            task = new Task(intentTask);

        } else {
            isNewCreate = true;
            mibdelete.setEnabled(false);
            task = new Task();

            //Init Time Priority Progress default for Task
            {
                task.setPriority(listPriority.get(0));
                task.setProgress(listProgress.get(0));
                //Start, End, Notify Date
                task.setNotifyDate(myCalendar.getTime().getTime());
                task.setStartDate(myCalendar.getTime().getTime());
                task.setEndDate(myCalendar.getTime().getTime());
            }
        }

        Logger.log("Thien","Image link from old task " + task.getImageLink());

        initializeView();
    }

    /**
     * Add Task or Update Task
     */
    public boolean save(){
        List<TaskProgress> listProgress = taskManager.getAllTaskProgresses();
        List<TaskPriority> listPriority = taskManager.getAllTaskPriorities();
        //todo: get all value: name, descrip, priority, prgress, picture ... -> task

        //name
        if(metname != null){
            if(metname.getText().length() == 0){
                Toast.makeText(this,getString(R.string.msg_require_task_name),Toast.LENGTH_SHORT).show();
                return false;
            }
            else{
                task.setName(metname.getText().toString());
            }
        }


        //description
        if(metdescription != null)
            task.setDescription(metdescription.getText().toString());

        //Image link have been added when get image or take photo
        //todo: set link image

        //Progress and Priority have been added
        //Task Progress
        //Task priority
        //todo: set Progress priority


        //Start, End, Notify Date
        //nothing to do
        //todo: set Time

        //Boolean notify
        if(mcbnotify != null) {
            Logger.log("Thien","Check: " + mcbnotify.isChecked());
            task.setNotify(mcbnotify.isChecked());
            Logger.log("Thien","Task: Check : " +  task.getNotify());
        }


        if(isNewCreate) {
            int id = taskManager.addTask(task);
            task.setId(id);
        }
        else{
            //todo: check with update task. st wrong
            taskManager.updateTask(task);
        }

        if(mcbnotify != null && mcbnotify.isChecked()){
            enableReminder(task.getNotifyDate());
        }

        return true;
    }

    /**
     *  Delete Task
     */
    @OnClick(R.id.ibDelete)
    public void deleteTask() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        taskManager.deleteTask(task);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.deletetask).setPositiveButton(R.string.Yes, dialogClickListener)
                .setNegativeButton(R.string.No, dialogClickListener).show();
    }

    /**
     * Data change
     */
    @OnTextChanged({R.id.task_name,R.id.description,R.id.timeStart,R.id.timeEnd,R.id.timeNotify})
    @OnCheckedChanged({R.id.cbNotify})
    public void onTaskChanged() {
        if (menuItem != null)
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_checked));
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {

            //Take Photo or Select Image
            if (requestCode == GET_PIC || requestCode == TAKE_PIC) {
                final int width = this.getResources().getDisplayMetrics().widthPixels;
                int height = 200;
                final float scale = this.getResources().getDisplayMetrics().density;
                final int pixelHeight = (int) (height * scale + 0.5f);
                mivpicture.setLayoutParams(new LinearLayout.LayoutParams(0,  0));

                if (requestCode == GET_PIC) {
                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Uri, Void> loadImage = new AsyncTask<Void, Uri, Void>() {
                        @Override
                        protected void onPreExecute(){
                            int heightProgressBar = 30;
                            int pixelHeightProgressBar = (int) (heightProgressBar * scale + 0.5f);
                            pgbLoadImage.setLayoutParams(new LinearLayout.LayoutParams(width,  pixelHeightProgressBar));
                        }

                        @Override
                        protected void onPostExecute(Void result){
                            pgbLoadImage.setLayoutParams(new LinearLayout.LayoutParams(width,  0));

                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            // Create an image file name
                            String filePath = data.getData().toString();
                            Logger.log("Thien", "Select image: Get Name: " + filePath);
                            String imageFileName = filePath.substring(filePath.lastIndexOf('/'), filePath.length());

                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            File imageRaw = null;
                            File imageScale = null;

                            imageRaw = new File(storageDir,
                                    imageFileName + ".jpg"
                            );
                            imageScale = new File(storageDir,
                                    imageFileName +
                                            "thumbnails.jpg"
                            );

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(TaskActivity.this.getContentResolver(), data.getData());

                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.2), (int) (bitmap.getHeight() * 0.2), true);

                                //Convert bitmap to byte array
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                resized.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                byte[] bitmapdata = bos.toByteArray();

                                //write the bytes in file
                                FileOutputStream fos = new FileOutputStream(imageScale);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();

                                //write the bytes in raw file
                                fos = new FileOutputStream(imageRaw);

                                bos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                byte[] rawbitmapdata = bos.toByteArray();

                                fos.write(rawbitmapdata);
                                fos.flush();
                                fos.close();

                            } catch (Exception e) {
                                Logger.log("Thien", "Select image Exception: " + e);
                            }

                            Uri scaleImagePath = FileProvider.getUriForFile(TaskActivity.this,
                                    "com.example.android.fileprovider",
                                    imageScale);

                            Logger.log("Thien", "Scale File of image (File): " + scaleImagePath);

                            publishProgress(scaleImagePath);
                            return null;
                        }


                        @Override
                        protected void onProgressUpdate(Uri... values) {
                            super.onProgressUpdate(values);

                            if (values.length > 0) {
                                try {
                                    mivpicture.setImageURI(values[0]);
                                    task.setImageLink(values[0].toString());

                                    if(mivpicture != null)
                                        mivpicture.setLayoutParams(new LinearLayout.LayoutParams(width,  pixelHeight));

                                    if(metdescription != null)
                                        metdescription.setLayoutParams(new LinearLayout.LayoutParams(width,  pixelHeight / 2));
                                } catch (Exception ex) {
                                    Logger.log("Thien", "Load Image From use Camera: In process AsyncTasl");
                                }

                            }
                        }
                    }.execute();
                }
                else {

                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Uri, Void> loadImage = new AsyncTask<Void, Uri, Void>() {
                        @Override
                        protected void onPreExecute(){
                            int heightProgressBar = 30;
                            int pixelHeightProgressBar = (int) (heightProgressBar * scale + 0.5f);
                            pgbLoadImage.setLayoutParams(new LinearLayout.LayoutParams(width,  pixelHeightProgressBar));
                        }

                        @Override
                        protected void onPostExecute(Void result){
                            pgbLoadImage.setLayoutParams(new LinearLayout.LayoutParams(width,  0));

                        }

                        @Override
                        protected Void doInBackground(Void... voids){
                            // Create an image file name
                            String filePath = imagePath.toString();
                            String imageFileName = filePath.substring(filePath.indexOf("JPEG"), filePath.lastIndexOf('.'));
                            Logger.log("Thien", "Image file name: " + imageFileName);

                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            File image = null;
                            image = new File(storageDir,
                                    imageFileName + "thumbnails" + ".jpg"
                            );

                            Logger.log("Thien", "Scale File of image (File): " + image.toString());
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(TaskActivity.this.getContentResolver(), imagePath);

                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.2), (int) (bitmap.getHeight() * 0.2), true);

                                //Convert bitmap to byte array
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                resized.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                byte[] bitmapdata = bos.toByteArray();

                                //write the bytes in file
                                FileOutputStream fos = new FileOutputStream(image);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Uri scaleImagePath = FileProvider.getUriForFile(TaskActivity.this,
                                    "com.example.android.fileprovider",
                                    image);

                            Logger.log("Thien", "Scale File of image (File): " + scaleImagePath);

                            publishProgress(scaleImagePath);
                            return null;
                        }


                        @Override
                        protected void onProgressUpdate(Uri... values) {
                            super.onProgressUpdate(values);

                            if(values.length > 0) {
                                try{
                                    mivpicture.setImageURI(values[0]);
                                    task.setImageLink(values[0].toString());

                                    if(mivpicture != null)
                                        mivpicture.setLayoutParams(new LinearLayout.LayoutParams(width,  pixelHeight));

                                    if(metdescription != null)
                                        metdescription.setLayoutParams(new LinearLayout.LayoutParams(width,  pixelHeight / 2));
                                }
                                catch (Exception ex){
                                    Logger.log("Thien","Load Image From use Camera: In process AsyncTasl");
                                }

                            }
                        }
                    }.execute();
                }

                // Event Task Changed
                onTaskChanged();
            }

            //Something else
            if(requestCode == REQUEST_IMAGE_CAPTURE){
                String result = data.getStringExtra(ConstKey.GENERATE_TEXT_FROM_CAMERA);
                metdescription.setText(metdescription.getText() + "\n" + result);
                onTaskChanged();
            }

            if (requestCode == REQUEST_VIDEO_CAPTURE ) {
                mvvVideo.setVideoURI(videoPath);

                int pixelsWidth = this.getResources().getDisplayMetrics().widthPixels;
                int height = 200;
                float scale = this.getResources().getDisplayMetrics().density;
                int pixelHeight = (int) (height * scale + 0.5f);
                mvvVideo.setLayoutParams(new LinearLayout.LayoutParams(pixelsWidth,  pixelHeight));

                //save uri video
                onTaskChanged();
            }
        }
    }

    /**
     * @param type
     * type: 1: picture
     * type: 2: video
     * @return
     * @throws IOException
     */
    private File createFile(int type) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = null;

        if(type == 1){
            String fileName = "JPEG_" + timeStamp + "_";
            file = File.createTempFile(
                    fileName,  /* prefix */
                    "raw.jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }
        else if(type == 2){
            String fileName = "MP4_" + timeStamp + "_";
            file = File.createTempFile(
                    fileName,  /* prefix */
                    "raw.mp4",         /* suffix */
                    storageDir      /* directory */
            );
        }

        return file;
    }
    /**
     * Open camera and take a photo
     */
    @OnClick(R.id.camera)
    public void takeAPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Logger.log("Thien","Call take picture");
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createFile(1);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Logger.log("Thien","Exception: " + ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imagePath = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                startActivityForResult(intent, TAKE_PIC);
            }
            else{
                Logger.log("Thien","Photo file null ");
            }

        }
        else{
            Logger.log("Thien","Fail: Call take picture");
        }
    }

    @OnClick(R.id.ivpicture)
    public void onImageClicked(){
        if(task.getImageLink()!= null) {
            Intent openImageIntent = new Intent(Intent.ACTION_VIEW);

            openImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            String sFileImage = task.getImageLink();

            String sRawName = sFileImage.substring(sFileImage.lastIndexOf('/'), sFileImage.lastIndexOf("thumbnails"));

            String sRawFile = sFileImage.substring(0, sFileImage.lastIndexOf('/')) + sRawName + ".jpg";

            openImageIntent.setData(Uri.parse(sRawFile));
            openImageIntent.putExtra("force_fullscreen", true);
            startActivity(openImageIntent);

        }
    }
    /**
     * Select image from gallery
     */
    @OnClick(R.id.image)
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);

        intent.setType("image/*");
        startActivityForResult(intent, GET_PIC);

    }

    @OnTouch(R.id.vvvideo)
    public boolean onVideoClicked(){
        Intent openVideoIntent = new Intent(Intent.ACTION_VIEW);
        if (openVideoIntent.resolveActivity(getPackageManager()) != null) {
            openVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if(videoPath != null){
                try{
                    openVideoIntent.setData(videoPath);
                    startActivity(openVideoIntent);
                }
                catch (Exception ex){
                    Logger.log("Thien","Function OnVideoClicked: " + ex);
                }
            }
        }
        return false;
    }

    @OnLongClick(R.id.ivpicture)
    public boolean onLongPictureClick() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        task.setImageLink(null);
                        mivpicture.setLayoutParams(new LinearLayout.LayoutParams(0,  0));

                        int width = TaskActivity.this.getResources().getDisplayMetrics().widthPixels;
                        int height = 150;
                        final float scale = TaskActivity.this.getResources().getDisplayMetrics().density;
                        int pixelHeight = (int) (height * scale + 0.5f);

                        metdescription.setLayoutParams(new LinearLayout.LayoutParams(width, pixelHeight));
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.msg_delete_image).setPositiveButton(R.string.Yes, dialogClickListener)
                .setNegativeButton(R.string.No, dialogClickListener).show();
        return false;
    }

    @OnClick(R.id.video)
    public void takeVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            File videoFile = null;
            try {
                videoFile = createFile(2);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Logger.log("Thien","Exception: " + ex);
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                videoPath = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        videoFile);

                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoPath);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
            else{
                Logger.log("Thien","Photo file null ");
            }
        }
    }

    @OnClick(R.id.generateText)
    public void OnGenerateText(){
        Intent takePicIntent = new Intent(TaskActivity.this,CameraActivity.class);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Open time picker
     */
    private void showTimePicker(){
        final List<TaskProgress> listProgress = taskManager.getAllTaskProgresses();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minutes);
                        myCalendar.set(Calendar.SECOND, 0);
                        myCalendar.set(Calendar.MILLISECOND,0);

                        if(TaskActivity.this.mTypeTime == START_TIME_FLAG) {
                            task.setStartDate(myCalendar.getTime().getTime());

                            if(metTimeStart != null)
                                metTimeStart.setText(CreateFormatTime(task.getStartDate()));

                            /**
                             * Compare Start Time and End Time.
                             * if Start time > End time
                             * => Set: End time = Start time
                             */
                            if ((int) (task.getStartDate() / 1000) > (int) (task.getEndDate() / 1000)) {
                                task.setEndDate(task.getStartDate());
                                if(metTimeEnd != null)
                                    metTimeEnd.setText(CreateFormatTime(task.getEndDate()));
                            }

                            /**
                             * Compare Start time and Notify time.
                             * if  Notify time > Start time
                             * => Set: Notify time = Start time
                             */

                            //Day of Start time = Day of Notify time
                            //Change day of notify time
                            Date notifyDate = new Date(task.getNotifyDate());
                            int notifyMinutes = notifyDate.getMinutes();
                            int notifyHours = notifyDate.getHours();
                            notifyDate.setTime(task.getStartDate());
                            notifyDate.setHours(notifyHours);
                            notifyDate.setMinutes(notifyMinutes);

                            if(notifyDate.getTime() >= task.getStartDate()){
                                task.setNotifyDate(task.getStartDate());
                            }

                            metTimeNotify.setText(CreateFormatNotifyTime(task.getStartDate(),task.getNotifyDate()));
                        }
                        else if(TaskActivity.this.mTypeTime == END_TIME_FLAG){
                            task.setEndDate(myCalendar.getTime().getTime());

                            /**
                             * If time end < time start: invalid
                             * Set: time end = time start
                             */
                            if (task.getEndDate() < task.getStartDate()) {
                                Date date = new Date(task.getStartDate());
                                Toast.makeText(TaskActivity.this,getString(R.string.error_time_end,
                                        date.getHours(),
                                        date.getMinutes(),
                                        date.getDate(),
                                        date.getMonth() + 1,
                                        date.getYear() + 1900),Toast.LENGTH_SHORT).show();
                                task.setEndDate(task.getStartDate());
                            }

                            if(metTimeEnd != null)
                                metTimeEnd.setText(CreateFormatTime(task.getEndDate()));
                        }
                        else{
                            Date date = myCalendar.getTime();
                            date.setTime(task.getStartDate());
                            date.setHours(hourOfDay);
                            date.setMinutes(minutes);

                            task.setNotifyDate(date.getTime());

                            if(task.getNotifyDate() > task.getStartDate()){
                                date.setTime(task.getStartDate());
                                Toast.makeText(TaskActivity.this,getString(R.string.error_time_notify,date.getHours(),date.getMinutes()),Toast.LENGTH_SHORT).show();
                                task.setNotifyDate(task.getStartDate());
                            }

                            metTimeNotify.setText(CreateFormatNotifyTime(task.getStartDate(),task.getNotifyDate()));
                        }
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    /**
     * Open date picker
     */
    private void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimePicker();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    /**
     * Set time Start
     */
    @OnClick(R.id.timeStart)
    public void setStartTime() {
        this.mTypeTime = START_TIME_FLAG;
        showDatePicker();
    }

    /**
     * Set Time End
     */
    @OnClick(R.id.timeEnd)
    public void setEndTime() {
        this.mTypeTime = END_TIME_FLAG;
        showDatePicker();
    }

    /**
     * Set notify time
     */
    @OnClick(R.id.timeNotify)
    public void setNotifyTime() {
        this.mTypeTime = NOTIFY_TIME_FLAG;
        showTimePicker();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mibCamera.setEnabled(true);
                initialize();
                return;
            }
        }
        Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show();
    }

    /**
     * Save data (on menu)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.isave:
                if(this.save()){
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_checked_issave));
                    finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     */
    public void initializeView()    {
        //name
        metname.setText(task.getName());

        //Description
        metdescription.setText(task.getDescription());

        //Priority
        if(recyclerViewAdapter != null){
            recyclerViewAdapter.setActive(task.getPriority().getId() - 1);
            Logger.log("Thien Priority","Value Task priority id: " + (task.getPriority().getId() - 1));
        }

        //Start date
        metTimeStart.setText(CreateFormatTime(task.getStartDate()));

        //End date
        metTimeEnd.setText(CreateFormatTime(task.getEndDate()));

        //Time notify
        metTimeNotify.setText(CreateFormatNotifyTime(task.getStartDate(),task.getNotifyDate()));

        if(task.getImageLink() != null){
            try{
                int width = this.getResources().getDisplayMetrics().widthPixels;
                int height = 200;
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixelHeight = (int) (height * scale + 0.5f);


                Logger.log("Thien", "Load Image Start with" + task.getImageLink());
                Uri uri = Uri.parse(task.getImageLink());
                mivpicture.setImageURI(uri);

                if (mivpicture != null)
                    mivpicture.setLayoutParams(new LinearLayout.LayoutParams(width, pixelHeight));

                if (metdescription != null)
                    metdescription.setLayoutParams(new LinearLayout.LayoutParams(width, pixelHeight / 2));

            }catch (Exception e){
                Logger.log("Thien","Load Image Error");
                Logger.log("Thien",e.getMessage());
                //todo: catch error when link is wrong or picture was deleted
            }
        }

        //Progress
        mspprogress.setSelection(task.getProgress().getId() - 1);

        //Notify
        Logger.log("Thien","Initialize View Checkbox: " + task.getNotify());
        mcbnotify.setChecked(task.getNotify());

        //Hide Button Delete
        if(isNewCreate == true){
            if(mibdelete != null){
                mibdelete.setLayoutParams(new LinearLayout.LayoutParams(0,  0));
            }
        }
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_task, menu);

        menuItem = menu.findItem(R.id.isave);
        return true;
    }

    private String CreateFormatNotifyTime(long timeStart,long timeNotify){
        Calendar notifyCalendar = Calendar.getInstance();

        notifyCalendar.setTimeInMillis(timeNotify);


        Date dateNotify = new Date(timeNotify);

        return getString(R.string.time_notify_hour_minutes_day,
                dateNotify.getHours(),
                dateNotify.getMinutes(),
                dateNotify.getDate(),
                dateNotify.getMonth() + 1,
                dateNotify.getYear() + 1900);

    }

    private String CreateFormatTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy  hh:mm a");
        return format.format(date);
    }

    private void enableReminder(long time) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);

        notificationIntent.putExtra(ConstKey.GET_TASK,new Gson().toJson(task));

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 22, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();

        Logger.log("Khanh","Time from current: " + (time - System.currentTimeMillis()) );
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, broadcast);
    }

    @OnTouch(R.id.description)
    public boolean onScrollDescription(View view, MotionEvent event) {
        view.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return false;
    }
}

//todo CheckError nhấn 2 lần 2 nút image button priority
//Lỗi nhấn 2 lần 2 nút image button priority