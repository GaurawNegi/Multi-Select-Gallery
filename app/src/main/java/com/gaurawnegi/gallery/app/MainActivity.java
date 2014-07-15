package com.gaurawnegi.gallery.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    private ArrayList<String> mSelectedPhotos=new ArrayList<String>();
    private ListView listView=null;
    private ArrayAdapter mListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= (ListView) findViewById(R.id.listView);
        mListViewAdapter =new SelectedItemListAdapter(getApplicationContext(),mSelectedPhotos);
        listView.setAdapter(mListViewAdapter);
        findViewById(R.id.select).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {

                ArrayList<String> result = data.getStringArrayListExtra(MultiSelectGallery.KEY_ARRAYLIST_SELECTED_PHOTOS);
                mSelectedPhotos.clear();
                if (result == null) {
                    return;
                }
                
                    Iterator<String> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        String path = (String) iterator.next();
                        if (!mSelectedPhotos.contains(path)) {
                            mSelectedPhotos.add(path);
                        }

                }
                mListViewAdapter.notifyDataSetChanged();

            }
        } catch (Exception e) {

        }


    }

    @Override
    public void onClick(View view) {
    switch(view.getId()){
        case R.id.select:
            Intent intent=new Intent(this, MultiSelectGallery.class);
            intent.putExtra(MultiSelectGallery.KEY_ARRAYLIST_SELECTED_PHOTOS,mSelectedPhotos);
            startActivityForResult(intent, 100);
            break;


    }

    }

    public  class SelectedItemListAdapter extends ArrayAdapter<String>{

        private DisplayImageOptions mDisplayImageOptions=null;
        private ImageLoader mImageLoader;

        public SelectedItemListAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
            createImageLoader();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.listview_image_view,null);
                convertView.setTag(new ListViewHolder((ImageView)convertView.findViewById(R.id.thumb),(TextView)convertView.findViewById(R.id.path)));
            }

            ListViewHolder holder= (ListViewHolder) convertView.getTag();

            holder.path.setText(getItem(position));
            mImageLoader.displayImage("file://" + getItem(position),holder.thumb,mDisplayImageOptions);
            return convertView;
        }

        public void createImageLoader(){


            DisplayImageOptions.Builder defaultOptionsBuilder = new DisplayImageOptions.Builder()//
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



    }

    public static class ListViewHolder{
        public ListViewHolder(ImageView thumb,TextView path) {
            this.thumb = thumb;
            this.path=path;
        }

        public ImageView thumb;
        public TextView path;
    }
}
