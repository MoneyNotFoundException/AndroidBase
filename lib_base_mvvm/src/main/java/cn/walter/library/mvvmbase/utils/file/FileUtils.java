package cn.walter.library.mvvmbase.utils.file;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import cn.walter.library.mvvmbase.base.BaseApplication;
import cn.walter.library.mvvmbase.config.AppConfig;
import cn.walter.library.mvvmbase.utils.LogUtils;
import cn.walter.library.mvvmbase.utils.aes.Base64Api;
import top.zibin.luban.Luban;

/**
 * 文件操作工具类
 */
public class FileUtils {

    /**
     * 保存头像
     *
     * @param b
     */
    public static void saveAvatar(Bitmap b) {
        if (b == null) {
            return;
        }
        // 创建文件夹
        String fileName = "avatar.jpg";
        String avatarPath = FileUtils.getAvatarPath();
        File avatarPathfile = new File(avatarPath);
        if (!avatarPathfile.exists()) {
            avatarPathfile.mkdirs();
        }
        // 写入文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        File avatarFile = new File(avatarPathfile, fileName);
        if (avatarFile.exists()) {
            avatarFile.delete();
        }
        FileUtils.storeFileByInStream(isBm, avatarFile);
    }

    /**
     * 保存图片
     *
     * @param b
     */
    public static void saveImage(Bitmap b, String fileName) {
        if (b == null) {
            return;
        }
        // 创建文件夹
        //        String fileName = "upload.jpg";
        String imagePath = FileUtils.getImagePath() + "tianqi/";
        File imagePathfile = new File(imagePath);
        if (!imagePathfile.exists()) {
            imagePathfile.mkdirs();
        }
        // 写入文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        File imageFile = new File(imagePathfile, fileName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        FileUtils.storeFileByInStream(isBm, imageFile);
    }

    /**
     * 保存图片
     *
     * @param b
     */
    public static void saveDcimImage(Bitmap b, String fileName) {
        if (b == null) {
            return;
        }
        // 写入文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        File imageFile = new File(fileName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        FileUtils.storeFileByInStream(isBm, imageFile);
    }

    /**
     * 保存图片
     *
     * @param b
     */
    public static void saveZjImage(Bitmap b, String fileName, Context context) {
        if (b == null) {
            return;
        }
        File appDir = new File(getDcimPhoto());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File pictureFile = new File(appDir, fileName);
        if (pictureFile.exists()) {
            pictureFile.delete();
        }
        // 写入文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        FileUtils.storeFileByInStream(isBm, pictureFile);
        try {
            String insertImage = MediaStore.Images.Media.insertImage(context.getContentResolver(), pictureFile.getAbsolutePath(), fileName, null);
            File file1 = new File(getRealPathFromURI(Uri.parse(insertImage), context));
            updatePhotoMedia(file1, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //更新图库
    private static void updatePhotoMedia(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    //得到绝对地址
    private static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String fileStr = cursor.getString(column_index);
        cursor.close();
        return fileStr;
    }


    /**
     * 保存图片
     *
     * @param b
     */
    public static void saveImage(Bitmap b, String path, String fileName) {
        if (b == null) {
            return;
        }
        // 创建文件夹
        //        String fileName = "upload.jpg";
        String imagePath = path;
        File imagePathfile = new File(imagePath);
        if (!imagePathfile.exists()) {
            imagePathfile.mkdirs();
        }
        // 写入文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        File imageFile = new File(imagePathfile, fileName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        FileUtils.storeFileByInStream(isBm, imageFile);
    }

    public static boolean imgIsExistence(String imgCount) {
        String imagePath = FileUtils.getImagePath() + "tianqi/";
        File imageFile = new File(imagePath, imgCount + ".jpg");
        return imageFile.exists();
    }

    public static void deleteAvata() {
        String fileName = "avatar.jpg";
        String avatarPath = FileUtils.getAvatarPath();
        File avatarFile = new File(new File(avatarPath), fileName);
        if (avatarFile.exists()) {
            if (avatarFile.delete()) {
                Logger.e("头像", "删除成功");
            }
        }
    }

    /**
     * 加载头像 如果为空 默认 返回程序icon
     */
    public static Bitmap loadAvator() {
        return FileUtils.path2Bitmap(FileUtils.getAvatarPath()
            + "avatar.jpg");
    }

    /**
     * 获取背景图片
     */
    public static Bitmap loadBackground(String imgCount) throws IOException {
        return FileUtils.path2Bitmap(Luban.with(BaseApplication.getContext())
            .get(FileUtils.getImagePath()
                + "tianqi/" + imgCount + ".jpg").getAbsolutePath());
    }


    /**
     * 压缩图片并保存
     *
     * @param bmp
     * @param
     */
    public static void compressBmpToFile(Bitmap bmp) {
        if (bmp == null) {
            return;
        }
        // 创建文件夹
        String fileName = "upload.jpg";
        String imagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;

        File imagePathfile = new File(imagePath);
        if (!imagePathfile.exists()) {
            imagePathfile.mkdirs();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        File file = new File(imagePathfile, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统安装路径
     */
    public static String getSystemPackagePath(Context c) {
        String path = c.getApplicationContext().getFilesDir()
            .getAbsolutePath();
        return path;
    }

    /**
     * 获取头像路径
     */

    public static String getAvatarPath() {
        // 头像保存路径
        return AppConfig.DEFAULT_SAVE_AVATAR_PATH;
    }

    /**
     * 获取图片路径
     *
     * @return
     */
    public static String getImagePath() {
        // 头像保存路径
        return AppConfig.DEFAULT_SAVE_IMAGE_PATH;
    }


    public static String getPathFromUri(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = context.getContentResolver().query(uri, proj, null,
            null, null);
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        String path = cursor.getString(column_index);
        return path;
    }

    public static String getDcimPhoto() {
        return Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;

    }

    public static String getImagePathFromUri(Context context, Uri fileUrl) {
        String fileName = null;
        Uri filePathUri = fileUrl;
        if (fileUrl != null) {
            if (fileUrl.getScheme().toString().compareTo("content") == 0) {
                // content://开头的uri
                Cursor cursor = context.getContentResolver().query(fileUrl,
                    null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    fileName = cursor.getString(column_index); // 取出文件路径

                    // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
                    if (!fileName.startsWith("/storage")
                        && !fileName.startsWith("/mnt")) {
                        // 检查是否有”/mnt“前缀
                        fileName = "/mnt" + fileName;
                    }
                    cursor.close();
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) { // file:///开头的uri

                fileName = filePathUri.toString();// 替换file://
                fileName = filePathUri.toString().replace("file://", "");
                int index = fileName.indexOf("/sdcard");
                fileName = index == -1 ? fileName : fileName.substring(index);

                // if (!fileName.startsWith("/mnt")) {
                // // 加上"/mnt"头
                // fileName = "/mnt" + fileName;
                // }
            }
        }
        return fileName;
    }

    /**
     * 图片路径转为 bitmap
     *
     * @param pathString
     * @return
     */
    public static Bitmap path2Bitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string.replace("data:image/jpeg;base64,", ""), Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将input写到file文件中
     *
     * @param input
     * @return
     */
    public static void storeFileByInStream(InputStream input, File file) {
        if (null == input) {
            return;
        }
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (null != output) {
                    output.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩byte[]
     *
     * @param data
     * @return
     */
    public static byte[] Zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            gzip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * 压缩
     *
     * @param data 待压缩数据
     * @return byte[] 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    /**
     * 解压缩
     *
     * @param data 待压缩的数据
     * @return byte[] 解压缩后的数据
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    /**
     * 将input写到file文件中
     *
     * @param
     * @return
     */
    public static void storeFileByInStream(byte[] byteData, File file) {
        if (null == byteData) {
            return;
        }
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(byteData, 0, byteData.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (null != output) {
                    output.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static byte[] getBytes2(String filePath) {
        byte[] data = null;
        byte[] buffer = new byte[1024];
        int size = 0;
        try {
            FileInputStream in = new FileInputStream(filePath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            System.out.println("bytes available:" + in.available());
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            in.close();
            data = out.toByteArray();
            out.flush();
            out.close();
            System.out.println("bytes size got is:" + data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 将file文件内容读到byte[]数组中
     *
     * @param file
     * @return
     */
    public static byte[] toByteArray(File file) {
        if (!file.exists()) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(
            (int) file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 保存2进制数组到sdcard
     *
     * @param dir
     * @param fileName
     * @param datas
     * @return
     */
    public static final boolean saveBytestoSdcard(File dir, String fileName,
                                                  byte[] datas) {
        boolean sResult = false;
        if (!dir.exists()) {
            return false;
        }
        File writeFile = new File(dir, fileName);
        sResult = saveBytestoSdcard(writeFile, datas);
        return sResult;
    }

    public static final boolean saveBytestoSdcard(File file, byte[] datas) {
        boolean sResult = false;

        BufferedOutputStream bos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(file), 1024);
            bos.write(datas);
            bos.flush();
            bos.close();
            sResult = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sResult;
    }

    public static void stortFileByBytes(byte[] fileContent, File tempPdfFile) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempPdfFile, true);
            outputStream.write(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 读每一行文本内容
     *
     * @param file
     * @return
     */
    public static List<String> readFileEveryLine(File file) {
        if (!file.exists() || file.length() < 1) {
            return null;
        }
        List<String> resultList = new ArrayList<String>();
        FileReader fileReader = null;
        BufferedReader bufferReader = null;
        try {
            fileReader = new FileReader(file);
            bufferReader = new BufferedReader(fileReader);
            while (bufferReader.ready()) {
                String lineStr = bufferReader.readLine();
                if (!TextUtils.isEmpty(lineStr)) {
                    resultList.add(lineStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bufferReader) {
                    bufferReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fileReader) {
                    try {
                        fileReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 文件是否在dir中存在
     *
     * @return File
     */
    public static final boolean isExitInDir(File dir, String fileName) {
        if (null != dir && dir.exists()) {
            File file = new File(dir, fileName);
            return file.exists();
        }
        return false;
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile)
        throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    public static void saveBitmap2SdCard(String bitName, Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName + ".png");
        try {

            if (!f.exists()) {

                f.createNewFile();
            }
        } catch (IOException e) {
            Log.e("保存图片到SD卡", "在保存图片时出错：" + e.toString());

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sd卡是否存在
     *
     * @return
     */
    public static final boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取文件创建或修改时间
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getFileCreateTime(String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return "";
        long time = file.lastModified();// 返回文件最后修改时间，是以个long型毫秒数
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            .format(new Date(time));
        return ctime;
    }

    /**
     * 删除文件 或文件夹
     *
     * @param filePath
     */
    public static void deleteFiles(String filePath) {
        // FileHelper.deleteFiles2(filePath);
        RecursionDeleteFile(new File(filePath));
    }

    public static void RecursionDeleteFile(File file) {
        if (!file.exists())
            return;

        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
        }
    }

    public static byte[] getBitmapByte(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static final byte[] stream2byte(InputStream inStream)
        throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public static InputStream bitmap2InputStream(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }


    public static String bitmap2Base64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64Api.encode(bitmapBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * bitmap转base64不压缩
     *
     * @param bitmap
     * @return
     */
    public static String bitmap2Base64NoCompress(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64Api.encode(bitmapBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * bitmap转base64不压缩
     *
     * @param bitmap
     * @return
     */
    public static String bitmap2Base64To30Compress(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64Api.encode(bitmapBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static File CacheRoot;

    private static ReadWriteLock rwl = new ReentrantReadWriteLock();


    /**
     * 存储Json文件
     *
     * @param json     json字符串
     * @param fileName 存储的文件名
     * @param append   true 增加到文件末，false则覆盖掉原来的文件
     */
    public static void writeJson(Context c, String json, String fileName,
                                 boolean append) {

        rwl.writeLock().lock();
        CacheRoot = c.getFilesDir();
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            File ff = new File(CacheRoot, fileName);
            boolean boo = ff.exists();
            fos = new FileOutputStream(ff, append);
            os = new ObjectOutputStream(fos);
            if (append && boo) {
                FileChannel fc = fos.getChannel();
                fc.truncate(fc.position() - 4);

            }

            os.writeObject(json);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (os != null) {

                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            rwl.writeLock().unlock();

        }

    }

    /**
     * 读取json数据
     *
     * @param fileName
     * @return 返回值为list
     */

    @SuppressWarnings("resource")
    public static List<String> readJson(Context c, String fileName) {

        rwl.readLock().lock();
        CacheRoot = c.getFilesDir();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<String> result = new ArrayList<String>();
        File des = new File(CacheRoot, fileName);
        try {
            fis = new FileInputStream(des);
            ois = new ObjectInputStream(fis);
            while (fis.available() > 0)
                result.add((String) ois.readObject());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            rwl.readLock().unlock();

        }


        return result;
    }


    /**
     * 从assets读内容
     *
     * @param context
     * @param name
     * @return
     */
    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getName() + ".readFileFromAssets---->" + name + " not found");
        }
        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            if (is != null) {
                closeIO(is);
            }
        }
        return null == resultSb ? null : resultSb.toString();
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(FileUtils.class.getClass().getName(), e);
            }
        }
    }


    /**
     * 保存bitmap到本地
     *
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Bitmap mBitmap, String fileName) {
        try {

            File file = new File(fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static void saveImageToGallery(Context context, Bitmap bmp, String fileName) {
        File appDir = new File(AppConfig.DEFAULT_SAVE_QRCODE_IMAGE_PATH);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String sFileName = "";
        //        if (fileName.isEmpty()) {
        //            sFileName = System.currentTimeMillis() + ".jpg";
        //        } else {
        //        }
        sFileName = fileName + ".jpg";

        File file = new File(appDir, sFileName);
        if (file.exists()) {
            file = recursiveQuery(fileName, appDir, file);
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        } catch (IOException var14) {
            var14.printStackTrace();
        }

        //一次广播系统可能更细不出来，所以连续发两次
        //        8.0手机不用保存到系统相册数据库
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //        } else {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            long timeMillis = System.currentTimeMillis();
            String systemTime = String.valueOf(timeMillis);
            String brand = Build.BRAND;
            String model = Build.MODEL;
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, systemTime);
            exifInterface.setAttribute(ExifInterface.TAG_MAKE, brand);
            exifInterface.setAttribute(ExifInterface.TAG_MODEL, model);
            exifInterface.saveAttributes();
            Logger.e("systemTime-->" + systemTime + "\n设备品牌" + brand + "\n设备型号" + model);
        } catch (FileNotFoundException var11) {
            var11.printStackTrace();
        } catch (IOException var12) {
            var12.printStackTrace();
        }
        //        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        //        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    private static int count = 1;

    /**
     * 当两个图片的名字相同时，创建文件逻辑
     *
     * @param fileName 文件名称
     * @param appDir   文件夹
     * @param file     文件路径
     * @return
     */
    private static File recursiveQuery(String fileName, File appDir, File file) {
        String sf = file.toString();
        //截取图片名称后  (1)
        String substring = sf.substring(count >= 10 ? sf.length() - 8 : sf.length() - 7, sf.length() - 4);
        if (substring.contains("(") && substring.contains(")")) {
            String s = substring.substring(1, count >= 10 ? 3 : 2);
            int parseInt = Integer.parseInt(s);
            count = ++parseInt;
        }
        String mFileName = fileName + "(" + count + ")" + ".jpg";
        file = new File(appDir, mFileName);
        //有名字相同的就递归调用，直到不同未知
        if (file.exists()) {
            file = recursiveQuery(fileName, appDir, file);
        }
        return file;
    }

    public static File getSaveFile(String fileName) {
        File file = new File(getImagePath(), fileName);
        return file;
    }

    public static File saveImageToGallery(Context context, Bitmap bmp, String fileName, File filePath) {
        File appDir = filePath;
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        //        String sFileName = "";
        //        if (fileName.isEmpty()) {
        //                    sFileName = System.currentTimeMillis() + ".jpg";
        //        String fileName = System.currentTimeMillis() + ""/* + "_" + (index + 1)*/;
        //        sFileName = fileName + ".jpg";
        //        } else {
        //        }
        //        sFileName = fileName + ".jpg";

        File file = new File(appDir, fileName);
        if (file.exists()) {
            file = recursiveQuery(fileName, appDir, file);
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        } catch (IOException var14) {
            var14.printStackTrace();
        }

        //一次广播系统可能更细不出来，所以连续发两次
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //        8.0手机不用保存到系统相册数据库
        } else {
            //保存到系统相册必须要有下面的这些，不然保存不成功
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
                long timeMillis = System.currentTimeMillis();
                String systemTime = String.valueOf(timeMillis);
                String brand = Build.BRAND;
                String model = Build.MODEL;
                ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                exifInterface.setAttribute(ExifInterface.TAG_DATETIME, systemTime);
                exifInterface.setAttribute(ExifInterface.TAG_MAKE, brand);
                exifInterface.setAttribute(ExifInterface.TAG_MODEL, model);
                exifInterface.saveAttributes();
                Logger.e("systemTime-->" + systemTime + "\n设备品牌" + brand + "\n设备型号" + model);
            } catch (FileNotFoundException var11) {
                var11.printStackTrace();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        //        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        return file;
    }

    /**
     * @param outFile 图片的目录路径
     * @param bitmap
     * @return
     */
    public static File storeBitmap(File outFile, Bitmap bitmap) {
        // 检测是否达到存放文件的上限
        if (!outFile.exists() || outFile.isDirectory()) {
            outFile.getParentFile().mkdirs();
        }
        FileOutputStream fOut = null;
        try {
            outFile.deleteOnExit();
            outFile.createNewFile();
            fOut = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (IOException e1) {
            outFile.deleteOnExit();
        } finally {
            if (null != fOut) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    outFile.deleteOnExit();
                }
            }
        }
        return outFile;
    }


    /**
     * 读取照片旋转角度
     *
     * @param filepath 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Logger.e("IOException-->cannot read exif-->" + ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    /**
     * @param context
     * @param photoUri
     * @return 从系统相册选择的
     */
    public static int getOrientation(Context context, Uri photoUri) {
        int orientation = 0;
        Cursor cursor = context.getContentResolver().query(photoUri,
            new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() != 1) {
                return -1;
            }
            cursor.moveToFirst();
            orientation = cursor.getInt(0);
            cursor.close();
        }
        return orientation;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 获取指定文件夹大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     *
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            LogUtils.e("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LogUtils.e("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                LogUtils.e("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LogUtils.e("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            LogUtils.e("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                    .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            LogUtils.e("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            LogUtils.e("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    public static String readArrayListFromAssets(Context context, String name) {
        Logger.w("开始读取文件的时间 = " + new Date().getTime());
        String json = readFileFromAssets(context, name);
        return json;
    }

    //将Byte数组转换成文件
    public static File getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }
}
