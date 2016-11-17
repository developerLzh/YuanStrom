package com.lzh.yuanstrom.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.lzh.yuanstrom.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2016/10/31.
 */

public class PhotoHelper {
    private Activity context;

    private int CAMERA;
    private int PICTURE;
    private int CROP;

    private String cameraPath;

    private String tempPath;
    private Uri fileUri;

    public String getCameraPath() {
        return cameraPath;
    }

    public PhotoHelper(Activity context, int CAMERA, int PICTURE, int CROP) {
        this.context = context;
        this.CAMERA = CAMERA;
        this.PICTURE = PICTURE;
        this.CROP = CROP;
    }

    /*
   * 从相册获取
   */
    public void gallery() {
//        // 激活系统图库，选择一张图片
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        context.startActivityForResult(intent, PICTURE);
    }

    /**
     * 上传图片对话框
     *
     * @param title :设置对话框标题
     */
    public void choosePic(String title) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setNeutralButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(context.getString(R.string.from_gallery), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gallery();
                    }
                })
                .setNegativeButton(context.getString(R.string.from_camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera();
                    }
                })
                .show();
    }

    /**
     * 从相机获取
     */
    public void camera() {
        if (!hasSdcard()) {
            //如果存在存储卡，将数据照片保存到本地
            Toast.makeText(context, context.getResources().getString(R.string.no_sdcard), Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg");
        cameraPath = file.getAbsolutePath();// 获取相片的保存路径

        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }

//        Uri imageUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        context.startActivityForResult(intent, CAMERA);
    }

    /**
     * 裁剪图片
     *
     * @param aspectX c裁剪框X
     * @param aspectY 裁剪框Y
     */
    public void crop(Uri uri, int outWidth, int outHeight, int aspectX, int aspectY) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outWidth);
        intent.putExtra("outputY", outHeight);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        context.startActivityForResult(intent, CROP);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            if (cameraPath != null) {
                File file1 = new File(cameraPath);
                if (file1.exists()) {
                    file1.delete();
                }
            }
            return;
        }

        if (requestCode == PICTURE) {
            if (data != null) {
                // 得到图片的全路径
                if (data != null) {
                    Uri dataUri = data.getData();
                    getFileUri(context, dataUri);
                    crop(getFileUri(), 360, 360, 1, 1);
                }
            }

        } else if (requestCode == CAMERA) {
            if (cameraPath != null) {
                File file = new File(cameraPath);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                crop(uri, 360, 360, 1, 1);
            } else {
                ToastUtil.showMessage(context, context.getString(R.string.no_sdcard));
            }
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件不存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    public void getFileUri(Context context, Uri contentUri) {
        String realPath = getRealFilePath(context, contentUri);

        if (!hasSdcard()) {
            //如果存在存储卡，将数据照片保存到本地
            Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg");

//        File file = createTemp(context);

        tempPath = file.getAbsolutePath();

        //兼容android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }


//        fileUri = Uri.fromFile(file);

        copyFile(realPath, tempPath);
    }

    public String getTempPath() {
        return tempPath;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public static void deleteTemp(String path) {
        if (StringUtils.isNotBlank(path)) {
            try {
                File file = new File(path);
                file.delete();
            } catch (Exception e) {
                Log.e("fileExc", e.toString());
            }
        }
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public void setCameraPath(String cameraPath) {
        this.cameraPath = cameraPath;
    }
}
