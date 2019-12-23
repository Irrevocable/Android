package cn.edu.hznu.weibo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import cn.edu.hznu.weibo.Utils.ResizeLinearLayout;
import cn.edu.hznu.weibo.Utils.RichEditText;
import cn.edu.hznu.weibo.Utils.UI.StatusBarUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("HandlerLeak")
public class EditorActivity extends Activity implements View.OnClickListener {
    public static final int RELEASE_SUCCESS=0;
    private TextView cancel;
    private TextView name;
    private Button send;
    private Handler handler;


    private ResizeLinearLayout baseContent;
    private EditText articleTitle;
    private RichEditText contentRichEditText;
    private TextView completeImg;
    private ImageView galleryImg;

    private int appHeight;
    private int baseLayoutHeight;

    private int currentStatus;
    private static final int SHOW_TOOLS = 1;
    private static final int SHOW_KEY_BOARD = 2;
    private static final int RESIZE_LAYOUT = 1;

    private boolean flag = false; // 控制何时显示下方tools

    private InputHandler inputHandler = new InputHandler();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.send_btn:
                if (contentRichEditText.getText().length() != 0) {
                    String content = contentRichEditText.getRichText();
                    content=content.replaceAll("/storage/emulated/0/Download","img");
                    System.out.println(content);
                    sendReleaseRequest(content);
                }
                break;
        }
    }

    private void sendReleaseRequest(String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", "release")
                            .add("content", content)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/deal")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message msg = new Message();
                    if (responseData.equals("success")) {
                        msg.what = RELEASE_SUCCESS;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class InputHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case RESIZE_LAYOUT:
                    if (msg.arg1 == SHOW_TOOLS) {
                        currentStatus = SHOW_TOOLS;
                    } else {
                        currentStatus = SHOW_KEY_BOARD;
                        baseLayoutHeight = baseContent.getHeight();
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        baseContent = (ResizeLinearLayout) findViewById(R.id.editor_base_content);

        completeImg = (TextView) findViewById(R.id.editor_edit_complete);
        galleryImg = (ImageView) findViewById(R.id.editor_gallery_img);

//        articleTitle = (EditText) findViewById(R.id.editor_article_title);
        contentRichEditText = (RichEditText) findViewById(R.id.editor_edit_area);
        contentRichEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    send.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    send.setTextColor(Color.parseColor("#F4CA8F"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        appHeight = getAppHeight();
        initImageLoader(this);
        init();
        StatusBarUtils.with(this).init();//图片沉浸式
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        name=(TextView)findViewById(R.id.username);
        name.setText(getIntent().getStringExtra("name"));
        send = (Button) findViewById(R.id.send_btn);
        send.setOnClickListener(EditorActivity.this);
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case RELEASE_SUCCESS:
                        Toast toast=Toast.makeText(EditorActivity.this,"发布成功!",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    private void init() {
        baseContent.setOnResizeListener(new ResizeLinearLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                // TODO Auto-generated method stub
                int selector = SHOW_TOOLS;
                if (h < oldh) {
                    selector = SHOW_KEY_BOARD;
                }
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = selector;
                inputHandler.sendMessage(msg);
            }
        });
        // 完成
        completeImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(EditorActivity.this,
                        contentRichEditText.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        galleryImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                gallery();
            }
        });
    }

    /**
     * 获取应用显示区域高度。。。 PS:该方法放到工具类使用会报NPE ，怀疑是没有传入activity所致，没有深究
     *
     * @return
     */
    public int getAppHeight() {
        /**
         * 获取屏幕物理尺寸高(单位：px)
         */
        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);

        /**
         * 获取设备状态栏高度
         */
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, top = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            top = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        /**
         * 屏幕高度减去状态栏高度即为应用显示区域高度
         */
        return ds.heightPixels - top;
    }

    /**
     * 系统软键盘与工具栏的切换显示
     */
    private void showTools(int id) {
        if (id == R.id.editor_gallery_img) {
            flag = false;
            // if (currentStatus == SHOW_TOOLS &&
            // contentRichEditText.hasFocus()) {
            if (currentStatus == SHOW_TOOLS) {
                showSoftKeyBoard();
            }
        } else {
            flag = true;
            if (currentStatus == SHOW_KEY_BOARD) {
                showSoftKeyBoard();
            }
        }
    }

    /**
     * 反复切换系统软键盘
     */
    private void showSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 工具栏添加图片的逻辑
     */
    public void gallery() {
        // 调用系统图库
        // Intent getImg = new Intent(Intent.ACTION_GET_CONTENT);
        // getImg.addCategory(Intent.CATEGORY_OPENABLE);
        // getImg.setType("image/*");
        Intent getImg = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(getImg, 1001);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001: {
                    // 添加图片
                    Bitmap originalBitmap = null;
                    Uri originalUri = data.getData();
                    // try {
                    // originalBitmap = BitmapFactory.decodeStream(resolver
                    // .openInputStream(originalUri));
                    // originalBitmap =
                    // ImageUtils.loadImage(resolver.openInputStream(originalUri));
                    originalBitmap = ImageLoader.getInstance().loadImageSync(
                            originalUri.toString());
                    if (originalBitmap != null) {
                        contentRichEditText.addImage(originalBitmap,
                                getAbsoluteImagePath(originalUri));
                    } else {
                        Toast.makeText(this, "获取图片失败", Toast.LENGTH_LONG).show();
                    }
                    //
                    // } catch (FileNotFoundException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // Toast.makeText(this, e.getMessage(),
                    // Toast.LENGTH_LONG).show();
                    // }
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * 获取指定uri的本地绝对路径
     *
     * @param uri
     * @return
     */
    @SuppressWarnings("deprecation")
    protected String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }

}
