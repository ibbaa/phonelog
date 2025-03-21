package net.ibbaa.phonelog.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import androidx.documentfile.provider.DocumentFile;
import net.ibbaa.phonelog.LogFileManager;

/**
 * Utility class for managing log files using Android document file API.
 */
public class DocumentFileManager {

    private final static int BUFFER_SIZE_1024 = 1024;
    private final static int MAX_DUPLICATE_FILES = 99;

    /**
     * Constructor
     */
    public DocumentFileManager() {

    }

    /**
     * Generate a valid file name by adding a suffix if duplicates are found
     * 
     * @param folder    the folder
     * @param file      the file
     * @param timestamp the timestamp
     * @return the valid file name
     */
    public String getValidFileName(DocumentFile folder, String file, Long timestamp) {
	LogFileManager logFileManager = new LogFileManager();
	try {
	    if (!fileExists(folder, file)) {
		return file;
	    }
	    String timestampFileName = file;
	    if (timestamp != null) {
		timestampFileName = logFileManager.suffixFileName(file, logFileManager.getTimestampSuffix(timestamp));
		if (!fileExists(folder, timestampFileName)) {
		    return timestampFileName;
		}
	    }
	    for (int ii = 1; ii <= MAX_DUPLICATE_FILES; ii++) {
		String numberFileName = logFileManager.suffixFileName(timestampFileName, getNumberSuffix(ii));
		if (!fileExists(folder, numberFileName)) {
		    return numberFileName;
		}
	    }
	} catch (Exception exc) {
	    // Do nothing
	}
	return null;
    }

    private String getNumberSuffix(int number) {
	return "(" + number + ")";
    }

    /**
     * Zip the provided files
     * 
     * @param context       the Android context
     * @param documentFiles the files
     * @param zipFile       the zip file
     */
    public void zipDocumentFiles(Context context, List<DocumentFile> documentFiles, DocumentFile zipFile) {
	ZipOutputStream zipOutputStream = null;
	ParcelFileDescriptor fileDescriptor = null;
	try {
	    fileDescriptor = context.getContentResolver().openFileDescriptor(zipFile.getUri(), "w");
	    if (fileDescriptor == null) {
		return;
	    }
	    zipOutputStream = new ZipOutputStream(new FileOutputStream(fileDescriptor.getFileDescriptor()));
	    for (DocumentFile currentDocumentFile : documentFiles) {
		if (currentDocumentFile.exists() && currentDocumentFile.isFile()) {
		    writeDocumentFileToZip(context, currentDocumentFile, zipOutputStream);
		}
	    }
	    for (DocumentFile currentDocumentFile : documentFiles) {
		if (currentDocumentFile.exists() && currentDocumentFile.isFile()) {
		    currentDocumentFile.delete();
		}
	    }
	} catch (FileNotFoundException exc) {
	    // do nothing
	} finally {
	    if (zipOutputStream != null) {
		try {
		    zipOutputStream.flush();
		    zipOutputStream.close();
		} catch (IOException exc) {
		    // do nothing
		}
	    }
	    if (fileDescriptor != null) {
		try {
		    fileDescriptor.close();
		} catch (IOException exc) {
		    // do nothing
		}
	    }
	}
    }

    private void writeDocumentFileToZip(Context context, DocumentFile file, ZipOutputStream zipOutputStream) {
	ZipEntry zipEntry = null;
	ParcelFileDescriptor fileDescriptor = null;
	FileInputStream fileInputStream = null;
	try {
	    fileDescriptor = context.getContentResolver().openFileDescriptor(file.getUri(), "r");
	    if (fileDescriptor == null) {
		return;
	    }
	    fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
	    zipEntry = new ZipEntry(file.getName());
	    zipOutputStream.putNextEntry(zipEntry);
	    byte[] buffer = new byte[BUFFER_SIZE_1024];
	    int read;
	    while ((read = fileInputStream.read(buffer, 0, BUFFER_SIZE_1024)) >= 0) {
		zipOutputStream.write(buffer, 0, read);
	    }
	} catch (Exception exc) {
	    // do nothing
	} finally {
	    if (zipEntry != null) {
		try {
		    zipOutputStream.closeEntry();
		} catch (IOException exc) {
		    // do nothing
		}
	    }
	    if (fileInputStream != null) {
		try {
		    fileInputStream.close();
		} catch (IOException exc) {
		    // do nothing
		}
	    }
	    if (fileDescriptor != null) {
		try {
		    fileDescriptor.close();
		} catch (IOException exc) {
		    // do nothing
		}
	    }
	}
    }

    /**
     * Delete the oldest file
     * 
     * @param files the files
     */
    public void deleteOldestDocumentFile(List<DocumentFile> files) {
	if (files == null) {
	    return;
	}
	try {
	    DocumentFile min = null;
	    for (DocumentFile file : files) {
		if (min == null || file.lastModified() < min.lastModified()) {
		    min = file;
		}
	    }
	    if (min != null) {
		min.delete();
	    }
	} catch (Exception exc) {
	    // do nothing
	}
    }

    /**
     * Returns if the specified file exists in the specified folder
     * 
     * @param folder   the fiolder
     * @param fileName the file
     * @return if the file exists
     */
    public boolean fileExists(DocumentFile folder, String fileName) {
	return folder.findFile(fileName) != null;
    }
}
