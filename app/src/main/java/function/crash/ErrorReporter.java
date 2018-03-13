/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package function.crash;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * The ErrorReporter is a Singleton object in charge of collecting crash context
 * data and sending crash reports. It registers itself as the Application's
 * Thread default {@link UncaughtExceptionHandler}.
 * </p>
 * <p>
 * When a crash occurs, it collects data of the crash context (device, system,
 * stack trace...) and writes a report file in the application private
 * directory. This report file is then sent :
 * <ul>
 * </p>
 */
public class ErrorReporter implements Thread.UncaughtExceptionHandler {
	private static final String LOG_TAG = CrashReport.LOG_TAG;

	/**
	 * Checks and send reports on a separate Thread.
	 *
	 * @author Kevin Gaudin
	 */
	public final class ReportsSenderWorker extends Thread {
		private String mBody = null;
		private String mReportFileName = null;

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// checkAndSendReports(mContext, mReportFileName);
			try {
				sendMail(mContext, mReportFileName, mBody);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void setCommentReportFileName(String reportFileName) {
			mReportFileName = reportFileName;
		}

		void setCustomComment(String body) {
			mBody = body;
		}
	}

	/**
	 * This is the number of previously stored reports that we send in
	 * to avoid ANR on application start.
	 */
	private static final int MAX_SEND_REPORTS = 5;

	// These are the fields names in the POST HTTP request sent to
	// the GoogleDocs form. Any change made on the structure of the form
	// will need a mapping check of these constants.
	private static final String SVN_KEY = "SVN";
	private static final String VERSION_NAME_KEY = "VersionName";
	private static final String VERSION_CODE_KEY = "VersionCode";
	private static final String PACKAGE_NAME_KEY = "PackageName";
	private static final String APP_CHANNEL_KEY = "PackageChannel";
	private static final String FILE_PATH_KEY = "FilePath";
	private static final String PHONE_MODEL_KEY = "PhoneModel";
	private static final String ANDROID_VERSION_KEY = "AndroidVersion";
	private static final String BOARD_KEY = "BOARD";
	private static final String BRAND_KEY = "BRAND";
	private static final String DEVICE_KEY = "DEVICE";
	private static final String DISPLAY_KEY = "DISPLAY";
	private static final String FINGERPRINT_KEY = "FINGERPRINT";
	private static final String HOST_KEY = "HOST";
	private static final String ID_KEY = "ID";
	private static final String MODEL_KEY = "MODEL";
	private static final String PRODUCT_KEY = "PRODUCT";
	private static final String TAGS_KEY = "TAGS";
	private static final String TIME_KEY = "TIME";
	private static final String TYPE_KEY = "TYPE";
	private static final String USER_KEY = "USER";
	private static final String TOTAL_MEM_SIZE_KEY = "TotalMemSize";
	private static final String AVAILABLE_MEM_SIZE_KEY = "AvaliableMemSize";
	private static final String CUSTOM_DATA_KEY = "CustomData";
	private static final String STACK_TRACE_KEY = "StackTrace";
	private static final String OUT_OF_MEMORY_ERROR = "bitmap size exceeds VM";
	private static final String WIDGET_INIT = "android.widget.RemoteViews.<init>";
	private static final String UPGRADE_ERROR = "result:3java.lang.ArrayIndexOutOfBoundsException: result:3";

	// This is where we collect crash data
	private Properties mCrashProperties = new Properties();

	// Some custom parameters can be added by the application developer. These
	// parameters are stored here.
	Map<String, String> mCustomParameters = new HashMap<String, String>();
	// This key is used in the mCustomParameters Map to store user comment in
	// NOTIFICATION interaction mode.
	static final String USER_COMMENT_KEY = "user.comment";
	// This key is used to store the silent state of a report sent by
	// handleSilentException().
	static final String IS_SILENT_KEY = "silent";
	static final String SILENT_PREFIX = IS_SILENT_KEY + "-";
	static final String ERROR_FILE_TYPE = "_stk.txt";

	static final String EXTRA_REPORT_FILE_NAME = "REPORT_FILE_NAME";

	// A reference to the system's previous default UncaughtExceptionHandler
	// kept in order to execute the default exception handling after sending
	// the report.
	private Thread.UncaughtExceptionHandler mDfltExceptionHandler;

	// Our singleton instance.
	private static ErrorReporter sInstanceSingleton;

	// The application context
	private Context mContext;

	// User interaction mode defined by the application developer.
//	private ReportingInteractionMode mReportingInteractionMode = ReportingInteractionMode.SILENT;

	// Bundle containing resources to be used in UI elements.
	private Bundle mCrashResources = new Bundle();

	// The Url we have to post the reports to.
	private Uri mFormUri;

	private String mCrashFilePath = null;

	private boolean mIsOutOfMemoryError = false;

	private boolean mIsUpgradeError = false;

	/**
	 * Use this method to provide the Url of the crash reports destination.
	 *
	 * @param formUri
	 *            The Url of the crash reports destination (HTTP POST).
	 */
	public void setFormUri(Uri formUri) {
		mFormUri = formUri;
	}

	/**
	 * <p>
	 * Use this method to provide the ErrorReporter with data of your running
	 * application. You should call this at several key places in your code the
	 * same way as you would output important debug data in a log file. Only the
	 * latest value is kept for each key (no history of the values is sent in
	 * the report).
	 * </p>
	 * <p>
	 * The key/value pairs will be stored in the GoogleDoc spreadsheet in the
	 * "custom" column, as a text containing a 'key = value' pair on each line.
	 * </p>
	 *
	 * @param key
	 *            A key for your custom data.
	 * @param value
	 *            The value associated to your key.
	 */
	public void addCustomData(String key, String value) {
		mCustomParameters.put(key, value);
	}

	/**
	 * Generates the string which is posted in the single custom data field in
	 * the GoogleDocs Form.
	 *
	 * @return A string with a 'key = value' pair on each line.
	 */
	private String createCustomInfoString() {
		String customInfo = "";
		Iterator<String> iterator = mCustomParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String currentKey = iterator.next();
			String currentVal = mCustomParameters.get(currentKey);
			customInfo += currentKey + " = " + currentVal + "\n";
		}
		return customInfo;
	}

	/**
	 * Create or return the singleton instance.
	 *
	 * @return the current instance of ErrorReporter.
	 */
	public static ErrorReporter getInstance() {
		synchronized (ErrorReporter.class) {
			if (sInstanceSingleton == null) {
				sInstanceSingleton = new ErrorReporter();
			}
		}
		return sInstanceSingleton;
	}

	/**
	 * <p>
	 * This is where the ErrorReporter replaces the default
	 * {@link UncaughtExceptionHandler}.
	 * </p>
	 *
	 * @param context
	 *            The android application context.
	 */
	public void init(Context context) {
		// Logcat.d(LOG_TAG, "Thread : " + Thread.currentThread().getName());
		// mDfltExceptionHandler =
		// Thread.currentThread().getUncaughtExceptionHandler();
		mDfltExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		// Thread.currentThread().setUncaughtExceptionHandler(this);
		mContext = context;
	}

	/**
	 * Calculates the free memory of the device. This is based on an inspection
	 * of the filesystem, which in android devices is stored in RAM.
	 *
	 * @return Number of bytes available.
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * Calculates the total memory of the device. This is based on an inspection
	 * of the filesystem, which in android devices is stored in RAM.
	 *
	 * @return Total number of bytes.
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * Collects crash data.
	 *
	 * @param context
	 *            The application context.
	 */
	private void retrieveCrashData(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			if (pi != null) {
//				mCrashProperties.put(SVN_KEY, String.valueOf(GoAppUtils.getSvnCode(mContext)));
				// Application Version
				mCrashProperties.put(VERSION_NAME_KEY, pi.versionName != null
						? pi.versionName
						: "not set");
				mCrashProperties.put(VERSION_CODE_KEY, Integer.toString(pi.versionCode));
			} else {
				// Could not retrieve package info...
				mCrashProperties.put(VERSION_NAME_KEY, "Package info unavailable");
				mCrashProperties.put(VERSION_CODE_KEY, "Package info unavailable");
			}
			// Application Channel
//			mCrashProperties.put(APP_CHANNEL_KEY, GoAppUtils.getChannel(mContext));
			// Application Package name
			mCrashProperties.put(PACKAGE_NAME_KEY, context.getPackageName());
			// Device model
			mCrashProperties.put(PHONE_MODEL_KEY, android.os.Build.MODEL);
			// Android version
			mCrashProperties.put(ANDROID_VERSION_KEY, android.os.Build.VERSION.RELEASE);

			// Android build data
			mCrashProperties.put(BOARD_KEY, android.os.Build.BOARD);
			mCrashProperties.put(BRAND_KEY, android.os.Build.BRAND);
			mCrashProperties.put(DEVICE_KEY, android.os.Build.DEVICE);
			mCrashProperties.put(DISPLAY_KEY, android.os.Build.DISPLAY);
			mCrashProperties.put(FINGERPRINT_KEY, android.os.Build.FINGERPRINT);
			mCrashProperties.put(HOST_KEY, android.os.Build.HOST);
			mCrashProperties.put(ID_KEY, android.os.Build.ID);
			mCrashProperties.put(MODEL_KEY, android.os.Build.MODEL);
			mCrashProperties.put(PRODUCT_KEY, android.os.Build.PRODUCT);
			mCrashProperties.put(TAGS_KEY, android.os.Build.TAGS);
			mCrashProperties.put(TIME_KEY, String.valueOf(android.os.Build.TIME));
			mCrashProperties.put(TYPE_KEY, android.os.Build.TYPE);
			mCrashProperties.put(USER_KEY, android.os.Build.USER);

			// Device Memory
			mCrashProperties.put(TOTAL_MEM_SIZE_KEY, "" + getTotalInternalMemorySize());
			mCrashProperties.put(AVAILABLE_MEM_SIZE_KEY, "" + getAvailableInternalMemorySize());

			// Application file path
			mCrashProperties.put(FILE_PATH_KEY, context.getFilesDir().getAbsolutePath());
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error while retrieving crash data", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang
	 * .Thread, java.lang.Throwable)
	 */
	public void uncaughtException(Thread t, Throwable e) {
		// 保护AdMob 挂掉的问题
		if (t != null && t.getName().startsWith("AdWorker")) {
			Log.w("ADMOB", "AdWorker thread thrown an exception.", e);
			return;
		}

		try {
			disable();
			e.printStackTrace();
			// Generate and send crash report
			handleException(e);
		} catch (Exception err) {
		}

//		if (mReportingInteractionMode == ReportingInteractionMode.TOAST) {
//			try {
//				// Wait a bit to let the user read the toast
//				final int sleep = 4000;
//				Thread.sleep(sleep);
//			} catch (InterruptedException e1) {
//				Logcat.e(LOG_TAG, "Error : ", e1);
//			}
//		}
//
//		if (mReportingInteractionMode == ReportingInteractionMode.SILENT) {
//			// If using silent mode, let the system default handler do it's job
//			// and display the force close diaLogcat.
//			mDfltExceptionHandler.uncaughtException(t, e);
//		} else {
			// If ACRA handles user notifications whit a Toast or a Notification
			// the Force Close dialog is one more notification to the user...
			// We choose to close the process ourselves using the same actions.
			CharSequence appName = "Application";
			try {
				PackageManager pm = mContext.getPackageManager();
				appName = pm.getApplicationInfo(mContext.getPackageName(), 0).loadLabel(
						mContext.getPackageManager());
				Log.e(LOG_TAG, appName + " fatal error : " + e.getMessage(), e);
			} catch (NameNotFoundException e2) {
				Log.e(LOG_TAG, "Error : ", e2);
			} finally {
				android.os.Process.killProcess(android.os.Process.myPid());
				final int code = 10;
				System.exit(code);
			}
//		}
	}

	/**
	 * Send a report for this Throwable.
	 *
	 * @param e
	 *            The Throwable to be reported. If null the report will contain
	 *            a new Exception("Report requested by developer").
	 */
	public void handleException(Throwable e) {
		if (e == null) {
			e = new Exception("Report requested by developer");
		}

//		if (reportingInteractionMode == ReportingInteractionMode.TOAST) {
//			Thread thread = new Thread() {
//
//				/*
//				 * (non-Javadoc)
//				 *
//				 * @see java.lang.Thread#run()
//				 */
//				@Override
//				public void run() {
//					Looper.prepare();
//					Toast.makeText(mContext, mCrashResources.getInt(CrashReport.RES_TOAST_TEXT),
//							Toast.LENGTH_LONG).show();
//					Looper.loop();
//				}
//
//			};
//			thread.start();
//		}
		retrieveCrashData(mContext);
		// Date CurDate = new Date();
		// Report += "Error Report collected on : " + CurDate.toString();

		// Add custom info, they are all stored in a single field
		mCrashProperties.put(CUSTOM_DATA_KEY, createCustomInfoString());

		// Build stack trace
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		printWriter.append(e.getMessage());
		e.printStackTrace(printWriter);
		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		mCrashProperties.put(STACK_TRACE_KEY, result.toString());
		printWriter.close();

//		if (!RingtoneEnv.sSIT) {
//			FirebaseCrash.report(e);
//		}

		mIsOutOfMemoryError = false;
		mIsUpgradeError = false;
		// Always write the report file
		String reportFileName = saveCrashReportFile();
		if (!mIsOutOfMemoryError && !mIsUpgradeError) {
//			if (reportingInteractionMode == ReportingInteractionMode.SILENT
//					|| reportingInteractionMode == ReportingInteractionMode.TOAST) {
//				// Send reports now
//				checkAndSendReports(mContext, null);
//			} else if (reportingInteractionMode == ReportingInteractionMode.NOTIFICATION) {
			// Send reports when user accepts
			notifySendReport(reportFileName);
//			}
		}
	}

//	public void handleSilentException(Throwable e) {
//		// Mark this report as silent.
//		mCrashProperties.put(IS_SILENT_KEY, "true");
//		handleException(e, ReportingInteractionMode.SILENT);
//	}

	/**
	 * Send a status bar notification. The action triggered when the
	 * notification is selected is to start the {@link CrashReportDialog}
	 * Activity.
	 *
	 * @see CrashReport#getCrashResources()
	 */
	void notifySendReport(String reportFileName) {
		// This notification can't be set to AUTO_CANCEL because after a crash,
		// clicking on it restarts the application and this triggers a check
		// for pending reports which issues the notification back.
		// Notification cancellation is done in the dialog activity displayed
		// on notification click.
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Default notification icon is the warning symbol
		int icon = android.R.drawable.stat_notify_error;
		if (mCrashResources.containsKey(CrashReport.RES_NOTIF_ICON)) {
			// Use a developer defined icon if available
			icon = mCrashResources.getInt(CrashReport.RES_NOTIF_ICON);
		}

		CharSequence tickerText = mContext.getText(mCrashResources
				.getInt(CrashReport.RES_NOTIF_TICKER_TEXT));
		CharSequence contentTitle = mContext.getText(mCrashResources
				.getInt(CrashReport.RES_NOTIF_TITLE));
		CharSequence contentText = mContext.getText(mCrashResources
				.getInt(CrashReport.RES_NOTIF_TEXT));

		Intent notificationIntent = new Intent(mContext, CrashReportDialog.class);
		notificationIntent.putExtra(EXTRA_REPORT_FILE_NAME, reportFileName);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notification = NotificationFactory
				.getNotification(mContext, icon, tickerText, contentTitle, contentText,
						contentIntent, null);
		if (null != notification) {
			notificationManager.notify(CrashReport.NOTIF_CRASH_ID, notification);
		}
	}

	/**
	 * When a report can't be sent, it is saved here in a file in the root of
	 * the application private directory.
	 */
	private String saveCrashReportFile() {
		try {
			Log.d(LOG_TAG, "Writing crash report file.");
			long timestamp = System.currentTimeMillis();
			String isSilent = mCrashProperties.getProperty(IS_SILENT_KEY);
			String fileName = createSaveFilePath();
			fileName += (isSilent != null ? SILENT_PREFIX : "") + "stack-" + timestamp
					+ ERROR_FILE_TYPE;
			File file = new File(fileName);
			FileOutputStream trace = new FileOutputStream(file, true);
			String track = mCrashProperties.getProperty(STACK_TRACE_KEY);
			if (track.contains(OUT_OF_MEMORY_ERROR) && track.contains(WIDGET_INIT)) {
				mIsOutOfMemoryError = true;
			} else if (track.contains(UPGRADE_ERROR)) {
				mIsUpgradeError = true;
			}

			track = track.replaceAll("\\n\\t", "\n");
			mCrashProperties.setProperty(STACK_TRACE_KEY, track);
			// mCrashProperties.store(trace, "");
			storeToOutputStream(trace, mCrashProperties);
			trace.flush();
			trace.close();
			return fileName;
		} catch (Exception e) {
			Log.e(LOG_TAG, "An error occured while writing the report file...", e);
		}
		return null;
	}

	private String createSaveFilePath() {
		if (mCrashFilePath == null) {
			mCrashFilePath = Environment.getExternalStorageDirectory().getPath() + "/FavoriteCode/log/";
			File destDir = new File(mCrashFilePath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		}

		return mCrashFilePath;
	}

	/**
	 * Returns an array containing the names of available crash report files.
	 *
	 * @return an array containing the names of available crash report files.
	 */
	String[] getCrashReportFilesList() {
		File dir = mContext.getFilesDir();

		//        Logcat.d(LOG_TAG, "Looking for error files in " + dir.getAbsolutePath());

		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(ERROR_FILE_TYPE);
			}
		};
		if (dir != null) {
			return dir.list(filter);
		} else {
			return null;
		}
	}


	private void sendMail(Context context, String file, String body) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		String[] receiver = new String[] { "gowidgetbugs@gmail.com" };
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = null;
		if (pi != null) {
			version = pi.versionName;
		} else {
			version = "unknow";
		}
		CharSequence channel = "200";
		String versionString = " v" + version + " " + channel + " Fix ";
		//        String projectName = mContext.getResources().getText(R.string.app_name)
		//                .toString();
		//        if (projectName == null || projectName.trim().equals("")) {
		//            projectName = LocalPath.APP_NAME;
		//        }
		//        String subject = projectName + versionString
		//                + mContext.getString(R.string.crash_subject);

		// 邮件标题
		String subject = "FavoriteCode" + versionString + "Error";

		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receiver);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		if (body != null) {
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
		}
		File fileIn = new File(file);
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileIn));
		emailIntent.setType("plain/text");
		IntentInvoker.startActivity(context, emailIntent);

	}

	/**
	 * This method looks for pending reports and does the action required
	 * depending on the interaction mode set.
	 */
	public void checkReportsOnApplicationStart() {
		String[] filesList = getCrashReportFilesList();
		if (filesList != null && filesList.length > 0) {
			boolean onlySilentReports = containsOnlySilentReports(filesList);
//			if (mReportingInteractionMode == ReportingInteractionMode.SILENT
//					|| mReportingInteractionMode == ReportingInteractionMode.TOAST
//					|| (mReportingInteractionMode == ReportingInteractionMode.NOTIFICATION && onlySilentReports)) {
//				if (mReportingInteractionMode == ReportingInteractionMode.TOAST) {
//					Toast.makeText(mContext, mCrashResources.getInt(CrashReport.RES_TOAST_TEXT),
//							Toast.LENGTH_LONG).show();
//				}
//				new ReportsSenderWorker().start();
//			} else if (mReportingInteractionMode == ReportingInteractionMode.NOTIFICATION) {
				ErrorReporter.getInstance().notifySendReport(filesList[filesList.length - 1]);
//			}
		}

	}

	/**
	 * Delete all report files stored.
	 */
	public void deletePendingReports() {
		String[] filesList = getCrashReportFilesList();
		if (filesList != null) {
			for (String fileName : filesList) {
				new File(mContext.getFilesDir(), fileName).delete();
			}
		}
	}

	/**
	 * Provide the UI resources necessary for user interaction.
	 * 
	 * @param crashResources
	 */
	void setCrashResources(Bundle crashResources) {
		mCrashResources = crashResources;
	}

	/**
	 * Disable ACRA : sets this Thread's {@link UncaughtExceptionHandler} back
	 * to the system default.
	 */
	public void disable() {
		if (mDfltExceptionHandler != null) {
			Thread.setDefaultUncaughtExceptionHandler(mDfltExceptionHandler);
		}
	}

	/**
	 * Checks if the list of pending reports contains only silently sent
	 * reports.
	 * 
	 * @return True if there only silent reports. False if there is at least one
	 *         nont-silent report.
	 */
	public boolean containsOnlySilentReports(String[] reportFileNames) {
		for (String reportFileName : reportFileNames) {
			if (!reportFileName.startsWith(SILENT_PREFIX)) {
				return false;
			}
		}
		return true;
	}

	public synchronized void storeToOutputStream(OutputStream out, Properties properties)
			throws IOException {
		if (properties == null) {
			return;
		}

		String lineSeparator = System.getProperty("line.separator");
		if (lineSeparator == null) {
			lineSeparator = "\n";
		}

		StringBuilder buffer = new StringBuilder();
		OutputStreamWriter writer = new OutputStreamWriter(out, "ISO8859_1"); //$NON-NLS-1$
		// 输出日期
		writer.write("#"); //$NON-NLS-1$
		writer.write(new Date().toString());
		writer.write(lineSeparator);

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			buffer.append(key);
			buffer.append('=');
			buffer.append((String) entry.getValue());
			buffer.append(lineSeparator);
			writer.write(buffer.toString());
			buffer.setLength(0);
		}
		writer.flush();
	}
}
