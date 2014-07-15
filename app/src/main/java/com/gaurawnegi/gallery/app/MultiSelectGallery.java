package com.gaurawnegi.gallery.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaurawnegi.gallery.FolderGalleryItemViewManager;
import com.gaurawnegi.gallery.GalleryActivity;
import com.gaurawnegi.gallery.PhotoGalleryItemViewManager;
import com.gaurawnegi.gallery.SelectedFilesManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;


public class MultiSelectGallery extends GalleryActivity implements OnClickListener {

    private int maxPhotoSelectionAllowed=10;
    private DisplayImageOptions mDisplayImageOptions=null;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_multiselect_gallery);
        createImageLoader();
        mLayoutInflater = LayoutInflater.from(this);
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> selectedImages = intent
                    .getStringArrayListExtra(KEY_ARRAYLIST_SELECTED_PHOTOS);
            if (selectedImages != null && !selectedImages.isEmpty()) {
                setAlreadySelectedPhotos(selectedImages);
            }
        }
        super.onCreate(savedInstanceState);
        findViewById(R.id.done).setOnClickListener(this);
    }

    public void createImageLoader(){


        Builder defaultOptionsBuilder = new Builder()//
            .cacheOnDisc(true)//
            .cacheInMemory(true)//
            .delayBeforeLoading(0)//
            .considerExifParams(true)//
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)//
            .bitmapConfig(Bitmap.Config.RGB_565)//
            ;//

        mDisplayImageOptions=defaultOptionsBuilder.build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(getApplicationContext())//
                .defaultDisplayImageOptions(mDisplayImageOptions)//
                .memoryCacheSizePercentage(25) // default
                .discCacheSize(50 * 1024 * 1024);//
        ImageLoaderConfiguration config = builder.build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

    }

    @Override
    protected void toogleFolderView(boolean b) {

    }

    @Override
    protected void changeSelectedImageCount(int i) {

    }

    @Override
    protected int getGridViewId() {
        return R.id.gridGallery;
    }

    @Override
    protected void onFolderClick(View view, String s, String s2) {

    }

    @Override
    protected void onPhotoClick(View v, String imagePath, boolean isSelected,Cursor c) {
        PhotoViewHolder holder = (PhotoViewHolder) v.getTag();
        if (isSelected) {
            holder.selected.setVisibility(View.VISIBLE);
            holder.screenCover.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.INVISIBLE);
            holder.screenCover.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected boolean isValidFolder(View v, String folderName, String folderId){
        return true;
    }

    @Override
    protected boolean isValidPhoto(View v, String imagePath, Cursor c) {
        return true;
    }

    @Override
    protected FolderGalleryItemViewManager getFolderGalleryItemViewManager() {
        return new FolderGalleryItemViewManager() {
            @Override
            public void bindView(View view, Context context, Cursor c,String folderName, String thumbnailPath,int folderIdColumnIndex, int imageCountColumnIndex) {
                FolderViewHolder holder = (FolderViewHolder) view.getTag();
                holder.thumbnail.setImageResource(R.drawable.ic_launcher);
                holder.thumbnail.setTag(thumbnailPath);
                mImageLoader.displayImage("file://" + thumbnailPath,holder.thumbnail,mDisplayImageOptions);
                holder.totalImageCount.setText("("+ c.getString(imageCountColumnIndex) + ")");
                holder.folderName.setText(folderName);
                SelectedFilesManager selectedFilesManager = getSelectedFilesManager();
                int filesCount = selectedFilesManager.getFilesCount(c
                        .getString(folderIdColumnIndex));
                if (filesCount > 0) {
                    holder.selectedImageCount.setText(filesCount + "");
                    holder.screenCover.setVisibility(VISIBLE);
                    holder.selectedImageCount.setVisibility(VISIBLE);
                    holder.selectedImageCount
                            .setBackgroundResource(filesCount < 10 ? R.drawable.ic_notification
                                    : R.drawable.ic_notification);
                } else {
                    holder.selectedImageCount.setVisibility(GONE);
                    holder.screenCover.setVisibility(GONE);
                }

            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                View convertView = mLayoutInflater.inflate(
                        R.layout.gallery_folder_item, null);
                FolderViewHolder holder = new FolderViewHolder();
                holder.screenCover = convertView.findViewById(R.id.screenCover);
                holder.folderName = (TextView) convertView
                        .findViewById(R.id.folderDisplayName);
                holder.totalImageCount = (TextView) convertView
                        .findViewById(R.id.totalImageCount);
                holder.selectedImageCount = (TextView) convertView
                        .findViewById(R.id.selectedImageCount);
                holder.thumbnail = (ImageView) convertView
                        .findViewById(R.id.thumb);
                convertView.setTag(holder);

                return convertView;
            }
        };
    }

    @Override
    protected PhotoGalleryItemViewManager getPhotoGalleryItemViewManager() {
        return new PhotoGalleryItemViewManager() {
            @Override
            public void bindView(View view, Context context, Cursor c,String thumbnailPath, String imagepath, int folderIdColumnIndex, int folderNameColumnIndex,int imageNameColumnIndex) {
                PhotoViewHolder holder = (PhotoViewHolder) view.getTag();
                if (holder==null)
                    return;

                holder.thumbnail.setImageResource(R.drawable.ic_launcher);
                boolean isSelected = getSelectedFilesManager().contains(
                        c.getString(folderIdColumnIndex), imagepath);
                if (isSelected) {
                    holder.selected.setVisibility(VISIBLE);
                    holder.screenCover.setVisibility(VISIBLE);
                } else {
                    holder.selected.setVisibility(INVISIBLE);
                    holder.screenCover.setVisibility(INVISIBLE);
                }

                mImageLoader.displayImage("file://" + thumbnailPath,holder.thumbnail,mDisplayImageOptions);

            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                View convertView = mLayoutInflater.inflate(R.layout.gallery_image_view, null);
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumb);
                ImageView selected = (ImageView) convertView
                        .findViewById(R.id.isSelected);
                View screenCover = convertView.findViewById(R.id.screenCover);
                convertView.setTag(new PhotoViewHolder(thumbnail, selected,
                        screenCover));
                return convertView;
            }
        };
    }

    @Override
    protected int getMaxPhotoSelectionAllowed() {
        return maxPhotoSelectionAllowed;
    }

    @Override
    protected GalleryView initialGalleryView() {
        return GalleryView.Folder;
    }

    @Override
    protected SelectedFilesManager initialSelectedFilesManager() {
        return null;
    }

    @Override
    protected void onEmptyGallery() {
        findViewById(R.id.emptyGalleryMsg).setVisibility(VISIBLE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.done:
                onDoneClick();
                break;


        }
    }


    private static class PhotoViewHolder {
        public PhotoViewHolder(ImageView thumbnail, ImageView selected,
                               View screenCover) {
            this.thumbnail = thumbnail;
            this.selected = selected;
            this.screenCover = screenCover;
        }

        public ImageView thumbnail;
        public ImageView selected;
        public View screenCover;
    }

    private static class FolderViewHolder {
        public TextView folderName;
        public TextView totalImageCount;
        public TextView selectedImageCount;
        public View screenCover;
        public ImageView thumbnail;
    }


}
