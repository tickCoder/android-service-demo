[TOC]

# android-service-demo

android平台上的service相关demo.

## 一、测试Demo

首先建立一个工程，选择`Empty Activity`，命名为`ServiceDemo`。

## 二、依赖于其他界面的YYService

这样的Service属于其他apk文件的一个组件，编译出的文件为`aar`文件，不能独立安装在系统中。

在工程中新建一个Module，选择`Android Library`，命名为`YYService`，并将其添加到`ServiceDemo`的依赖中。

在YYService的Module中增加YYService:Service、增加IYYService.aidl。

## 三、独立的XXSerice

独立的Service指的是编译出来后是一个单独的apk文件，这个apk文件没有任何界面，也不与其他的界面程序相关联，仅仅是作为一个Service存在与系统中。

在工程中新建一个Module，选择`Phone & Tablet Module`，命名为`XXService`，并选择`Add No Activity`。还需要在`Run/Debug Configurations`中将其`Launch Options`设置为`Nothing`

## 基本调用流程

### 添加`XXService`

其父类为`Service`，并需要在`AndroidManifest.xml`中设置其`Service`声明：

```
<service
    android:name=".XXService"
    android:enabled="true"
    android:exported="true"
    android:process=":remote"
    android:permission="com.example.tickcoder.xxservice.permission.xx">
    <intent-filter>
        <action android:name="com.example.tickcoder.xxservice.action"/>
    </intent-filter>
</service>
```

### 添加`IXXService.aidl`并声明接口

```
// IXXService.aidl
package com.example.tickcoder.xxservice;

// Declare any non-default types here with import statements

interface IXXService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
    String bindXXTest(String clientName);
    String askXXForAnswer(String clientName);
}
```

### 实现`IXXService.aidl`接口

在`XXService`中引入`IXXService.aidl`，并在`onBind`中返回：

```
private IXXService.Stub mIServiceBinder = new IXXService.Stub() {
    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public String askXXForAnswer(String clientName) throws RemoteException {
        Log.e("TICK", "XXService-askXXForAnswer");
        return "Hello Mr. " + clientName + ", I'm XXService, what can I do for you?";
    }

    @Override
    public String bindXXTest(String clientName) throws RemoteException {
        return "Hello Mr. " + clientName + ", I'm XXService!";
    }
};

@Override
public IBinder onBind(Intent intent) {
    Log.e("TICK", getClass().getName() + "-onBind");
    return mIServiceBinder;
}
```

### 调用AIDL接口

在调用方`(ServiceDemo)`中加入`aidl`文件，并实现以下：

```
private final static String XXSERVICE_PKGNAME = "com.example.tickcoder.xxservice";
private final static String XXSERVICE_NAME = "com.example.tickcoder.xxservice.XXService";
private final static String XXSERVICE_AIDLACTION = "com.example.tickcoder.xxservice.action";
private boolean isXXServiceConnected = false;

private IXXService mIXXService;
private ServiceConnection mXXServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.e("TICK", "XXService-onServiceConnected");
        String pkgName = name.getPackageName();
        if (pkgName.equalsIgnoreCase(XXSERVICE_PKGNAME)) {
            isXXServiceConnected = true;
            try {
                mIXXService = IXXService.Stub.asInterface(service);
                String response = mIXXService.bindXXTest("Outer");
                Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("TICK", "XXService-onServiceDisconnected");
        isXXServiceConnected = false;
    }
};

# 启动、绑定、发送消息、解绑、停止
case R.id.btn_xxservice_start: {
    Intent service = new Intent();
    service.setAction(XXSERVICE_AIDLACTION);
    service.setPackage(XXSERVICE_PKGNAME);
    startService(service);
    break;
}
case R.id.btn_xxservice_bind: {
    Intent service = new Intent();
    service.setAction(XXSERVICE_AIDLACTION);
    service.setPackage(XXSERVICE_PKGNAME);
    bindService(service, mXXServiceConnection, BIND_AUTO_CREATE);
    break;
}
case R.id.btn_xxservice_send: {
    if (mIXXService != null) {
        try {
            String response = mIXXService.askXXForAnswer("Outer");
            Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    break;
}
case R.id.btn_xxservice_unbind: {
    if (mIXXService != null && isXXServiceConnected) {
        unbindService(mXXServiceConnection);
        isXXServiceConnected = false;
    }
    break;
}
case R.id.btn_xxservice_stop: {
    Intent service = new Intent();
    service.setAction(XXSERVICE_AIDLACTION);
    service.setPackage(XXSERVICE_PKGNAME);
    stopService(service);
    isXXServiceConnected = false;
    break;
}
```

### 自定义类型

AIDL默认只支持几种基本类型，如果要支持自定义类型，例如`Book`类(`Book.java`)，需要实现`Parcelable`:

```
 public class Book implements Parcelable {
    private int bookId;
    private String bookName;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Book (int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    public int describeContents() {
        return 0;
    }

    private Book(Parcel source) {
        bookId = source.readInt();
        bookName = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
```

还需要为`Book`创建一个同名`Book.aidl`

```
// Book.aidl
package com.example.tickcoder.xxservice;

// Declare any non-default types here with import statements
import com.example.tickcoder.xxservice.Book;

parcelable Book;
```

申明接口`IBookManager.aidl`：

```
// IBookManager.aidl
package com.example.tickcoder.xxservice;

// Declare any non-default types here with import statements
import com.example.tickcoder.xxservice.Book;

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
    List<Book> getBookList();
    void addBook(in Book book);
}
```

把`Book.java`、`Book.aidl`、`IBookManager.aidl`都复制(需要保留包路径)到调用`XXService`的调用方中，方能编译运行成功，否则会提示`import class not found`。

## `build.gradle`中设置`aidl`路径：

```
android {
    ...
    sourceSets {
        main {
            java.srcDirs = ["src/main/java"]
            aidl.srcDirs = ["src/main/aidl"]
        }
    }
}
```

## 自动复制AIDL文件到Demo中

在主工程(`android-service-demo`)的`build.gradle`文件中增加如下：

```
task copyAIDL(type: Copy) {
    from "XXService/src/main/aidl/"
    into "ServiceDemo/src/main/aidl/"
}
```

在右侧`Gradle projects`中选择`Tasks-other-copyAIDL`，右键选择`Run...`，即可执行复制。此时在顶部运行按钮左侧会多出一个运行选项`copyAIDL`，先保存它，以后就可以直接选中此项，然后点击运行，即可自动执行`copyAIDL`了。

另外，怎么自动把类似`Book.java`复制到`Demo`中呢？该如何筛选出哪些需要复制(如`Book.java`)，哪些不要(如`XXService.java`)呢？
