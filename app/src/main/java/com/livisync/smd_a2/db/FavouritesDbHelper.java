package com.livisync.smd_a2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import com.livisync.smd_a2.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper for managing favourites using SQLite.
 * Handles CRUD operations for the favourites table.
 */
public class FavouritesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fastmart_favourites.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVOURITES = "favourites";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_ORIGINAL_PRICE = "originalPrice";
    private static final String COLUMN_IMAGE_RES_ID = "imageResId";
    private static final String COLUMN_SELLER_ID = "sellerId";
    private static final String COLUMN_TYPE = "type";

    // SQL to create table
    private static final String CREATE_TABLE_FAVOURITES =
            "CREATE TABLE " + TABLE_FAVOURITES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUCT_ID + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_PRICE + " REAL NOT NULL, " +
                    COLUMN_ORIGINAL_PRICE + " REAL, " +
                    COLUMN_IMAGE_RES_ID + " INTEGER, " +
                    COLUMN_SELLER_ID + " TEXT, " +
                    COLUMN_TYPE + " TEXT)";

    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVOURITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }

    /**
     * Insert a product into favourites.
     * @param product Product object to add to favourites
     * @return true if insertion successful, false otherwise
     */
    public boolean addToFavourites(Product product) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_ID, product.getProductId());
            values.put(COLUMN_NAME, product.getName());
            values.put(COLUMN_DESCRIPTION, product.getDescription());
            values.put(COLUMN_PRICE, product.getPrice());
            values.put(COLUMN_IMAGE_RES_ID, product.getImageResId());
            values.put(COLUMN_SELLER_ID, product.getSellerId());
            values.put(COLUMN_TYPE, product.getType());

            long result = db.insert(TABLE_FAVOURITES, null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a product from favourites by product ID.
     * @param productId Product ID to remove
     * @return true if deletion successful, false otherwise
     */
    public boolean removeFromFavourites(String productId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete(TABLE_FAVOURITES, COLUMN_PRODUCT_ID + " = ?", new String[]{productId});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a product exists in favourites.
     * @param productId Product ID to check
     * @return true if product is favourited, false otherwise
     */
    public boolean isFavourited(String productId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_FAVOURITES, null, COLUMN_PRODUCT_ID + " = ?", 
                    new String[]{productId}, null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all favourited products.
     * @return List of Product objects
     */
    public List<Product> getAllFavourites() {
        List<Product> favourites = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_FAVOURITES, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    String productId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID));
                    String sellerId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_ID));

                    Product product = new Product(productId, name, type, description, price, imageResId, sellerId);
                    favourites.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return favourites;
    }

    /**
     * Get the count of favourited products.
     * @return Number of products in favourites
     */
    public int getFavouritesCount() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FAVOURITES, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Clear all favourites.
     */
    public void clearAllFavourites() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_FAVOURITES, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
