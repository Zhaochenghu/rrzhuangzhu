package com.bxchongdian.app.views.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jaiky.imagespickers.ImageConfig;
import com.jaiky.imagespickers.ImageLoader;
import com.jaiky.imagespickers.ImageSelector;
import com.jaiky.imagespickers.ImageSelectorActivity;
import com.jaiky.imagespickers.container.GridViewForScrollView;
import com.jaiky.imagespickers.preview.MultiImgShowActivity;
import com.jaiky.imagespickers.utils.Utils;
import com.bxchongdian.app.R;
import com.bxchongdian.app.utils.GlideLoader;
import com.bxchongdian.app.utils.IntentUtils;
import com.bxchongdian.app.utils.ValidationUtils;
import com.bxchongdian.app.utils.image.Compressor;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.CheckErrorRequest;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.model.response.UploadFileResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvFileUtils;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.functions.Func1;

/********************************
 * Created by lvshicheng on 2017/6/13.
 ********************************/
@Route(path = "/login/station/error/common")
@RuntimePermissions
public class ErrorCorrectionActivity extends LvBaseAppCompatActivity {

    private static String[] items       = {
        "位置描述纠错",
        "停车费纠错",
        "无法充电反馈",
        "意见反馈"
    };
    private static String[] itemDetails = {
        "请输入该电站的具体位置描述",
        "免费",
        "请描述无法充电的现象",
        "我们的进步离不开您的每一个建议与创意，期待您的声音。"
    };

    @BindView(R.id.btn_commit)
    Button   btnCommit;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_content_count)
    TextView tvContentCount;
    @BindView(R.id.et_number)
    EditText etNumber;

    @BindView(R.id.grid_view)
    GridViewForScrollView gridView;

    private int subType;
    private File file;
    private String substationId;

    public static void navigation(int subType, String substationId) {
        ARouter.getInstance()
            .build("/login/station/error/common")
            .withInt("key", subType)
            .withString("substationId", substationId)
            .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_erro_correction);
    }

    @Override
    protected void initView() {
        super.initView();

        subType = getIntent().getIntExtra("key", -1);
        initToolbarNav(items[subType - 1]);
        etContent.setHint(itemDetails[subType - 1]);
        etContent.addTextChangedListener(watcher);
        substationId = getIntent().getStringExtra("substationId");
        initPhotoPicker();
    }

    @OnClick(R.id.btn_commit)
    public void commit() {
        if (etContent.getText().toString().trim().length() < 6){
            showToast("描述至少6个字");
            return;
        }else if (!LvTextUtil.isEmpty(etNumber.getText().toString().trim())){
            ValidationUtils utils = new ValidationUtils(this);
            if (!utils.phoneNumValidate(etNumber)){
                return;
            }
        }
        showLoadingDialog("提交反馈");
        ArrayList<String> datas = ((GridAdapter) gridView.getAdapter()).getDatas();
        if (datas != null && datas.size() > 0) {
            Observable.from(datas)
                    .flatMap(new Func1<String, Observable<UploadFileResponse>>() {
                        @Override
                        public Observable<UploadFileResponse> call(String path) {
                            DebugLog.log("path: %s", path);
                            try {
//                                DebugLog.log("压缩前：" + formatFileSize(getFileSize(new File(path))));
                                file = new Compressor(getApplicationContext()).compressToFile(new File(path));
//                                DebugLog.log("压缩后：" + formatFileSize(getFileSize(file)));
//                                DebugLog.log("压缩后的path：" + file.getAbsolutePath());
                                String fileMD5 = LvFileUtils.getFileMD5_32(file);
                                MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("md5orsha1", fileMD5 == null ? "" : fileMD5)
                                        .addFormDataPart("upfile", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                                        .build();
                                return ApiComponentHolder.sApiComponent.apiService().uploadSingleImage(body);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return Observable.error(new Throwable("文件压缩失败！"));
                        }
                    })
                    .map(new Func1<UploadFileResponse, String>() {
                        @Override
                        public String call(UploadFileResponse uploadFileResponse) {
                            //清除文件
                            if (file != null && file.exists()){
                                file.delete();
//                                boolean isDelete = file.delete();
//                                DebugLog.log("文件删除：" + isDelete);
                            }
                            if (uploadFileResponse.isSuccess()) {
                                return uploadFileResponse.content;
                            } else {
                                DebugLog.log("文件上传失败");
                                Observable.error(new Throwable("文件上传失败"));
                            }
                            return null;
                        }
                    })
                    .toList()
                    .flatMap(new Func1<List<String>, Observable<SimpleResponse>>() {
                        @Override
                        public Observable<SimpleResponse> call(List<String> strings) {
                            StringBuilder sbr = new StringBuilder();
                            for (int i = 0; i < strings.size(); i++) {
                                DebugLog.log(strings.get(i));
                                sbr.append(strings.get(i)).append(",");
                            }

                            CheckErrorRequest request = new CheckErrorRequest();
                            request.subType = String.valueOf(subType);
                            request.substationId = substationId; // 子站ID够了
                            request.cpId = "";
                            request.phone = etNumber.getText().toString();
                            request.remark = etContent.getText().toString();
                            request.imgUrl = sbr.toString();
                            return ApiComponentHolder.sApiComponent.apiService().checkError(request);
                        }
                    })
                    .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                    .subscribe(new SimpleSubscriber<SimpleResponse>() {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            dismissLoadingDialog();
                            showToast("提交失败，请重试");
                        }

                        @Override
                        public void onNext(SimpleResponse response) {
                            dismissLoadingDialog();
                            if (response.isSuccess()) {
                                showToast("提交成功");
                                finish();
                            } else {
                                showToast(response.msg);
                            }
                        }
                    });
        } else {
            CheckErrorRequest request = new CheckErrorRequest();
            request.subType = String.valueOf(subType);
            request.substationId = substationId; // 子站ID够了
            request.cpId = "";
            request.phone = etNumber.getText().toString();
            request.remark = etContent.getText().toString();

            ApiComponentHolder.sApiComponent.apiService().checkError(request)
                    .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                    .subscribe(new SimpleSubscriber<SimpleResponse>() {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            dismissLoadingDialog();
                            showToast("提交失败，请重试");
                        }

                        @Override
                        public void onNext(SimpleResponse response) {
                            dismissLoadingDialog();
                            if (response.isSuccess()) {
                                showToast("提交成功");
                                finish();
                            } else {
                                showToast(response.msg);
                            }
                        }
                    });
        }


    }


    /**
     * ---------- 权限处理
     */
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void turnToPick() {
        ImageSelector.open(this, imageConfig);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void showRationForPick(final PermissionRequest request) {
        showPermissionDialog();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void neverAskAgain() {
        showPermissionDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ErrorCorrectionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void showPermissionDialog() {
        new AlertDialog.Builder(this)
            .setMessage("需要读取照片或者拍照，建议您在设置中开启")
            .setNegativeButton("取消", null)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentUtils.turnToAppDetail(ErrorCorrectionActivity.this);
                }
            })
            .setCancelable(false)
            .show();
    }

    /**
     * ---------- 权限处理 END
     */

    public static final int REQUEST_CODE = 123;

    private ImageConfig imageConfig;

    private void initPhotoPicker() {
        GridAdapter gridAdapter = new GridAdapter(this, GlideLoader.getInstance());
        gridView.setAdapter(gridAdapter);

        imageConfig = new ImageConfig.Builder(GlideLoader.getInstance())
            .steepToolBarColor(getResources().getColor(R.color.titleBlue))
            .titleBgColor(getResources().getColor(R.color.titleBlue))
            .titleSubmitTextColor(getResources().getColor(R.color.white))
            .titleTextColor(getResources().getColor(R.color.white))
            // 开启多选   （默认为多选）
            .mutiSelect()
            // 多选时的最大数量   （默认 9 张）
            .mutiSelectMaxSize(6)
            // 已选择的图片路径
            .pathList(((GridAdapter) gridView.getAdapter()).mDatas)
            // 拍照后存放的图片路径（默认 /temp/picture）
            .filePath("/temp")
            // 开启拍照功能 （默认关闭）
            .showCamera()
            .requestCode(REQUEST_CODE)
            .build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            ((GridAdapter) gridView.getAdapter()).onDataChange(pathList);
        }
    }

    TextWatcher watcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
            try {
                tvContentCount.setText("还可以输入" + (200 - s.length()) + "字");
            }catch (Exception e){
                showToast("输入的文字超过了200字");
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                editStart = etContent.getSelectionStart();
                editEnd = etContent.getSelectionEnd();
                if (temp.length() > 230){
                    etContent.setText(temp.toString().substring(0,200));
                }else if (temp.length() > 200) {
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editEnd;
                    etContent.setText(s);
                    etContent.setSelection(tempSelection);
                }
            }catch (Exception e){
                showToast("输入的文字超过了200字");
            }

        }
    };

    private class GridAdapter extends BaseAdapter {

        private Context     mContext;
        private ImageLoader imageLoader;

        ArrayList<String> mDatas = new ArrayList<>();

        private FrameLayout.LayoutParams fl = null;

        private int containerWidth = 1080;

        private int rowCount = 3;

        GridAdapter(Context context, ImageLoader imageLoader) {
            this.mContext = context;
            this.imageLoader = imageLoader;

            initImgSize();
        }

        ArrayList<String> getDatas() {
            return mDatas;
        }

        private void initImgSize() {
            //带删除
            // 如果行数为4，中间间隔3个3dp共9dp + 距离右边4个8dp共32dp
            int size = containerWidth - Utils.dip2px(mContext, 3 * (rowCount - 1) + 8 * rowCount);
            size = Math.round(size / (float) rowCount);
            fl = new FrameLayout.LayoutParams(size, size);
            //填充上右，为删除按钮让出空间
            fl.setMargins(0, Utils.dip2px(mContext, 8), Utils.dip2px(mContext, 8), 0);
        }

        @Override
        public int getCount() {
            if (mDatas == null) {
                return 1;
            } else {
                if (mDatas.size() < 9) {
                    return mDatas.size() + 1;
                } else {
                    return mDatas.size();
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gradview_item, parent, false);
                holder.ivContent = (ImageView) convertView.findViewById(R.id.activity_item_ivImage);
                holder.ivContent.setLayoutParams(fl);
                holder.ivDel = (ImageView) convertView.findViewById(R.id.activity_item_ivDelete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.position = position;
            if ((mDatas == null || mDatas.size() < 7) && position == getCount() - 1) { // 添加ICON
                if (position < 6){
                    imageLoader.displayResource(mContext, R.drawable.global_img_default, holder.ivContent);
                    holder.ivDel.setVisibility(View.INVISIBLE);
                }else { //如果大于6，添加icon消失
                    holder.ivContent.setVisibility(View.GONE);
                    holder.ivDel.setVisibility(View.GONE);
                }
            } else {
                holder.ivDel.setVisibility(View.VISIBLE);
                imageLoader.displayImage(mContext, mDatas.get(position), holder.ivContent);

                holder.ivDel.setTag(mDatas.get(position));
                holder.ivDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //移除图片
                        mDatas.remove(v.getTag().toString());
                        notifyDataSetChanged();
                    }
                });
            }

            holder.ivContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((mDatas == null || mDatas.size() < 7)
                        && holder.position == getCount() - 1) { // 添加ICON
                        ErrorCorrectionActivityPermissionsDispatcher
                            .turnToPickWithCheck((ErrorCorrectionActivity) mContext);
                    } else {
                        //图片放大
                        Intent intent = new Intent(mContext, MultiImgShowActivity.class);
                        intent.putStringArrayListExtra("photos", mDatas);
                        intent.putExtra("position", holder.position);
                        Activity ac = (Activity) mContext;
                        ac.startActivity(intent);
                        ac.overridePendingTransition(com.jaiky.imagespickers.R.anim.zoom_in, 0);
                    }
                }
            });
            return convertView;
        }

        void onDataChange(List<String> data) {
            this.mDatas.clear();
            this.mDatas.addAll(data);
            this.notifyDataSetChanged();
        }

        class ViewHolder {
            ImageView ivContent;
            ImageView ivDel;

            int position;
        }
    }

//    /**
//     * 转换文件大小
//     * @param fileS
//     * @return
//     *
//     */
//    public static String formatFileSize(long fileS) {
//        DecimalFormat df = new DecimalFormat("#.00");
//        String fileSizeString = "";
//        String wrongSize = "0B";
//        if (fileS == 0) {
//            return wrongSize;
//        }
//        if (fileS < 1024) {
//            fileSizeString = df.format((double) fileS) + "B";
//        } else if (fileS < 1048576) {
//            fileSizeString = df.format((double) fileS / 1024) + "KB";
//        } else if (fileS < 1073741824) {
//            fileSizeString = df.format((double) fileS / 1048576) + "MB";
//        } else {
//            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
//        }
//        return fileSizeString;
//    }
//
//    /**
//     * 获取指定文件大小
//     * @param file
//     * @return
//     * @throws Exception 　　
//     */
//    public long getFileSize(File file) throws Exception {
//        long size = 0;
//        if (file.exists()) {
//            FileInputStream fis = null;
//            fis = new FileInputStream(file);
//            size = fis.available();
//        } else {
//            file.createNewFile();
//            Log.e("获取文件大小", "文件不存在!");
//        }
//        return size;
//    }
}
