package com.bjut.printer.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bjut.printer.application.MyApplication;
import com.bjut.printer.utils.Constants;

import android.os.Environment;

public class UtilSendAndReceiveFile {
//	public static MyApplication mApplication;
//	private static Socket socket;
//	private static DataInputStream input = null;
//	private static DataOutputStream output = null;
//	private static PrintStream printStream = null;
//	private static BufferedReader bufferReader = null;
//	private static HashMap<String, File> sendFileMap = null;
//	private static ArrayList<File> fileSendList = new ArrayList<File>();
//	private static boolean notSendOverAllFiles = true;
//	public static boolean receiveMessage = false;
//	public static boolean receiveFile = false;
//	private static File nowRecevieFile = null;
//	public static String savePath = null;
//	private static long fileLength = 0;
//	private static long hasReceiveBigFileLength = 0;
//	private static long hasSendBigFileLength = 0;
//	private static long bigFileLength = 0;
//	private static String bigFileName = "HAHANONAME";
//	private static boolean bigFileSendOver = false;
//	public static boolean isConnected = false;
//	public static boolean canSendFile = true;
//	public static boolean cancelSend = false;
//	public static boolean isSending = false;
//	private static boolean begin = true;

//	public static int notiProBarCurrentValue = 0;
//	public static String notiFileName = "";
//
//	public static File rootFile = null;
//
//	public static int cilentNum = 0;

//	public static void initUtilSendAndRecFile(Socket mSocket) {
//		try {
//			isConnected = true;
//			begin = true;
//			socket = mSocket;
//			printStream = new PrintStream(socket.getOutputStream(), true, "utf-8");
//			bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
//			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//			output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//			rootFile = Environment.getExternalStorageDirectory();
//			receiveMessage = true;
//			new Thread() {
//				public void run() {
//					receiveMessage();
//				}
//			}.start();
//			if (mApplication.mMainActivity.playstatus) {
//				sendMessage(Constants.INT_IS_PRINTING +  Constants.STR_REGEX+ mApplication.m_szAndroidID);
//			} else {
//				sendMessage(Constants.INT_IS_CONNECTED + Constants.STR_REGEX + mApplication.m_szAndroidID);
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			isConnected = false;
//			e.printStackTrace();
//		}
//	}

//	public static void beginSendFile() {
//		notSendOverAllFiles = true;
//		canSendFile = false;
//		isSending = true;
//		sendMessage(Constants.INT_READY_SEND + "");
//	}

//	public static void sendMessage(String message) {
//		printStream.println(Constants.STR_MESSAGE_PREFIX + Constants.STR_REGEX + message);
//	}

//	private static String getInfoBuff(char[] buff, int count) {
//		char[] temp = new char[count];
//		for (int i = 0; i < count; i++) {
//			temp[i] = buff[i];
//		}
//		return new String(temp);
//	}

//	public static String replace(String from, String to, String source) {
//		if (source == null || from == null || to == null)
//			return null;
//		StringBuffer bf = new StringBuffer("");
//		int index = -1;
//		while ((index = source.indexOf(from)) != -1) {
//			bf.append(source.substring(0, index) + to);
//			source = source.substring(index + from.length());
//			index = source.indexOf(from);
//		}
//		bf.append(source);
//		return bf.toString();
//	}

//	public static String replaceBlank(String str) {
//		String dest = "";
//		if (str != null) {
//			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//			Matcher m = p.matcher(str);
//			dest = m.replaceAll("");
//		}
//		return dest;
//	}
//
//	public static String getStringNoBlank(String str) {
//		if (str != null && !"".equals(str)) {
//			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//			Matcher m = p.matcher(str);
//			String strNoBlank = m.replaceAll("");
//			return strNoBlank;
//		} else {
//			return str;
//		}
//	}
/**
	private static void receiveMessage() {
		try {
			while (begin) {
				while (socket.isConnected() && !socket.isInputShutdown()) {
					if (receiveMessage == false) {
						continue;
					}
					int count = 0;
					char[] buffer = new char[256];
					// if((count = bufferReader.read(buffer))>0)
					String message = bufferReader.readLine();
					if (message != null) {
						String[] messages = message.split(Constants.STR_REGEX);
						if (messages.length < 2)
							continue;
						String msg = messages[1].trim();
						msg = msg.replace('\n', ' ');
						msg = msg.replace('\r', ' ');
						String msgconent = getStringNoBlank(msg);
						switch (Integer.parseInt(msgconent)) {
						case Constants.INT_OTHER_CONNECT_SERVER:
							break;
						case Constants.INT_READY_SEND:
							canSendFile = false;
							sendMessage(Constants.INT_BEGIN_SEND + "");
							break;
						case Constants.INT_NOT_RECEIVE_FILE:
							otherNotReceiveFile();
							break;
						case Constants.INT_BEGIN_SEND:
							if (notSendOverAllFiles)
								sendFileInf();
							if (cancelSend) {
								sendMessage(Constants.INT_OTHER_CANCEL_SEND + "");
								canSendFile = true;
								cancelSend = false;
								hasSendBigFileLength = 0;
							}
							break;
						case Constants.INT_RECEIVE_FILE:
							nowRecevieFile = new File(savePath, messages[2]);
							fileLength = Long.parseLong(messages[3]);
							if (fileLength > rootFile.getFreeSpace()) {
								sendMessage(Constants.INT_HAS_NOT_ROOM + "");
							} else {
								if (mApplication.mMainActivity.playstatus) {
									sendMessage(Constants.INT_IS_PRINTING + "");
								} else if (!mApplication.mMainActivity.gatestatus){
									sendMessage(Constants.INT_IS_OPENED + "");
								}else {
									if (nowRecevieFile.exists())
										nowRecevieFile.delete();
									receiveMessage = false;
									receiveFile = true;
									new Thread() {
										public void run() {
											sendMessage(Constants.INT_OK + Constants.STR_REGEX);
											receiveFile(nowRecevieFile, fileLength);
										}
									}.start();
								}
							}
							break;
						case Constants.INT_BIG_FILE:
							nowRecevieFile = new File(savePath, messages[2]);
							fileLength = Long.parseLong(messages[3]);
							bigFileLength = Long.parseLong(messages[4]);

							if ((!bigFileName.equals(nowRecevieFile.getName())) && (bigFileLength > rootFile.getFreeSpace())) {
								sendMessage(Constants.INT_HAS_NOT_ROOM + "");
							} else {
								if (!bigFileName.equals(nowRecevieFile.getName())) {
									bigFileName = nowRecevieFile.getName();
									if (nowRecevieFile.exists())
										nowRecevieFile.delete();
								}
								receiveMessage = false;
								receiveFile = true;
								new Thread() {
									public void run() {
										sendMessage(Constants.INT_OK + Constants.STR_REGEX);
										receiveBigFile(nowRecevieFile, fileLength);
									}
								}.start();
							}
							break;
						case Constants.INT_OK:
							new Thread() {
								public void run() {
									try {
										Thread.sleep(500);
										sendAFile(fileSendList.get(Constants.ZERO_NO));
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
							break;
						case Constants.INT_SEND_OVER:
							hasReceiveBigFileLength = 0;
							canSendFile = true;
							UtilSendAndReceiveFile.socket.close();
							break;
						case Constants.INT_OTHER_CANCEL_SEND:
							receiveFile = false;
							receiveMessage = true;
							canSendFile = true;
							hasReceiveBigFileLength = 0;
							nowRecevieFile = null;
							break;
						case Constants.INT_OTHER_EXIT:
							release();
							begin = false;
							isConnected = false;
							cilentNum = 0;
							break;
						case Constants.INT_HAS_NOT_ROOM:
							isSending = false;
							notSendOverAllFiles = false;
							canSendFile = true;
							break;
						case Constants.INT_PLAY:
							mApplication.filepath = savePath + "/" + nowRecevieFile.getName();
							mApplication.mMainActivity.PC_SEND = true;
							mApplication.mMainActivity.UIHandler.sendEmptyMessage(6);
							break;
						case Constants.INT_STATUS:
							if (mApplication.mMainActivity.playstatus) {
								sendMessage(Constants.INT_IS_PRINTING +  Constants.STR_REGEX+ mApplication.m_szAndroidID);
							} else {
								sendMessage(Constants.INT_IS_CONNECTED + Constants.STR_REGEX + mApplication.m_szAndroidID);
							}
							break;
						}
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

	}
 */

/*
	private static void receiveFile(File file, long fileLength) {
		try {
			long receiveFileLength = 0;
			DataOutputStream fileOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			byte[] buffer = new byte[1024 * 5];
			while (receiveFileLength < fileLength) {
				int r = input.read(buffer);
				if(r>=0)
				{
					fileOutput.write(buffer, 0, r);
					fileOutput.flush();
					receiveFileLength += r;
				}
				else
				{
					break;
				}
				
			}
			fileOutput.close();
			notiFileName = file.getName();
			notiProBarCurrentValue = 100;
			receiveMessage = true;
			receiveFile = false;
			UtilSendAndReceiveFile.fileLength = 0;
			sendMessage(Constants.INT_BEGIN_SEND + "");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/

/**
	private static void receiveBigFile(File file, long fileLength) {
		try {
			long receiveFileLength = 0;
			RandomAccessFile fileOutput = new RandomAccessFile(file, "rw");
			fileOutput.seek(file.length());
			byte[] buffer = new byte[10 * 1024];
			while (receiveFileLength < fileLength) {
				int r = input.read(buffer);
				fileOutput.write(buffer, 0, r);
				receiveFileLength += r;
			}
			fileOutput.close();
			receiveMessage = true;
			receiveFile = false;
			hasReceiveBigFileLength += receiveFileLength;
			notiFileName = file.getName();
			notiProBarCurrentValue = (int) (hasReceiveBigFileLength * 100 / bigFileLength);
			UtilSendAndReceiveFile.fileLength = 0;
			if (fileLength < Constants.CUT_BIG_LENGTH) {
				hasReceiveBigFileLength = 0;
				notiFileName = file.getName();
				notiProBarCurrentValue = 100;
				bigFileName = "HAHANONAME";
			}
			sendMessage(Constants.INT_BEGIN_SEND + "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
 */

/**
	private static void sendAFile(File file) {
		System.out.println("ServerSocketWifi---> SendFile run at 164 ");
		try {
			if (file.length() > Constants.BIG_LENGTH)
				sendBigFile(file, hasSendBigFileLength);
			else {
				DataInputStream inputFile = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				byte[] buffer = new byte[1024 * 5];
				while (socket.isConnected() && !socket.isOutputShutdown()) {
					int r = inputFile.read(buffer);
					if (r == -1) {
						inputFile.close();
						notiFileName = file.getName();
						notiProBarCurrentValue = 100;
						if (fileSendList.size() > 0) {
							fileSendList.remove(Constants.ZERO_NO);
							receiveFile = false;
							receiveMessage = true;
							if (fileSendList.size() == 0) {
								notSendOverAllFiles = false;
								Thread.sleep(2 * 1000);
								sendMessage(Constants.INT_SEND_OVER + "");
								canSendFile = true;
								isSending = false;
							}
						}

						break;
					}
					output.write(buffer, 0, r);
					output.flush();
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 */

/**
	private static void sendBigFile(File file, long offset) {
		try {
			long hasSendFileLength = 0;
			RandomAccessFile inputFile = new RandomAccessFile(file, "r");
			inputFile.seek(offset);
			byte[] buffer = new byte[10 * 1024];
			while (hasSendFileLength < Constants.CUT_BIG_LENGTH) {
				int r = inputFile.read(buffer);
				if (r == -1) {
					inputFile.close();
					hasSendBigFileLength = 0;
					bigFileSendOver = true;
					if (fileSendList.size() > 0) {
						fileSendList.remove(Constants.ZERO_NO);
						receiveFile = false;
						receiveMessage = true;
						if (fileSendList.size() == 0) {
							notSendOverAllFiles = false;
							Thread.sleep(2 * 1000);
							sendMessage(Constants.INT_SEND_OVER + "");
							canSendFile = true;
							isSending = false;
						}
					}
					break;
				}
				output.write(buffer, 0, r);
				output.flush();
				hasSendFileLength += r;
			}
			inputFile.close();
			hasSendBigFileLength += hasSendFileLength;
			int currentValue = (int) (hasSendBigFileLength * 100 / bigFileLength);
			if (bigFileSendOver) {
				notiFileName = file.getName();
				notiProBarCurrentValue = 100;
				hasSendBigFileLength = 0;
				bigFileSendOver = false;
				bigFileName = "HAHANONAME";
			} else {
				notiFileName = file.getName();
				notiProBarCurrentValue = currentValue;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 */

//	public static void setSendFileMap(HashMap<String, File> sendFileMap) {
//		UtilSendAndReceiveFile.sendFileMap = sendFileMap;
//		refreshFileList();
//	}
//
//	private static void sendFileInf() {
//		File nowFile = fileSendList.get(Constants.ZERO_NO);
//		if (nowFile.length() > Constants.BIG_LENGTH) {
//			long thisFileLength = Constants.CUT_BIG_LENGTH;
//			bigFileLength = nowFile.length();
//			if ((nowFile.length() - hasSendBigFileLength) < Constants.CUT_BIG_LENGTH)
//				thisFileLength = nowFile.length() - hasSendBigFileLength;
//			sendMessage(Constants.INT_BIG_FILE + Constants.STR_REGEX + nowFile.getName() + Constants.STR_REGEX + thisFileLength + Constants.STR_REGEX
//					+ nowFile.length());
//			if (!bigFileName.equals(nowFile.getName())) {
//				bigFileName = nowFile.getName();
//			}
//		} else {
//			sendMessage(Constants.INT_RECEIVE_FILE + Constants.STR_REGEX + nowFile.getName() + Constants.STR_REGEX + nowFile.length());
//		}
//
//	}
//
//	public static void otherNotReceiveFile() {
//		isSending = false;
//		notSendOverAllFiles = false;
//		canSendFile = true;
//		// fileSendList.clear();
//	}
//
//	public static void cancelSendFile() {
//		cancelSend = true;
//		isSending = false;
//		notSendOverAllFiles = false;
//	}
//
//	private static void refreshFileList() {
//		// notSendOverAllFiles = true ;
//		if (fileSendList.size() > 0)
//			fileSendList.clear();
//		Collection<File> collection = sendFileMap.values();
//		Iterator<File> iterator = collection.iterator();
//		while (iterator.hasNext()) {
//			File file = (File) iterator.next();
//			if (file.isFile())
//				fileSendList.add(file);
//			else
//				addFileInDirectory(file);
//		}
//	}
//
//	private static void addFileInDirectory(File directoryFile) {
//		File[] files = directoryFile.listFiles();
//		if (files.length > 0)
//			for (File file : files) {
//				if (file.isFile())
//					fileSendList.add(file);
//				else
//					addFileInDirectory(file);
//			}
//	}
//
//	public static void release() {
//		try {
//			printStream.close();
//			bufferReader.close();
//			input.close();
//			output.close();
//			socket.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	
}
