package com.takaharabooks.app.vcnews.ui.common;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

import androidx.collection.LruCache;

public class ImageProcessor
{
    private boolean m_bSaveDataCacheOnSD;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    public	static final String DISK_CACHE_SUBDIR = "thumbnails";
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;

    public ImageProcessor(Context context, LruCache<String, Bitmap> memoryCache) {
        // Memory Cache
        mMemoryCache = memoryCache;

        // Disk Cache
        File cacheDir = getCacheDir(context, DISK_CACHE_SUBDIR);
        mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        String strKey = key.replace(File.separator, "_");
        Bitmap bmp = mMemoryCache.get(strKey);
        if(null == bmp){
            bmp = getBitmapFromDiskCache(key);
            if(null != bmp) addBitmapToMemCache(key, bmp);
        }
        return mMemoryCache.get(strKey);
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap)
    {
        // 例外処理
        if(key == null) return;
        if(bitmap == null) return;
        String strKey = key.replace(File.separator, "_");
        // Add to memory cache as before
        mMemoryCache.put(strKey, bitmap);
    }

    public static File getCacheDir(Context context, String uniqueName) {
        // 外部ストレージが使える場合はそっちのディレクトリを使う
        final String cachePath = DiskLruCache.getExternalCacheDir(context).getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    public void addBitmapToCache(String key, Bitmap bitmap)
    {
        // 例外処理
        if(key == null) return;
        if(bitmap == null) return;
        String strKey = key.replace(File.separator, "_");
        // Add to memory cache as before
        addBitmapToMemCache(key, bitmap);

        // Also add to disk cache
        if (m_bSaveDataCacheOnSD && !mDiskCache.containsKey(strKey)) {
            mDiskCache.put(strKey, bitmap);
        }
    }

    public Bitmap getBitmapFromDiskCache(String key)
    {
        // 例外処理
        if(key == null) return null;
        String strKey = key.replace(File.separator, "_");
        return mDiskCache.get(strKey);
    }
}