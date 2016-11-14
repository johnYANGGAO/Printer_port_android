package com.bjut.printer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.bjut.printer.Security.SymEncrypt;
import com.bjut.printer2.R;
import com.bjut.printer.application.MyApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore.Video;
import android.util.Log;

public class MyUtils {

	private static final String AES_ALGORITHM = "AES/CTR/NoPadding";
	private static final String PROVIDER = "BC";
	private IvParameterSpec ivSpec;
	private SecretKeySpec secretKeySpec;
    public static  MyApplication application; 
	public static String getSDPath() {
		File sdDir ;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString();
		} else {
			return null;
		}
	}

	public static String getSavepath() {
		String path = "";
		String sdcardpath = getSDPath();

		String sdStateString = android.os.Environment.getExternalStorageState();

		if (sdcardpath != null
				&& sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)
				&& android.os.Environment.getExternalStorageDirectory()
						.canWrite()) {

			path = sdcardpath + "/";

		} else {
			// path = "/data/data/" + application.getPackageName() + "/";
		}



		return path;
	}

	public static int writeString(String file, String str, int mode) {
		// mode:0:create
		// 1:overwrite
		java.io.File outputFile = new java.io.File(file);
		if (outputFile.exists() && mode == 0) {
			return -1;
		}
		java.io.PrintWriter output;
		try {
			output = new java.io.PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return -2;
		}
		output.print(str);
		output.close();
		return 0;
	}

	public static String readString(String file) {
		String inputStr = "";
		java.io.File inputFile = new java.io.File(file);
		java.util.Scanner input;
		try {
			input = new java.util.Scanner(inputFile);
			while (input.hasNext()) {
				inputStr += input.next();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		input.close();
		return inputStr;
	}

	public static int writeBytes(String file, byte[] bytes) {
		java.io.ObjectOutputStream out;
		try {
			out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(
					file));
			out.writeObject(bytes);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}

		return 0;
	}

	public static byte[] readBytes(String file) {
		byte[] r = null;
		java.io.ObjectInputStream in;
		try {
			in = new java.io.ObjectInputStream(
					new java.io.FileInputStream(file));
			r = (byte[]) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	public static int EncFile(String strfromFile, String strtoFile,
			Boolean rewrite, Boolean removeSrcFile) {
		byte[] outputBytes = SymEncrypt.encrypt(strfromFile, "key", "AES");
		int fowbr = writeBytes(strtoFile, outputBytes);
		if (fowbr != 0) {
			return 1;
		}
		return 0;
	}

	public void writeFile(Context c, String fileName, String theFile) {

		try {
			Cipher cipher = Cipher.getInstance(AES_ALGORITHM, PROVIDER);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
			byte[] encrypted = cipher.doFinal(theFile.getBytes());
			OutputStream os = c.openFileOutput(fileName, 0);
			os.write(encrypted);
			os.flush();
			os.close();
		} catch (Exception e) {
			Log.e(this.getClass().toString(), e.getMessage(), e);
		}
	}

	public static int DecFile(String strfromFile, String strtoFile,
			Boolean rewrite, Boolean removeSrcFile) {

		byte[] inputBytes = readBytes(strfromFile);
		if (inputBytes == null) {

			return 1;
		}

		String outputStr = SymEncrypt.decrypt(inputBytes, "key", "AES");
		int fowws = writeString(strtoFile, outputStr, 0);
		if (fowws != 0) {

			return 2;
		}
		return 0;
	}


	public static void copyfile(String strfromFile, String strtoFile,
			Boolean rewrite, Boolean removeSrcFile) {

		File fromFile = new File(strfromFile);

		File toFile = new File(strtoFile);

		if (!fromFile.exists()) {
			return;
		}

		if (!fromFile.isFile()) {
			return;
		}
		if (!fromFile.canRead()) {
			return;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}

		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);

			byte[] bt = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}

			fosfrom.close();
			fosto.close();
			if (removeSrcFile) {

				fromFile.delete();

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean isFilePic(String path) {
		boolean ret = false;
		File tmpFile = new File(path);
		if (tmpFile.length() <= 32) {
			return ret;

		}

		try {
			FileInputStream inputStream = new FileInputStream(path);
			byte[] buffer = new byte[2];
			String filecode = "";
			String fileType = "";
			if (inputStream.read(buffer) != -1) {
				for (int i = 0; i < buffer.length; i++) {
					filecode += Integer.toString((buffer[i] & 0xFF));
				}
				switch (Integer.parseInt(filecode)) {
				case 7790:
					fileType = "exe";
					break;
				case 7784:
					fileType = "midi";
					break;
				case 8297:
					fileType = "rar";
					break;
				case 8075:
					fileType = "zip";
					break;
				case 255216:
					fileType = "jpg";
					ret = true;
					break;
				case 7173:
					fileType = "gif";
					ret = true;
					break;
				case 6677:
					fileType = "bmp";
					ret = true;
					break;
				case 13780:
					fileType = "png";
					ret = true;
					break;
				default:
					fileType = "unknown type: " + filecode;
				}

			}
			System.out.println(fileType);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static void saveBitmap(Bitmap mBitmap, String filename)
			throws IOException {

		File f = new File(filename);
		f.createNewFile();
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
	
	public  static Bitmap GetCombinedBitMap(Bitmap bitmap1) {


		
        Bitmap bitmap2 = ((BitmapDrawable) application.getResources().getDrawable(
                R.drawable.play_normal)).getBitmap();

        Bitmap newBitmap = null;

        newBitmap = Bitmap.createBitmap(bitmap1);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();

        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();

        int w_2 = bitmap2.getWidth();
        int h_2 = bitmap2.getHeight();

        paint.setColor(Color.GRAY);
        paint.setAlpha(125);
        canvas.drawRect(0, 0, bitmap1.getWidth(), bitmap1.getHeight(), paint);

        paint = new Paint();
        canvas.drawBitmap(bitmap2, Math.abs(w - w_2) / 2,
                Math.abs(h - h_2) / 2, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
       return newBitmap;
      
    }
public static String getFileName(String pathandname){
		
		int start=pathandname.lastIndexOf("/");
		int end=pathandname.lastIndexOf(".");
		if(start!=-1 && end!=-1){
			return pathandname.substring(start+1,end);	
		}else{
			return null;
		}
		
	}


public static String FormetFileSize(long fileS) {
  
    String fileSizeString = "";
    if (fileS < 1024) {
    	 DecimalFormat df = new DecimalFormat("#");
        fileSizeString = df.format((double) fileS) + "B";
    } else if (fileS < 1048576) {
    	 DecimalFormat df = new DecimalFormat("#.00");
        fileSizeString = df.format((double) fileS / 1024) + "K";
    } else if (fileS < 1073741824) {
    	 DecimalFormat df = new DecimalFormat("#.00");
        fileSizeString = df.format((double) fileS / 1048576) + "M";
    } else {
    	 DecimalFormat df = new DecimalFormat("#.00");
        fileSizeString = df.format((double) fileS / 1073741824) +"G";
    }
    return fileSizeString;
 }


	public static String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                    int kind) {
            Bitmap bitmap = null;
            File f=new File(videoPath);
          //  bitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), kind);
            
             bitmap = ThumbnailUtils.createVideoThumbnail( f.getAbsolutePath(), Video.Thumbnails.MINI_KIND);
            
            
            System.out.println("w"+bitmap.getWidth());
            System.out.println("h"+bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            bitmap=GetCombinedBitMap(bitmap);
            return bitmap;
    }
	
}
