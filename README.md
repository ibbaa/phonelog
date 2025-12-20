# phonelog

<i>phonelog</i> is a very simple logging facility for Java and Android. It can be used in any Java code, but it is primarily meant for Android and does provide some features only useable in Android.

## Motivation

The problem with Android logging is that on real devices you have to connect the device to your computer and fiddle with adb to see the logs, which is not feasible for real live tests. Besides that Android system logging truncates output and swallows log messages if the amount of log messages is excessive. There is no easy way to seperate between debugging/test environments and production since the designated approach is to remove log statements from the code with ProGuard (R8) in production. The approach of <i>phonelog</i> is to write log files to the device storage. In production only log messages with severity ERROR are written to Android system log. Alternatively the ProGuard approach can be used to remove log statements in production for performance reasons.

## Installation

<i>phonelog</i> is in Maven Central and can be added as a dependency to your project.

Maven:

```
<dependency>
    <groupId>net.ibbaa.phonelog</groupId>
    <artifactId>phonelog</artifactId>
    <version>2.1.1</version>
</dependency>
```
Gradle:

```
implementation 'net.ibbaa.phonelog:phonelog:2.0.0'
```
The jar files are also released here on Github. 

## Signature

The released jar files are signed with GPG and can be verified as such:

`gpg --verify phonelog-VERSION.jar.asc phonelog-VERSION.jar`

The necessary public key can be retrieved as such:

`gpg --keyserver keyserver.ubuntu.com --recv-keys 05233A4E81F5C2BF94881F046F4B86DD15CBC5EC`

## Usage

<i>phonelog</i> does not use configuration files but is configured through Java with feasible defaults. If you use the `android.util.Log` class you can simply replace this with `net.ibbaa.phonelog.Log` and it should compile. By default `net.ibbaa.phonelog.Log` does not log anything. The designated approach is to configure a `net.ibbaa.phonelog.FileLogger` or `net.ibbaa.phonelog.android.DocumentFileLogger` for debugging and a `net.ibbaa.phonelog.JavaSystemLogger` or `net.ibbaa.phonelog.android.AndroidSystemLogger` for production on startup of the app:

```
String logDir = new File(getExternalFilesDir(null), "log").getAbsolutePath();
if (BuildConfig.DEBUG) {
  Log.initialize(new FileLogger(logDir));
} else {
  Log.initialize(new JavaSystemLogger());
}
```

This example writes the log files to app specific storage in a folder named <i>log</i> for the DEBUG build with DEBUG log level. The app specific storage is `Android/data/your.app.id`. Theoratically any folder can be used but for the app specific storage no special access permissions are necessary. For the RELEASE build messages go to Java system log, which essentially logs to Android system log just as `android.util.Log` does with ERROR log level. This way does also work for pure Java applications without Android.

Alternatively the logger implemenation `net.ibbaa.phonelog.android.DocumentFileLogger` can be used. The `DocumentFileLogger` relies on the storage access framework. For recent versions of Android it is the designated way to get read and write access besides the app specific storage. The class `net.ibbaa.phonelog.anroid.AndroidSystemLogger`
can be used instead of the `net.ibbaa.phonelog.JavaSystemLogger` which delegates everything 1:1 to Android system log. This does only work for Android, of course.

Everything in the package `net.ibbaa.phonelog` is pure Java code and can be used in Java environments without any additional dependency. The package `net.ibbaa.phonelog.android` contains classes that rely on Android to work. It requires at least Android 5 (API level 21) and should work with all subsequent versions of Android. Besides the core Android library (which is always available) the document file api is used. The dependency 

```
implementation "androidx.documentfile:documentfile:1.0.1"
```
is required for that. It usually comes with other libraries of the Jetpack library transitively and should be available in most apps anyway. <i>phonelog</i> does not ship any libraries transitively and is compiled against repackaged Android libraries to remain compatible to pure Java.

## Logger

<i>phonelog</i> depends on implementations of `net.ibbaa.phonelog.ILogger` and provides the three implementations `net.ibbaa.phonelog.FileLogger`, `net.ibbaa.phonelog.android.DocumentFileLogger`, `net.ibbaa.phonelog.JavaSystemLogger` and `net.ibbaa.phonelog.android.AndroidSystemLogger`. `net.ibbaa.phonelog.android.DocumentFileLogger` and `net.ibbaa.phonelog.android.AndroidSystemLogger` rely on Android, the others do require only Java. Any logger takes a delegate logger in its constructor and forwards the message to the delegate, if provided, before processing it on its own.

The log levels are the same as in `android.util.Log`: VERBOSE, DEBUG, INFO, WARN, ERROR with VERBOSE being the finest.

### FileLogger

`FileLogger` takes the log directory in the only necessary constructor parameter. Log files go to this directory with the name `app.log`. In Android the app specific storage can be used, which can be obtained using `context.getExternalFilesDirs(null)` but generally, any directory with write permissions can be used and in older versions of Android, this can be nearly any directory of the external storage. After 10 MByte the log file is rotated and renamed to `app_timestamp.log`. After 50 log files the files are archived by creating a `app_timestamp.zip` file with the 50 log files. The archives are never deleted, but automatic deletion of older archives can be configured. This should be good for really excessive logging and while it can fill up storage, in practice apps can run for months or years before there's any real danger of running out of space, even without deletion. `FileLogger` is very fast by keeping log messages in a queue and processing them in the background. `FileLogger` can be used in pure Java applications.

The constructor parameters are:

- <i>logDirectory</i>: the directory log files are written to. Must be provided and can be any folder with write permissions.
- <i>logFileName</i>: the name of the log file. Default is `app.log`. Timestamp and other suffixes are appended on demand.
- <i>maxLevel</i>: the max log level. Messages finer than this are not logged. Default is DEBUG.
- <i>maxFileSize</i>: the log file size in bytes before rotating the file. Default is 10,485,760 (10 MByte).
- <i>archiveFileCount</i>: the number of log files before archiving takes place. Default is 50.
- <i>deleteFileCount</i>: the number of archives before the oldest one is deleted. Default is -1, i.e. nothing is deleted.
- <i>logFormatter</i>: an implementation of `net.ibbaa.phonelog.ILogFormatter`. Default is `net.ibbaa.phonelog.DefaultLogFormatter`.
- <i>delegateLog</i>: an implementation of `net.ibbaa.phonelog.ILogger`. Log messages are forwarded to the delegate but are also processed by the logger.

### DocumentFileLogger

`DocumentFileLogger` relies on the Android storage access framework and is the designated way to get read and write permissions on arbitrary folders in recent version of Android. The <i>logDirectory</i> must be the string representation of an Android content uri pointing to a directory. How to use the storage access framework is documented in the Android [DocumentFile documentation](https://developer.android.com/training/data-storage/shared/documents-files). Access to a directory's contents is necessary with the `ACTION_OPEN_DOCUMENT_TREE` intent action and the flags `FLAG_GRANT_PERSISTABLE_URI_PERMISSION`, `FLAG_GRANT_READ_URI_PERMISSION` and `FLAG_GRANT_WRITE_URI_PERMISSION`. The resulting uri can be converted to a string using `toString()` and passed to the `DocumentFileLogger` as <i>logDirectory</i>. Besides that, everything else is identical to the [FileLogger](#filelogger). `DocumentFileLogger` only works for Android.

The constructor parameters are:

- <i>context</i>: the application context.
- <i>logDirectory</i>: the directory log files are written to. Must be obtained using the storage access framework with read and write directory permissions.
- <i>logFileName</i>: the name of the log file. Default is `app.log`. Timestamp and other suffixes are appended on demand.
- <i>maxLevel</i>: the max log level. Messages finer than this are not logged. Default is DEBUG.
- <i>maxFileSize</i>: the log file size in bytes before rotating the file. Default is 10,485,760 (10 MByte).
- <i>archiveFileCount</i>: the number of log files before archiving takes place. Default is 50.
- <i>deleteFileCount</i>: the number of archives before the oldest one is deleted. Default is -1, i.e. nothing is deleted.
- <i>logFormatter</i>: an implementation of `net.ibbaa.phonelog.ILogFormatter`. Default is `net.ibbaa.phonelog.DefaultLogFormatter`.
- <i>delegateLog</i>: an implementation of `net.ibbaa.phonelog.ILogger`. Log messages are forwarded to the delegate but are also processed by the logger.

### ILogFormatter

Implementations of this interface are used for formatting the log messages for `FileLogger` and `DocumentFileLogger`. Provided implementations are `net.ibbaa.phonelog.DefaultLogFormatter` and `net.ibbaa.phonelog.PassthroughMessageLogFormatter`. 

- `DefaultLogFormatter` uses the format TIMESTAMP THREAD-ID LEVEL TAG MESSAGE, e.g. `2020-11-21 01:38:05.626 [pool-600-thread-1] DEBUG de.ibba.keepitup.service.network.PingCommand: Ping output: PING 193.99.144.80 (193.99.144.80) 56(84) bytes of data.`.
- `PassthroughMessageLogFormatter` does not format the message but writes it as it is, ignoring the tag.

### JavaSystemLogger

`JavaSystemLogger` delegates to `java.util.logging.Logger`. It is is primarily useful in pure Java environments. No constructor parameter is necessary, but some are available:

- <i>maxLevel</i>: the max log level. Messages finer than this are not logged. Default is ERROR.
- <i>handler</i>: an implementation of `java.util.logging.Handler`. Default is no handler.
- <i>delegateLog</i>: an implementation of `net.ibbaa.phonelog.ILogger`. Log messages are forwarded to the delegate but are also processed by the logger.

For Android the log levels of `android.util.Log` do not map 1:1 to pure Java log levels, but `JavaSystemLogger` does the translation, so it's the same just as `android.util.Log` would be used.

A problem with Java system logging in Android is, that it ignores messages with DEBUG level and finer. This is no problem if `JavaSystemLogger` is used as intended for production use, because all other levels are logged just fine. If you want to use <i>phonelog</i> with `JavaSystemLogger` for all levels, you can configure a custom handler (passed in constructor). `net.ibbaa.phonelog.android.AndroidSystemLoggingHandler` can be used for that. Additionally `net.ibbaa.phonelog.JavaSystemLogger.removeSystemHandler` can be called to remove the predefined handler. Otherwise log messages with high levels are written twice. This approach is not really meaningful, but can be done anyway if really wanted. The `AndroidSystemLoggerr` is recommended for Android.

### AndroidSystemLogger

`AndroidSystemLogger` delegates to `android.util.Log` 1:1. It only works for Android. No constructor parameter is necessary, but some are available:

- <i>maxLevel</i>: the max log level. Messages finer than this are not logged. Default is ERROR.
- <i>delegateLog</i>: an implementation of `net.ibbaa.phonelog.ILogger`. Log messages are forwarded to the delegate but are also processed by the logger.

## Dump

The dump feature can be used to write large amounts of data to a file regularly during app execution. It uses the logging facility and works similar but with the difference that the dumped data is collected in the background. With logging, the written data (the message) is assembled before it is passed to the logger. If there is a huge amount of data, collecting and assembling it can be time consuming, blocking the app and making it unresponsive. The data that can be written with the dump feature includes e.g. the content of database tables or large data files. This is only meant for debugging purposes of course. It can be configured as such:

```
String dumpDir = new File(getExternalFilesDir(null), "dump").getAbsolutePath();
if (BuildConfig.DEBUG) {
  Dump.initialize(new FileDump(dumpDir)));
}
```
With this configuration, dump files are written to app specific storage in a folder named <i>dump</i>. To write a dump, the class `net.ibbaa.phonelog.Dump` is used as such:

`Dump.dump(tag, message, baseFileName, this::dataToDump)`

<i>tag</i> and <i>message</i> are optional and <i>baseFileName</i> can be inferred, so this is usually enough:

`Dump.dump(this::dataToDump)`

The written dump files are text files. The method <i>dataToDump</i> must not take any parameters and must return a `List` of arbitrary objects. The functional interface is defined as such:

```
@FunctionalInterface
public interface IDumpSource {
    List<?> objectsToDump();
}
```

The name of the text file is the provided <i>baseFileName</i>. The current timestamp and the extension <i>.txt</i> is appended to the file name. If no <i>baseFileName</i> is provided, the simple class name of the first dumped object is used (all lower case). If a <i>tag</i> and <i>message</i> is provided, a log message as described [here](#logger) is written using the `DefaultLogFormatter`. If no <i>tag</i> and <i>message</i> is provided, this message is not written. Then for each object in the returned `List`, the method `toString()` is called and the result is written as a line to the text file. This is all done in the background, the call to `Dump.dump` immediately returns. Each `Dump.dump` call writes one file, the size of the file is not limited. Besides that, the [described](#filelogger) rules apply, i.e. after 50 files of the same <i>baseFileName</i>, an archive is created. By default, the archives will never be deleted, but this can be changed.

This approach can also be used in pure Java environments.

### FileDump

The dump feature depends on the implementation of `net.ibbaa.phonelog.IDump`. The only provided implementation is `net.ibbaa.phonelog.FileDump`. There is no implemenation for the storage access framework at the moment.

The constructor parameters are:

- <i>dumpDirectory</i>: the directory dump files are written to. Must be provided and can be any folder with write permissions. For Android, the app specific storage can be used. 
- <i>archiveFileCount</i>: the number of dump files with the same <i>baseFileName</i> before archiving takes place. Default is 50.
- <i>deleteFileCount</i>: the number of archives before the oldest one is deleted. Default is -1, i.e. nothing is deleted.
- <i>dumpFileExtension</i>: file extension of the dump files. Default is <i>txt</i>.
- <i>emptyMessage</i>: a message that is witten to the file if an empty list is passed to `dump`. Default is `null`, i.e. nothing is written.

## Build

<i>phonelog</i> uses Maven and can simply be built with `mvn clean install`. The profile `signing` signs the artifacts with a GPG key as required by Maven Central using `mvn clean install -Psigning`. For that in `settings.xml` the GPG properties must be defined for the build to work:

```
<properties>
    <gpg.executable>gpg</gpg.executable>
    <gpg.keyname>the key name</gpg.keyname>
    <gpg.passphrase>the passphrase</gpg.passphrase>
</properties>
```
For the `net.ibbaa.phonelog.android` package, the necessary Android dependencies are marked as <i>provided</i> in the `pom.xml`. The Android core library is in Maven central but the `androidx.documentfile:documentfile` library is not, besides the fact that `androidx.documentfile:documentfile` is packaged as <i>aar</i>, which cannot be processed by standard Maven. The necassary plugin does require a full Android SDK installation and has many other drawbacks. So <i>phonelog</i> provides the script `install_androidx.sh`, which downloads the required library from Google, repackages the necessary classes as <i>jar</i> and installs this in the local Maven repository as

```
<dependency>
    <groupId>net.ibbaa.phonelog.android</groupId>
    <artifactId>documentfile_repackage</artifactId>
    <version>1.1.0</version>
</dependency>
```

This script can be executed as part of the build process before the Maven build.
