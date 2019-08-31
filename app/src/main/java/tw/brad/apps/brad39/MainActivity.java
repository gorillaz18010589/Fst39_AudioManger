package tw.brad.apps.brad39;
//目的手機音效
//android intent share 分享叫你手機的裝置去做
// api'com.google.guava:guava:28.0-android'
//開權限 <uses-permission android:name="android.permission.RECORD_AUDIO"/>
//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
//RECORD_AUDIO:錄音權限
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity { private AudioManager amgr;
    private File sdroot;
    private MediaRecorder recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //權限設置
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    123);
        }



        amgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        sdroot = Environment.getExternalStorageDirectory();
    }

    public  void test1(View view){
        amgr.playSoundEffect(AudioManager.FX_KEYPRESS_INVALID,1);//使用聲音效果(1.聲音管理者.)
    }
    public  void test2(View view){
        amgr.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,1);//按鍵聲
    }
    //增加音量
    public  void test3(View view){
        amgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,0);
    }
    //減少音量
    public  void test4(View view){
        amgr.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,0);
    }

    //叫別人的錄音
    public  void test5(View view){
        //拜託手機內建的錄音檔出來錄音
       Intent intent= new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);//(1.多媒體物件.底下的音響.底下的Media.錄音程式)
        startActivityForResult(intent,123);//啟用intent過去

        //把錄音的檔案寫入在指定位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(sdroot, "brad20190825.amr")));

        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Log.v("brad", "OK");
            // Sound recorder does not support EXTRA_OUTPUT
            Uri uri = data.getData();
            try {
                String filePath = getAudioFilePathFromUri(uri);
                Log.v("brad", filePath);
//                copyFile(filePath, new File(sdroot, "brad.amr"));
//                getContentResolver().delete(uri, null, null);
//                (new File(filePath)).delete();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }else if (resultCode == RESULT_CANCELED){
            Log.v("brad", "Cancel");
        }
    }


    //取得錄音檔案位置
    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();//移動到第一列
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    private void copyFile(String fileName, File target) throws IOException {
        Files.copy(new File(fileName), target);
    }

    //自己錄影的檔播放,在背景偷錄
    public void test6(View view) {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            recorder.setOutputFile(new File(sdroot, "brad0825.mp3").getAbsolutePath());
            //recorder.setOutputFile(new File(sdroot, "brad0825.3gp"));
            recorder.prepare();
            recorder.start();

        }catch (Exception e){
            Log.v("brad", e.toString());
        }

    }
    //停止錄音
    public void test7(View view) {
        if (recorder != null){
            recorder.stop();
            recorder.reset();   // You can reuse the object by going back to setAudioSource() step
            recorder.release();
        }
    }
}