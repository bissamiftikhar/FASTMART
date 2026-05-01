package com.livisync.smd_a2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.livisync.smd_a2.models.CartItem;
import com.livisync.smd_a2.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fastmart.db";
    private static final int DB_VERSION = 1;

    // Favourites table
    private static final String TABLE_FAV = "favourites";
    private static final String FAV_ID = "id";
    private static final String FAV_PRODUCT_ID = "product_id";
    private static final String FAV_NAME = "name";
    private static final String FAV_TYPE = "type";
    private static final String FAV_PRICE = "price";
    private static final String FAV_IMAGE = "image_res_id";

    // Cart table
    private static final String TABLE_CART = "cart";
    private static final String CART_ID = "id";
    private static final String CART_PRODUCT_ID = "product_id";
    private static final String CART_NAME = "name";
    private static final String CART_TYPE = "type";
    private static final String CART_PRICE = "price";
    private static final String CART_IMAGE = "image_res_id";
    private static final String CART_QTY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create favourites table
        db.execSQL("CREATE TABLE " + TABLE_FAV + " (" +
                FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FAV_PRODUCT_ID + " TEXT, " +
                FAV_NAME + " TEXT, " +
                FAV_TYPE + " TEXT, " +
                FAV_PRICE + " REAL, " +
                FAV_IMAGE + " INTEGER)");

        // Create cart table
        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CART_PRODUCT_ID + " TEXT, " +
                CART_NAME + " TEXT, " +
                CART_TYPE + " TEXT, " +
                CART_PRICE + " REAL, " +
                CART_IMAGE + " INTEGER, " +
                CART_QTY + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // ---- FAVOURITES ----

    public boolean addFavourite(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FAV_PRODUCT_ID, p.getProductId());
        cv.put(FAV_NAME, p.getName());
        cv.put(FAV_TYPE, p.getType());
        cv.put(FAV_PRICE, p.getPrice());
        cv.put(FAV_IMAGE, p.getImageResId());
        long result = db.insert(TABLE_FAV, null, cv);
        return result != -1;
    }

    public boolean isFavourite(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAV, null, FAV_PRODUCT_ID + "=?",
                new String[]{productId}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean removeFavourite(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FAV, FAV_PRODUCT_ID + "=?", new String[]{productId});
        return rows > 0;
    }

    public List<Product> getAllFavourites() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAV, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Product p = new Product(
                        cursor.getString(cursor.getColumnIndexOrThrow(FAV_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FAV_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FAV_TYPE)),
                        "",
                        cursor.getDouble(cursor.getColumnIndexOrThrow(FAV_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(FAV_IMAGE)),
                        ""
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ---- CART ----

    public boolean addToCart(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if already in cart
        Cursor cursor = db.query(TABLE_CART, null, CART_PRODUCT_ID + "=?",
                new String[]{item.getProductId()}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow(CART_QTY));
            cursor.close();
            return updateCartQuantity(item.getProductId(), qty + 1);
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put(CART_PRODUCT_ID, item.getProductId());
        cv.put(CART_NAME, item.getName());
        cv.put(CART_TYPE, item.getType());
        cv.put(CART_PRICE, item.getPrice());
        cv.put(CART_IMAGE, item.getImageResId());
        cv.put(CART_QTY, 1);
        long result = db.insert(TABLE_CART, null, cv);
        return result != -1;
    }

    public boolean updateCartQuantity(String productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CART_QTY, quantity);
        int rows = db.update(TABLE_CART, cv, CART_PRODUCT_ID + "=?", new String[]{productId});
        return rows > 0;
    }

    public boolean removeFromCart(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CART, CART_PRODUCT_ID + "=?", new String[]{productId});
        return rows > 0;
    }

    public List<CartItem> getAllCartItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CART, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(CART_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CART_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CART_TYPE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(CART_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CART_IMAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CART_QTY))
                );
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CART_ID)));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }

    public double getCartTotal() {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CART, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(CART_PRICE));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(CART_QTY));
                total += price * qty;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }
}