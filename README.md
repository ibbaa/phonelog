# phonelog

<i>phonelog</i> is a very simple logging facility for Android. It is a pure Java library without any dependencies, so it can be used in any Java code, but it is primarily meant for Android.

## Motivation

The problem with Android logging is that on real devices you have to connect the device to your computer and fiddle with adb to see the logs, which is not feasible for real live tests. Besides that Android system logging truncates output and swallows log messages if the amount of log messages is excessive. There is no easy way to seperate between debugging/test environments and production since the designated approach is to remove log statements from the code with ProGuard (R8) in production. The approach of <i>phonelog</i> is to write log files to app specific storage. In production only log messages with severity ERROR are written to Android system log. Alternatively the ProGuard approach can be used to remove log statements in production for performance reasons.

## Installation

TODO: Publish to Maven central and provide example for Maven and Gradle

## Signature

The released jar files are signed with GPG and can be verified as such:

`gpg --verify phonelog-VERSION.jar.asc phonelog-VERSION.jar`

The necessary public key can be retrieved as such:

`gpg --keyserver keyserver.ubuntu.com --recv-keys 05233A4E81F5C2BF94881F046F4B86DD15CBC5EC`

TODO: Upload public key to keyserver.ubuntu.com

## Usage

<i>phonelog</i> does not use configuration files but is configured through Java with feasible defaults. If you use the `android.util.Log` class you can simply replace this with `net.ibbaa.phonelog.Log` and it should compile. By default `net.ibbaa.phonelog.Log` does not log anything. The designated approach is to configure a `net.ibbaa.phonelog.FileLogger` for debugging and a `net.ibbaa.phonelog.JavaSystemLogger` for production on startup of the app:

```
String logDir = new File(getExternalFilesDir(null), "log").getAbsolutePath();
if (BuildConfig.DEBUG) {
  Log.initialize(new FileLogger(logDir));
} else {
  Log.initialize(new JavaSystemLogger());
}
```

This writes the log files to app specific storage in a folder named <i>log</i> for the DEBUG build with DEBUG log level. The app specific storage is `Android/data/your.app.id`. Theoratically any folder can be used but for the app specific storage you don't need any special access permissions. For the RELEASE build messages go to Java system log, which essentially logs to Android system log just as `android.util.Log` does with ERROR log level. Alternatively the class `net.ibbaa.phonelog.AndroidSystemLogger` can be used, but the source has to be copied to your app and the lines with `android.util.Log` must be commented in because the class depends on Android which is unwanted for <i>phonelog</i>. Of course you can as well configure ProGuard to remove logging in production or to configure nothing for the RELEASE build which essentially swallows all log messages.

## Logger

<i>phonelog</i> depends on implementations of `net.ibbaa.phonelog.ILogger` and provides the three implementations `net.ibbaa.phonelog.FileLogger`, `net.ibbaa.phonelog.JavaSystemLogger` and `net.ibbaa.phonelog.AndroidSystemLogger`. Any logger takes a delegate logger in its constructor and forwards the message to the delegate, if provided, before processing it on its own.

The log levels are the same as in `android.util.Log`: VERBOSE, DEBUG, INFO, WARN, ERROR with VERBOSE being the finest.

### FileLogger

`FileLogger` takes the log directory in the only necessary constructor parameter. Log files go to this directory with the name `app.log`. After 10 MByte the log file is rotated and renamed to `app_timestamp.log`. After 50 log files the files are archived by creating a `app_timestamp.zip` file with the 50 log files. The archives are never deleted, but automatic deletion of older archives can be configured. This should be good for really excessive logging and while it can fill up storage, in practice apps can run for months or years before there's any real danger of running out of space, even without deletion. `FileLogger` is very fast by keeping log messages in a queue and processing them in the background.

The constructor parameters are:

- <i>logDirectory</i>: the directory log files are written to. Must be provided and can be any folder with write permissions.
- <i>logFileName</i>: the name of the log file. Default is `app.log`. Timestamp and other suffixes are appended on demand.
- <i>maxLevel</i>: the max log level. Messages finer than this are not logged. Default is DEBUG.
- <i>maxFileSize</i>: the log file size in bytes before rotating the file. Default is 10,485,760 (10 MByte).
- <i>archiveFileCount</i>: the number of log files before archiving takes place. Default is 50.
- <i>deleteFileCount</i>: the number of archives before the oldest one is deleted. Default is -1, i.e. nothing is deleted.
- <i>logFormatter</i>: an implementation of `net.ibbaa.phonelog.ILogFormatter`. Default is `net.ibbaa.phonelog.DefaultLogFormatter`.
- <i>delegateLog</i>: an implementation of `net.ibbaa.phonelog.ILogger`. Log messages are forwarded to the delegate but are also processed by the logger.

### ILogFormatter

Implementations of this interface are used for formatting the log messages for `FileLogger`. Provided implementations are `net.ibbaa.phonelog.DefaultLogFormatter` and `net.ibbaa.phonelog.PassthroughMessageLogFormatter`. 

- `DefaultLogFormatter` uses the format TIMESTAMP THREAD-ID LEVEL TAG MESSAGE, e.g. `2020-11-21 01:38:05.626 [pool-600-thread-1] DEBUG de.ibba.keepitup.service.network.PingCommand: Ping output: PING 193.99.144.80 (193.99.144.80) 56(84) bytes of data.`.
- `PassthroughMessageLogFormatter` does not format the message but writes it as it is, ignoring the tag.

### JavaSystemLogger

`JavaSystemLogger` delegates to `java.util.logging.Logger`, which essentially is the same as using `android.util.Log`, besides the log levels do not map 1:1, but `JavaSystemLogger` does the translation, so it's the same just as `android.util.Log` would be used. No constructor parameter is necessary, but some are available:

- <i>maxLevel</i>: the max log level. Messages finer than this are not logged. Default is ERROR.
- <i>handler</i>: an implementation of `java.util.logging.Handler`. Default is no handler.
- <i>delegateLog</i>: an implementation of `net.ibbaa.phonelog.ILogger`. Log messages are forwarded to the delegate but are also processed by the logger.

A problem with Java system logging in Android is, that it ignores messages with DEBUG level and finer. This is no problem if `JavaSystemLogger` is used as intended for production use, because all other levels are logged just fine. If you want to use <i>phonelog</i> with `JavaSystemLogger` for all levels, you can configure a custom handler (passed in constructor). `net.ibbaa.phonelog.AndroidSystemLoggingHandler` can be used for that, but the source has to be copied to your app and the lines with `android.util.Log` must be commented in. Additionally `net.ibbaa.phonelog.JavaSystemLogger.removeSystemHandler` can be called to remove the predefined handler. Otherwise log messages with high levels are written twice. This approach is not really meaningful, but can be done anyway if really wanted.

### AndroidSystemLoggingHandler

`AndroidSystemLoggingHandler` delegates to `android.util.Log`. Since it depends on Android classes, it can't be used as provided. The relevant lines are commented out, so it does nothing. It must be copied to your app and the lines with `android.util.Log` must be commented in. This class does not have the mentioned problem with DEBUG log messages being swallowed.

The constructor parameters are:

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

The name of the text file is the provided <i>baseFileName</i>. The current timestamp and the extension <i>.txt</i> is appended to the file name. If no <i>baseFileName</i> is provided, the simple class name of the first dumped object is used (all lower case). If a <i>tag</i> and <i>message</i> is provided, a log message as described [here](#logger) is written using the `DefaultLogFormatter`. If no <i>tag</i> and <i>message</i> is provided, this message is not written. Then for each object in the returned `List`, the method `toString()` is called and the result is written as a line to the text file. This is all done in the background, the call to `Dump.dump` immediately returns. Each `Dump.dump` call writes one file, the size of the file is not limited. Besides that, the [descibed](#FileLogger) rules apply, i.e. after 50 files of the same <i>baseFileName</i>, an archive is created. By default, the archives will never be deleted, but this can be changed.

### FileDump

The dump feature depends on the implementation of `net.ibbaa.phonelog.IDump`. The only provided implementation is `net.ibbaa.phonelog.FileDump`.

The constructor parameters are:

- <i>dumpDirectory</i>: the directory dump files are written to. Must be provided and can be any folder with write permissions.
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
